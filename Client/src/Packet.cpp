#include <cstring>
#include <algorithm>
#include "../include/Packet.h"
#include "../include/utils.h"

using namespace std;

Packet::Packet() : Packet(OpCode::INVALID) {}

Packet::Packet(OpCode opcode) : opcode(opcode) {}

OpCode stringToCommand(const std::string& command)
{
	OpCode code = OpCode::INVALID;
	for (auto const& entry : map_PacketTypeToString)
	{
		if (command == entry.second)
		{
			code = entry.first;
			break;
		}
	}
	return code;
}

const char * Packet::serializePacket() const
{
	auto toSend = allocatePacket();
	shortToBytes((short)(opcode), toSend);
	return toSend;
}

Packet::~Packet() {}

StringPacket::StringPacket(OpCode type, const std::string& str) : Packet(type), content(str){}

const char * StringPacket::serialize() const
{
	auto toSend = allocatePacket();
	shortToBytes((short)(opcode), toSend);
	strncpy(toSend+2, content.c_str(), packetSize() - getOpcodeSize());
	return toSend;
}

size_t StringPacket::packetSize() const
{
	return getOpcodeSize() + content.size() + 1;
}


LoginPacket::LoginPacket(const std::string& loginUsername) : StringPacket(OpCode::LOGRQ, loginUsername) {}

DelPacket::DelPacket(const std::string& fileNameToDelete) : StringPacket(OpCode::DELRQ, fileNameToDelete) {}

RWPacket::RWPacket(OpCode type, const std::string& fileToMove) : StringPacket(type, fileToMove) {}


AckPacket::AckPacket(short block) : Packet(OpCode::ACK), blockNumber(block) {}

const char * AckPacket::serialize() const
{
	auto toSend = allocatePacket();
	shortToBytes((short)(opcode), toSend);
	shortToBytes((blockNumber), toSend+2);
	return toSend;
}

size_t AckPacket::packetSize() const
{
	return getOpcodeSize() + 2;
}

std::ostream& operator<<(std::ostream& os, ErrorTypes al)
{
	return os << (short)(al);
}

ErrorPacket::ErrorPacket(ErrorTypes type, const std::string& message) : Packet(OpCode::ERRORmsg), errorNumber(type), errorMessage(message) {}

const char * ErrorPacket::serialize() const
{
	auto toSend = allocatePacket();
	shortToBytes((short)(opcode), toSend);
	shortToBytes((short)(errorNumber), toSend+2);
	strncpy(toSend+4, errorMessage.c_str(), packetSize() - getOpcodeSize() - 2);
	return toSend;
}

size_t ErrorPacket::packetSize() const
{
	return getOpcodeSize() + sizeof(ErrorTypes) + errorMessage.size() + 1;
}

std::ostream& operator<<(std::ostream& os, BCastType bType)
{
	switch (bType) {
            case BCastType::DELETED:
            {
                return os << "del";
            }
            case BCastType::ADDED:
            {
                return os << "add";
            }
            default:
                return os;
	}
}

BCastPacket::BCastPacket(BCastType messageType, const std::string& fileChanged) : Packet(OpCode::BCAST), messageType(messageType), fileName(fileChanged) {}

const char * BCastPacket::serialize() const
{
	auto toSend = allocatePacket();
	auto currentPtr = toSend;
	shortToBytes((short)(opcode), toSend);
	currentPtr += 2;
	currentPtr[0] = static_cast<char>(messageType);
	currentPtr += sizeof(messageType);
	strncpy(currentPtr, fileName.c_str(),
		packetSize() - getOpcodeSize() - 2);
	return toSend;
}

size_t BCastPacket::packetSize() const
{
	return getOpcodeSize() + sizeof(messageType) + fileName.size() + 1;
}


DataPacket::DataPacket(short size, short blckNumber, const void* dataPkt) : Packet(OpCode::DATA), pcktSize(size), blockNumber(blckNumber), data(new char[size])
{
	memcpy(data, dataPkt, size);
}

DataPacket::DataPacket(const DataPacket & other) : DataPacket(other.pcktSize, other.blockNumber, other.data) {}

DataPacket & DataPacket::operator=(const DataPacket & other)
{
	opcode = other.opcode;
	pcktSize = other.pcktSize;
	blockNumber = other.blockNumber;
	char * new_data = new char[pcktSize]();
	std::copy_n(other.data, pcktSize, new_data);
	delete data;
	data = new_data;
	return *this;
}

const char * DataPacket::serialize() const
{
	auto toSend = allocatePacket();
	shortToBytes((short)(opcode), toSend);
	shortToBytes((pcktSize), toSend+2);
	shortToBytes((blockNumber), toSend+4);
	memcpy(toSend+6, data, pcktSize);
	return toSend;
}

size_t DataPacket::packetSize() const
{
	return getOpcodeSize() + 2 + 2 + pcktSize;
}

DataPacket::~DataPacket()
{
	delete data;
	data = nullptr;
}

Packet * deserializePacket(const char * buff)
{
	auto opcode = OpCode(bytesToShort((char*)(buff)));
	auto curr = buff + 2;

	switch (opcode) {
        case OpCode::RRQ:
        case OpCode::WRQ: {
            std::string fileName(curr);
            return new RWPacket(opcode, fileName);
        }
        case OpCode::DATA:
            return new DataPacket(bytesToShort((char*)(curr)),
                bytesToShort((char*)(curr + 2)), curr + 2 * 2);
        case OpCode::ACK: {
            return new AckPacket(bytesToShort((char*)(curr)));
        }
        case OpCode::ERRORmsg: {
            ErrorTypes errorCode = (ErrorTypes)(bytesToShort((char*)(curr)));
            curr += 2;
            std::string errorMessage(curr);
            return new ErrorPacket(errorCode, errorMessage);
        }
        case OpCode::DIRQ: {
            return new DisconnectPacket();
        }
        case OpCode::LOGRQ: {
            std::string fileName(curr);
            return new LoginPacket(fileName);
        }
        case OpCode::DELRQ: {
            std::string fileName(curr);
            return new DelPacket(fileName);
        }
        case OpCode::BCAST: {
            BCastType errorCode = (BCastType)(*(curr));
            curr += 2;
            std::string fileName(curr);
            return new BCastPacket(errorCode, fileName);
        }
        case OpCode::DISC: {
            return new DisconnectPacket();
        }
        case OpCode::INVALID:
        default:
            return nullptr;
        }


}
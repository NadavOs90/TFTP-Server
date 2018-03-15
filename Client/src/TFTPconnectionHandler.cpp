#include "../include/TFTPconnectionHandler.h"
#include <iostream>
#include "../include/utils.h"

TFTPconnectionHandler::TFTPconnectionHandler(const std::string& host, short port) : ConnectionHandler(host, port) {}

TFTPconnectionHandler::TFTPconnectionHandler(const TFTPconnectionHandler& other) : ConnectionHandler(other) {}

bool TFTPconnectionHandler::sendPacket(const Packet & pcktToSend)
{
	auto toSend = pcktToSend.serialize();
	auto res = sendBytes(toSend, pcktToSend.packetSize());
	delete toSend;
	return res;
}

bool TFTPconnectionHandler::readPacket(Packet ** toRead)
{
	if (nullptr == toRead) { return false; }
	*toRead = nullptr;
	try {
		char opcodeBytes[2] = { 0 };
		if (!getBytes(opcodeBytes, 2))
		{
			return false;
		}
		auto opcode = OpCode(bytesToShort(opcodeBytes));
		switch (opcode) {
			case OpCode::ACK: {
				char readBytes[2] = {0};
				if (!getBytes(readBytes,2))
				{
					return false;
				}
				auto ackNumber = (bytesToShort(readBytes));
				*toRead = new AckPacket(ackNumber);
				return true;
			}
			case OpCode::DATA: {
				char readBytes[2] = { 0 };
				if (!getBytes(readBytes,2))
				{
					return false;
				}
				auto packetSize = (bytesToShort(readBytes));
				if (!getBytes(readBytes,2))
				{
					return false;
				}
				auto blockNumber = (bytesToShort(readBytes));
				auto dataPkt = new char[packetSize];
				if (!getBytes(dataPkt, packetSize))
				{
					return false;
				}
				*toRead = new DataPacket(packetSize, blockNumber, dataPkt);
				delete[] dataPkt;
				return true;
			}
			case OpCode::ERRORmsg: {
				char readBytes[2] = { 0 };
				if (!getBytes(readBytes,2))
				{
					return false;
				}
				auto errorCode = (ErrorTypes)(bytesToShort(readBytes));
				std::string errorMessage;
				if (!getFrameAscii(errorMessage, '\0'))
				{
					return false;
				}
				*toRead = new ErrorPacket(errorCode, errorMessage);
				return true;
			}
			case OpCode::BCAST: {
				auto bCastType = BCastType::ADDED;
				if (!getBytes(reinterpret_cast<char*>(&bCastType), 1))
				{
					return false;
				}
				std::string fileName;
				if (!getFrameAscii(fileName, '\0'))
				{
					return false;
				}
				*toRead = new BCastPacket(bCastType, fileName);
				return true;
			}
			case OpCode::INVALID:
			default:
				break;
			}
	}
	catch (std::exception& e) {
		std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
		return false;
	}
	return false;
}

TFTPconnectionHandler::~TFTPconnectionHandler() {}

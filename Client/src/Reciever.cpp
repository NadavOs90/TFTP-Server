#include "../include/Reciever.h"
#include <iostream>
#include "../include/Packet.h"
#include "../include/utils.h"


Reciever::Reciever(const std::string& host, short port) : connection(host, port),
disconnecting(false), fileToHandle(), packetNum(0), ackCallback(nullptr),
finishedCallback(nullptr), buffer() {}

Reciever::Reciever(const Reciever& other) : connection(other.connection),
disconnecting(other.disconnecting), fileToHandle(other.fileToHandle),
packetNum(other.packetNum), ackCallback(other.ackCallback),
finishedCallback(other.finishedCallback), buffer(other.buffer) {}

Reciever::~Reciever() {}

bool Reciever::login(const std::string& username)
{
	LoginPacket loginRequest(username);
	if (!connection.sendPacket(loginRequest))
	{
		return false;
	}
	packetNum = 0;
	return true;
}

bool Reciever::DelFile(const std::string& fileName)
{
	DelPacket delRequest(fileName);
	if (!connection.sendPacket(delRequest))
	{
		return false;
	}	
	packetNum = 0;
	return true;
}

bool Reciever::disconnect()
{
	DisconnectPacket dis;
	if (!connection.sendPacket(dis))
	{
		return false;
	}
	packetNum = 0;
	disconnecting = true;
	return true;
}

bool Reciever::WriteFile(const std::string& fileName)
{
	fileToHandle = fileName;
	RWPacket WQR(OpCode::WRQ, fileName);
	ackCallback = [this]() {
		std::ifstream * fileInput = new std::ifstream(fileToHandle, std::ifstream::binary);
		if (!(*fileInput)) {
			delete fileInput;
		}
		fileInput->exceptions(std::ifstream::goodbit);

		ackCallback = [this,fileInput]() {
			this->sendNextDataPacket(fileInput);
		};
		sendNextDataPacket(fileInput);
	};

	packetNum = 0;
	if (!connection.sendPacket(WQR))
	{
		return false;
	}
	return true;
}

bool Reciever::ReadFile(const std::string& fileName)
{
	RWPacket RRQ(OpCode::RRQ, fileName);
	fileToHandle = fileName;
	finishedCallback = [this](std::vector<char> result)
	{
		std::ofstream fileWriter(this->fileToHandle, std::ofstream::out | std::ofstream::binary);
		fileWriter.write( result.data(),result.size());
		fileWriter.close();
		std::cout << "RRQ " << fileToHandle << " complete" << std::endl;
	};
	packetNum = 1;
	if (!connection.sendPacket(RRQ))
	{
		return false;
	}
	return true;
}

bool Reciever::DirList()
{
	DirqPacket dirReq;

	finishedCallback = [this](std::vector<char> result)
	{
		char * data = result.data();
		std::vector<std::string> fileStrings;
		rawSplit(data,result.size(), '\0', fileStrings);
		for (auto i : fileStrings) {
			std::cout << i << std::endl;
		}
	};
	packetNum = 1;
	if (!connection.sendPacket(dirReq))
	{
		return false;
	}
	return true;
}

void Reciever::handleDataPacket(DataPacket* pckt)
{
	AckPacket ackPckt(packetNum);

	buffer.insert(buffer.end(),
		pckt->data,
		pckt->data + pckt->pcktSize);
	if (512 > pckt->pcktSize) {
		finishedCallback(buffer);
		finishedCallback = nullptr;
		packetNum = 0; //reset
		buffer.clear();
	}
	else {
		packetNum++;
	}

	connection.sendPacket(ackPckt);
}

void Reciever::sendNextDataPacket(std::ifstream * fileInput)
{
	auto nextBlockNumber = ++packetNum;
	char dataRead[512];
	fileInput->read(dataRead, sizeof(dataRead));
	std::streamsize count = fileInput->gcount();
	DataPacket dataPckt((short)(count), nextBlockNumber, dataRead);
	if (count != 512)
	{
		ackCallback = [this]()
		{
			ackCallback = nullptr;
			packetNum = 0;
			std::cout << "WRQ " << fileToHandle << " complete" << std::endl;
		};
		fileInput->close();
		delete fileInput;
	}

	packetNum = nextBlockNumber;
	connection.sendPacket(dataPckt);
}

void Reciever::listen()
{
	while (true) {
		Packet * readPacket = nullptr;
		if (!connection.readPacket(&readPacket)) {
			return;
		}
		switch (readPacket->opcode) {
			case OpCode::ACK: {
				std::cout << (*readPacket) << " ";
				AckPacket * pckt = (AckPacket*)(readPacket);
				std::cout << pckt->blockNumber << std::endl;
				if (disconnecting) {
					if (packetNum == pckt->blockNumber) {
						return;
					}
				}
				if (ackCallback &&
					(packetNum == pckt->blockNumber)) {
					ackCallback();
				}
				break;
			}
			case OpCode::ERRORmsg: {
				std::cout << (*readPacket) << " ";
				ErrorPacket * pckt = (ErrorPacket*)(readPacket);
				std::cout  << pckt->errorNumber << std::endl;
				ackCallback = nullptr;
				finishedCallback = nullptr;
				packetNum = 0;
				break;
			}
			case OpCode::BCAST:
			{
				std::cout << (*readPacket) << " ";
				BCastPacket * pckt = (BCastPacket*)(readPacket);
				std::cout << pckt->messageType << " " << pckt->fileName << std::endl;
				break;
			}
			case OpCode::DATA:
			{
				DataPacket * pckt = (DataPacket*)(readPacket);
				if (packetNum == pckt->blockNumber)
				{
					handleDataPacket(pckt);
				}
				break;
			}
			default: {
					ErrorPacket err(ErrorTypes::IllegalTFTP, ErrorMessages[ErrorTypes::IllegalTFTP]);
					connection.sendPacket(err);
					break;
				}
		}
		delete readPacket;
	}

}

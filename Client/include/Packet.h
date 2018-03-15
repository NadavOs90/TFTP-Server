#ifndef PACKET_PARSER__
#define PACKET_PARSER__
#include <string>
#include <cstdint>
#include <ostream>
#include <map>

enum class OpCode : short
{
	RRQ=1, // Read Request 
	WRQ, //Write Request
	DATA, //Data
	ACK, //Acknowledgement
	ERRORmsg, //exception due to C++
	DIRQ, //Directory Listing Request 
	LOGRQ, // Login Request
	DELRQ, //Delete Request
	BCAST, //Broadcast
	DISC, //Disconnect
	INVALID
};

OpCode stringToCommand(const std::string&);

typedef std::map<OpCode, const std::string> pcktTypeToString_type;

static pcktTypeToString_type map_PacketTypeToString{
	{ OpCode::LOGRQ,"LOGRQ"},
	{ OpCode::DELRQ,"DELRQ"},
	{ OpCode::RRQ,"RRQ"},
	{ OpCode::WRQ,"WRQ"},
	{ OpCode::DIRQ,"DIRQ"},
	{ OpCode::ACK,"ACK"},
	{ OpCode::BCAST,"BCAST"},
	{ OpCode::ERRORmsg,"Error"},
	{ OpCode::DISC,"DISC"},
	{ OpCode::INVALID,"INVALID"},
};


class Packet
{
protected:

	size_t getOpcodeSize() const
	{
		return sizeof(opcode);
	}

	char * allocatePacket() const
	{
		return new char[packetSize()];
	}

	const char * serializePacket() const;

public:
	Packet();

	explicit Packet(OpCode opcode);

	virtual const char * serialize() const = 0;

	virtual size_t packetSize() const {
		return getOpcodeSize();
	}

	OpCode opcode;

	friend std::ostream& operator<<(std::ostream& os, const Packet& pckt) {
		os << map_PacketTypeToString[pckt.opcode];
		return os;
	}

	virtual ~Packet();
};

class DirqPacket : public Packet {
public:
	DirqPacket() : Packet(OpCode::DIRQ) {}

	const char * serialize() const override {
		return serializePacket();
	}


};

class DisconnectPacket : public Packet {
public:
	DisconnectPacket() : Packet(OpCode::DISC) {}
	const char * serialize() const override {
		return serializePacket();
	}
};

class StringPacket : public Packet {

protected:
	StringPacket(OpCode type, const std::string& str);
public:
	std::string content;
	const char * serialize() const override;

	virtual size_t packetSize() const override;
};

class LoginPacket : public StringPacket {
public:
	LoginPacket(const std::string& loginUsername);
};

class DelPacket : public StringPacket {

public:
	DelPacket(const std::string& filenameToDelete);
};

class RWPacket : public StringPacket {

public:
	RWPacket(OpCode type, const std::string& fileToMove);
};

class DataPacket : public Packet {
public:

	short pcktSize;
	short blockNumber;
	char* data;
	DataPacket(short size, short blckNumber, const void * dataPkt);

	DataPacket(const DataPacket& other);
	DataPacket& operator=(const DataPacket& other);

	const char * serialize() const override;

	virtual size_t packetSize() const override;

	virtual ~DataPacket();
};

class AckPacket : public Packet {
public:
	short blockNumber;

	explicit AckPacket(short block);

	const char * serialize() const override;

	virtual size_t packetSize() const override;
};

enum class ErrorTypes : short
{
	UnDef,
	FileNotFound,
	AccessViolation,
	DiskFull,
	IllegalTFTP,
	FileAlreadyExists,
	UserNotLoggedOn,
	UserAlreadyLoggedIn,
};

std::ostream& operator<<(std::ostream& os, ErrorTypes al);

static std::map<ErrorTypes, const std::string> ErrorMessages{
	{ ErrorTypes::UnDef,"Not defined, see error message (if any)."},
	{ ErrorTypes::FileNotFound,"File not found � RRQ of non-existing file" },
	{ ErrorTypes::AccessViolation,"Access violation � File cannot be written, read or deleted" },
	{ ErrorTypes::DiskFull,"Disk full or allocation exceeded � No room in disk." },
	{ ErrorTypes::IllegalTFTP,"Illegal TFTP operation � Unknown Opcode." },
	{ ErrorTypes::FileAlreadyExists,"File already exists � File name exists on WRQ." },
	{ ErrorTypes::UserNotLoggedOn,"User not logged in � Any opcode received before Login completes." },
	{ ErrorTypes::UserAlreadyLoggedIn,"User already logged in � Login username already connected." }
};

class ErrorPacket : public Packet {
public:
	ErrorTypes errorNumber;
	std::string errorMessage;

	ErrorPacket(ErrorTypes type, const std::string& message);

	virtual const char * serialize() const override;

	virtual size_t packetSize() const override;
};

enum class BCastType : char {
	DELETED,
	ADDED
};

std::ostream& operator<<(std::ostream& os, BCastType al);


class BCastPacket : public Packet {
public:
	BCastType messageType;
	std::string fileName;
	BCastPacket(BCastType messageType, const std::string& fileChanged);

	virtual const char * serialize() const override;

	virtual size_t packetSize() const override;
};

Packet * deserializePacket(const char * buff);

#endif

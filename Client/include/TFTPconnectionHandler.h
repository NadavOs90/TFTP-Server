#ifndef __TFTP_CONNECTION_HANDLER__
#define __TFTP_CONNECTION_HANDLER__
#include <string>
#include "connectionHandler.h"
#include "Packet.h"

class TFTPconnectionHandler : public ConnectionHandler
{
public:
	TFTPconnectionHandler(const std::string& host, short port);

	TFTPconnectionHandler(const TFTPconnectionHandler& other);

	bool sendPacket(const Packet& toSend);

	bool readPacket(Packet** toRead);

	virtual ~TFTPconnectionHandler();
};
#endif

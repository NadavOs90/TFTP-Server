#ifndef  __TFTPManager__
#define __TFTPManager__
#include <string>
#include <vector>
#include <fstream>
#include <functional>
#include "TFTPconnectionHandler.h"

class Reciever
{
	TFTPconnectionHandler connection;

	/**
	* \brief Signal for listener thread if after reciving an ACK0 it should disconnect.
	*/
	bool disconnecting;

	std::string fileToHandle; //garunteed only one is valid at a time.
	short packetNum = 0;

	//handling read requests using ACK packets
	std::function <void()> ackCallback;
	void sendNextDataPacket(std::ifstream * fileInput);

	//Handling RRQ and DIRLIST packets
	std::function <void(std::vector<char>)> finishedCallback;
	std::vector<char> buffer;
	/**
	 * \brief Function that accumulates the data.
	 *			If we finished, we call the callback
	 * \param pckt
	 */
	void handleDataPacket(DataPacket * pckt);

public:
	Reciever(const std::string& host, short port);
	Reciever(const Reciever& other); //copy ctor


	/*
		Connects.
	*/
	bool connect() { return connection.connect(); }

	/**
	 * \brief MUST EXPLICTLY BE CALLED, WILL NOT CLOSE SOCKET OTHERWISE
	 */
	void close() { connection.close(); }

	/**
	 * \brief Attempts a login with given username
	 * \param username
	 * \return TFTP error code
	 */
	bool login(const std::string& username);

	/**
	* \brief Disconnects from the server
	* \return TFTP error code
	*/
	bool disconnect();

	/**
	 * \brief Attempts to send a file
	 * \param fileName - fileName to send
	 * \return TFTP error code
	 */
	bool WriteFile(const std::string& fileName);

	/**
	* \brief Attempts to read a file
	* \param fileName - fileName to send
	* \return TFTP error code
	*/
	bool ReadFile(const std::string& fileName);


	/**
	 * \brief Requests a dirlist from server
	 * \return TFTP error code
	 */
	bool DirList();

	/**
	* \brief Attempts to delete a file from server
	* \param fileName - fileName to delete
	* \return TFTP error code
	*/
	bool DelFile(const std::string& fileName);

	void operator()() {
		listen();
	}

	void listen();


	virtual ~Reciever();
};

#endif // ! __TFTPManager__

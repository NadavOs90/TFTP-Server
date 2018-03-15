#include <iostream>
#include <vector>
#include <boost/thread.hpp>
#include "../include/utils.h"
#include "../include/Packet.h"
#include "../include/Reciever.h"

bool shouldTerminate = false;

void msgSend(Reciever *receiver){
	while (!shouldTerminate) {
		std::string line;
		std::getline(std::cin, line);
		line = trim(line);
		if (0 == line.length()) { continue; }

		std::vector<std::string> parsedStrings;
		stringSplit(line, ' ', parsedStrings);
		if (0 == parsedStrings.size()) { continue; }

		auto command = parsedStrings.at(0); //get the command
		auto commandType = stringToCommand(command);
		auto res = false;
		switch (commandType)
		{
			case OpCode::DISC:
			{
				res = receiver->disconnect();
				shouldTerminate = true;
				break;

			}
			case OpCode::LOGRQ:
			{
				if (2 != parsedStrings.size())
				{
					continue;
				}
				auto userName = parsedStrings.at(1);
				res = receiver->login(userName);
				break;

			}
			case OpCode::DELRQ: {
				if (2 != parsedStrings.size())
				{
					//bad input, wtf?
					continue;
				}
				auto fileName = parsedStrings.at(1);
				res = receiver->DelFile(fileName);
				break;

			}

			case OpCode::RRQ: {
				if (2 != parsedStrings.size())
				{
					continue;
				}
				auto fileName = parsedStrings.at(1);
				res = receiver->ReadFile(fileName);
				break;

			}
			case OpCode::WRQ: {
				if (2 != parsedStrings.size())
				{
					continue;
				}
				auto fileName = parsedStrings.at(1);
				res = receiver->WriteFile(fileName);
				break;
			}
			case OpCode::DIRQ: {
				res = receiver->DirList();
				break;
			}
                        default:
                            break;
		}
		if(res){
                    //some fix
                }
	}
}

int main(int argc, char *argv[]) {
	if (argc < 3) {
		std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
		return -1;
	}
	std::string host = argv[1];
	short port = (short)(atoi(argv[2]));

	Reciever receiver(host, port);
	if (!receiver.connect()) {
		std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
		return 1;
	}

	boost::thread reciever(std::ref(receiver));
	boost::thread sender(msgSend,&receiver);
	sender.join();
	reciever.join();

	receiver.close();

	return 0;
}

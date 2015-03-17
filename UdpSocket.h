/*
 * UdpSocket.h
 *
 *  Created on: 12 Dec 2014
 *      Author: Asier
 */

#ifndef UDPSOCKET_H_
#define UDPSOCKET_H_

	class Address {
public:
	Address();
	Address(unsigned char a, unsigned char b, unsigned char c, unsigned char d,
			unsigned short port);
	Address(unsigned int address, unsigned short port);

	unsigned int GetAddress() const;
	unsigned char GetA() const;
	unsigned char GetB() const;
	unsigned char GetC() const;
	unsigned char GetD() const;
	unsigned short GetPort() const;

	bool operator ==(const Address & other) const;
	bool operator !=(const Address & other) const;

private:
	unsigned int address;
	unsigned short port;
};


class Socket {

public:
	Socket();
	~Socket();

	static bool InitializeSockets();
	static void ShutdownSockets();
	static void wait(float seconds);

	bool Open(unsigned short port);
	void Close();
	bool IsOpen() const;
	bool Send(const Address & destination, const void * data, int size);
	int Receive(Address & sender, void * data, int size);
private:
	int socket;
};

#endif /* UDPSOCKET_H_ */

package org.artiom.client;

import org.artiom.net.Packet;
import org.artiom.net.Socket;

import java.io.IOException;
import java.net.SocketException;

public class Client extends Socket {

	public Client(int clPacketSize, int svPacketSize) throws SocketException {
		super(clPacketSize, svPacketSize);
	}

	public Client(int clPacketSize, int svPacketSize, int port) throws IOException {
		super(clPacketSize, svPacketSize, port);
	}

	@Override
	protected void onReceive(Packet p) {

	}
}

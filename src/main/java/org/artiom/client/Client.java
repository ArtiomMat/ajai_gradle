package org.artiom.client;

import org.artiom.net.Packet;
import org.artiom.net.Socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

public class Client extends Socket {
	/**
	 * Creates a socket, does not bind it though, to bind it call {@link Socket#bind()}
	 *
	 * @param port The port used.
	 * @throws IOException The socket failed to be created.
	 */
	public Client(int port) throws IOException {
		super(port);
	}

	@Override
	protected void onReceive(InetAddress from) {
		System.out.println("RECEIVED: "+inPacketBB.get());
	}

	@Override
	protected void onReceiveException(Exception e) {

	}

	@Override
	protected void onProtocolMismatch(InetAddress address) {

	}
}

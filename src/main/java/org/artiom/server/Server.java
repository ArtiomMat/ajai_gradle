package org.artiom.server;

import org.artiom.net.Packet;
import org.artiom.net.Socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

public class Server extends Socket {

	public Server(int i) throws IOException {
		super(i);
	}

	@Override
	protected void onReceive(InetAddress from) {

	}

	@Override
	protected void onReceiveException(Exception e) {

	}

	public static void main(String[] args) {
		try {
			Thread serverThread = new Thread(new Server(2132));
			serverThread.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

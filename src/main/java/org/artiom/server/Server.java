package org.artiom.server;

import org.artiom.net.Socket;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class Server extends Socket {

	public Server(int i) throws IOException {
		super(i);

		bind();

		File svDir = new File("./server/");
		if (!svDir.exists()) {
			System.out.println("No server directory present, create one?(Y/N)");
			// TODO
			if (!svDir.mkdirs())
				throw new IOException("Failed to create server directory.");
			System.out.println("Done.");
		}
	}

	@Override
	protected void onReceive(InetAddress from) {

	}

	@Override
	protected void onReceiveException(Exception e) {

	}

	@Override
	protected void onLinkStatusChange(boolean mitm) {

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

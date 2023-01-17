package org.artiom.server;

import org.artiom.client.Client;
import org.artiom.net.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

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
		try {
			write(70);
			send(from);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void onReceiveException(Exception e) {

	}

	@Override
	protected void onProtocolMismatch(InetAddress address) {

	}

	public static void main(String[] args) throws IOException {
		System.out.println("MAIN");

		if (args.length > 0) {
			try {
				if (Objects.equals(args[0], "s")) {
					Server sv = new Server(6969);
					Thread serverThread = new Thread(sv);
					serverThread.start();

					URL whatismyip = new URL("http://checkip.amazonaws.com");
					BufferedReader in = new BufferedReader(new InputStreamReader(
							whatismyip.openStream()));

					String ip = in.readLine(); //you get the IP as a String
					System.out.println(ip);
				} else {
					Scanner s = new Scanner(System.in);
					String str = s.nextLine();
					Client c = new Client(6969);
					Thread clientThread = new Thread(c);
					clientThread.start();

					c.write(69);
					c.send(InetAddress.getByName(str));
					clientThread.join();
				}
			} catch (IOException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

package org.artiom.server;

import org.artiom.client.Client;
import org.artiom.net.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
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
		System.out.println("RECEIVED: "+inPacketBB.get()+"\nECHOING...");

	}

	@Override
	protected void onReceiveException(Exception e) {

	}

	@Override
	protected void onProtocolMismatch(InetAddress address) {

	}

	public static void main(String[] args) throws IOException {
		System.out.println("MAIN");
		Kernel k = new Kernel(new byte[]{1,0,-1, 2,0,-2, 1,0,-1}, 3, 3);
		Map m = new Map("0.png");
		Map conv = k.convolve(m, 1);
		ActivationFunction.relu(conv);
		conv.save("convolved.png");

		if (args.length > 0) {
			try {
				if (Objects.equals(args[0], "s")) {
					Server sv = new Server(Constants.PORT);
					Thread serverThread = new Thread(sv);
					serverThread.start();

					System.out.println(InetAddress.getLocalHost().getHostAddress());
				} else {
					Scanner s = new Scanner(System.in);
					String str = s.nextLine();
					Client c = new Client(Constants.PORT);
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

//package org.artiom.server;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.*;
//
//public class Server2 implements Runnable {
//	public static final int RECEIVE_PACKET_SIZE_MAX = 4096;
//	public static final int SEND_PACKET_SIZE_MAX = 4096;
//
//	private int port;
//	private DatagramSocket socket;
//	private boolean alive = true;
//	DatagramPacket snd = new DatagramPacket(new byte[SEND_PACKET_SIZE_MAX], SEND_PACKET_SIZE_MAX);
//	DatagramPacket rcv = new DatagramPacket(new byte[RECEIVE_PACKET_SIZE_MAX], RECEIVE_PACKET_SIZE_MAX);
//
//	public Server2(int port) throws IOException {
//		// Apparently it is bound automatically lol.
//		socket = new DatagramSocket(port);
//
//		File svDir = new File("./server/");
//		if (!svDir.exists()){
//			System.out.println("No server directory present, create one?(Y/N)");
//			// TODO
//			svDir.mkdirs();
//			System.out.println("Done.");
//		}
//	}
//
//
////	@Override
////	public void run() {
//////		ClientPacket clPacket = new ClientPacket(rcv);
////		while (alive) {
////			try {
////				System.out.println("Waiting for packets...");
////				socket.receive(rcv);
////				InetAddress addr = rcv.getAddress();
////				System.out.println("Received from:\n" +
////								addr.getHostName() +
////								"\n" +
////								addr.getHostAddress());
////			} catch (IOException e) {
////				throw new RuntimeException(e);
////			}
////		}
////	}
//
//
//}

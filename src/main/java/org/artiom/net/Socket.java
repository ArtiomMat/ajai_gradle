package org.artiom.net;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The socket is not just another wrapper for UDP.
 * Security of packets is the #1 concern, especially when the class is used in apps like MeatMe.
 * <p>
 * It contains a rather basic, yet effective algorithm for encrypting packets to avoid being victim to MITM attacks.
 * Not only is it immune to MITM attacks, it recognizes them because it has a shared key with other sockets it
 * interacts with.
 * <p>
 * However, recognizing if the packet data is valid or if it's malicious is the responsibility of the inheriting
 * sockets, the class obviously won't check it for you.
 */
public abstract class Socket implements Runnable {
	private DatagramSocket socket;
	private boolean alive = true;
	private boolean bound = false;
	private int port;
	private DatagramPacket outPacket, inPacket;

	private byte[] privateKey;

	/** Byte buffer for its respective packet */
	protected ByteBuffer outPacketBB, inPacketBB;

	private DatagramPacket createPacket() {
		return new DatagramPacket(new byte[Constants.PACKET_SIZE_MAX], Constants.PACKET_SIZE_MAX);
	}

	private ByteBuffer createBB(DatagramPacket p) {
		return ByteBuffer.wrap(p.getData()).order(ByteOrder.nativeOrder());
	}

	/**
	 * Creates a socket, does not bind it though, to bind it call {@link Socket#bind()}
	 * @param port The port used.
	 * @throws IOException The socket failed to be created.
	 */
	public Socket(int port) throws IOException {
		socket = new DatagramSocket();

		privateKey = new byte[Constants.PRIVATE_KEY_BYTES_NUM];
		ThreadLocalRandom.current().nextBytes(privateKey);

		inPacket = createPacket();
		outPacket = createPacket();

		outPacketBB = createBB(outPacket);
		inPacketBB = createBB(inPacket);

		this.port = port;
	}

	/**
	 * Binds the socket to the port given in the constructor.
	 * @throws IOException
	 */
	public void bind() throws IOException {
		socket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port));

		File svDir = new File("./server/");
		if (!svDir.exists()) {
			System.out.println("No server directory present, create one?(Y/N)");
			// TODO
			svDir.mkdirs();
			System.out.println("Done.");
		}
	}

	public void kill() {
		alive = false;
	}

	public boolean canWrite(int size) {
		if (outPacketBB.position() + size >= Constants.PACKET_SIZE_MAX)
			return false;
		return true;
	}

	/**
	 * Prepares the out packet for writing. Crucial!
	 */
	public void startWrite() {
		outPacketBB.clear();
		outPacketBB.putInt(Constants.PROTOCOL_VERSION);
	}

	protected void write(int fragmentType) throws IndexOutOfBoundsException {
		outPacketBB.put((byte) fragmentType);
	}

	/**
	 *
	 * @param to
	 * @throws IOException
	 */
	public void send(InetAddress to) throws IOException {
		// Encrypting the data
		byte[] data = outPacketBB.array();
		int length = outPacketBB.position();
		for (int i = 0; i < length; i++) {
			data[i] +=
		}

		outPacket.setAddress(to);
		socket.send(outPacket);
	}

	/**
	 * Crucial to make sure if the packet is ok.
	 * @return If the protocol version of the in packet matches ours.
	 */
	public boolean startRead() {
		return inPacketBB.getInt() == Constants.PROTOCOL_VERSION;
	}

	protected abstract void onReceive(InetAddress from);
	protected abstract void onReceiveException(Exception e);

	@Override
	public void run() {
		while (alive) {
			try {
				socket.receive(inPacket);
				onReceive(inPacket.getAddress());
			} catch (IOException e) {
				onReceiveException(e);
			}
		}
	}

}

package org.artiom.net;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The socket is not just another wrapper for UDP.
 * Security of packets is the #1 concern, especially when the class is used in apps like MeatMe.
 * <p>
 * It contains a rather basic, yet effective algorithm for encrypting packets to avoid being victim to MITM attacks.
 * Not only is it immune to MITM attacks, it recognizes them because it has a shared key with other sockets it
 * interacts with.
 * <p>
 * Though they have to be eliminated, MITM attacks are rare. The real horror are malicious sockets, that
 * are custom written or reverse engineered to exploit the protocol, Socket is virtually un-exploitable(according to my
 * tests of course), now the inheriting classes, they must watch out when building their protocol.
 */
public abstract class Socket implements Runnable {
	private final DatagramSocket socket;
	private boolean alive = true;
	private final int port;
	private final DatagramPacket outPacket;
	private final DatagramPacket inPacket;

	private final byte[] key;

	/** links that have a 0 in them are actually blocked addresses */
	private final HashMap<InetAddress, Integer> links;

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

		// Initialize the private key
		key = new byte[Constants.PRIVATE_KEY_BYTES_NUM];
		ThreadLocalRandom.current().nextBytes(key);

		links = new HashMap<>();

		inPacket = createPacket();
		outPacket = createPacket();

		outPacketBB = createBB(outPacket);
		inPacketBB = createBB(inPacket);

		this.port = port;

		startWrite();
	}

	/**
	 * Binds the socket to the port given in the constructor.
	 */
	public void bind() throws IOException {
		socket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port));
	}

	public void kill() {
		alive = false;
	}

	public void block(InetAddress address) {
		links.put(address, 0);
	}

	public boolean canWrite(int size) {
		if (outPacketBB.position() + size >= Constants.PACKET_SIZE_MAX)
			return false;
		return true;
	}

	/**
	 * Prepares the out packet for writing. Crucial!
	 */
	private void startWrite() {
		outPacketBB.clear();
		outPacketBB.putInt(Constants.PROTOCOL_VERSION);
	}

	protected void write(int fragmentType) throws IndexOutOfBoundsException {
		outPacketBB.put((byte) fragmentType);
	}

	/**
	 * If link isn't established .
	 * @param to
	 */
	public void send(InetAddress to) throws IOException {
		// Encrypting the data
		byte[] data = outPacketBB.array();
		int length = outPacketBB.position();
		for (int i = 0; i < length; i++) {
//			data[i] +=
		}

		outPacket.setAddress(to);
		socket.send(outPacket);
		startWrite();
	}

	protected abstract void onReceive(InetAddress from);
	protected abstract void onReceiveException(Exception e);
	protected abstract void onLinkStatusChange(boolean mitm);

	@Override
	public void run() {
		while (alive) {
			try {
				socket.receive(inPacket);

				// Parse the packet real quick
				InetAddress address = inPacket.getAddress();

				// Check if we already have an existing connection
				if (links.get(address) != null) {
					int keyToCheck = links.get(address);

				}
				else {
					// Gotta check if there
				}

//				inPacketBB.getInt() == Constants.PROTOCOL_VERSION

				onReceive(inPacket.getAddress());
			} catch (IOException e) {
				onReceiveException(e);
			}
		}
	}

}

package org.artiom.net;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
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
	private static class LinkStatus {
		/** Is this the first time we come in contact with the address?
		 * For cases where we send first to the link it's to check if it's responsive, and make sure it accepted us.
		 * For case where the link sends to us it's to know if we gotta initialize it by the time we decrypt the packet
		 * */
		public boolean firstTime=true;
		/** If we sent a packet and awaiting the next stage of sending the packet, decrypted by us */
		public boolean sent=false;
		/** If we are in process of decrypting the packet sent to us by the other party */
		public boolean decrypting=false;
		/** Private key we share with the party to make sure mitm attacks don't happen
		 * TODO: Find out how to actually use it, MITM can just skip over the key if it's place in the start of a packet or modify the key in the first instance */
		public int key;

		public LinkStatus(int key) {
			this.key = key;
		}

		public LinkStatus() {}
	}

	private final DatagramSocket socket;
	private boolean alive = true;
	private final int port;
	private final DatagramPacket outPacket;
	private final DatagramPacket inPacket;

	private final byte[] key;

	/** links that have a 0 in them are actually blocked addresses */
	private final HashMap<InetAddress, LinkStatus> links;

	/** Byte buffer for its respective packet */
	protected ByteBuffer outPacketBB, inPacketBB;


	private DatagramPacket createPacket() {
		DatagramPacket p = new DatagramPacket(new byte[Constants.PACKET_SIZE_MAX], Constants.PACKET_SIZE_MAX);
		p.setPort(port);
		return p;
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
		this.port = port;

		socket = new DatagramSocket(null);

		// Initialize the private key
		key = new byte[Constants.PRIVATE_KEY_BYTES_NUM];
		ThreadLocalRandom.current().nextBytes(key);

		links = new HashMap<>();

		inPacket = createPacket();
		outPacket = createPacket();

		outPacketBB = createBB(outPacket);
		inPacketBB = createBB(inPacket);

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

	public void write(int fragmentType) throws IndexOutOfBoundsException {
		outPacketBB.put((byte) fragmentType);
	}

	public byte read() throws IndexOutOfBoundsException {
		return outPacketBB.get();
	}


	/**
	 *
	 * @param p The packet we apply the key on
	 * @param factor -1 or 1 purely, the factor used on the value.
	 */
	private void applyKey(DatagramPacket p, int factor) {
		int length = p.getLength();
		byte[] data = p.getData();
		System.out.println("APPLYING KEY("+key[0]*factor+") ON("+length+"): "+data[0]);
		for (int i = 0, j = 0; i < length; i++) {
			if (j >= Constants.PRIVATE_KEY_BYTES_NUM)
				j = 0;
			data[i] += key[j++]*factor;
//			data[i] += key[0]*factor;
		}
		System.out.println("APPLIED: "+data[0]);
	}

	/**
	 * @return if we are still in process of decrypting a packet we can't send it to that address yet.
	 * @param to
	 */
	public boolean send(InetAddress to) throws IOException {
		LinkStatus linkStatus = links.get(to);
		if (linkStatus != null) {
			if (linkStatus.decrypting)
				return false;
		}
		// Doesn't exist? this is the first time then.
		else {
			linkStatus = new LinkStatus(ThreadLocalRandom.current().nextInt());
			links.put(to, linkStatus);
		}


		System.out.println("STAGE 1 SEND");
		outPacket.setLength(outPacketBB.position());
		// First step for us, send the encrypted packet.
		outPacket.setAddress(to);
		applyKey(outPacket, 1);
		socket.send(outPacket);
		linkStatus.sent = true;

		startWrite();
		return true;
	}

	public void setTimeout(int timeoutMillis) throws SocketException {
		socket.setSoTimeout(timeoutMillis);
	}

	protected abstract void onReceive(InetAddress from);
	protected abstract void onReceiveException(Exception e);
	protected abstract void onProtocolMismatch(InetAddress from);

	@Override
	public void run() {
		while (alive) {
			try {
				socket.receive(inPacket);

				// Parse the packet real quick
				InetAddress address = inPacket.getAddress();

				// Check if we already have an existing link
				if (links.get(address) != null) {
					System.out.println("EXISTS");
					LinkStatus linkStatus = links.get(address);

					// Second and last step for us, send the decrypted packet(decrypted from our key).
					if (linkStatus.sent) {
						System.out.println("STAGE 2 SEND");
						applyKey(inPacket, -1);
						socket.send(inPacket);
						linkStatus.sent = false;
					}
					// This means this is the second and last step to receive our packet from the other mf.
					else if (linkStatus.decrypting) {
						System.out.println("STAGE 2 RECEIVE");
						applyKey(inPacket, -1);
						// For the first time there is the packet header.
						if (linkStatus.firstTime) {
							int version = inPacketBB.getInt();
							if (version != Constants.PROTOCOL_VERSION) {
								onProtocolMismatch(address);
							}
							linkStatus.firstTime = false;
						}
						linkStatus.decrypting = false;
						onReceive(inPacket.getAddress());
					}
					// This means this is the first step to receive our packet, encrypt it and send it back.
					else {
						System.out.println("STAGE 1 RECEIVE");
						applyKey(inPacket, 1);
						socket.send(inPacket);
						linkStatus.decrypting = true;
					}

				}
				// If there is no existing link let's create it, it's the first time.
				else {
					LinkStatus linkStatus = new LinkStatus();
					links.put(address, linkStatus);

					// FIXME Duplicated code!!!!! from the else aboveeee
					// This means this is the first step to receive our packet, encrypt it and send it back.
					System.out.println("STAGE 1 RECEIVE");
					applyKey(inPacket, 1);
					socket.send(inPacket);
					linkStatus.decrypting = true;
				}

			} catch (IOException e) {
				System.out.println("EXCEPTION");
				onReceiveException(e);
			}
		}
	}

}

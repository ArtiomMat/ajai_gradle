package org.artiom.net;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Packet {
	private DatagramPacket dgPacket;
	protected final byte[] dataRaw;
	protected final ByteBuffer dataBB;
	protected final int dataLength;

	public DatagramPacket getDgPacket() {
		return dgPacket;
	}

	/**
	 * Used to set the address of the wrapped datagram packet, should be used for sending.
	 */
	void setAddress(InetAddress address) {
		dgPacket.setAddress(address);
	}

	InetAddress getAddress() {
		return dgPacket.getAddress();
	}

	public Packet(DatagramPacket dgPacket) {
		this.dgPacket = dgPacket;

		this.dataRaw = dgPacket.getData();
		this.dataLength = dgPacket.getLength();

		// Default ByteBuffer order is big endian, like the UDP protocol. so all we got to do is .order(native_endian)
		dataBB = ByteBuffer.wrap(dataRaw).order(ByteOrder.nativeOrder());
	}

	public boolean canWrite(int size) {
		if (dataBB.position() + size >= dataLength)
			return false;
		return true;
	}

	public Packet(int sizeMax) {
		this(new DatagramPacket(new byte[sizeMax], sizeMax));
	}

	protected int read() {
		return dataBB.get();
	}

	public void clear() {
		dataBB.clear();
	}

	/**
	 * Calls clear() aswell.
	 */
	public void startWrite() {
		clear();
		dataBB.putInt(Constants.PROTOCOL_VERSION);
	}

	/**
	 *
	 * @return If the protocol version is the same for the sender and us.
	 */
	public boolean startRead() {
		return dataBB.getInt() == Constants.PROTOCOL_VERSION;
	}

	/**
	 *
	 * @param fragmentType Actually written as byte into the buffer, not int, to save space.
	 * @return If enough space to write a byte true, otherwise false.
	 * @throws IndexOutOfBoundsException
	 */
	protected void write(int fragmentType) throws IndexOutOfBoundsException {
		dataBB.put((byte) fragmentType);
	}
}

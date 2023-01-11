package org.artiom.client;

import org.artiom.net.Map;
import org.artiom.net.Packet;
import org.artiom.server.Server2;

import java.security.InvalidParameterException;

public class ClientPacket extends Packet {

	public static final int FRAG_NULL = 0;
	/** @see ClientPacket.FragVerify */
	public static final int FRAG_VERIFY = 1;

	public ClientPacket(int dgPacket) {
		super(dgPacket);
	}

	public static class FragVerify {
		public static final int ID_NULL = Integer.MIN_VALUE;
		private int id;
		public Map faceMap;

		/**
		 *
		 * @param faceMap A 1:1 greyscale(Map.L8) map of the face. Default size is 64x64 pixels, from Server.
		 * @param id The ID is given to existing users, by adding it, it is implied the user exists, ID_NULL for nothing.
		 */
		public FragVerify(Map faceMap, int id) throws InvalidParameterException {
			this.id = id;
			if (	faceMap.getWidth() == faceMap.getHeight() &&
					faceMap.getWidth() == Server2.FACE_MAP_DIMENTION_SIZE &&
					faceMap.getPixelFormat() == Map.L8
			)
				this.faceMap = faceMap;
			else
				throw new InvalidParameterException("Invalid map.");
		}

		/**
		 * You imply that you don't exist in the database, incorrect usage will lead to ban, a client isn't expected
		 * to think it exists in the database unless it clearly was notified of it.
		 * @see ClientPacket2.FragVerify#FragVerify(Map,int)
		 */
		public FragVerify(Map faceMap) throws InvalidParameterException {
			this(faceMap, ID_NULL);
		}


		public FragVerify() {
			faceMap = null;
			id = ID_NULL;
		}
	}

	public boolean write(FragVerify f) {
		write(FRAG_VERIFY);
		// id
		dataBB.putInt(f.id);
		// faceMap
		dataBB.putInt(f.faceMap.getWidth());
		dataBB.putInt(f.faceMap.getHeight());
		dataBB.putInt(f.faceMap.getPixelFormat());
		dataBB.put(f.faceMap.getPixels());
		return true; // TODO
	}

	public boolean read(FragVerify f) {
		f.id = dataBB.getInt();

		f.faceMap = new Map(dataBB.getInt(), dataBB.getInt(), dataBB.getInt());
		for (int i = 0; i < f.faceMap.getPixels().length; i++)
			f.faceMap.getPixels()[i] = dataBB.get();

		return true;
	}
}

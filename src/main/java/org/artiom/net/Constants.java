package org.artiom.net;

public interface Constants {
	int PROTOCOL_VERSION = 1;

	int PORT = 6969;

	// This must never change.
	int PRIVATE_KEY_BYTES_NUM = 4;

	int PACKET_SIZE_MAX = 4096;

	int FACE_MAP_DIMENSION_SIZE = 64;
}

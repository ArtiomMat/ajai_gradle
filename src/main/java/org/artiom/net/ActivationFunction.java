package org.artiom.net;

public class ActivationFunction {
	public static final int NONE = 0;
	public static final int RELU = 1;
	public static final int LEAKY_RELU = 2;
	public static final int ELU = 3;
	/**
	 * Should be used for the output neurons, as it squishes the values given to it to be from 0 to 1
	 */
	public static final int SIGMOID = 5;
	/**
	 * Should be used for the output neurons, as it squishes the values given to it to be from -1 to 1
	 */
	public static final int TANH = 4;

//	public static void relu(Map m) {
//		byte[] pixels = m.getPixels();
//		for (int i = 0; i < pixels.length; i++)
//			if ((int)(pixels[i])& < 0)
//				pixels[i] = 0;
//	}
}

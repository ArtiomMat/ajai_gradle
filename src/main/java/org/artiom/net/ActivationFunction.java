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

	public static float relu(float f) {
		if (f < 0)
			return 0f;
		return f;
	}

	public static void relu(Float[] f) {
		for (int i = 0; i < f.length; i++)
			f[i] = relu(f[i]);
	}

	public static void relu(Map m) {
		relu(m.getPixels());
	}

	public static float leakyRelu(float f) {
		if (f < 0)
			return f*0.0001f;
		return f;
	}

	public static void leakyRelu(Float[] f) {
		for (int i = 0; i < f.length; i++)
			f[i] = leakyRelu(f[i]);
	}

	public static void leakyRelu(Map m) {
		leakyRelu(m.getPixels());
	}

	public static float elu(float f) {
		// Alpha is the factor, and it's 1.0f by default, so nothing going on here except e^f-1
		return (float) Math.expm1(f);
	}

	public static void elu(Float[] f) {
		for (int i = 0; i < f.length; i++)
			f[i] = elu(f[i]);
	}

	public static void elu(Map m) {
		elu(m.getPixels());
	}

	public static float sigmoid(float f) {
		return (float) (1/(1+Math.exp(-f)));
	}

	public static void sigmoid(Float[] f) {
		for (int i = 0; i < f.length; i++)
			f[i] = sigmoid(f[i]);
	}

	public static void sigmoid(Map m) {
		sigmoid(m.getPixels());
	}

	public static float tanh(float f) {
		return (float) Math.tanh(f);
	}

	public static void tanh(Float[] f) {
		for (int i = 0; i < f.length; i++)
			f[i] = tanh(f[i]);
	}

	public static void tanh(Map m) {
		tanh(m.getPixels());
	}
}

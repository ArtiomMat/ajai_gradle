package org.artiom.net;

public class ActivationFunction {

	public static float auto(float f, LayerType type) {
		return switch (type) {
			case SIGMOID -> sigmoid(f);
			case TANH -> tanh(f);
			case LEAKY_RELU -> leakyRelu(f);
			case ELU -> elu(f);
			default -> relu(f);
		};
	}
	public static void auto(Float[] f, LayerType type) {
		for (int i = 0; i < f.length; i++)
			f[i] = auto(f[i], type);
	}
	public static void auto(Map m, LayerType type) {
		auto(m.getPixels(), type);
	}

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

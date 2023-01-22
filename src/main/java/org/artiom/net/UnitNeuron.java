package org.artiom.net;

import java.util.concurrent.ThreadLocalRandom;

class UnitNeuron extends Unit<Float, Float> {
	protected float[] weights;
	protected float bias;

	protected UnitNeuron(Float[] inputs) {
		super(inputs);

		bias = ThreadLocalRandom.current().nextFloat();

		weights = new float[inputs.length];
		for (int i = 0; i < inputs.length; i++)
			weights[i] = ThreadLocalRandom.current().nextFloat();
	}

	@Override
	protected Float activate() {
		output = bias;
		for (int i = 0; i < inputs.length; i++)
			output += inputs[i] * weights[i];
		return output;
	}
}

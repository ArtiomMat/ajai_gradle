package org.artiom.net;

import java.util.concurrent.ThreadLocalRandom;

class NeuronUnit {
	protected Float[] inputs;
	protected float[] weights;
	protected float bias;

	protected Float output;

	protected NeuronUnit(Float[] inputs) {
		this.inputs = inputs;

		bias = ThreadLocalRandom.current().nextFloat();

		weights = new float[inputs.length];
		for (int i = 0; i < inputs.length; i++)
			weights[i] = ThreadLocalRandom.current().nextFloat();
	}
}

package org.artiom.net;

import java.io.IOException;

class NeuronLayer {
	protected Neuron[] neurons;
	protected Float[] outputs;

	protected NeuronLayer(NeuronLayer prev, int neuronsNum) {
		outputs = new Float[neuronsNum];
		neurons = new Neuron[neuronsNum];

		for (int i = 0; i < neuronsNum; i++) {
			outputs[i] = 0f;
			neurons[i] = new Neuron(prev.outputs);
		}
	}
}

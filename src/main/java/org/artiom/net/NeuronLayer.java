package org.artiom.net;

class NeuronLayer {
	protected NeuronUnit[] neurons;
	protected Float[] outputs;

	protected boolean

	protected NeuronLayer(NeuronLayer prev, int neuronsNum) {
		outputs = new Float[neuronsNum];
		neurons = new NeuronUnit[neuronsNum];

		for (int i = 0; i < neuronsNum; i++) {
			outputs[i] = 0f;
			neurons[i] = new NeuronUnit(prev.outputs);
		}
	}
}

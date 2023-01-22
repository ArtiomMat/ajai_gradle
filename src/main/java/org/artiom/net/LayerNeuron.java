package org.artiom.net;

class LayerNeuron extends Layer<Float> {

	protected LayerNeuron(Float[] inputs, LayerType[] extraLayers, int unitsNum) {
		super(inputs, extraLayers, unitsNum);

		units = new UnitNeuron[unitsNum];
		outputs = new Float[unitsNum];

		for (int i = 0; i < unitsNum; i++) {
			outputs[i] = 0f;
			units[i] = new UnitNeuron(inputs);
		}
	}

	@Override
	protected Float[] activate() {
		for (Unit<Float> unit : units)
			unit.activate();
		for (LayerType extraLayer : extraLayers)
			ActivationFunction.auto(outputs, extraLayer);

		return outputs;
	}

	@Override
	protected Float[] getOutputsAsFloats() {
		return outputs;
	}

}

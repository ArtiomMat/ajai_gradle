package org.artiom.net;

public class LayerKernel extends Layer<Map>{
	protected LayerKernel(Map[] inputs, LayerType[] extraLayers, int unitsNum) {
		super(inputs, extraLayers, unitsNum);
	}

	@Override
	protected Map[] activate() {
		return new Map[0];
	}

	@Override
	protected Float[] getOutputsAsFloats() {
		return new Float[0];
	}
}

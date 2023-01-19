package org.artiom.net;

import java.io.IOException;

public class NeuronBrain {
	private boolean newInputs;
	private Float[] inputs = null;
	private float[] outputs = null;
	private NeuronLayer[] layers;

	protected void setInputs(Float[] inputs) {
		newInputs = true;
		this.inputs = inputs;
	}

	/** NOTE: Inputs are copied, since we use Float[] internally */
	public void setInputs(float[] inputs) {
		Float[] obj = new Float[inputs.length];
		for (int i = 0; i < inputs.length; i++)
			obj[i] = inputs[i];

		setInputs(obj);
	}

	public void setInputs (Map map) {
		// TODO: Possibly have to separate each channel of map, unsure, theoretically shouldn't make a difference network should adapt and order it's neurons accordingly.
		setInputs(map.getPixels());
	}

	public float[] getOutputs() {
		if (inputs == null)
			return null;

		if (newInputs) {
			// TODO
			return null;
		}

		return outputs;
	}

	public void save(String fp) throws IOException {

	}

	public void load(String fp) throws IOException {

	}
}

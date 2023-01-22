package org.artiom.net;

import java.util.ArrayList;

public class Brain<DataTypeIn, DataTypeOut> {
	private DataTypeIn[] inputs;
	private ArrayList<Layer<?>> layers;

	private Object[] getLastLayerOutputs() {
		if (layers.size() == 0)
			return inputs;
		return layers.get(layers.size()-1).outputs;
	}

	/**
	 *
	 * @param cfg Array of the layers in order. You must set the first layer type to a layer that can process
	 *            {@link DataTypeIn}, and the last layer type to {@link LayerTypeOut}
	 */
	public Brain(LayerType[] cfg) {
		layers = new ArrayList<>();

		LayerType type = cfg[0];
		int width, height, stride; // Width is used as size argument for neuron layers
		ArrayList<LayerType> extraLayers = new ArrayList<>();
		for (int i = 1; i < cfg.length; i++) {
			if (cfg[i] == LayerType.NEURONS || cfg[i] == LayerType.KERNELS) {
				// Creating the layer
				width = cfg[++i].ordinal();

				LayerType[] extraLayersArr = new LayerType[extraLayers.size()];
				extraLayers.toArray(extraLayersArr);

				Layer<?> layer;
				if (type == LayerType.NEURONS)
					layer = new LayerNeuron(
							(Float[]) getLastLayerOutputs(),
							extraLayersArr,
							width
					);
				else // supposed to be LayerType.KERNELS
					layer = null; // TODO

				// Adding the layer
				layers.add(layer);

				// Resetting and cleaning up stuff
				extraLayers.clear();
				type = cfg[i];
			}
			else
				extraLayers.add(cfg[i]);
		}
	}

	public void setInputs(DataTypeIn[] inputs) {
		this.inputs = inputs;
	}

	public DataTypeOut activate() {
		for (Layer<?> layer : layers)
			layer.activate();
		return (DataTypeOut) getLastLayerOutputs();
	}
}

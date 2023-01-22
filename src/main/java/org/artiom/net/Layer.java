package org.artiom.net;

public abstract class Layer<DataType> {
	protected Unit<DataType>[] units;
	protected DataType[] outputs;
	protected DataType[] outputsAsFloats;

	/** Dropout, Pools, etc. 0 terminated or 4 elements max */
	protected final LayerType[] extraLayers;

	protected Layer(DataType[] inputs, LayerType[] extraLayers, int unitsNum) {
		this.extraLayers = extraLayers.clone();
	}


	/** Activates the units, while also applying the extraLayers, and finally setting the outputsAsFloats  */
	protected abstract DataType[] activate();
	protected abstract Float[] getOutputsAsFloats();
}

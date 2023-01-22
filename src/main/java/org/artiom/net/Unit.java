package org.artiom.net;

abstract class Unit<DataType> {
	protected DataType[] inputs;
	protected DataType output;

	protected Unit(DataType[] inputs) {
		this.inputs = inputs;
	}

	protected DataType getOutput() {
		return output;
	}

	protected abstract DataType activate();
}

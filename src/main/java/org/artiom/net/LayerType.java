package org.artiom.net;

public enum LayerType {
	/**
	 * Hyperparameters: width, height, stride, number of kernels
	 * */
	KERNELS,
	/**
	 * Hyperparameters: number of neurons
	 * */
	NEURONS,

	DROPOUT,

	/**
	 * Exclusive to kernel brains.
	 * <p>
	 * Hyperparameters: width AND height, stride
	 */
	MAX_POOL,
	MIN_POOL,
	AVG_POOL,

	/** Should be used for the output neurons, as it squishes the values given to it to be from 0 to 1 */
	SIGMOID,
	/** Should be used for the output neurons, as it squishes the values given to it to be from -1 to 1 */
	TANH,
	/** Should be used for hidden layers, ELU is preferred for speed of gradient descent. */
	LEAKY_RELU,
	/** Use for hidden layers, preferred for speed of gradient descent. */
	ELU,
	/** Use for hidden layers, Can cause the Dying ReLU problem, use ELU or Leaky ReLU, prefer ELU. */
	RELU,
}

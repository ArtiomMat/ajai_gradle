package org.artiom.net;

class KernelUnit {
	protected Map[] inputs;

	/** The filter has as many channels as the inputs, the output is a feature map with the same  */
	protected Map filter;
	protected float bias;
	protected int stride;


	public KernelUnit(Float[] pixels, int width, int height, int pixelFormat) {
		super(pixels, width, height, pixelFormat);
	}

	public KernelUnit(Float[] pixels, int width, int height) {
		super(pixels, width, height);
	}

	public KernelUnit(int width, int height) {
		super(width, height);
	}

	public KernelUnit(int width, int height, int pixelFormat) {
		super(width, height, pixelFormat);
	}

	protected Map convolve(int stride) {
		/*if (other.getWidth() % stride != 0 || other.getHeight() % stride != 0 || width > other.getWidth()  || height > other.getWidth()
		|| channels != other.getChannels())
			return null;

		Map ret = new Map((other.getWidth()-width)/stride+1, (other.getHeight()-height)/stride+1, other.getChannels());

		float[] ourPixel = new float[channels], otherPixel = new float[channels];
		// The loop for the ret map
		for (int x = 0; x < ret.getWidth(); x++) {
			for (int y = 0; y < ret.getHeight(); y++) {
				float[] value = new float[channels];
				int sx = stride*x, sy = stride*y;

				// The loop for the current map, where we pool each pixel for the ret map
				for (int _x = sx; _x < sx+width; _x++) {
					for (int _y = sy; _y < sy+height; _y++) {
						getPixel(_x-sx, _y-sy, ourPixel);
						other.getPixel(_x, _y, otherPixel);
						for (int i = 0; i < channels; i++) {
							value[i] += otherPixel[i]*ourPixel[i];
						}
					}
				}

				ret.setPixel(x, y, value);
			}
		}

		return ret;*/
	}

}

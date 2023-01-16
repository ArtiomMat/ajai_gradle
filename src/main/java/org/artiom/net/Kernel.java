package org.artiom.net;

public class Kernel extends Map{

	public Kernel(float[] pixels, int width, int height, int pixelFormat) {
		super(pixels, width, height, pixelFormat);
	}

	public Kernel(float[] pixels, int width, int height) {
		super(pixels, width, height);
	}

	public Kernel(int width, int height) {
		super(width, height);
	}

	public Kernel(int width, int height, int pixelFormat) {
		super(width, height, pixelFormat);
	}

	public Map convolve(Map other, int stride) {
		if (other.getWidth() % stride != 0 || other.getHeight() % stride != 0 || width > other.getWidth()  || height > other.getWidth()
		|| pixelFormat != other.getPixelFormat())
			return null;

		Map ret = new Map((other.getWidth()-width)/stride+1, (other.getHeight()-height)/stride+1, other.getPixelFormat());

		float[] ourPixel = new float[pixelFormat], otherPixel = new float[pixelFormat];
		// The loop for the ret map
		for (int x = 0; x < ret.getWidth(); x++) {
			for (int y = 0; y < ret.getHeight(); y++) {
				float[] value = new float[pixelFormat];
				int sx = stride*x, sy = stride*y;

				// The loop for the current map, where we pool each pixel for the ret map
				for (int _x = sx; _x < sx+width; _x++) {
					for (int _y = sy; _y < sy+height; _y++) {
						getPixel(_x-sx, _y-sy, ourPixel);
						other.getPixel(_x, _y, otherPixel);
						for (int i = 0; i < pixelFormat; i++) {
							value[i] += otherPixel[i]*ourPixel[i];
						}
					}
				}

				ret.setPixel(x, y, value);
			}
		}

		return ret;
	}

}

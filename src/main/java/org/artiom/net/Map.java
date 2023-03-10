package org.artiom.net;

import javax.imageio.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Map {

	/**
	 * A pixel's channel value ranges from 0f to 1f
	 */
	protected Float[] pixels;
	protected int channels;
	protected int width, height;

	public int getChannels() {
		return channels;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Float[] getPixels() {
		return pixels;
	}

	/**
	 *
	 * @param pixels Doesn't copy the array.
	 */
	public Map(
			Float[] pixels,
			int width,
			int height,
			int pixelFormat
	) {
		this.width = width;
		this.height = height;
		this.channels = pixelFormat;

		/*this.pixels = new byte[width*height*pixelFormat];
		if (pixels.length == this.pixels.length) {
			for (int i = 0; i < this.pixels.length; i++) {
				this.pixels[i] = pixels[i];
			}
		}*/
		this.pixels = pixels;
	}

	/** Assumes pixelType=L8 */
	public Map(Float[] pixels, int width, int height) {
		this(pixels, width, height, 1);
	}

	/** Assumes pixelType=L8 */
	public Map(int width, int height) {
		this(new Float[width*height*1], width, height, 1);
	}

	public Map(int width, int height, int pixelFormat) {
		this(new Float[width*height*pixelFormat], width, height, pixelFormat);
	}

	public Map(String fp) throws IOException {
		load(fp);
	}

	public void setPixel(int x, int y, float[] pixel) {
		for (int i = 0; i < channels; i++)
			pixels[(y*width+x)* channels +i] = pixel[i];
	}

	public void getPixel(int x, int y, float[] pixel) {
		for (int i = 0; i < channels; i++)
			pixel[i] = pixels[(y*width+x)* channels +i];
	}

	public float[] getPixel(int x, int y) {
		float[] pixel = new float[channels];
		getPixel(x, y, pixel);
		return pixel;
	}

	private void init(Raster r) {
		width = r.getWidth();
		height = r.getHeight();

		pixels = new Float[width*height* channels];
	}

	// Make sure the image is 1:1 ratio, otherwise it won't be accepted
	public void load(String fp) throws IOException {
		BufferedImage bf = ImageIO.read(new File(fp));

		Raster r = bf.getData();

		// Determining pixelFormat
		{
			ColorModel cm = bf.getColorModel();

			boolean a = cm.hasAlpha();
			int depth = cm.getPixelSize();
			int colors = cm.getNumColorComponents();

			boolean supported = true;
			if (!a && depth == 8 && colors == 1) {
				channels = 1;

				System.out.println(cm.getColorSpace());

				init(r);
				// We use bf.getRGB in grayscale because r.getPixel
				// seems to return contrasted values, no idea why.
				int rgb;
				float[] pFloat = new float[channels];
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						rgb = bf.getRGB(x, y);
						// Apparently the rgb is not empty in the other 24 bits, so we apply the big AND boy
						pFloat[0] = (rgb&0xFF)/255f;
						setPixel(x, y, pFloat);
					}
				}
			}
			else if (!a && depth == 24 && colors == 3) {
				channels = 3;
				init(r);
				float[] pFloat = new float[channels];
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						r.getPixel(x, y, pFloat);
						for (int i = 0; i < 3; i++)
							pFloat[i] /= 255f;

						System.out.println(Arrays.toString(pFloat));
						setPixel(x, y, pFloat);
					}
				}
			}
			else if (a && depth == 32 && colors == 3) {
				channels = 3;
				/* TODO: Skip over the alpha channel */
				System.out.println("ARGB Not supported yet");
				supported = false;
			}
			else
				supported = false;

			if (!supported) {
				throw new IOException(
						"Map.load(): Unsupported pixel format. Check Map's formats at the top of the class definition."
				);
			}
		}


		/*byte[] rBuf = ((DataBufferByte) r.getDataBuffer()).getData();
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = rBuf[i];
		}*/


	}

	public void save(String fp) throws IOException {
		int type;
		if (channels == 1)
			type = BufferedImage.TYPE_BYTE_GRAY;
		else
			type = BufferedImage.TYPE_INT_RGB;
		BufferedImage bi = new BufferedImage(width, height, type);

		float[] pixel = new float[channels];
		int[] pixelInt = new int[channels];
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				getPixel(x, y, pixel);
				for (int i = 0; i < channels; i++)
					pixelInt[i] = (int) (pixel[i]*255);
				System.out.println(Arrays.toString(pixelInt));

				int rgb;
				 if (channels == 3)
					rgb = (pixelInt[0] << 16) | (pixelInt[1] << 8) | pixelInt[2];
				else
					rgb = (pixelInt[0] << 16) | (pixelInt[0] << 8) | pixelInt[0];

				bi.setRGB(x, y, rgb);
			}
		}

		ImageIO.write(bi, "PNG", new File(fp));
	}
/*
	public int placeOn(Map on, int _x, int _y) {
		int ret = 0;
		for (int x = _x; x < _x+dim; x++) {
			for (int y = _y; y < _y+dim; y++) {
				int a = getPixel(x-_x, y-_y);
				int b = on.getPixel(x, y);
				ret += a*b;
			}
		}
		return ret;
	}
*/
	/**
	 * Performs a pool on the map by getting the average pixel.
	 * @param dimPool Size of the pool's kernel dimension.
	 * @param stride The stride size, both on x and y.
	 * @return New pool-ed map.
	 */
	public Map poolAvg(int dimPool, int stride) {
		if (width % stride != 0 || height % stride != 0 || dimPool > width  || dimPool > height)
			return null;

		Map ret = new Map((width-dimPool)/stride+1, (height-dimPool)/stride+1, channels);

		float[] p = new float[channels];
		// The loop for the ret map
		for (int x = 0; x < ret.getWidth(); x++) {
			for (int y = 0; y < ret.getHeight(); y++) {
				float[] value = new float[channels];

				int sx = stride*x, sy = stride*y;

				// The loop for the current map, where we pool each pixel for the ret map
				for (int _x = sx; _x < sx+dimPool; _x++) {
					for (int _y = sy; _y < sy+dimPool; _y++) {
						getPixel(_x, _y, p);
						for (int i = 0; i < channels; i++) {
							value[i] += p[i];
						}
					}
				}

				for (int i = 0; i < channels; i++) {
					value[i] /= (dimPool * dimPool);
				}

				ret.setPixel(x, y, value);
			}
		}

		return ret;
	}

	@Deprecated
	private float getPixelLuminance(int i) {
		i*= channels;

		if (channels >1)
			return 0.2126f * pixels[i+0] + 0.7152f * pixels[i+1] + 0.0722f * pixels[i+2];
//			return 0.299f*pixels[i+0] + 0.587f*pixels[i+1] + 0.114f*pixels[i+2];
		else
			return pixels[i];
	}

	@Deprecated
	private float getPixelLuminance(int x, int y) {
		return getPixelLuminance(y*width+x);
	}

	/** Creates a map based on luminance(not really actually uses average of the RGB's sum), for FORMAT_RGB maps */
	public Map getLuminanceMap() {
		if (channels == 1)
			return this;

		Map ret = new Map(width, height, 1);

		// We don't use luminance actually
		for (int i = 0; i < ret.pixels.length; i++) {
			float sum = pixels[i* channels] + pixels[i* channels +1] + pixels[i* channels +2];
			ret.pixels[i] = sum/3;
		}

		return ret;
	}

	private float getPixelAvg(int x, int y) {
		float sum = 0;
		for (int i = 0; i < channels; i++)
			sum += pixels[(y*width+x)* channels +i];
		return sum/3;
	}


	/**
	 *
	 * @param dimPool
	 * @param stride
	 * @param max if we want to use pool max set max to 1 else set it to -1
	 * @return
	 */
	private Map poolMinMax(int dimPool, int stride, int max) {
		if (width % stride != 0 || height % stride != 0 || dimPool > width  || dimPool > height)
			return null;

		Map ret = new Map((width-dimPool)/stride+1, (height-dimPool)/stride+1, channels);

		float[] p = new float[channels];
		// The loop for the ret map
		for (int x = 0; x < ret.getWidth(); x++) {
			for (int y = 0; y < ret.getHeight(); y++) {
				int sx = stride*x, sy = stride*y;

				float bestL = getPixelAvg(sx, sy);
				int bestX=sx, bestY=sy;

				// The loop for the current map, where we pool each pixel for the ret map
				for (int _x = sx; _x < sx+dimPool; _x++) {
					for (int _y = sy+1; _y < sy+dimPool; _y++) {
						getPixel(_x, _y, p);
						// -1(x-y)>0 = x-y<0
						if (max*(getPixelAvg(_x, _y)-bestL) > 0) {
							bestX = _x;
							bestY = _y;
						}
					}
				}

				ret.setPixel(x, y, getPixel(bestX, bestY));
			}
		}

		return ret;
	}

	public Map poolMax(int dimPool, int stride) {
		return poolMinMax(dimPool, stride, 1);
	}
	public Map poolMin(int dimPool, int stride) {
		return poolMinMax(dimPool, stride, -1);
	}

}
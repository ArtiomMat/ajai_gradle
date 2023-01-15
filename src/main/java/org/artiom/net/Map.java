package org.artiom.net;

import javax.imageio.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

// TODO: ONE BIG TODO, TURN EVERYTHING HERE INTO A FLOAT. GOOD LUCK MAN! SHOULD HAVE CALCULATED THIS BEFORE HAND.

public class Map {
	public static final int L8 = 1;
	// In 5-6-5 bit format! Not 5-5-5, leaving a useless bit is bad for AI!
	public static final int RGB16 = 2;
	public static final int RGB24 = 3;
	public static final int ARGB32 = 4;

	protected byte[] pixels;
	protected int pixelFormat;
	protected int width, height;

	public int getPixelFormat() {
		return pixelFormat;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public byte[] getPixels() {
		return pixels;
	}

	/**
	 *
	 * @param pixels Doesn't copy the array.
	 */
	public Map(
			byte[] pixels,
			int width,
			int height,
			int pixelFormat
	) {
		this.width = width;
		this.height = height;
		this.pixelFormat = pixelFormat;

		/*this.pixels = new byte[width*height*pixelFormat];
		if (pixels.length == this.pixels.length) {
			for (int i = 0; i < this.pixels.length; i++) {
				this.pixels[i] = pixels[i];
			}
		}*/
		this.pixels = pixels;
	}

	/** Assumes pixelType=L8 */
	public Map(byte[] pixels, int width, int height) {
		this(pixels, width, height, L8);
	}

	/** Assumes pixelType=L8 */
	public Map(int width, int height) {
		this(new byte[width*height*L8], width, height, L8);
	}

	public Map(int width, int height, int pixelFormat) {
		this(new byte[width*height*pixelFormat], width, height, pixelFormat);
	}

	public Map(String fp) throws IOException {
		load(fp);
	}

	public void setPixel(int index, byte[] pixel) {
		/*for (int i = 0; i < pixelFormat; i++)
			pixels[index*pixelFormat+i] = pixel[i];*/
		if (pixelFormat >= 0)
			System.arraycopy(pixel, 0, pixels, index * pixelFormat, pixelFormat);
	}

	public void setPixel(int x, int y, byte[] pixel) {
		setPixel(y*width+x, pixel);
	}

	public void getPixel(int x, int y, byte[] pixel) {
		if (pixelFormat >= 0)
			System.arraycopy(pixels, (y * width + x) * pixelFormat, pixel, 0, pixelFormat);
	}

	public byte[] getPixel(int x, int y) {
		byte[] pixel = new byte[pixelFormat];
		getPixel(x, y, pixel);
		return pixel;
	}

	private void init(Raster r) {
		width = r.getWidth();
		height = r.getHeight();

		pixels = new byte[width*height*pixelFormat];
	}

	// TODO: Implement: save, load

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
				pixelFormat = L8;

				System.out.println(cm.getColorSpace());

				init(r);
				// We use bf.getRGB in grayscale because r.getPixel
				// seems to return contrasted values, no idea why.
				int rgb;
				byte[] pByte = new byte[pixelFormat];
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						rgb = bf.getRGB(x, y);
						pByte[0] = (byte)rgb;
						setPixel(x, y, pByte);
					}
				}
			}
			else if (!a && depth == 16 && colors == 3) {
				// We need to make sure we are in 5-6-5 order
				if (cm.getComponentSize()[1] != 6)
					supported = false;
				else {
					pixelFormat = RGB16;
					// TODO
					supported = false;
				}
			}
			else if (!a && depth == 24 && colors == 3) {
				pixelFormat = RGB24;
				System.out.println("LOL RGB");
				init(r);
				int[] p = new int[pixelFormat];
				byte[] pByte = new byte[pixelFormat];
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						r.getPixel(x, y, p);
						pByte[0] = (byte)p[0];
						pByte[1] = (byte)p[1];
						pByte[2] = (byte)p[2];
						if (x == 0 && y == 0)
							System.out.println();

						setPixel(x, y, pByte);
					}
				}
			}
			else if (a && depth == 32 && colors == 3) {
				pixelFormat = ARGB32;
				/* TODO: Make sure the pixels are in ARGB and not RGBA, maybe test it with
				image with alpha and see which index corresponds to the alpha. */
				System.out.println("FUCKCUFFK");
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

	// FIXME: Contrast getting stronger as you save? it gets darker for some reason.
	public void save(String fp) throws IOException {
		int type;
		if (pixelFormat == L8)
			type = BufferedImage.TYPE_BYTE_GRAY;
		else if (pixelFormat == RGB16)
			type = BufferedImage.TYPE_USHORT_565_RGB;
		else if (pixelFormat == RGB24)
			type = BufferedImage.TYPE_INT_RGB;
		else
			type = BufferedImage.TYPE_INT_ARGB;
		BufferedImage bi = new BufferedImage(width, height, type);

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				byte[] pixel = getPixel(x, y);
				int[] pixelInt = new int[pixelFormat];
				for (int i = 0; i < pixel.length; i++)
					pixelInt[i] = (int)(pixel[i]) & 0xFF;

				int rgb;
				if (pixelFormat == 4)
					rgb = pixelInt[1]<<16 | pixelInt[2] << 8 | pixelInt[3];
				else if (pixelFormat == 3) {
					rgb = pixelInt[0]<<16 | pixelInt[1] << 8 | pixelInt[2];
					// System.out.println(pixelInt[0]);
				}
				else if (pixelFormat == 2)
					rgb = 0;// TODO
				else
					rgb = pixelInt[0]<<16 | pixelInt[0] << 8 | pixelInt[0];

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

		Map ret = new Map((width-dimPool)/stride+1, (height-dimPool)/stride+1, pixelFormat);

		int[] valueInt = new int[pixelFormat];
		byte[] value = new byte[pixelFormat];
		byte[] p = new byte[pixelFormat];
		// The loop for the ret map
		for (int x = 0; x < ret.getWidth(); x++) {
			for (int y = 0; y < ret.getHeight(); y++) {
				int sx = stride*x, sy = stride*y;

				// The loop for the current map, where we pool each pixel for the ret map
				for (int _x = sx; _x < sx+dimPool; _x++) {
					for (int _y = sy; _y < sy+dimPool; _y++) {
						getPixel(_x, _y, p);
						for (int i = 0; i < pixelFormat; i++)
							valueInt[i] += p[i]&0xFF;
					}
				}

				for (int i = 0; i < pixelFormat; i++) {
					valueInt[i] /= (dimPool * dimPool);
					value[i] = (byte)(valueInt[i]);

					valueInt[i] = 0; // reset
				}

				ret.setPixel(x, y, value);
			}
		}

		return ret;
	}

	private float getPixelLuminance(int x, int y) {
		int i = y*width+x;
		if (pixelFormat>1)
			return 0.2126f * pixels[i+0] + 0.7152f * pixels[i+1] + 0.0722f * pixels[i+2];
		else
			return (pixels[i]&0xFF)/255.0f;
	}

	@Deprecated
	private float getPixelSum(int x, int y) {
		int sum = 0;
		for (int i = 0; i < pixelFormat; i++)
			sum += pixels[y*width+x+i];
		return sum/(255*3.0f);
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

		Map ret = new Map((width-dimPool)/stride+1, (height-dimPool)/stride+1, pixelFormat);

		byte[] p = new byte[pixelFormat];
		// The loop for the ret map
		for (int x = 0; x < ret.getWidth(); x++) {
			for (int y = 0; y < ret.getHeight(); y++) {
				int sx = stride*x, sy = stride*y;

				float bestL = getPixelLuminance(sx, sy);
				int bestX=sx, bestY=sy;

				// The loop for the current map, where we pool each pixel for the ret map
				for (int _x = sx; _x < sx+dimPool; _x++) {
					for (int _y = sy+1; _y < sy+dimPool; _y++) {
						getPixel(_x, _y, p);
						// -1(x-y)>0 = x-y<0
						if (max*(getPixelLuminance(_x, _y)-bestL) > 0) {
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
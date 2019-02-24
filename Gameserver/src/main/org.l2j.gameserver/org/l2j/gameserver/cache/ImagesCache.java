package org.l2j.gameserver.cache;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.l2j.commons.formats.dds.DDSConverter;
import org.l2j.gameserver.Config;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.gameserver.settings.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Bonux
**/
public class ImagesCache
{
	private static final Logger _log = LoggerFactory.getLogger(ImagesCache.class);
	private static final int[] SIZES = new int[] { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024 };
	private static final int MAX_SIZE = SIZES[SIZES.length - 1];

	public static final Pattern HTML_PATTERN = Pattern.compile("%image:(.*?)%", Pattern.DOTALL);

	private final static ImagesCache _instance = new ImagesCache();
	private static final int INITIAL_HASH_CODE = 17;
	private static final int CONSTANT_HASH_CODE = 37;

	public final static ImagesCache getInstance()
	{
		return _instance;
	}

	private final Map<String, Integer> _imagesId = new HashMap<String, Integer>();
	/** Получение изображения по ID */
	private final IntObjectMap<byte[]> _images = new HashIntObjectMap<byte[]>();

	/** Блокировка для чтения/записи объектов из "кэша" */
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	private ImagesCache()
	{
		load();
	}

	public void load()
	{
		_log.info("ImagesCache: Loading images...");

		var dir = getSettings(ServerSettings.class).dataPackRootPath().resolve("images");
		if(Files.notExists(dir) || !Files.isDirectory(dir))
		{
			_log.info("ImagesCache: Files missing, loading aborted.");
			return;
		}

		int count = loadImagesDir(dir.toFile());

		_log.info("ImagesCache: Loaded " + count + " images");
	}

	private int loadImagesDir(File dir)
	{
		int count = 0;
		for(File file : dir.listFiles())
		{
			if(file.isDirectory())
			{
				count += loadImagesDir(file);
				continue;
			}

			if(!checkImageFormat(file))
				continue;

			String fileName = file.getName();

			if(_imagesId.containsKey(fileName.toLowerCase()))
			{
				_log.warn("Duplicate image name \"" + fileName + "\". Replacing with " + file.getPath());
				continue;
			}

			BufferedImage image = resizeImage(file);
			if(image == null)
				continue;

			try
			{
				ByteBuffer buffer = DDSConverter.convertToDxt1NoTransparency(image);
				byte[] array = buffer.array();
				int imageId = Math.abs((INITIAL_HASH_CODE * CONSTANT_HASH_CODE + fileName.hashCode()) * CONSTANT_HASH_CODE + Arrays.hashCode(array));

				_imagesId.put(fileName.toLowerCase(), imageId);
				_images.put(imageId, array);
			}
			catch(Exception e)
			{
				_log.error("ImagesChache: Error while loading " + fileName + " (" + image.getWidth() + "x" + image.getHeight() + ") image.", (Throwable)e);
			}

			count++;
		}
		return count;
	}

	private static BufferedImage resizeImage(File file)
	{
		BufferedImage image;
		try
		{
			image = ImageIO.read(file);
		}
		catch(IOException ioe)
		{
			_log.error("ImagesCache: Error while resizing " + file.getName() + " image.");
			return null;
		}

		if(image == null)
			return null;

		int width = image.getWidth();
		int height = image.getHeight();

		boolean resizeWidth = true;
		if(width > MAX_SIZE)
		{
			image = image.getSubimage(0, 0, MAX_SIZE, height);
			resizeWidth = false;
		}

		boolean resizeHeight = true;
		if(height > MAX_SIZE)
		{
			image = image.getSubimage(0, 0, width, MAX_SIZE);
			resizeHeight = false;
		}

		int resizedWidth = width;
		if(resizeWidth)
		{
			for(int size : SIZES)
			{
				if(size < width)
					continue;

				resizedWidth = size;
				break;
			}
		}

		int resizedHeight = height;
		if(resizeHeight)
		{
			for(int size : SIZES)
			{
				if(size < height)
					continue;

				resizedHeight = size;
				break;
			}
		}

		if(resizedWidth != width || resizedHeight != height)
		{
			BufferedImage resizedImage = new BufferedImage(resizedWidth, resizedHeight, image.getType());
			Graphics2D g = resizedImage.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(image, 0, 0, resizedWidth, resizedHeight, 0, 0, image.getWidth(), image.getHeight(), null);
			g.dispose(); 
			return resizedImage;
		}
		return image;
	}

	public int getImageId(String val)
	{
		int imageId = -1;

		readLock.lock();
		try
		{
			if(_imagesId.get(val.toLowerCase()) != null)
				imageId = _imagesId.get(val.toLowerCase());
		}
		finally
		{
			readLock.unlock();
		}

		return imageId;
	}

	public byte[] getImage(int imageId)
	{
		byte[] image = null;

		readLock.lock();
		try
		{
			image = _images.get(imageId);
		}
		finally
		{
			readLock.unlock();
		}

		return image;
	}

	private static boolean checkImageFormat(File file)
	{
		String filename = file.getName();
		if(filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".bmp"))
			return true;
		return false;
	}
}
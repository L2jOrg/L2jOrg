package org.l2j.gameserver.data.htm;

import java.io.File;
import java.io.IOException;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.utils.ArabicConv;
import org.l2j.gameserver.utils.HtmlUtils;
import org.l2j.gameserver.utils.Language;
import org.l2j.gameserver.utils.Util;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Кэширование html диалогов.
 *
 * В кеше список вот так
 * admin/admhelp.htm
 * admin/admin.htm
 * admin/admserver.htm
 * admin/banmenu.htm
 * admin/charmanage.htm
 */
public class HtmCache
{
	public static final int DISABLED = 0; // кеширование отключено (только для тестирования)
	public static final int LAZY = 1; // диалоги кешируются по мере обращения
	public static final int ENABLED = 2; // все диалоги кешируются при загрузке сервера

	private static final Logger _log = LoggerFactory.getLogger(HtmCache.class);

	private final static HtmCache _instance = new HtmCache();

	public static HtmCache getInstance()
	{
		return _instance;
	}

	private final Cache[] _cache = new Cache[Language.VALUES.length];

	private HtmCache()
	{
		for(int i = 0; i < _cache.length; i++)
			_cache[i] = CacheManager.getInstance().getCache(getClass().getName() + "." + Language.VALUES[i].name());
	}

	public void reload()
	{
		clear();

		switch(Config.HTM_CACHE_MODE)
		{
			case ENABLED:
				for(Language lang : Language.VALUES)
				{
					if(!Config.AVAILABLE_LANGUAGES.contains(lang))
						continue;

					File root = new File(Config.DATAPACK_ROOT, "data/html/" + lang.getShortName());
					if(!root.exists())
					{
						_log.info("HtmCache: Not find html dir for lang: " + lang);
						continue;
					}
					load(lang, root, root.getAbsolutePath() + "/");

					root = new File(Config.DATAPACK_ROOT, "custom/html/" + lang.getShortName());
					if(!root.exists())
					{
						//_log.info("HtmCache: Not find html dir for lang: " + lang);
						continue;
					}
					load(lang, root, root.getAbsolutePath() + "/");

					_log.info(String.format("HtmCache: parsing %d documents; lang: %s.", _cache[lang.ordinal()].getSize(), lang));
				}
				break;
			case LAZY:
				_log.info("HtmCache: lazy cache mode.");
				break;
			case DISABLED:
				_log.info("HtmCache: disabled.");
				break;
		}
	}

	private void load(Language lang, File f, final String rootPath)
	{
		if(!f.exists())
		{
			_log.info("HtmCache: dir not exists: " + f);
			return;
		}
		File[] files = f.listFiles();

		//FIXME [VISTALL] может лучше использовать Apache FileUtils?
		for(File file : files)
		{
			if(file.isDirectory())
				load(lang, file, rootPath);
			else
			{
				if(file.getName().endsWith(".htm"))
				{
					try
					{
						putContent(lang, file, rootPath);
					}
					catch(IOException e)
					{
						_log.error("HtmCache: file error: " + e, e);
					}
				}
			}
		}
	}

	private String putContent(Language lang, File f, final String rootPath) throws IOException
	{
		String content = readContent(f);

		String path = f.getAbsolutePath().substring(rootPath.length()).replace("\\", "/");

		content = HtmlUtils.bbParse(content);

		_cache[lang.ordinal()].put(new Element(path.toLowerCase(), content));

		return content;
	}

	/**
	 * Получить html.
	 *
	 * @param fileName путь до html относительно data/html/LANG
	 * @param player
	 * @return существующий диалог, либо null и сообщение об ошибке в лог, если диалога не существует
	 */
	public String getHtml(String fileName, Player player)
	{
		Language lang = player == null ? Config.DEFAULT_LANG : player.getLanguage();
		String cache = getCache(fileName, lang);

		if(cache == null)
			_log.warn("Dialog: " + "data/html/" + lang.getShortName() + "/" + fileName + " not found.");

		return cache;
	}

	/**
	 * Получить существующий html.
	 *
	 * @param fileName путь до html относительно data/html/LANG
	 * @param player
	 * @return null если диалога не существует
	 */
	public String getIfExists(String fileName, Player player)
	{
		Language lang = player == null ? Config.DEFAULT_LANG : player.getLanguage();
		return getCache(fileName, lang);
	}

	/**
	 * Получить шаблоны из html.
	 *
	 * @param fileName путь до html относительно data/html/LANG
	 * @param player
	 * @return TIntStringHashMap
	 */
	public HtmTemplates getTemplates(String fileName, Player player)
	{
		Language lang = player == null ? Config.DEFAULT_LANG : player.getLanguage();
		HtmTemplates templates = Util.parseTemplates(fileName, lang, getHtml(fileName, player));
		if(templates == null)
			return HtmTemplates.EMPTY_TEMPLATES;
		return templates;
	}

	public String getCache(String file, Language lang)
	{
		if(file == null)
			return null;

		final String fileLower = file.toLowerCase();
		String cache = get(lang, fileLower);

		if(cache == null)
		{
			switch(Config.HTM_CACHE_MODE)
			{
				case ENABLED:
					if(lang == Language.ENGLISH)
						cache = get(Language.RUSSIAN, fileLower);
					else
						cache = get(Language.ENGLISH, fileLower);
					break;
				case LAZY:
					cache = loadLazy(lang, file);
					if(cache == null)
					{
						if(lang == Language.ENGLISH)
							cache = loadLazy(Language.RUSSIAN, file);
						else
							cache = loadLazy(Language.ENGLISH, file);
					}
					break;
				case DISABLED:
					cache = loadDisabled(lang, file);
					if(cache == null)
					{
						if(lang == Language.ENGLISH)
							cache = loadDisabled(Language.RUSSIAN, file);
						else
							cache = loadDisabled(Language.ENGLISH, file);
					}
					break;
			}
		}

		return cache;
	}

	private String loadDisabled(Language lang, String file)
	{
		String cache = null;
		File f = getFile(new File(Config.DATAPACK_ROOT, "data/html/" + lang.getShortName() + "/" + file));
		if(f.exists())
		{
			try
			{
				cache = readContent(f);

				cache = HtmlUtils.bbParse(cache);
			}
			catch(IOException e)
			{
				_log.info("HtmCache: File error: " + file + " lang: " + lang);
			}
		}
		f = getFile(new File(Config.DATAPACK_ROOT, "custom/html/" + lang.getShortName() + "/" + file));
		if(f.exists())
		{
			try
			{
				cache = readContent(f);

				cache = HtmlUtils.bbParse(cache);
			}
			catch(IOException e)
			{
				_log.info("HtmCache: File error: " + file + " lang: " + lang);
			}
		}
		return cache;
	}

	private String loadLazy(Language lang, String file)
	{
		String cache = null;
		File root = new File(Config.DATAPACK_ROOT, "data/html/" + lang.getShortName());
		File f = getFile(new File(root, file));
		if(f.exists())
		{
			try
			{
				cache = putContent(lang, f, root.getAbsolutePath() + "/");
			}
			catch(IOException e)
			{
				_log.info("HtmCache: File error: " + file + " lang: " + lang);
			}
		}
		root = new File(Config.DATAPACK_ROOT, "custom/html/" + lang.getShortName());
		f = getFile(new File(root, file));
		if(f.exists())
		{
			try
			{
				cache = putContent(lang, f, root.getAbsolutePath() + "/");
			}
			catch(IOException e)
			{
				_log.info("HtmCache: File error: " + file + " lang: " + lang);
			}
		}
		return cache;
	}

	private String get(Language lang, String f)
	{
		Element element = _cache[lang.ordinal()].get(f);

		if(element == null)
			element = _cache[Language.ENGLISH.ordinal()].get(f);

		return element == null ? null : (String) element.getObjectValue();
	}

	public void clear()
	{
		for(int i = 0; i < _cache.length; i++)
			_cache[i].removeAll();
	}

	private static String readContent(File file) throws IOException
	{
		String content = FileUtils.readFileToString(file, "UTF-8");
		if(Config.HTM_SHAPE_ARABIC)
			content = ArabicConv.shapeArabic(content);
		return content;
	}

	private static File getFile(File file)
	{
		if(!file.exists())
		{
			File dir = file.getParentFile();
			if(dir != null && dir.isDirectory())
			{
				for(File f : dir.listFiles())
				{
					if(f.getName().equalsIgnoreCase(file.getName()))
						return f;
				}
			}

		}
		return file;
	}
}
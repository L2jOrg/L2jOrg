package l2s.gameserver.data.string;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.utils.Language;

/**
 * Author: VISTALL
 * Date:  19:27/29.12.2010
 */
public final class StringsHolder extends AbstractHolder
{
	private static final StringsHolder _instance = new StringsHolder();

	private final Map<Language, Map<String, String>> _strings = new HashMap<Language, Map<String, String>>();

	public static StringsHolder getInstance()
	{
		return _instance;
	}

	private StringsHolder()
	{
		//
	}

	public String getString(String name, Player player)
	{
		Language lang = player == null ? Config.DEFAULT_LANG : player.getLanguage();
		return getString(name, lang);
	}

	public String getString(Player player, String name)
	{
		Language lang = player == null ? Config.DEFAULT_LANG : player.getLanguage();
		return getString(name, lang);
	}

	public String getString(String address, Language lang)
	{
		Map<String, String> strings = _strings.get(lang);
		String value = strings == null ? null : strings.get(address);
		if(value == null)
		{
			if(lang == Language.ENGLISH)
			{
				strings = _strings.get(Language.RUSSIAN);
				value = strings == null ? null : strings.get(address);
			}
			else
			{
				strings = _strings.get(Language.ENGLISH);
				value = strings == null ? null : strings.get(address);
			}
		}
		return value;
	}

	public void load()
	{
		for(Language lang : Language.VALUES)
		{
			_strings.put(lang, new HashMap<String, String>());

			if(!Config.AVAILABLE_LANGUAGES.contains(lang))
				continue;

			File file = new File(Config.DATAPACK_ROOT, "data/string/strings/" + lang.getShortName() + ".properties");
			if(!file.exists())
			{
				// Проверяем только английский и русский, потому что они являються базовыми.
				if(lang == Language.ENGLISH || lang == Language.RUSSIAN)
					warn("Not find file: " + file.getAbsolutePath());
			}
			else
			{
				LineNumberReader reader = null;
				try
				{
					reader = new LineNumberReader(new FileReader(file));
					String line = null;
					while((line = reader.readLine()) != null)
					{
						if(line.startsWith("#"))
							continue;

						StringTokenizer token = new StringTokenizer(line, "=");
						if(token.countTokens() < 2)
						{
							error("Error on line: " + line + "; file: " + file.getName());
							continue;
						}

						String name = token.nextToken();
						String value = token.nextToken();
						while(token.hasMoreTokens())
							value += "=" + token.nextToken();

						_strings.get(lang).put(name, value);
					}
				}
				catch(Exception e)
				{
					error("Exception: " + e, e);
				}
				finally
				{
					try
					{
						reader.close();
					}
					catch(Exception e)
					{
						//
					}
				}
			}
		}

		log();
	}

	public void reload()
	{
		clear();
		load();
	}

	@Override
	public void log()
	{
		for(Map.Entry<Language, Map<String, String>> entry : _strings.entrySet())
		{
			if(!Config.AVAILABLE_LANGUAGES.contains(entry.getKey()))
				continue;
			info("load strings: " + entry.getValue().size() + " for lang: " + entry.getKey());
		}
	}

	@Override
	public int size()
	{
		return _strings.size();
	}

	@Override
	public void clear()
	{
		_strings.clear();
	}
}

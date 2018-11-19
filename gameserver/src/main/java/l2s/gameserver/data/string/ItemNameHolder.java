package l2s.gameserver.data.string;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

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
 * @author: Bonux
 */
public final class ItemNameHolder extends AbstractHolder
{
	private static final ItemNameHolder _instance = new ItemNameHolder();

	private final Map<Language, TIntObjectMap<String>> _itemNames = new HashMap<Language, TIntObjectMap<String>>();

	public static ItemNameHolder getInstance()
	{
		return _instance;
	}

	private ItemNameHolder()
	{
		//
	}

	public String getItemName(Language lang, int itemId)
	{
		TIntObjectMap<String> itemNames = _itemNames.get(lang);
		String name = itemNames.get(itemId);
		if(name == null)
		{
			if(lang == Language.ENGLISH)
			{
				itemNames = _itemNames.get(Language.RUSSIAN);
				name = itemNames.get(itemId);
			}
			else
			{
				itemNames = _itemNames.get(Language.ENGLISH);
				name = itemNames.get(itemId);
			}
		}
		return name;
	}

	public String getItemName(Player player, int itemId)
	{
		Language lang = player == null ? Config.DEFAULT_LANG : player.getLanguage();
		return getItemName(lang, itemId);
	}

	public void load()
	{
		for(Language lang : Language.VALUES)
		{
			_itemNames.put(lang, new TIntObjectHashMap<String>());

			if(!Config.AVAILABLE_LANGUAGES.contains(lang))
				continue;

			File file = new File(Config.DATAPACK_ROOT, "data/string/itemname/" + lang.getShortName() + ".txt");
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
						StringTokenizer token = new StringTokenizer(line, "\t");
						if(token.countTokens() < 2)
						{
							error("Error on line: " + line + "; file: " + file.getName());
							continue;
						}

						int id = Integer.parseInt(token.nextToken());
						String value = token.nextToken();

						_itemNames.get(lang).put(id, value);
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
		for(Map.Entry<Language, TIntObjectMap<String>> entry : _itemNames.entrySet())
		{
			if(!Config.AVAILABLE_LANGUAGES.contains(entry.getKey()))
				continue;
			info("load item names: " + entry.getValue().size() + " for lang: " + entry.getKey());
		}
	}

	@Override
	public int size()
	{
		return _itemNames.size();
	}

	@Override
	public void clear()
	{
		_itemNames.clear();
	}
}
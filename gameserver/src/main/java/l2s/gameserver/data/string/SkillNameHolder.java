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
import l2s.gameserver.model.Skill;
import l2s.gameserver.utils.Language;
import l2s.gameserver.utils.SkillUtils;

/**
 * Author: VISTALL
 * Date:  19:27/29.12.2010
 */
public final class SkillNameHolder extends AbstractHolder
{
	private static final SkillNameHolder _instance = new SkillNameHolder();

	private final Map<Language, TIntObjectMap<String>> _skillNames = new HashMap<Language, TIntObjectMap<String>>();

	public static SkillNameHolder getInstance()
	{
		return _instance;
	}

	private SkillNameHolder()
	{
		//
	}

	public String getSkillName(Language lang, int hashCode)
	{
		TIntObjectMap<String> skillNames = _skillNames.get(lang);
		String name = skillNames.get(hashCode);
		if(name == null)
		{
			if(lang == Language.ENGLISH)
			{
				skillNames = _skillNames.get(Language.RUSSIAN);
				name = skillNames.get(hashCode);
			}
			else
			{
				skillNames = _skillNames.get(Language.ENGLISH);
				name = skillNames.get(hashCode);
			}
		}
		return name;
	}

	public String getSkillName(Player player, int hashCode)
	{
		Language lang = player == null ? Config.DEFAULT_LANG : player.getLanguage();
		return getSkillName(lang, hashCode);
	}

	public String getSkillName(Language lang, Skill skill)
	{
		return getSkillName(lang, skill.hashCode());
	}

	public String getSkillName(Player player, Skill skill)
	{
		return getSkillName(player, skill.hashCode());
	}

	public String getSkillName(Language lang, int id, int level)
	{
		return getSkillName(lang, SkillUtils.generateSkillHashCode(id, level));
	}

	public String getSkillName(Player player, int id, int level)
	{
		return getSkillName(player, SkillUtils.generateSkillHashCode(id, level));
	}

	public void load()
	{
		for(Language lang : Language.VALUES)
		{
			_skillNames.put(lang, new TIntObjectHashMap<String>());

			if(!Config.AVAILABLE_LANGUAGES.contains(lang))
				continue;

			File file = new File(Config.DATAPACK_ROOT, "data/string/skillname/" + lang.getShortName() + ".txt");
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
						int level = Integer.parseInt(token.nextToken());
						int hashCode = SkillUtils.generateSkillHashCode(id, level);
						String value = token.hasMoreTokens() ? token.nextToken() : "";

						_skillNames.get(lang).put(hashCode, value);
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
		for(Map.Entry<Language, TIntObjectMap<String>> entry : _skillNames.entrySet())
		{
			if(!Config.AVAILABLE_LANGUAGES.contains(entry.getKey()))
				continue;
			info("load skill names: " + entry.getValue().size() + " for lang: " + entry.getKey());
		}
	}

	@Override
	public int size()
	{
		return _skillNames.size();
	}

	@Override
	public void clear()
	{
		_skillNames.clear();
	}
}

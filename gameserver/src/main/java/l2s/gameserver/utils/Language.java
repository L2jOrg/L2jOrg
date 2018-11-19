package l2s.gameserver.utils;

import l2s.gameserver.Config;

/**
 * @author B0nux
 * @date 16:03/10.10.2011
 */
public enum Language
{
	// With offical client support
	KOREAN(0, "ko", "k"),
	ENGLISH(1, "en", "e"),
	CHINESE(4, "zh", "cn"),
	THAI(5, "th", "th"),
	RUSSIAN(8, "ru", "ru"),
	ENGLISH_EU(9, "eu", "eu"),
	// Custom
	PORTUGUESE(-1, "pt", "e"),
	SPANISH(-2, "es", "e"),
	ARABIC(-3, "ar", "e"),
	GREEK(-4, "el", "e"),
	GEORGIAN(-5, "ka", "e"),
	HUNGARIAN(-6, "hu", "e"),
	FINNISH(-7, "fi", "e"),
	UKRAINIAN(-8, "uk", "e"),
	VIETNAMESE(-9, "vi", "e");

	public static final Language[] VALUES = values();

	public static final String LANG_VAR = "lang@";

	private final int _id;
	private final String _shortName;
	private final String _datName;

	private Language(int id, String shortName, String datName)
	{
		_id = id;
		_shortName = shortName;
		_datName = datName;
	}

	public int getId()
	{
		return _id;
	}

	public String getShortName()
	{
		return _shortName;
	}

	public String getDatName()
	{
		return _datName;
	}

	public static Language getLanguage(int langId)
	{
		for(Language lang : VALUES)
			if(lang.getId() == langId)
				return lang;
		return Config.DEFAULT_LANG;
	}

	public static Language getLanguage(String shortName)
	{
		if(shortName != null)
		{
			for(Language lang : VALUES)
			{
				if(lang.getShortName().equalsIgnoreCase(shortName))
					return lang;
			}
		}
		return Config.DEFAULT_LANG;
	}
}
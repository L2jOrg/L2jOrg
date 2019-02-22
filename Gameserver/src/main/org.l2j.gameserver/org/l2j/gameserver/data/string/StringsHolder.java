package org.l2j.gameserver.data.string;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.utils.Language;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static java.util.Objects.isNull;

/**
 * Author: VISTALL
 * Date:  19:27/29.12.2010
 */
public final class StringsHolder extends AbstractHolder
{
	private static final StringsHolder _instance = new StringsHolder();

	private final Map<Language, Map<String, String>> _strings = new HashMap<Language, Map<String, String>>();

	private StringsHolder() {
		//
	}

	public String getString(Player player, String name)
	{
		Language lang = player == null ? Config.DEFAULT_LANG : player.getLanguage();
		return getString(name, lang);
	}

	public String getString(String address, Language lang) {
		Map<String, String> strings = _strings.get(lang);
		String value = strings == null ? null : strings.get(address);
		if(isNull(value)) {
			strings = _strings.get(Config.DEFAULT_LANG);
			value = strings == null ? null : strings.get(address);
		}
		return value;
	}

	public void load() {
		for(Language lang : Language.VALUES) {

			if(!Config.AVAILABLE_LANGUAGES.contains(lang))
				continue;

			_strings.put(lang, new HashMap<>());

			File file = new File(Config.DATAPACK_ROOT, String.format("data/string/strings/%s.properties", lang.getShortName()));
			if(!file.exists()) {
				if(lang == Config.DEFAULT_LANG) {
					logger.warn("Not find file: {}", file.getAbsolutePath());
				}
			} else {
				try {
					for(var line : Files.readAllLines(file.toPath())) {
						if (line.startsWith("#"))
							continue;

						StringTokenizer token = new StringTokenizer(line, "=");
						if (token.countTokens() < 2) {
							logger.error("Error on line: {}; file {}", line, file.getName());
							continue;
						}

						String name = token.nextToken();
						StringBuilder value = new StringBuilder(token.nextToken());

						while (token.hasMoreTokens()) {
							value.append("=").append(token.nextToken());
						}
						_strings.get(lang).put(name, value.toString());
					}
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}
			logger.info("Load strings: {} for lang: {}", _strings.get(lang).size(), lang);
		}
	}

	public void reload() {
		clear();
		load();
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

	public static StringsHolder getInstance() {
		return _instance;
	}
}

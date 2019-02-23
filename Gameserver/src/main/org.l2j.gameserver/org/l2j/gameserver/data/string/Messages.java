package org.l2j.gameserver.data.string;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
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
public final class Messages extends AbstractHolder {

	private static final Messages _instance = new Messages();

	private final IntObjectMap<Map<String, String>> resources = new HashIntObjectMap<>();

	private Messages() {
		//
	}

	public String getMessage(Player player, String name)
	{
		Language lang = player == null ? Config.DEFAULT_LANG : player.getLanguage();
		return getMessage(name, lang);
	}

	public String getMessage(String address, Language lang) {
		Map<String, String> strings = resources.get(lang.getId());
		String value = strings == null ? null : strings.get(address);
		if(isNull(value)) {
			strings = resources.get(Config.DEFAULT_LANG.getId());
			value = strings == null ? null : strings.get(address);
		}
		return value;
	}

	public void load() {
		for(Language lang : Language.VALUES) {

			if(!Config.AVAILABLE_LANGUAGES.contains(lang))
				continue;

			resources.put(lang.getId(), new HashMap<>());

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
						resources.get(lang.getId()).put(name, value.toString());
					}
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}
			logger.info("Load strings: {} for lang: {}", resources.get(lang.getId()).size(), lang);
		}
	}

	public void reload() {
		clear();
		load();
	}

	@Override
	public int size()
	{
		return resources.size();
	}

	@Override
	public void clear()
	{
		resources.clear();
	}

	public static Messages getInstance() {
		return _instance;
	}
}

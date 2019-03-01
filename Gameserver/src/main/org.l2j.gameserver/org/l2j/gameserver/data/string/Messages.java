package org.l2j.gameserver.data.string;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.utils.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Author: VISTALL
 * Date:  19:27/29.12.2010
 */
public final class Messages  {

	private static final Logger LOGGER = LoggerFactory.getLogger(Messages.class);
	private static final Messages INSTANCE = new Messages();

	private final IntObjectMap<Map<String, String>> resources = new HashIntObjectMap<>();

	private Messages() {
		//
	}

	public String getMessage(Player player, String key) {
		var lang = isNull(player) ? Language.ENGLISH : player.getLanguage();
		return getMessage(key, lang);
	}

	public String getMessage(String key, Language lang) {
		var resource = resources.get(lang.getId());
		String value = isNull(resource) ? null : resource.get(key);
		if(isNull(value)) {
			resource = resources.get(Language.ENGLISH.getId());
			value = isNull(resource) ? null : resource.get(key);
		}
		return value;
	}

	public void load() {
		var serverSettings = getSettings(ServerSettings.class);
		for(Language lang : serverSettings.availableLanguages()) {
			var filePath = serverSettings.dataPackRootPath().resolve(String.format("data/string/strings/%s.properties", lang.getShortName()));
			if(Files.notExists(filePath)) {
				if(lang == Language.ENGLISH) {
					LOGGER.warn("Not find file: {}", filePath);
				}
			} else {
				resources.put(lang.getId(), new HashMap<>());
				try {
					for(var line : Files.readAllLines(filePath)) {
						if (line.startsWith("#"))
							continue;

						StringTokenizer token = new StringTokenizer(line, "=");
						if (token.countTokens() < 2) {
							LOGGER.error("Error on line: {}; file {}", line, filePath);
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
					LOGGER.error(e.getLocalizedMessage(), e);
				}
			}
			LOGGER.info("Load strings: {} for lang: {}", resources.get(lang.getId()).size(), lang);
		}
	}

	public void reload() {
		resources.clear();
		load();
	}

	public static Messages getInstance() {
		return INSTANCE;
	}
}

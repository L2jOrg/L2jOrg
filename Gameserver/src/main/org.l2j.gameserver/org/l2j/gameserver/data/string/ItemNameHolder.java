package org.l2j.gameserver.data.string;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.utils.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.util.StringTokenizer;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Bonux
 */
public final class ItemNameHolder {

	private static final Logger LOGGER = LoggerFactory.getLogger(ItemNameHolder.class);
	private static final ItemNameHolder INSTANCE = new ItemNameHolder();

	private final IntObjectMap<IntObjectMap<String>> resources = new HashIntObjectMap<>();

	private ItemNameHolder() {
		//
	}

	public String getItemName(Language lang, int itemId) {
		IntObjectMap<String> itemNames = resources.get(lang.getId());
		String name = itemNames.get(itemId);
		if(isNull(name) && lang != Language.ENGLISH) {
			itemNames = resources.get(Language.ENGLISH.getId());
			name = itemNames.get(itemId);
		}
		return name;
	}

	public String getItemName(Player player, int itemId) {
		Language lang = isNull(player) ? Language.ENGLISH: player.getLanguage();
		return getItemName(lang, itemId);
	}

	public void load() {
		var serverSettings = getSettings(ServerSettings.class);
		for(Language lang : serverSettings.availableLanguages()) {

			var filePath = serverSettings.dataPackRootPath().resolve(String.format("data/string/itemname/%s.txt", lang.getShortName()));
			if(Files.notExists(filePath)) {
				if(lang == Language.ENGLISH) {
					LOGGER.warn("Not find file: {}", filePath);
				}
			} else {
				resources.put(lang.getId(), new HashIntObjectMap<>());
				try (LineNumberReader reader = new LineNumberReader(new FileReader(filePath.toFile()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						StringTokenizer token = new StringTokenizer(line, "\t");
						if (token.countTokens() < 2) {
							LOGGER.error("Error on line: {}; file {}", line, filePath);
							continue;
						}

						int id = Integer.parseInt(token.nextToken());
						String value = token.nextToken();

						resources.get(lang.getId()).put(id, value);
					}
				} catch (Exception e) {
					LOGGER.error(e.getLocalizedMessage(), e);
				}
			}
			LOGGER.info("Loaded item names: {} for lang: {}", resources.get(lang.getId()).size(), lang);
		}
	}

	public void reload() {
		resources.clear();
		load();
	}

	public static ItemNameHolder getInstance() {
		return INSTANCE;
	}
}
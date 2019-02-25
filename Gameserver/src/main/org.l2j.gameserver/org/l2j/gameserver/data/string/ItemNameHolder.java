package org.l2j.gameserver.data.string;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.utils.Language;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Bonux
 */
public final class ItemNameHolder extends AbstractHolder {
	private static final ItemNameHolder INSTANCE = new ItemNameHolder();

	private final Map<Language, IntObjectMap<String>> _itemNames = new HashMap<>();

	private ItemNameHolder() {
		//
	}

	public String getItemName(Language lang, int itemId) {
		IntObjectMap<String> itemNames = _itemNames.get(lang);
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
		Language lang = isNull(player) ? Language.ENGLISH: player.getLanguage();
		return getItemName(lang, itemId);
	}

	public void load() {
		var serverSettings = getSettings(ServerSettings.class);
		for(Language lang : Language.VALUES) {

			var filePath = serverSettings.dataPackRootPath().resolve("data/string/itemname/" + lang.getShortName() + ".txt");
			if(Files.notExists(filePath)) {
				if(lang == Language.ENGLISH) {
					logger.warn("Not find file: {}", filePath);
				}
			}
			else
			{
				_itemNames.put(lang, new HashIntObjectMap<>());
				try (LineNumberReader reader = new LineNumberReader(new FileReader(filePath.toFile()))) {
					String line = null;
					while ((line = reader.readLine()) != null) {
						StringTokenizer token = new StringTokenizer(line, "\t");
						if (token.countTokens() < 2) {
							logger.error("Error on line: {}; file {}", line, filePath);
							continue;
						}

						int id = Integer.parseInt(token.nextToken());
						String value = token.nextToken();

						_itemNames.get(lang).put(id, value);
					}
				} catch (Exception e) {
					logger.error("Exception: " + e, e);
				}
				//
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
		for(Map.Entry<Language, IntObjectMap<String>> entry : _itemNames.entrySet())
		{
			if(!getSettings(ServerSettings.class).availableLanguages().contains(entry.getKey()))
				continue;

			logger.info("load item names: " + entry.getValue().size() + " for lang: " + entry.getKey());
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

	public static ItemNameHolder getInstance() {
		return INSTANCE;
	}
}
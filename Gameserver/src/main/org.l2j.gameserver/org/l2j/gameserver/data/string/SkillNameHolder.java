package org.l2j.gameserver.data.string;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.utils.Language;
import org.l2j.gameserver.utils.SkillUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.LineNumberReader;
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
public final class SkillNameHolder {

	private static final Logger logger = LoggerFactory.getLogger(SkillNameHolder.class);
	private final Map<Language, IntObjectMap<String>> skillNames = new HashMap<>();

	private static final SkillNameHolder INSTANCE = new SkillNameHolder();

	private SkillNameHolder() {
		//
	}

	public String getSkillName(Language lang, int hashCode) {
		IntObjectMap<String> skillNames = this.skillNames.get(lang);
		String name = skillNames.get(hashCode);
		if(isNull(name) && lang != Language.ENGLISH) {
			skillNames = this.skillNames.get(Language.ENGLISH);
			name = skillNames.get(hashCode);
		}
		return name;
	}

	public String getSkillName(Player player, int hashCode) {
		Language lang = isNull(player) ? Language.ENGLISH : player.getLanguage();
		return getSkillName(lang, hashCode);
	}

	public String getSkillName(Player player, Skill skill) {
		return getSkillName(player, skill.hashCode());
	}

	public void load() {
		var serverSettings = getSettings(ServerSettings.class);
		for(Language lang : serverSettings.availableLanguages()) {
			var file = serverSettings.dataPackRootPath().resolve(String.format("data/string/skillname/%s.txt", lang.getShortName()));
			if(Files.notExists(file)) {
				if(lang == Language.ENGLISH) {
					logger.warn("Not find file: {}", file);
				}
			} else {
				skillNames.put(lang, new HashIntObjectMap<>());
				try (LineNumberReader reader = new LineNumberReader(new FileReader(file.toFile()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						StringTokenizer token = new StringTokenizer(line, "\t");
						if (token.countTokens() < 2) {
							logger.error("Error on line: {}; file: {}", line, file);
							continue;
						}

						int id = Integer.parseInt(token.nextToken());
						int level = Integer.parseInt(token.nextToken());
						int hashCode = SkillUtils.generateSkillHashCode(id, level);
						String value = token.hasMoreTokens() ? token.nextToken() : "";

						skillNames.get(lang).put(hashCode, value);
					}
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}
			logger.info("load skill names: {} for lang: {}", skillNames.get(lang).size(), lang);
		}
	}

	public void reload() {
		skillNames.clear();
		load();
	}

	public static SkillNameHolder getInstance() {
		return INSTANCE;
	}
}

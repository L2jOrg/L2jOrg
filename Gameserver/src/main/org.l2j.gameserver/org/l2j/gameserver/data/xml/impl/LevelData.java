/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author JoeAlisson
 */
public final class LevelData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(LevelData.class);

    private byte maxLevel = 90;
    private final IntMap<LevelInfo> levelInfos = new HashIntMap<>();

    private LevelData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/level-data.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/level-data.xml");
        LOGGER.info("Max Player Level is: {}", maxLevel);
        LOGGER.info("Loaded {} levels info", levelInfos.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        var list = doc.getFirstChild();
        maxLevel = parseByte(list.getAttributes(),"max-level");
        forEach(list, "level-data", node -> {
            var attr = node.getAttributes();
            var level = parseInt(attr, "level");
            levelInfos.put(level, new LevelInfo(parseShort(attr,"characteristic-points"), parseLong(attr, "experience"), parseFloat(attr,"xp-percent-lost")));
        });
        var maxInfo = levelInfos.keySet().stream().max().orElse(1);
        if(maxInfo < maxLevel) {
            maxLevel = (byte) maxInfo;
            LOGGER.warn("Adjusting maxLevel to max level info {}", maxLevel);
        }
    }

    public short getCharacteristicPoints(int level) {
        return levelInfos.get(min(level, maxLevel)).characteristicPoints;
    }

    public long getExpForLevel(int level) {
        return levelInfos.get(max(1, min(level, maxLevel + 1))).experience;
    }

    public float getXpPercentLost(int level) {
        return levelInfos.get(min(level, maxLevel)).expPercentLost;
    }

    public long getMaxExp() {
        return levelInfos.get(91).experience;
    }

    public byte getMaxLevel() {
        return maxLevel;
    }

    public static void init() {
        getInstance().load();
    }

    public static LevelData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final LevelData INSTANCE = new LevelData();
    }

    private static class LevelInfo {
        private final short characteristicPoints;
        private final long experience;
        private final float expPercentLost;

        public LevelInfo(short characteristicPoints, long experience, float expPercentLost) {
            this.characteristicPoints = characteristicPoints;
            this.experience = experience;
            this.expPercentLost = expPercentLost;
        }
    }
}

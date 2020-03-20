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

import static java.lang.Math.min;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author JoeAlisson
 */
public final class LevelData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(LevelData.class);

    private byte maxLevel = 86;
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
    }

    @Override
    public void parseDocument(Document doc, File f) {
        var list = doc.getFirstChild();
        maxLevel = (byte) (parseByte(list.getAttributes(),"max-level") + 1);
        forEach(list, "level-data", node -> {
            var attr = node.getAttributes();
            var level = parseInt(attr, "level");
            levelInfos.put(level, new LevelInfo(parseShort(attr,"characteristic-points"), parseLong(attr, "experience"), parseFloat(attr,"xp-percent-lost")));
        });
    }

    public short getCharacteristicPoints(int level) {
        return levelInfos.get(min(level, maxLevel)).characteristicPoints;
    }

    public long getExpForLevel(int level) {
        return levelInfos.get(min(level, maxLevel)).experience;
    }

    public float getXpPercentLost(int level) {
        return levelInfos.get(min(level, maxLevel)).expPercentLost;
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
        private short characteristicPoints;
        private long experience;
        private float expPercentLost;

        public LevelInfo(short characteristicPoints, long experience, float expPercentLost) {
            this.characteristicPoints = characteristicPoints;
            this.experience = experience;
            this.expPercentLost = expPercentLost;
        }
    }
}

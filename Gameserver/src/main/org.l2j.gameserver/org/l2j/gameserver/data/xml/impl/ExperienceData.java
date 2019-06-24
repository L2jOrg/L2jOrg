package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.min;
import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * This class holds the Experience points for each level for players and pets.
 *
 * @author mrTJO
 */
public final class ExperienceData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExperienceData.class);

    private final Map<Integer, Long> _expTable = new HashMap<>();
    private final Map<Integer, Double> _traningRateTable = new HashMap<>();

    private byte MAX_LEVEL;
    private byte MAX_PET_LEVEL;

    private ExperienceData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/experience.xsd");
    }

    @Override
    public void load() {
        _expTable.clear();
        _traningRateTable.clear();
        parseDatapackFile("data/stats/experience.xml");
        LOGGER.info("Loaded {} levels", _expTable.size());
        LOGGER.info("Max Player Level is: " + (MAX_LEVEL - 1));
        LOGGER.info("Max Pet Level is: " + (MAX_PET_LEVEL - 1));
    }

    @Override
    public void parseDocument(Document doc, File f) {
        final Node table = doc.getFirstChild();
        final NamedNodeMap tableAttr = table.getAttributes();

        MAX_LEVEL = (byte) (Byte.parseByte(tableAttr.getNamedItem("maxLevel").getNodeValue()) + 1);
        MAX_PET_LEVEL = (byte) (Byte.parseByte(tableAttr.getNamedItem("maxPetLevel").getNodeValue()) + 1);

        if (MAX_PET_LEVEL > MAX_LEVEL) {
            MAX_PET_LEVEL = MAX_LEVEL; // Pet level should not exceed owner level.
        }

        int maxLevel;
        for (Node n = table.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("experience".equals(n.getNodeName())) {
                final NamedNodeMap attrs = n.getAttributes();
                maxLevel = parseInteger(attrs, "level");
                if (maxLevel > MAX_LEVEL) {
                    break;
                }
                _expTable.put(maxLevel, parseLong(attrs, "tolevel"));
                _traningRateTable.put(maxLevel, parseDouble(attrs, "trainingRate"));
            }
        }
    }

    /**
     * Gets the exp for level.
     *
     * @param level the level required.
     * @return the experience points required to reach the given level.
     */
    public long getExpForLevel(int level) {
        return _expTable.get(min(level, MAX_LEVEL));
    }

    public double getTrainingRate(int level) {
        return _traningRateTable.get(min(level, MAX_LEVEL));
    }

    /**
     * Gets the max level.
     *
     * @return the maximum level acquirable by a player.
     */
    public byte getMaxLevel() {
        return MAX_LEVEL;
    }

    /**
     * Gets the max pet level.
     *
     * @return the maximum level acquirable by a pet.
     */
    public byte getMaxPetLevel() {
        return MAX_PET_LEVEL;
    }

    public static ExperienceData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ExperienceData INSTANCE = new ExperienceData();
    }
}

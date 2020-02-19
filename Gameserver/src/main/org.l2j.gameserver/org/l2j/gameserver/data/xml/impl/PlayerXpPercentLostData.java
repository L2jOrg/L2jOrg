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
import java.util.Arrays;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * This class holds the Player Xp Percent Lost Data for each level for players.
 *
 * @author Zealar
 */
public final class PlayerXpPercentLostData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerXpPercentLostData.class);

    private final int _maxlevel = ExperienceData.getInstance().getMaxLevel();
    private final double[] _playerXpPercentLost = new double[_maxlevel + 1];

    private PlayerXpPercentLostData() {
        Arrays.fill(_playerXpPercentLost, 1.);
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/playerXpPercentLost.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/stats/chars/playerXpPercentLost.xml");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("xpLost".equalsIgnoreCase(d.getNodeName())) {
                        final NamedNodeMap attrs = d.getAttributes();
                        final Integer level = parseInteger(attrs, "level");
                        if (level > _maxlevel) {
                            break;
                        }
                        _playerXpPercentLost[level] = parseDouble(attrs, "val");
                    }
                }
            }
        }
    }

    public double getXpPercent(int level) {
        if (level > _maxlevel) {
            LOGGER.warn("Require to high level inside PlayerXpPercentLostData (" + level + ")");
            return _playerXpPercentLost[_maxlevel];
        }
        return _playerXpPercentLost[level];
    }

    public static PlayerXpPercentLostData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PlayerXpPercentLostData INSTANCE = new PlayerXpPercentLostData();
    }
}

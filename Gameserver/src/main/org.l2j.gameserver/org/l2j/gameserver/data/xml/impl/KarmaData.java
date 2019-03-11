package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.util.IGameXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author UnAfraid
 */
public class KarmaData implements IGameXmlReader {
    private static final Logger LOGGER = Logger.getLogger(KarmaData.class.getName());

    private final Map<Integer, Double> _karmaTable = new HashMap<>();

    public KarmaData() {
        load();
    }

    /**
     * Gets the single instance of KarmaData.
     *
     * @return single instance of KarmaData
     */
    public static KarmaData getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public synchronized void load() {
        _karmaTable.clear();
        parseDatapackFile("data/stats/chars/pcKarmaIncrease.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _karmaTable.size() + " karma modifiers.");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("pcKarmaIncrease".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("increase".equalsIgnoreCase(d.getNodeName())) {
                        final NamedNodeMap attrs = d.getAttributes();
                        final int level = parseInteger(attrs, "lvl");
                        if (level >= Config.PLAYER_MAXIMUM_LEVEL) {
                            break;
                        }
                        _karmaTable.put(level, parseDouble(attrs, "val"));
                    }
                }
            }
        }
    }

    /**
     * @param level
     * @return {@code double} modifier used to calculate karma lost upon death.
     */
    public double getMultiplier(int level) {
        return _karmaTable.get(level);
    }

    private static class SingletonHolder {
        protected static final KarmaData _instance = new KarmaData();
    }
}

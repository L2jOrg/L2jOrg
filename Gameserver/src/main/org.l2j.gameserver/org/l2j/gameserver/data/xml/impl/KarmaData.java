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

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * @author UnAfraid
 */
public class KarmaData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(KarmaData.class);

    private final Map<Integer, Double> _karmaTable = new HashMap<>();

    private KarmaData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/pcKarmaIncrease.xsd");
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

    public static KarmaData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final KarmaData INSTANCE = new KarmaData();
    }
}

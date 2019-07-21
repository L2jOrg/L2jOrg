package org.l2j.gameserver.data.xml.impl;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.options.EnchantOptions;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.isInteger;

/**
 * @author UnAfraid
 */
public class EnchantItemOptionsData extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantItemOptionsData.class);

    private final Map<Integer, Map<Integer, EnchantOptions>> _data = new HashMap<>();

    private EnchantItemOptionsData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/EnchantItemOptions.xsd");
    }

    @Override
    public synchronized void load() {
        _data.clear();
        parseDatapackFile("data/EnchantItemOptions.xml");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        int counter = 0;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("item".equalsIgnoreCase(d.getNodeName())) {
                        final int itemId = parseInteger(d.getAttributes(), "id");
                        if (!_data.containsKey(itemId)) {
                            _data.put(itemId, new HashMap<>());
                        }
                        for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                            if ("options".equalsIgnoreCase(cd.getNodeName())) {
                                final EnchantOptions op = new EnchantOptions(parseInteger(cd.getAttributes(), "level"));
                                _data.get(itemId).put(op.getLevel(), op);

                                for (byte i = 0; i < 3; i++) {
                                    final Node att = cd.getAttributes().getNamedItem("option" + (i + 1));
                                    if ((att != null) && isInteger(att.getNodeValue())) {
                                        op.setOption(i, parseInteger(att));
                                    }
                                }
                                counter++;
                            }
                        }
                    }
                }
            }
        }
        LOGGER.info("Loaded: {} Items and {} Options.", _data.size(), counter);
    }

    /**
     * @param itemId
     * @param enchantLevel
     * @return enchant effects information.
     */
    public EnchantOptions getOptions(int itemId, int enchantLevel) {
        if (!_data.containsKey(itemId) || !_data.get(itemId).containsKey(enchantLevel)) {
            return null;
        }
        return _data.get(itemId).get(enchantLevel);
    }

    /**
     * @param item
     * @return enchant effects information.
     */
    public EnchantOptions getOptions(Item item) {
        return item != null ? getOptions(item.getId(), item.getEnchantLevel()) : null;
    }


    public static EnchantItemOptionsData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final EnchantItemOptionsData INSTANCE = new EnchantItemOptionsData();
    }
}

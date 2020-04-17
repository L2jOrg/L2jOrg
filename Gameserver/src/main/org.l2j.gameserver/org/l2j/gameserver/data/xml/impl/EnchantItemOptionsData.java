package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.options.EnchantOptions;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.computeIfNonNull;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class EnchantItemOptionsData extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantItemOptionsData.class);

    private final IntMap<IntMap<EnchantOptions>> data = new HashIntMap<>();

    private EnchantItemOptionsData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/EnchantItemOptions.xsd");
    }

    @Override
    public synchronized void load() {
        data.clear();
        parseDatapackFile("data/EnchantItemOptions.xml");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", list -> forEach(list, "item", itemNode -> forEach(itemNode, "options", optionsNode -> {
                var attr = optionsNode.getAttributes();
                var option = new EnchantOptions(parseInt(attr, "level"));
                for (byte i = 0; i < 3 ; i++) {
                    option.setOption(i, parseInt(attr, "option" + (i+1)));
                }
                data.computeIfAbsent(parseInt(itemNode.getAttributes(), "id"), id -> new HashIntMap<>()).put(option.getLevel(), option);
            })
        ));
        LOGGER.info("Loaded {} Option Items.", data.size());
    }

    /**
     * @param itemId
     * @param enchantLevel
     * @return enchant effects information.
     */
    public EnchantOptions getOptions(int itemId, int enchantLevel) {
        if (!data.containsKey(itemId) || !data.get(itemId).containsKey(enchantLevel)) {
            return null;
        }
        return data.get(itemId).get(enchantLevel);
    }

    /**
     * @param item
     * @return enchant effects information.
     */
    public EnchantOptions getOptions(Item item) {
        return computeIfNonNull(item, i -> getOptions(i.getId(), i.getEnchantLevel()));
    }

    public static void init() {
        getInstance().load();
    }

    public static EnchantItemOptionsData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final EnchantItemOptionsData INSTANCE = new EnchantItemOptionsData();
    }
}

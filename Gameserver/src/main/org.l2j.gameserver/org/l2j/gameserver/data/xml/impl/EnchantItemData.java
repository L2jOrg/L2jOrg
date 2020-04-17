package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.items.enchant.EnchantScroll;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Loads item enchant data.
 *
 * @author UnAfraid
 * @author JoeAlisson
 */
public class EnchantItemData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantItemData.class);

    private final IntMap<EnchantScroll> scrolls = new HashIntMap<>();

    private EnchantItemData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/EnchantItemData.xsd");
    }

    @Override
    public synchronized void load() {
        scrolls.clear();
        parseDatapackFile("data/EnchantItemData.xml");
        LOGGER.info("Loaded {} Enchant Scrolls.", scrolls.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "scroll", enchant -> {
            var attr = enchant.getAttributes();
            var id = parseInt(attr, "id");
            var grade = parseEnum(attr, CrystalType.class, "grade");
            var maxEnchant = parseInt(attr, "max-enchant");
            var group = parseInt(attr, "group");
            try {
                var scroll = new EnchantScroll(id, grade, maxEnchant, group);
                forEach(enchant, "item", item -> {
                    var _attr = item.getAttributes();
                    var _id = parseInt(_attr, "id");
                    scroll.addItem(_id);
                });
                scrolls.put(id, scroll);
            } catch (NullPointerException e) {
                LOGGER.warn("Unexistent enchant scroll:{} defined in enchant data!", id);
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Wrong enchant scroll item type: {} defined in enchant data!", id);
            }
        }));
    }

    /**
     * Gets the enchant scroll.
     *
     * @param scroll the scroll
     * @return enchant template for scroll
     */
    public final EnchantScroll getEnchantScroll(Item scroll) {
        return scrolls.get(scroll.getId());
    }

    public static void init() {
        getInstance().load();
    }

    public static EnchantItemData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final EnchantItemData INSTANCE = new EnchantItemData();
    }
}

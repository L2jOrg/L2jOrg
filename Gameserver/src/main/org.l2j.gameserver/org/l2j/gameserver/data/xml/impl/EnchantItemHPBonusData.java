package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * This class holds the Enchant HP Bonus Data.
 *
 * @author MrPoke, Zoey76
 */
public class EnchantItemHPBonusData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantItemHPBonusData.class);
    private static final float FULL_ARMOR_MODIFIER = 1.5f; // TODO: Move it to config!
    private final Map<CrystalType, List<Integer>> _armorHPBonuses = new EnumMap<>(CrystalType.class);

    /**
     * Instantiates a new enchant hp bonus data.
     */
    private EnchantItemHPBonusData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/enchantHPBonus.xsd");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("enchantHP".equalsIgnoreCase(d.getNodeName())) {
                        final List<Integer> bonuses = new ArrayList<>(12);
                        for (Node e = d.getFirstChild(); e != null; e = e.getNextSibling()) {
                            if ("bonus".equalsIgnoreCase(e.getNodeName())) {
                                bonuses.add(Integer.parseInt(e.getTextContent()));
                            }
                        }
                        _armorHPBonuses.put(parseEnum(d.getAttributes(), CrystalType.class, "grade"), bonuses);
                    }
                }
            }
        }
    }

    @Override
    public void load() {
        _armorHPBonuses.clear();
        parseDatapackFile("data/stats/enchantHPBonus.xml");
        LOGGER.info("Loaded {} Enchant HP Bonuses.", _armorHPBonuses.size());
    }

    /**
     * Gets the HP bonus.
     *
     * @param item the item
     * @return the HP bonus
     */
    public final int getHPBonus(Item item) {
        final List<Integer> values = _armorHPBonuses.get(item.getTemplate().getCrystalType());
        if ((values == null) || values.isEmpty() || (item.getOlyEnchantLevel() <= 0)) {
            return 0;
        }

        final int bonus = values.get(Math.min(item.getOlyEnchantLevel(), values.size()) - 1);
        if (item.getBodyPart() == BodyPart.FULL_ARMOR) {
            return (int) (bonus * FULL_ARMOR_MODIFIER);
        }
        return bonus;
    }

    public static EnchantItemHPBonusData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final EnchantItemHPBonusData INSTANCE = new EnchantItemHPBonusData();
    }
}

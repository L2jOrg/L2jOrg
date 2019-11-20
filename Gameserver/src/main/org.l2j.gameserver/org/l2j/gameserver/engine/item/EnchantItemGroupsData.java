package org.l2j.gameserver.engine.item;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.holders.RangeChanceHolder;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.enchant.EnchantItemGroup;
import org.l2j.gameserver.model.items.enchant.EnchantRateItem;
import org.l2j.gameserver.model.items.enchant.EnchantScrollGroup;
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

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class EnchantItemGroupsData extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantItemGroupsData.class);

    private final Map<String, EnchantItemGroup> itemGroups = new HashMap<>();
    private final IntMap<EnchantScrollGroup> scrollGroups = new HashIntMap<>();

    private EnchantItemGroupsData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/EnchantItemGroups.xsd");
    }

    @Override
    public synchronized void load() {
        itemGroups.clear();
        scrollGroups.clear();
        parseDatapackFile("data/EnchantItemGroups.xml");
        LOGGER.info("Loaded {} item group templates.", itemGroups.size());
        LOGGER.info("Loaded {} scroll group templates.", scrollGroups.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> {
            for (Node d = listNode.getFirstChild(); d != null; d = d.getNextSibling()) {
                switch (d.getNodeName()) {
                    case "enchantRateGroup" -> parseEnchantRateGroup(d);
                    case "enchantScrollGroup" -> parseEnchantScrollGroup(d);
                }
            }
        });
    }

    private void parseEnchantScrollGroup(Node node) {
        final int id = parseInteger(node.getAttributes(), "id");
        final EnchantScrollGroup group = new EnchantScrollGroup(id);

        forEach(node, "enchantRate", rate -> {
            var rateGroup = new EnchantRateItem(parseString(rate.getAttributes(), "group"));
            forEach(rate, "item", item -> {
                var attrs = item.getAttributes();
                rateGroup.addSlot(parseEnum(attrs, BodyPart.class, "slot").getId());

                if (attrs.getNamedItem("magicWeapon") != null) {
                    rateGroup.setMagicWeapon(parseBoolean(attrs, "magicWeapon"));
                }
                if (attrs.getNamedItem("id") != null) {
                    rateGroup.setItemId(parseInteger(attrs, "id"));
                }
            });
            group.addRateGroup(rateGroup);
        });
        scrollGroups.put(id, group);

    }

    private void parseEnchantRateGroup(Node node) {
        final String name = parseString(node.getAttributes(), "name");
        final EnchantItemGroup group = new EnchantItemGroup(name);

        forEach(node, "enchant", enchant -> {
            var attr = enchant.getAttributes();
            var min  = parseInt(attr, "from");
            var max = parseInt(attr, "until");
            double chance = parseDouble(attr, "chance");
            group.addChance(new RangeChanceHolder(min, max, chance));
        });
        itemGroups.put(name, group);
    }

    public EnchantItemGroup getItemGroup(ItemTemplate item, int scrollGroup) {
        final EnchantScrollGroup group = scrollGroups.get(scrollGroup);
        final EnchantRateItem rateGroup = group.getRateGroup(item);
        return nonNull(rateGroup) ? itemGroups.get(rateGroup.getName()) : null;
    }

    public EnchantItemGroup getItemGroup(String name) {
        return itemGroups.get(name);
    }

    public EnchantScrollGroup getScrollGroup(int id) {
        return scrollGroups.get(id);
    }

    public static void init() {
        getInstance().load();
    }

    public static EnchantItemGroupsData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final EnchantItemGroupsData INSTANCE = new EnchantItemGroupsData();
    }
}

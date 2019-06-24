package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.holders.RangeChanceHolder;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.items.enchant.EnchantItemGroup;
import org.l2j.gameserver.model.items.enchant.EnchantRateItem;
import org.l2j.gameserver.model.items.enchant.EnchantScrollGroup;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.util.GameUtils;
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
public final class EnchantItemGroupsData extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantItemGroupsData.class);

    private final Map<String, EnchantItemGroup> _itemGroups = new HashMap<>();
    private final Map<Integer, EnchantScrollGroup> _scrollGroups = new HashMap<>();

    private EnchantItemGroupsData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/EnchantItemGroups.xsd");
    }

    @Override
    public synchronized void load() {
        _itemGroups.clear();
        _scrollGroups.clear();
        parseDatapackFile("data/EnchantItemGroups.xml");
        LOGGER.info("Loaded {} item group templates.", _itemGroups.size());
        LOGGER.info("Loaded {} scroll group templates.", _scrollGroups.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("enchantRateGroup".equalsIgnoreCase(d.getNodeName())) {
                        final String name = parseString(d.getAttributes(), "name");
                        final EnchantItemGroup group = new EnchantItemGroup(name);
                        for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                            if ("current".equalsIgnoreCase(cd.getNodeName())) {
                                final String range = parseString(cd.getAttributes(), "enchant");
                                final double chance = parseDouble(cd.getAttributes(), "chance");
                                int min = -1;
                                int max = 0;
                                if (range.contains("-")) {
                                    final String[] split = range.split("-");
                                    if ((split.length == 2) && GameUtils.isDigit(split[0]) && GameUtils.isDigit(split[1])) {
                                        min = Integer.parseInt(split[0]);
                                        max = Integer.parseInt(split[1]);
                                    }
                                } else if (GameUtils.isDigit(range)) {
                                    min = Integer.parseInt(range);
                                    max = min;
                                }
                                if ((min > -1) && (max > 0)) {
                                    group.addChance(new RangeChanceHolder(min, max, chance));
                                }
                            }
                        }
                        _itemGroups.put(name, group);
                    } else if ("enchantScrollGroup".equals(d.getNodeName())) {
                        final int id = parseInteger(d.getAttributes(), "id");
                        final EnchantScrollGroup group = new EnchantScrollGroup(id);
                        for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                            if ("enchantRate".equalsIgnoreCase(cd.getNodeName())) {
                                final EnchantRateItem rateGroup = new EnchantRateItem(parseString(cd.getAttributes(), "group"));
                                for (Node z = cd.getFirstChild(); z != null; z = z.getNextSibling()) {
                                    if ("item".equals(z.getNodeName())) {
                                        final NamedNodeMap attrs = z.getAttributes();
                                        if (attrs.getNamedItem("slot") != null) {
                                            rateGroup.addSlot(ItemTable.SLOTS.get(parseString(attrs, "slot")));
                                        }
                                        if (attrs.getNamedItem("magicWeapon") != null) {
                                            rateGroup.setMagicWeapon(parseBoolean(attrs, "magicWeapon"));
                                        }
                                        if (attrs.getNamedItem("id") != null) {
                                            rateGroup.setItemId(parseInteger(attrs, "id"));
                                        }
                                    }
                                }
                                group.addRateGroup(rateGroup);
                            }
                        }
                        _scrollGroups.put(id, group);
                    }
                }
            }
        }
    }

    public EnchantItemGroup getItemGroup(L2Item item, int scrollGroup) {
        final EnchantScrollGroup group = _scrollGroups.get(scrollGroup);
        final EnchantRateItem rateGroup = group.getRateGroup(item);
        return rateGroup != null ? _itemGroups.get(rateGroup.getName()) : null;
    }

    public EnchantItemGroup getItemGroup(String name) {
        return _itemGroups.get(name);
    }

    public EnchantScrollGroup getScrollGroup(int id) {
        return _scrollGroups.get(id);
    }

    public static EnchantItemGroupsData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final EnchantItemGroupsData INSTANCE = new EnchantItemGroupsData();
    }
}

package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.L2ArmorSet;
import org.l2j.gameserver.model.holders.ArmorsetSkillHolder;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

/**
 * Loads armor set bonuses.
 *
 * @author godson, Luno, UnAfraid
 */
public final class ArmorSetsData extends IGameXmlReader{
    private static final Logger LOGGER = LoggerFactory.getLogger(ArmorSetsData.class);

    private final Map<Integer, L2ArmorSet> _armorSets = new HashMap<>();
    private final Map<Integer, List<L2ArmorSet>> _armorSetItems = new HashMap<>();

    private ArmorSetsData() {
        load();
    }

    @Override
    public void load() {
        _armorSets.clear();
        parseDatapackDirectory("data/stats/armorsets", false);
        LOGGER.info("Loaded {} Armor sets.", _armorSets.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node setNode = n.getFirstChild(); setNode != null; setNode = setNode.getNextSibling()) {
                    if ("set".equalsIgnoreCase(setNode.getNodeName())) {
                        final int id = parseInteger(setNode.getAttributes(), "id");
                        final int minimumPieces = parseInteger(setNode.getAttributes(), "minimumPieces", 0);
                        final boolean isVisual = parseBoolean(setNode.getAttributes(), "visual", false);
                        final L2ArmorSet set = new L2ArmorSet(id, minimumPieces, isVisual);
                        if (_armorSets.putIfAbsent(id, set) != null) {
                            LOGGER.warn("Duplicate set entry with id: " + id + " in file: " + f.getName());
                        }
                        for (Node innerSetNode = setNode.getFirstChild(); innerSetNode != null; innerSetNode = innerSetNode.getNextSibling()) {
                            switch (innerSetNode.getNodeName()) {
                                case "requiredItems": {
                                    forEach(innerSetNode, b -> "item".equals(b.getNodeName()), node ->
                                    {
                                        final NamedNodeMap attrs = node.getAttributes();
                                        final int itemId = parseInteger(attrs, "id");
                                        final L2Item item = ItemTable.getInstance().getTemplate(itemId);
                                        if (item == null) {
                                            LOGGER.warn("Attempting to register non existing required item: " + itemId + " to a set: " + f.getName());
                                        } else if (!set.addRequiredItem(itemId)) {
                                            LOGGER.warn("Attempting to register duplicate required item " + item + " to a set: " + f.getName());
                                        }
                                    });
                                    break;
                                }
                                case "optionalItems": {
                                    forEach(innerSetNode, b -> "item".equals(b.getNodeName()), node ->
                                    {
                                        final NamedNodeMap attrs = node.getAttributes();
                                        final int itemId = parseInteger(attrs, "id");
                                        final L2Item item = ItemTable.getInstance().getTemplate(itemId);
                                        if (item == null) {
                                            LOGGER.warn("Attempting to register non existing optional item: " + itemId + " to a set: " + f.getName());
                                        } else if (!set.addOptionalItem(itemId)) {
                                            LOGGER.warn("Attempting to register duplicate optional item " + item + " to a set: " + f.getName());
                                        }
                                    });
                                    break;
                                }
                                case "skills": {
                                    forEach(innerSetNode, b -> "skill".equals(b.getNodeName()), node ->
                                    {
                                        final NamedNodeMap attrs = node.getAttributes();
                                        final int skillId = parseInteger(attrs, "id");
                                        final int skillLevel = parseInteger(attrs, "level");
                                        final int minPieces = parseInteger(attrs, "minimumPieces", set.getMinimumPieces());
                                        final int minEnchant = parseInteger(attrs, "minimumEnchant", 0);
                                        final boolean isOptional = parseBoolean(attrs, "optional", false);
                                        final int artifactSlotMask = parseInteger(attrs, "slotMask", 0);
                                        final int artifactBookSlot = parseInteger(attrs, "bookSlot", 0);
                                        set.addSkill(new ArmorsetSkillHolder(skillId, skillLevel, minPieces, minEnchant, isOptional, artifactSlotMask, artifactBookSlot));
                                    });
                                    break;
                                }
                                case "stats": {
                                    forEach(innerSetNode, b -> "stat".equals(b.getNodeName()), node ->
                                    {
                                        final NamedNodeMap attrs = node.getAttributes();
                                        set.addStatsBonus(parseEnum(attrs, BaseStats.class, "type"), parseInteger(attrs, "val"));
                                    });
                                    break;
                                }
                            }
                        }

                        Stream.concat(set.getRequiredItems().stream(), set.getOptionalItems().stream()).forEach(itemHolder -> _armorSetItems.computeIfAbsent(itemHolder, key -> new ArrayList<>()).add(set));
                    }
                }
            }
        }
    }

    /**
     * @param setId the set id that is attached to a set
     * @return the armor set associated to the given item id
     */
    public L2ArmorSet getSet(int setId) {
        return _armorSets.get(setId);
    }

    /**
     * @param itemId the item id that is attached to a set
     * @return the armor set associated to the given item id
     */
    public List<L2ArmorSet> getSets(int itemId) {
        return _armorSetItems.getOrDefault(itemId, Collections.emptyList());
    }

    public static ArmorSetsData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final ArmorSetsData INSTANCE = new ArmorSetsData();
    }
}

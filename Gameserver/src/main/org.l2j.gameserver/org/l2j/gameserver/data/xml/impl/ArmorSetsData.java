package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.L2ArmorSet;
import org.l2j.gameserver.model.holders.ArmorsetSkillHolder;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.util.IGameXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Loads armor set bonuses.
 *
 * @author godson, Luno, UnAfraid
 */
public final class ArmorSetsData implements IGameXmlReader {
    private static final Logger LOGGER = Logger.getLogger(ArmorSetsData.class.getName());

    private final Map<Integer, L2ArmorSet> _armorSets = new HashMap<>();
    private final Map<Integer, List<L2ArmorSet>> _armorSetItems = new HashMap<>();

    protected ArmorSetsData() {
        load();
    }

    /**
     * Gets the single instance of ArmorSetsData
     *
     * @return single instance of ArmorSetsData
     */
    public static ArmorSetsData getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void load() {
        _armorSets.clear();
        parseDatapackDirectory("data/stats/armorsets", false);
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _armorSets.size() + " Armor sets.");
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
                            LOGGER.warning("Duplicate set entry with id: " + id + " in file: " + f.getName());
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
                                            LOGGER.warning("Attempting to register non existing required item: " + itemId + " to a set: " + f.getName());
                                        } else if (!set.addRequiredItem(itemId)) {
                                            LOGGER.warning("Attempting to register duplicate required item " + item + " to a set: " + f.getName());
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
                                            LOGGER.warning("Attempting to register non existing optional item: " + itemId + " to a set: " + f.getName());
                                        } else if (!set.addOptionalItem(itemId)) {
                                            LOGGER.warning("Attempting to register duplicate optional item " + item + " to a set: " + f.getName());
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

    private static class SingletonHolder {
        protected static final ArmorSetsData _instance = new ArmorSetsData();
    }
}

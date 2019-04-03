package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.items.combination.CombinationItem;
import org.l2j.gameserver.model.items.combination.CombinationItemReward;
import org.l2j.gameserver.model.items.combination.CombinationItemType;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author UnAfraid
 */
public class CombinationItemsData extends IGameXmlReader{
    private static final Logger LOGGER = LoggerFactory.getLogger(CombinationItemsData.class);
    private final List<CombinationItem> _items = new ArrayList<>();

    private CombinationItemsData() {
        load();
    }

    @Override
    public synchronized void load() {
        _items.clear();
        parseDatapackFile("data/CombinationItems.xml");
        LOGGER.info("Loaded {} combinations", _items.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "item", itemNode ->
        {
            final CombinationItem item = new CombinationItem(new StatsSet(parseAttributes(itemNode)));

            forEach(itemNode, "reward", rewardNode ->
            {
                final int id = parseInteger(rewardNode.getAttributes(), "id");
                final int count = parseInteger(rewardNode.getAttributes(), "count", 1);
                final CombinationItemType type = parseEnum(rewardNode.getAttributes(), CombinationItemType.class, "type");
                item.addReward(new CombinationItemReward(id, count, type));
                if (ItemTable.getInstance().getTemplate(id) == null) {
                    LOGGER.info(getClass().getSimpleName() + ": Could not find item with id " + id);
                }
            });
            _items.add(item);
        }));
    }

    public List<CombinationItem> getItems() {
        return _items;
    }

    public CombinationItem getItemsBySlots(int firstSlot, int secondSlot) {
        return _items.stream().filter(item -> (item.getItemOne() == firstSlot) && (item.getItemTwo() == secondSlot)).findFirst().orElse(null);
    }

    public List<CombinationItem> getItemsByFirstSlot(int id) {
        return _items.stream().filter(item -> item.getItemOne() == id).collect(Collectors.toList());
    }

    public static CombinationItemsData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final CombinationItemsData INSTANCE = new CombinationItemsData();
    }
}
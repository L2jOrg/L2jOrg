package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.items.combination.CombinationItem;
import org.l2j.gameserver.model.items.combination.CombinationItemReward;
import org.l2j.gameserver.model.items.combination.CombinationItemType;
import org.l2j.gameserver.util.IGameXmlReader;
import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author UnAfraid
 */
public class CombinationItemsData implements IGameXmlReader {
    private static final Logger LOGGER = Logger.getLogger(CombinationItemsData.class.getName());
    private final List<CombinationItem> _items = new ArrayList<>();

    protected CombinationItemsData() {
        load();
    }

    public static final CombinationItemsData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public synchronized void load() {
        _items.clear();
        parseDatapackFile("data/CombinationItems.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _items.size() + " combinations.");
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

    public int getLoadedElementsCount() {
        return _items.size();
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

    public List<CombinationItem> getItemsBySecondSlot(int id) {
        return _items.stream().filter(item -> item.getItemTwo() == id).collect(Collectors.toList());
    }

    private static class SingletonHolder {
        protected static final CombinationItemsData INSTANCE = new CombinationItemsData();
    }
}
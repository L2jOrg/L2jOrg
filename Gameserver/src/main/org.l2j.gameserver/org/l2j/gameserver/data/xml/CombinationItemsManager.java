package org.l2j.gameserver.data.xml;

import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.items.combination.CombinationItem;
import org.l2j.gameserver.model.items.combination.CombinationItemReward;
import org.l2j.gameserver.model.items.combination.CombinationItemType;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author UnAfraid
 */
public class CombinationItemsManager extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombinationItemsManager.class);
    private final List<CombinationItem> items = new ArrayList<>();

    private CombinationItemsManager() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/combination-items.xsd");
    }

    @Override
    public synchronized void load() {
        items.clear();
        parseDatapackFile("data/combination-items.xml");
        LOGGER.info("Loaded {} combinations", items.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "item", itemNode -> {
            final CombinationItem item = new CombinationItem(new StatsSet(parseAttributes(itemNode)));

            forEach(itemNode, "reward", rewardNode -> {
                var attrs = rewardNode.getAttributes();
                final int id = parseInteger(attrs, "id");
                final int count = parseInteger(attrs, "count", 1);
                final CombinationItemType type = parseEnum(attrs, CombinationItemType.class, "type");

                item.addReward(new CombinationItemReward(id, count, type));
                if (ItemEngine.getInstance().getTemplate(id) == null) {
                    LOGGER.warn("Could not find item with id {}", id);
                }
            });
            items.add(item);
        }));
    }

    public CombinationItem getItemsBySlots(int firstSlot, int secondSlot) {
        return items.stream().filter(item -> (item.getItemOne() == firstSlot) && (item.getItemTwo() == secondSlot)).findFirst().orElse(null);
    }

    public List<CombinationItem> getItemsByFirstSlot(int id) {
        return items.stream().filter(item -> item.getItemOne() == id).collect(Collectors.toList());
    }

    public static void init() {
        getInstance().load();
    }

    public static CombinationItemsManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final CombinationItemsManager INSTANCE = new CombinationItemsManager();
    }
}
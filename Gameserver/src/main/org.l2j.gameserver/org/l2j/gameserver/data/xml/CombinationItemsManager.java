/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.data.xml;

import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.item.combination.CombinationItem;
import org.l2j.gameserver.model.item.combination.CombinationItemReward;
import org.l2j.gameserver.model.item.combination.CombinationItemType;
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
        releaseResources();
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
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
package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.CrystallizationType;
import org.l2j.gameserver.model.holders.CrystallizationDataHolder;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.CrystalType;
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
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.util.GameUtils.isArmor;
import static org.l2j.gameserver.util.GameUtils.isWeapon;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class ItemCrystallizationData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCrystallizationData.class);

    private final Map<CrystalType, Map<CrystallizationType, List<ItemChanceHolder>>> crystallizationTemplates = new EnumMap<>(CrystalType.class);
    private final IntMap<CrystallizationDataHolder> items = new HashIntMap<>();

    private ItemCrystallizationData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/crystallizable-items.xsd");
    }

    @Override
    public void load() {
        crystallizationTemplates.clear();
        CrystalType.forEach(c -> crystallizationTemplates.put(c, new EnumMap<>(CrystallizationType.class)));

        items.clear();
        parseDatapackFile("data/crystallizable-items.xml");
        LOGGER.info("Loaded {} crystallization templates.", crystallizationTemplates.size());
        LOGGER.info("Loaded {} pre-defined crystallizable items.", items.size());

        // Generate remaining data.
        generateCrystallizationData();
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> {
            for(Node node = listNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
                switch (node.getNodeName()) {
                    case "templates" -> forEach(node, "template", this::parseTemplate);
                    case "items" -> forEach(node, "item", this::parseItem);
                }
            }
        });
    }

    private void parseTemplate(Node node) {
        forEach(node, "template", templateNode -> {
            var attr = templateNode.getAttributes();
            var crystalType = parseEnum(attr, CrystalType.class, "crystalType");
            var crystallizationType = parseEnum(attr, CrystallizationType.class, "crystallizationType");

            crystallizationTemplates.get(crystalType).put(crystallizationType, parseRewards(templateNode));
        });
    }

    private List<ItemChanceHolder> parseRewards(Node templateNode) {
        final List<ItemChanceHolder> crystallizeRewards = new ArrayList<>();

        forEach(templateNode, "item", itemNode -> {
            var attrs = itemNode.getAttributes();
            var itemId = parseInteger(attrs, "id");
            var itemCount = parseLong(attrs, "count");
            var itemChance = parseDouble(attrs, "chance");
            crystallizeRewards.add(new ItemChanceHolder(itemId, itemChance, itemCount));
        });
        return crystallizeRewards;
    }

    private void parseItem(Node node) {
        forEach(node, "item", itemNode -> {
            final int id = parseInteger(itemNode.getAttributes(), "id");
            items.put(id, new CrystallizationDataHolder(id, parseRewards(itemNode)));
        });
    }

    private List<ItemChanceHolder> calculateCrystallizeRewards(ItemTemplate item, List<ItemChanceHolder> crystallizeRewards) {
        if (isNull(crystallizeRewards)) {
            return null;
        }

        final List<ItemChanceHolder> rewards = new ArrayList<>();

        for (ItemChanceHolder reward : crystallizeRewards) {
            double chance = reward.getChance() * item.getCrystalCount();
            long count = reward.getCount();

            if (chance > 100.) {
                double countMul = Math.ceil(chance / 100.);
                chance /= countMul;
                count *= countMul;
            }

            rewards.add(new ItemChanceHolder(reward.getId(), chance, count));
        }

        return rewards;
    }

    private void generateCrystallizationData() {
        final int previousCount = items.size();

        if(crystallizationTemplates.values().stream().flatMap(c -> c.values().stream()).anyMatch(Predicate.not(List::isEmpty))) {
            for (ItemTemplate item : ItemEngine.getInstance().getAllItems()) {
                // Check if the data has not been generated.
                if ((isWeapon(item) || isArmor(item)) && item.isCrystallizable() && !items.containsKey(item.getId())) {

                    final List<ItemChanceHolder> holder = crystallizationTemplates.get(item.getCrystalType()).get(isWeapon(item) ? CrystallizationType.WEAPON : CrystallizationType.ARMOR);

                    if (nonNull(holder)) {
                        items.put(item.getId(), new CrystallizationDataHolder(item.getId(), calculateCrystallizeRewards(item, holder)));
                    }
                }
            }
        }

        LOGGER.atInfo().addArgument(() -> items.size() - previousCount).log("Generated {} crystallizable items from templates.");
    }

    /**
     * @param itemId
     * @return {@code CrystallizationData} for unenchanted items (enchanted items just have different crystal count, but same rewards),<br>
     * or {@code null} if there is no such data registered.
     */
    private CrystallizationDataHolder getCrystallizationData(int itemId) {
        return items.get(itemId);
    }

    /**
     * @param item to calculate its worth in crystals.
     * @return List of {@code ItemChanceHolder} for the rewards with altered crystal count.
     */
    public List<ItemChanceHolder> getCrystallizationRewards(Item item) {
        final List<ItemChanceHolder> result = new ArrayList<>();
        var data = getCrystallizationData(item.getId());
        if (nonNull(data)) {
            // If there are no crystals on the template, add such.
            if (data.getItems().stream().noneMatch(i -> i.getId() == item.getTemplate().getCrystalItemId())) {
                result.add(new ItemChanceHolder(item.getTemplate().getCrystalItemId(), 100, item.getCrystalCount()));
            }

            result.addAll(data.getItems());
        } else {
            // Add basic crystal reward.
            result.add(new ItemChanceHolder(item.getTemplate().getCrystalItemId(), 100, item.getCrystalCount()));
        }

        return result;
    }

    public static void init() {
        getInstance().load();
    }

    public static ItemCrystallizationData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ItemCrystallizationData INSTANCE = new ItemCrystallizationData();
    }
}

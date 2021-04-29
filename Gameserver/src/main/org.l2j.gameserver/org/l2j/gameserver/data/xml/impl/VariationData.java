/*
 * Copyright © 2019-2021 L2JOrg
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

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.options.*;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

/**
 * @author Pere
 * @author JoeAlisson
 */
public class VariationData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(VariationData.class);

    private final IntMap<Variation> variations = new HashIntMap<>();
    private final IntMap<IntMap<VariationFee>> fees = new HashIntMap<>();

    private VariationData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/augmentation/Variations.xsd");
    }

    @Override
    public void load() {
        variations.clear();
        fees.clear();
        parseDatapackFile("data/augmentation/Variations.xml");
        LOGGER.info("Loaded {} Variations.", variations.size() );
        LOGGER.info("Loaded {} Fees.", fees.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode ->
        {
            forEach(listNode, "variations", variationsNode ->
            {
                forEach(variationsNode, "variation", variationNode ->
                {
                    final int mineralId = parseInt(variationNode.getAttributes(), "mineralId");
                    if (ItemEngine.getInstance().getTemplate(mineralId) == null) {
                        LOGGER.warn("Mineral with item id {}  was not found.", mineralId);
                    }
                    final Variation variation = new Variation(mineralId);

                    forEach(variationNode, "optionGroup", groupNode ->
                    {
                        final String weaponTypeString = parseString(groupNode.getAttributes(), "weaponType").toUpperCase();
                        final VariationWeaponType weaponType = VariationWeaponType.valueOf(weaponTypeString);
                        final int order = parseInt(groupNode.getAttributes(), "order");

                        final List<OptionDataCategory> sets = new ArrayList<>();
                        forEach(groupNode, "optionCategory", categoryNode ->
                        {
                            final double chance = parseDouble(categoryNode.getAttributes(), "chance");
                            final Map<Options, Double> options = new HashMap<>();
                            forEach(categoryNode, "option", optionNode ->
                            {
                                final double optionChance = parseDouble(optionNode.getAttributes(), "chance");
                                final int optionId = parseInt(optionNode.getAttributes(), "id");
                                final Options opt = AugmentationEngine.getInstance().getOptions(optionId);
                                if (opt == null) {
                                    LOGGER.warn("Null option for id " + optionId);
                                    return;
                                }
                                options.put(opt, optionChance);
                            });
                            forEach(categoryNode, "optionRange", optionNode ->
                            {
                                final double optionChance = parseDouble(optionNode.getAttributes(), "chance");
                                final int fromId = parseInt(optionNode.getAttributes(), "from");
                                final int toId = parseInt(optionNode.getAttributes(), "to");
                                for (int id = fromId; id <= toId; id++) {
                                    final Options op = AugmentationEngine.getInstance().getOptions(id);
                                    if (op == null) {
                                        LOGGER.warn("Null option for id " + id);
                                        continue;
                                    }
                                    options.put(op, optionChance);
                                }
                            });

                            sets.add(new OptionDataCategory(options, chance));
                        });

                        variation.setEffectGroup(weaponType, order, new OptionDataGroup(sets));
                    });

                    variations.put(mineralId, variation);
                });
            });

            final Map<Integer, List<Integer>> itemGroups = new HashMap<>();
            forEach(listNode, "itemGroups", variationsNode ->
            {
                forEach(variationsNode, "itemGroup", variationNode ->
                {
                    final int id = parseInt(variationNode.getAttributes(), "id");
                    final List<Integer> items = new ArrayList<>();
                    forEach(variationNode, "item", itemNode ->
                    {
                        final int itemId = parseInt(itemNode.getAttributes(), "id");
                        if (ItemEngine.getInstance().getTemplate(itemId) == null) {
                            LOGGER.warn(": Item with id " + itemId + " was not found.");
                        }
                        items.add(itemId);
                    });

                    itemGroups.put(id, items);
                });
            });

            forEach(listNode, "fees", variationNode ->
            {
                forEach(variationNode, "fee", feeNode ->
                {
                    final int itemGroupId = parseInt(feeNode.getAttributes(), "itemGroup");
                    final List<Integer> itemGroup = itemGroups.get(itemGroupId);
                    final int itemId = parseInt(feeNode.getAttributes(), "itemId");
                    final int itemCount = parseInt(feeNode.getAttributes(), "itemCount");
                    final int cancelFee = parseInt(feeNode.getAttributes(), "cancelFee");
                    if (ItemEngine.getInstance().getTemplate(itemId) == null) {
                        LOGGER.warn(": Item with id " + itemId + " was not found.");
                    }

                    final VariationFee fee = new VariationFee(itemId, itemCount, cancelFee);
                    final IntMap<VariationFee> feeByMinerals = new HashIntMap<>();
                    forEach(feeNode, "mineral", mineralNode ->
                    {
                        final int mId = parseInt(mineralNode.getAttributes(), "id");
                        feeByMinerals.put(mId, fee);
                    });
                    forEach(feeNode, "mineralRange", mineralNode ->
                    {
                        final int fromId = parseInt(mineralNode.getAttributes(), "from");
                        final int toId = parseInt(mineralNode.getAttributes(), "to");
                        for (int id = fromId; id <= toId; id++) {
                            feeByMinerals.put(id, fee);
                        }
                    });

                    for (int item : itemGroup) {
                        var fees = this.fees.computeIfAbsent(item, k -> new HashIntMap<>());
                        fees.putAll(feeByMinerals);
                    }
                });
            });
        });
    }

    /**
     * Generate a new random variation instance
     *
     * @param variation  The variation template to generate the variation instance from
     * @param targetItem The item on which the variation will be applied
     * @return VariationInstance
     */
    public VariationInstance generateRandomVariation(Variation variation, Item targetItem) {
        VariationWeaponType weaponType = getVariationWeaponType(targetItem);
        Options option1 = variation.getRandomEffect(weaponType, 0);
        Options option2 = variation.getRandomEffect(weaponType, 1);
        return nonNull(option1) && nonNull(option2) ? new VariationInstance(targetItem.getObjectId(), variation.getMineralId(), option1.getId(), option2.getId()) : null;

    }

    public VariationWeaponType getVariationWeaponType(Item targetItem) {
        VariationWeaponType weaponType;
        if(targetItem.isMagicWeapon() ) {
            weaponType  = VariationWeaponType.MAGE;
        } else if(targetItem.getBodyPart() == BodyPart.BACK && targetItem.getEnchantLevel() < 10) {
            weaponType  = VariationWeaponType.CLOAK;
        } else {
            weaponType = VariationWeaponType.WARRIOR;
        }
        return weaponType;
    }

    public final Variation getVariation(int mineralId) {
        return variations.get(mineralId);
    }

    public final VariationFee getFee(int itemId, int mineralId) {
        return fees.getOrDefault(itemId, Containers.emptyIntMap()).get(mineralId);
    }

    public final long getCancelFee(int itemId, int mineralId) {
        var fees = this.fees.get(itemId);
        if (fees == null) {
            return -1;
        }

        VariationFee fee = fees.get(mineralId);
        if (fee == null) {
            // FIXME This will happen when the data is pre-rework or when augments were manually given, but still that's a cheap solution
            LOGGER.warn(": Cancellation fee not found for item [" + itemId + "] and mineral [" + mineralId + "]");
            fee = fees.values().iterator().next();
            if (fee == null) {
                return -1;
            }
        }

        return fee.getCancelFee();
    }

    public final boolean hasFeeData(int itemId) {
        var itemFees = fees.get(itemId);
        return (itemFees != null) && !itemFees.isEmpty();
    }


    public static void init() {
        getInstance().load();
    }

    public static VariationData getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton {
        protected static final VariationData INSTANCE = new VariationData();
    }
}

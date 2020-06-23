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

import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.SpecialItemType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.*;
import org.l2j.gameserver.network.serverpackets.MultiSellList;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

public final class MultisellData extends GameXmlReader {
    public static final int PAGE_SIZE = 40;
    private static final Logger LOGGER = LoggerFactory.getLogger(MultisellData.class);

    private final Map<Integer, MultisellListHolder> _multisells = new HashMap<>();

    private MultisellData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/multisell.xsd");
    }

    @Override
    public void load() {
        _multisells.clear();
        parseDatapackDirectory("data/multisell", false);
        if (getSettings(GeneralSettings.class).loadCustomMultisell()) {
            parseDatapackDirectory("data/multisell/custom", false);
        }

        LOGGER.info("Loaded {} multisell lists.", _multisells.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {

        try {
            forEach(doc, "list", listNode ->
            {
                final StatsSet set = new StatsSet(parseAttributes(listNode));
                final int listId = Integer.parseInt(f.getName().substring(0, f.getName().length() - 4));
                final List<MultisellEntryHolder> entries = new ArrayList<>(listNode.getChildNodes().getLength());

                forEach(listNode, itemNode ->
                {
                    if ("item".equalsIgnoreCase(itemNode.getNodeName())) {
                        final List<ItemChanceHolder> ingredients = new ArrayList<>(1);
                        final List<ItemChanceHolder> products = new ArrayList<>(1);
                        final MultisellEntryHolder entry = new MultisellEntryHolder(ingredients, products);

                        for (Node d = itemNode.getFirstChild(); d != null; d = d.getNextSibling()) {
                            if ("ingredient".equalsIgnoreCase(d.getNodeName())) {
                                final int id = parseInteger(d.getAttributes(), "id");
                                final long count = parseLong(d.getAttributes(), "count");
                                final byte enchantmentLevel = parseByte(d.getAttributes(), "enchantmentLevel", (byte) 0);
                                final Boolean maintainIngredient = parseBoolean(d.getAttributes(), "maintainIngredient", false);
                                final ItemChanceHolder ingredient = new ItemChanceHolder(id, 0, count, enchantmentLevel, maintainIngredient);

                                if (itemExists(ingredient)) {
                                    ingredients.add(ingredient);
                                } else {
                                    LOGGER.warn("Invalid ingredient id or count for itemId: " + ingredient.getId() + ", count: " + ingredient.getCount() + " in list: " + listId);
                                }
                            } else if ("production".equalsIgnoreCase(d.getNodeName())) {
                                final int id = parseInteger(d.getAttributes(), "id");
                                final long count = parseLong(d.getAttributes(), "count");
                                final double chance = parseDouble(d.getAttributes(), "chance", Double.NaN);
                                byte enchantmentLevel = parseByte(d.getAttributes(), "enchantmentLevel", (byte) 0);


                                final ItemChanceHolder product = new ItemChanceHolder(id, chance, count, enchantmentLevel);

                                if (itemExists(product)) {
                                    // Check chance only of items that have set chance. Items without chance (NaN) are used for displaying purposes.
                                    if ((!Double.isNaN(chance) && (chance < 0)) || (chance > 100)) {
                                        LOGGER.warn("Invalid chance for itemId: " + product.getId() + ", count: " + product.getCount() + ", chance: " + chance + " in list: " + listId);
                                        continue;
                                    }

                                    products.add(product);
                                } else {
                                    LOGGER.warn("Invalid product id or count for itemId: " + product.getId() + ", count: " + product.getCount() + " in list: " + listId);
                                }
                            }
                        }

                        final double totalChance = products.stream().filter(i -> !Double.isNaN(i.getChance())).mapToDouble(ItemChanceHolder::getChance).sum();
                        if (totalChance > 100) {
                            LOGGER.warn("Products' total chance of " + totalChance + "% exceeds 100% for list: " + listId + " at entry " + entries.size() + 1 + ".");
                        }

                        entries.add(entry);
                    } else if ("npcs".equalsIgnoreCase(itemNode.getNodeName())) {
                        // Initialize NPCs with the size of child nodes.
                        final IntSet allowNpc = new HashIntSet(itemNode.getChildNodes().getLength());
                        forEach(itemNode, n -> "npc".equalsIgnoreCase(n.getNodeName()), n -> allowNpc.add(Integer.parseInt(n.getTextContent())));

                        // Add npcs to stats set.
                        set.set("allowNpc", allowNpc);
                    }
                });

                set.set("listId", listId);
                set.set("entries", entries);

                _multisells.put(listId, new MultisellListHolder(set));
            });
        } catch (Exception e) {
            LOGGER.error("Error in file " + f, e);
        }
    }

    /**
     * This will generate the multisell list for the items.<br>
     * There exist various parameters in multisells that affect the way they will appear:
     * <ol>
     * <li>Inventory only:
     * <ul>
     * <li>If true, only show items of the multisell for which the "primary" ingredients are already in the player's inventory. By "primary" ingredients we mean weapon and armor.</li>
     * <li>If false, show the entire list.</li>
     * </ul>
     * </li>
     * <li>Maintain enchantment: presumably, only lists with "inventory only" set to true should sometimes have this as true. This makes no sense otherwise...
     * <ul>
     * <li>If true, then the product will match the enchantment level of the ingredient.<br>
     * If the player has multiple items that match the ingredient list but the enchantment levels differ, then the entries need to be duplicated to show the products and ingredients for each enchantment level.<br>
     * For example: If the player has a crystal staff +1 and a crystal staff +3 and goes to exchange it at the mammon, the list should have all exchange possibilities for the +1 staff, followed by all possibilities for the +3 staff.</li>
     * <li>If false, then any level ingredient will be considered equal and product will always be at +0</li>
     * </ul>
     * </li>
     * <li>Apply taxes: Uses the "taxIngredient" entry in order to add a certain amount of adena to the ingredients.
     * <li>
     * <li>Additional product and ingredient multipliers.</li>
     * </ol>
     *
     * @param listId
     * @param player
     * @param npc
     * @param inventoryOnly
     * @param ingredientMultiplier
     * @param productMultiplier
     */
    public final void separateAndSend(int listId, Player player, Npc npc, boolean inventoryOnly, double ingredientMultiplier, double productMultiplier) {
        final MultisellListHolder template = _multisells.get(listId);
        if (template == null) {
            LOGGER.warn("Can't find list id: " + listId + " requested by player: " + player.getName() + ", npcId: " + (npc != null ? npc.getId() : 0));
            return;
        }

        if (!template.isNpcAllowed(-1)) {
            if (isNull(npc) || !template.isNpcAllowed(npc.getId())) {
                if (player.isGM()) {
                    player.sendMessage("Multisell " + listId + " is restricted. Under current conditions cannot be used. Only GMs are allowed to use it.");
                }
                else {
                    LOGGER.warn("Player {} attempted to open multisell {} from npc {} which is not allowed!", player, listId, npc);
                    return;
                }
            }
        }

        // Check if ingredient/product multipliers are set, if not, set them to the template value.
        ingredientMultiplier = (Double.isNaN(ingredientMultiplier) ? template.getIngredientMultiplier() : ingredientMultiplier);
        productMultiplier = (Double.isNaN(productMultiplier) ? template.getProductMultiplier() : productMultiplier);

        final PreparedMultisellListHolder list = new PreparedMultisellListHolder(template, inventoryOnly, player.getInventory(), npc, ingredientMultiplier, productMultiplier);
        int index = 0;
        do {
            // send list at least once even if size = 0
            player.sendPacket(new MultiSellList(list, index));
            index += PAGE_SIZE;
        }
        while (index < list.getEntries().size());

        player.setMultiSell(list);
    }

    public final void separateAndSend(int listId, Player player, Npc npc, boolean inventoryOnly) {
        separateAndSend(listId, player, npc, inventoryOnly, Double.NaN, Double.NaN);
    }

    private boolean itemExists(ItemHolder holder) {
        final SpecialItemType specialItem = SpecialItemType.getByClientId(holder.getId());
        if (specialItem != null) {
            return true;
        }

        return nonNull(ItemEngine.getInstance().getTemplate(holder.getId()));
    }

    public static MultisellData getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton {
        private static final MultisellData INSTANCE = new MultisellData();
    }
}

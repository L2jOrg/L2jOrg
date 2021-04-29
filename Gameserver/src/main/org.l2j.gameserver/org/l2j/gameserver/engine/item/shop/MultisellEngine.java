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
package org.l2j.gameserver.engine.item.shop;

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.item.shop.multisell.*;
import org.l2j.gameserver.enums.SpecialItemType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.MultisellListPacket;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public final class MultisellEngine extends GameXmlReader {
    public static final int PAGE_SIZE = 40;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultisellEngine.class);
    private final IntMap<MultisellList> multisells = new HashIntMap<>();
    private int currentListId;

    private MultisellEngine() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/multisell/multisell.xsd");
    }

    @Override
    public void load() {
        multisells.clear();
        parseDatapackDirectory("data/multisell", true);
        LOGGER.info("Loaded {} multisell lists.", multisells.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        var listNode = doc.getFirstChild();
        currentListId = Integer.parseInt(f.getName().substring(0, f.getName().length() - 4));

        IntSet allowedNpcs = Containers.emptyIntSet();
        List<MultisellItem> items = new ArrayList<>(listNode.getChildNodes().getLength());
        var attrs = listNode.getAttributes();
        boolean chanceBased = parseBoolean(attrs, "chance-based");

        for(var node = listNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            switch (node.getNodeName()) {
                case "npcs" -> allowedNpcs = parseNpcs(node);
                case "item" -> {
                    var item =   parseItem(node, chanceBased);
                    if(nonNull(item)) {
                        items.add(item);
                    }
                }
            }
        }
        boolean applyTaxes = parseBoolean(attrs, "apply-taxes");
        boolean maintainEnchantment = parseBoolean(attrs, "maintain-enchantment");
        double ingredientMultiplier = parseDouble(attrs, "ingredient-multiplier");
        double productMultiplier = parseDouble(attrs, "ingredient-multiplier");
        boolean gmOnly = parseBoolean(attrs, "gm-only");
        multisells.put(currentListId, new MultisellList(currentListId, items, allowedNpcs, applyTaxes, chanceBased, maintainEnchantment, gmOnly, ingredientMultiplier, productMultiplier));
    }

    private MultisellItem parseItem(Node itemNode, boolean chanceBased) {
        List<MultisellIngredient> ingredients = new ArrayList<>();
        List<MultisellProduct> products = new ArrayList<>();
        for(var node = itemNode.getFirstChild(); nonNull(node); node = node.getNextSibling()) {
            switch (node.getNodeName()) {
                case "ingredient" -> {
                    var ingredient = parseIngredient(node);
                    if(isNull(ingredient)) {
                        return null;
                    }
                    ingredients.add(ingredient);
                }
                case "production" -> {
                    var product = parseProduction(node, chanceBased);
                    if(isNull(product)) {
                        return null;
                    }
                    products.add(product);
                }
            }
        }
        checkProductsChance(chanceBased, products);
        boolean stackable = checkStackableProducts(products);
        return new MultisellItem(ingredients, products, stackable);
    }

    private boolean checkStackableProducts(List<MultisellProduct> products) {
        boolean stackable = true;
        for (MultisellProduct product : products) {
            var template = ItemEngine.getInstance().getTemplate(product.id());
            if(nonNull(template) && !template.isStackable()) {
                stackable = false;
                break;
            }
        }
        return stackable;
    }

    private void checkProductsChance(boolean chanceBased, List<MultisellProduct> products) {
        if(chanceBased) {
            int  totalChance = 0;
            for (MultisellProduct product : products) {
                totalChance += product.chance();
            }
            if(totalChance > 100) {
                LOGGER.warn("Products' total chance {}% exceeds 100% for list: {}", totalChance, currentListId);
            }

        }
    }

    private MultisellProduct parseProduction(Node productionNode, boolean chanceBased) {
        var attrs = productionNode.getAttributes();
        int id = parseInt(attrs, "id");
        if(itemNotExists(id)) {
            LOGGER.warn("Invalid product id {} in multisell {}", id, currentListId);
            return null;
        }
        double chance = parseDouble(attrs, "chance");
        if(chanceBased && (chance < 0 || chance > 100)) {
            LOGGER.warn("Invalid chance {} for item {} in multisell {}", chance, id, currentListId);
            return null;
        }
        long count = parseLong(attrs, "count");
        int enchant = parseInt(attrs, "enchant");
        return new MultisellProduct(id, count, enchant, chance);
    }

    private MultisellIngredient parseIngredient(Node ingredientNode) {
        var attrs = ingredientNode.getAttributes();
        int id = parseInt(attrs, "id");
        if(itemNotExists(id)) {
            LOGGER.warn("Invalid ingredient id {} in multisell {}", id, currentListId);
            return null;
        }
        long count = parseLong(attrs, "count");
        int enchant = parseInt(attrs, "enchant");
        boolean consume = parseBoolean(attrs, "consume");
        return new MultisellIngredient(id, count, enchant, consume);
    }

    private boolean itemNotExists(int itemId) {
        final SpecialItemType specialItem = SpecialItemType.getByClientId(itemId);
        return !nonNull(specialItem) && !nonNull(ItemEngine.getInstance().getTemplate(itemId));
    }

    private IntSet parseNpcs(Node npcNode) {
        return parseIntSet(npcNode);
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
     */
    public final void separateAndSend(int listId, Player player, Npc npc, boolean inventoryOnly, double ingredientMultiplier, double productMultiplier) {
        final var template = multisells.get(listId);
        if (isNull(template)) {
            LOGGER.warn("Can't find list id: {} requested by player: {}, npcId: {}", listId, player,  (npc != null ? npc.getId() : 0));
            return;
        }

        if(template.gmOnly() && !player.isGM()) {
            LOGGER.warn("Player {} attempted to open only GM multisell {}", player, listId);
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

        final var list = new PreparedMultisellList(template, inventoryOnly, player.getInventory(), npc, ingredientMultiplier, productMultiplier);
        int index = 0;
        do {
            player.sendPacket(new MultisellListPacket(list, index));
            index += PAGE_SIZE;
        }
        while (index < list.size());

        player.setMultiSell(list);
    }

    public final void separateAndSend(int listId, Player player, Npc npc, boolean inventoryOnly) {
        separateAndSend(listId, player, npc, inventoryOnly, Double.NaN, Double.NaN);
    }

    public static void init() {
        getInstance().load();
    }

    public static MultisellEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton {
        private static final MultisellEngine INSTANCE = new MultisellEngine();
    }
}

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
package org.l2j.gameserver.network.clientpackets;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.engine.item.*;
import org.l2j.gameserver.engine.item.shop.MultisellEngine;
import org.l2j.gameserver.engine.item.shop.multisell.MultisellItem;
import org.l2j.gameserver.engine.item.shop.multisell.MultisellProduct;
import org.l2j.gameserver.engine.item.shop.multisell.PreparedMultisellList;
import org.l2j.gameserver.enums.SpecialItemType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.engine.item.ItemTemplate;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPCCafePointInfo;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.model.actor.Npc.INTERACTION_DISTANCE;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * @author JoeAlisson
 */
public class MultiSellChoose extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiSellChoose.class);

    private int listId;
    private int entryId;
    private long count;
    private int enchantLevel;
    private int augmentOption1;
    private int augmentOption2;
    private EnsoulOption[] soulCrystalOptions;
    private EnsoulOption[] soulCrystalSpecialOptions;

    @Override
    public void readImpl() throws Exception {
        listId = readInt();
        entryId = readInt();
        count = readLong();
        enchantLevel = readShort();
        augmentOption1 = readInt();
        augmentOption2 = readInt();
        readShort(); /*_attackAttribute*/
        readShort(); /*_attributePower*/
        readShort(); /*_fireDefence*/
        readShort(); /*_waterDefence*/
        readShort(); /*_windDefence*/
        readShort(); /*_earthDefence*/
        readShort(); /*_holyDefence*/
        readShort(); /*_darkDefence*/

        soulCrystalOptions = new EnsoulOption[readByte()]; // Ensoul size
        fillSoulOptions(soulCrystalOptions);

        soulCrystalSpecialOptions = new EnsoulOption[readByte()]; // Special ensoul size
        fillSoulOptions(soulCrystalSpecialOptions);
    }

    private void fillSoulOptions(EnsoulOption[] soulCrystalOptions) throws InvalidDataPacketException {
        for (int i = 0; i < soulCrystalOptions.length; i++) {
            final int ensoulId = readInt();
            soulCrystalOptions[i] = ItemEnsoulEngine.getInstance().getOption(ensoulId);
            if (isNull(soulCrystalOptions[i])) {
                throw new InvalidDataPacketException("Invalid soul crystal option:" + ensoulId);
            }
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();

        if (!validateItemCount(player)) {
            return;
        }

        PreparedMultisellList list = player.getMultiSell();
        final Npc npc = player.getLastFolkNPC();

        if (!validateMerchant(player, list, npc)) {
            return;
        }

        final var entry = list.get(entryId - 1); // Entry Id begins from 1. We currently use entry IDs as index pointer.
        if (!validateItem(player, entry)) {
            return;
        }

        final ItemInfo itemEnchantment = list.getItemEnchantment(entryId - 1); // Entry Id begins from 1. We currently use entry IDs as index pointer.
        final PlayerInventory inventory = player.getInventory();

        if (!validateItemEnchantment(player, itemEnchantment, inventory)) {
            return;
        }

        var products = entry.products();
        if (list.isChanceBased()) {
            var product = entry.randomProduct();
            products = nonNull(product) ? List.of(product) : Collections.emptyList();
        }

        try {
            if (!validateInventoryCapacity(player, list, products)) {
                return;
            }

            final Clan clan = player.getClan();
            if (!validateIngredients(player, clan, list, entry, inventory)) {
                return;
            }

            final InventoryUpdate iu = new InventoryUpdate();
            boolean itemEnchantmentProcessed = consumeIngredients(player, list, npc, entry, itemEnchantment, clan, iu);

            giveProducts(player, list, npc, itemEnchantment, inventory, products, clan, iu, itemEnchantmentProcessed);

            player.sendInventoryUpdate(iu);

            if (nonNull(npc) && list.applyTaxes()) {
                final OptionalLong taxPaid = entry.ingredients().stream().filter(i -> i.id() == CommonItem.ADENA).mapToLong(i -> Math.round(i.count() * list.getIngredientMultiplier() * list.getTaxRate()) * count).reduce(Math::multiplyExact);
                if (taxPaid.isPresent()) {
                    npc.handleTaxPayment(taxPaid.getAsLong());
                }
            }
        } catch (ArithmeticException ae) {
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }

        if (list.isInventoryOnly()) {
            MultisellEngine.getInstance().separateAndSend(list.id(), player, npc, list.isInventoryOnly(), list.getProductMultiplier(), list.getIngredientMultiplier());
        }
    }

    private void giveProducts(Player player, PreparedMultisellList list, Npc npc, ItemInfo itemEnchantment, PlayerInventory inventory, List<MultisellProduct> products, Clan clan, InventoryUpdate iu, boolean itemEnchantmentProcessed) {
        for (var product : products) {
            final long totalCount = Math.multiplyExact(list.getProductCount(product), count);
            final SpecialItemType specialItem = SpecialItemType.getByClientId(product.id());
            if (nonNull(specialItem)) {
                giveSpecialItem(player, clan, totalCount, specialItem);
            } else {
                // Give item.
                final Item addedItem = inventory.addItem("Multisell", product.id(), totalCount, player, npc, false);

                // Check if the newly given item should be enchanted.
                if (itemEnchantmentProcessed && list.maintainEnchantment() && nonNull(itemEnchantment) && addedItem.isEquipable() && addedItem.getTemplate().getClass().equals(itemEnchantment.getTemplate().getClass())) {
                    addEnchantmentAndEnsouls(itemEnchantment, addedItem);
                    itemEnchantmentProcessed = false;
                }
                if (product.enchant() > 0) {
                    addedItem.changeEnchantLevel(product.enchant());
                    addedItem.updateDatabase(true);
                }

                if (addedItem.getCount() > 1) {
                    player.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S).addItemName(addedItem.getId()).addLong(totalCount));
                } else if (addedItem.getEnchantLevel() > 0) {
                    player.sendPacket(getSystemMessage(SystemMessageId.ACQUIRED_S1_S2).addLong(addedItem.getEnchantLevel()).addItemName(addedItem.getId()));
                } else {
                    player.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1).addItemName(addedItem));
                }

                iu.addItem(addedItem);
            }
        }
    }

    private void addEnchantmentAndEnsouls(ItemInfo itemEnchantment, Item addedItem) {
        addedItem.changeEnchantLevel(itemEnchantment.getEnchantLevel());
        addedItem.setAugmentation(itemEnchantment.getAugmentation(), false);

        if (nonNull(soulCrystalOptions)) {
            for (EnsoulOption ensoul : soulCrystalOptions) {
                addedItem.addSpecialAbility(ensoul, EnsoulType.COMMON, false);
            }
        }
        if (soulCrystalSpecialOptions != null) {
            for (EnsoulOption ensoul : soulCrystalSpecialOptions) {
                addedItem.addSpecialAbility(ensoul, EnsoulType.SPECIAL, false);
            }
        }
        addedItem.updateDatabase(true);
    }

    private void giveSpecialItem(Player player, Clan clan, long totalCount, SpecialItemType specialItem) {
        switch (specialItem) {
            case CLAN_REPUTATION -> {
                if (clan != null) {
                    clan.addReputationScore((int) totalCount, true);
                }
            }
            case FAME -> {
                player.setFame((int) (player.getFame() + totalCount));
                player.sendPacket(new UserInfo(player));
            }
            case RAIDBOSS_POINTS -> {
                player.increaseRaidbossPoints((int) totalCount);
                player.sendPacket(new UserInfo(player));
            }
            case PC_CAFE_POINTS -> {
                player.setPcCafePoints(player.getPcCafePoints() + (int) totalCount);
                player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), (int) totalCount, 1));
            }
        }
    }

    private boolean consumeIngredients(Player player, PreparedMultisellList list, Npc npc, MultisellItem entry, ItemInfo itemEnchantment, Clan clan, InventoryUpdate iu) {
        var inventory = player.getInventory();
        boolean itemEnchantmentProcessed = (itemEnchantment == null);
        // Take all ingredients
        for (var ingredient : entry.ingredients()) {
            if (!ingredient.consume()) {
                continue;
            }

            final long totalCount = Math.multiplyExact(list.getIngredientCount(ingredient), count);
            final SpecialItemType specialItem = SpecialItemType.getByClientId(ingredient.id());
            if (nonNull(specialItem)) {
                consumeSpecialItem(player, clan, totalCount, specialItem);
            } else if (ingredient.enchant() > 0) {
                // Take the enchanted item.
                final Item destroyedItem = inventory.destroyItem("Multisell", inventory.getAllItemsByItemId(ingredient.id(), ingredient.enchant()).iterator().next(), totalCount, player, npc);
                itemEnchantmentProcessed = true;
                iu.addItem(destroyedItem);
            } else if (!itemEnchantmentProcessed && itemEnchantment.getId() == ingredient.id()) {
                final Item destroyedItem = inventory.destroyItem("Multisell", itemEnchantment.getObjectId(), totalCount, player, npc);
                itemEnchantmentProcessed = true;
                iu.addItem(destroyedItem);
            } else {
                final Item destroyedItem = inventory.destroyItemByItemId("Multisell", ingredient.id(), totalCount, player, npc);
                iu.addItem(destroyedItem);
            }
        }
        return itemEnchantmentProcessed;
    }

    private void consumeSpecialItem(Player player, Clan clan, long totalCount, SpecialItemType specialItem) {
        switch (specialItem) {
            case CLAN_REPUTATION: {
                clan.takeReputationScore((int) totalCount, true);
                player.sendPacket(getSystemMessage(SystemMessageId.S1_POINT_S_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_S_REPUTATION).addLong(totalCount));
                break;
            }
            case FAME: {
                player.setFame(player.getFame() - (int) totalCount);
                player.sendPacket(new UserInfo(player));
                // player.sendPacket(new ExBrExtraUserInfo(player));
                break;
            }
            case RAIDBOSS_POINTS: {
                player.setRaidbossPoints(player.getRaidbossPoints() - (int) totalCount);
                player.sendPacket(new UserInfo(player));
                player.sendPacket(getSystemMessage(SystemMessageId.YOU_CONSUMED_S1_RAID_POINTS).addLong(totalCount));
                break;
            }
            case PC_CAFE_POINTS: {
                player.setPcCafePoints((int) (player.getPcCafePoints() - totalCount));
                player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), (int) -totalCount, 1));
                break;
            }
        }
    }

    private boolean validateIngredients(Player player, Clan clan, PreparedMultisellList list, MultisellItem entry, PlayerInventory inventory) {
        // TODO change to primitive
        IntMap<Long> itemsAmount = new HashIntMap<>();
        for (var ingredient : entry.ingredients()) {
            var amount = itemsAmount.compute(ingredient.id(), (k, v) -> nonNull(v) ? v + ingredient.count() : ingredient.count());
            if (ingredient.enchant() > 0) {
                int found = 0;
                for (Item item : inventory.getAllItemsByItemId(ingredient.id(), ingredient.enchant())) {
                    if (item.getEnchantLevel() == ingredient.enchant()) {
                        found++;
                    }
                }

                if (found < amount) {
                    player.sendPacket(getSystemMessage(SystemMessageId.YOU_NEED_A_N_S1)
                            .addString("+" + ingredient.enchant() + " " + ItemEngine.getInstance().getTemplate(ingredient.id()).getName()));
                    return false;
                }
            } else if (!checkIngredients(player, clan, list, ingredient.id(), Math.multiplyExact(amount, count))) {
                return false;
            }
        }
        return true;
    }

    private boolean validateInventoryCapacity(Player player, PreparedMultisellList list, List<MultisellProduct> products) {
        var inventory = player.getInventory();
        int slots = 0;
        int weight = 0;
        for (var product : products) {
            if (product.id() < 0) {
                if (isNull(player.getClan()) && (SpecialItemType.CLAN_REPUTATION.getClientId() == product.id())) {
                    player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
                    return false;
                }
                continue;
            }

            final ItemTemplate template = ItemEngine.getInstance().getTemplate(product.id());
            final long totalCount = Math.multiplyExact(list.getProductCount(product), count);

            if (totalCount <= 0  || totalCount > Integer.MAX_VALUE) {
                player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
                return false;
            }

            if (!template.isStackable() || isNull(inventory.getItemByItemId(product.id()))) {
                slots++;
            }

            weight += totalCount * template.getWeight();

            if (!inventory.validateWeight(weight)) {
                player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return false;
            }

            if (slots > 0 && !inventory.validateCapacity(slots)) {
                player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
                return false;
            }
        }
        return true;
    }

    private boolean validateItemEnchantment(Player player, ItemInfo itemEnchantment, PlayerInventory inventory) {
        // Validate the requested item with its full stats.
        //@formatter:off
        if ((itemEnchantment != null) && ((count > 1)
                || (itemEnchantment.getEnchantLevel() != enchantLevel)
                || ((itemEnchantment.getAugmentation() == null) && ((augmentOption1 != 0) || (augmentOption2 != 0)))
                || ((itemEnchantment.getAugmentation() != null) && ((itemEnchantment.getAugmentation().getOption1Id() != augmentOption1) || (itemEnchantment.getAugmentation().getOption2Id() != augmentOption2)))
                || ((soulCrystalOptions.length > 0) && !CommonUtil.contains(soulCrystalOptions, itemEnchantment.getSoulCrystalOption()))
                || ((soulCrystalOptions.length == 0) && nonNull(itemEnchantment.getSoulCrystalOption()))
                || ((soulCrystalSpecialOptions.length > 0) && !CommonUtil.contains(soulCrystalSpecialOptions, itemEnchantment.getSoulCrystalSpecialOption()))
                || ((soulCrystalSpecialOptions.length == 0) && nonNull(itemEnchantment.getSoulCrystalSpecialOption()))
        ))
        //@formatter:on
        {
            LOGGER.warn("Player {} is trying to upgrade equippable item, but the stats doesn't match. multisell {} entry {}", player, listId, entryId);
            player.setMultiSell(null);
            return false;
        }

        if (nonNull(itemEnchantment) && isNull(inventory.getItemByObjectId(itemEnchantment.getObjectId()))) {
            player.sendPacket(getSystemMessage(SystemMessageId.YOU_NEED_A_N_S1).addItemName(itemEnchantment.getId()));
            return false;
        }
        return true;
    }

    private boolean validateItem(Player player, MultisellItem entry) {
        if (isNull(entry)) {
            LOGGER.warn("Player {} requested inexistant prepared multisell {} entry {}", player, listId, entryId);
            player.setMultiSell(null);
            return false;
        }

        if (!entry.stackable() && count > 1) {
            LOGGER.warn("Player {} is trying to set amount > 1 on non-stackable multisell {} entry {} ", player,  listId, entryId);
            player.setMultiSell(null);
            return false;
        }
        return true;
    }

    private boolean validateMerchant(Player player, PreparedMultisellList list, Npc npc) {
        if (isNull(list) || (list.id() != listId)) {
            player.setMultiSell(null);
            return false;
        }

        if (!player.isGM() && !list.isNpcAllowed(-1)) {
            if(isNull(npc) || !list.isNpcAllowed(npc.getId()) || player.getInstanceId() != npc.getInstanceId()  || !isInsideRadius3D(player, npc, INTERACTION_DISTANCE)) {
                player.setMultiSell(null);
                return false;
            }
        }
        return true;
    }

    private boolean validateItemCount(Player player) {
        if (isNull(player)) {
            return false;
        }

        if (!client.getFloodProtectors().getMultiSell().tryPerformAction("multisell choose")) {
            player.setMultiSell(null);
            return false;
        }

        if (count < 1 || count > 10000) { // 999 999 is client max.
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return false;
        }
        return true;
    }

    private boolean checkIngredients(Player player, Clan clan, PreparedMultisellList list, int ingredientId, long totalCount) {
        final SpecialItemType specialItem = SpecialItemType.getByClientId(ingredientId);
        if (nonNull(specialItem)) {
            return validateSpecialItem(player, clan, totalCount, specialItem);
        }

        // Check if the necessary items are there. If list maintains enchantment, allow all enchanted items, otherwise only unenchanted. TODO: Check how retail does it.
        if (player.getInventory().getInventoryItemCount(ingredientId, list.maintainEnchantment() ? -1 : 0, false) < totalCount) {
            player.sendPacket(getSystemMessage(SystemMessageId.YOU_NEED_S2_S1_S).addItemName(ingredientId).addLong(totalCount));
            return false;
        }
        return true;
    }

    private boolean validateSpecialItem(Player player, Clan clan, long totalCount, SpecialItemType specialItem) {
        return switch (specialItem) {
            case CLAN_REPUTATION -> {
                if (isNull(clan)) {
                    player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
                    yield false;
                } else if (!player.isClanLeader()) {
                    player.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_IS_ENABLED);
                    yield false;
                } else if (clan.getReputationScore() < totalCount) {
                    player.sendPacket(SystemMessageId.THE_CLAN_REPUTATION_IS_TOO_LOW);
                    yield false;
                }
                yield true;
            }
            case FAME -> {
                if (player.getFame() < totalCount) {
                    player.sendPacket(SystemMessageId.YOU_DON_T_HAVE_ENOUGH_FAME_TO_DO_THAT);
                    yield false;
                }
                yield true;
            }
            case RAIDBOSS_POINTS -> {
                if (player.getRaidbossPoints() < totalCount) {
                    player.sendPacket(SystemMessageId.NOT_ENOUGH_RAID_POINTS);
                    yield  false;
                }
                yield true;
            }
            case PC_CAFE_POINTS -> {
                if (player.getPcCafePoints() < totalCount) {
                    player.sendPacket(getSystemMessage(SystemMessageId.YOU_ARE_SHORT_OF_PA_POINTS));
                    yield false;
                }
                yield true;
            }
        };
    }
}
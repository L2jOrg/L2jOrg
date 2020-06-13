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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.EnchantItemAttributeRequest;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.model.item.Armor;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.options.VariationFee;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.network.SystemMessageId;

import java.util.Arrays;

public abstract class AbstractRefinePacket extends ClientPacket {
    /**
     * Checks player, source item, lifestone and gemstone validity for augmentation process
     *
     * @param player
     * @param item
     * @param mineralItem
     * @param feeItem
     * @param fee
     * @return
     */
    protected static boolean isValid(Player player, Item item, Item mineralItem, Item feeItem, VariationFee fee) {
        if (fee == null) {
            return false;
        }

        if (!isValid(player, item, mineralItem)) {
            return false;
        }

        // GemStones must belong to owner
        if (feeItem.getOwnerId() != player.getObjectId()) {
            return false;
        }
        // .. and located in inventory
        if (feeItem.getItemLocation() != ItemLocation.INVENTORY) {
            return false;
        }

        // Check for item id
        if (fee.getItemId() != feeItem.getId()) {
            return false;
        }
        // Count must be greater or equal of required number
        if (fee.getItemCount() > feeItem.getCount()) {
            return false;
        }

        return true;
    }

    /**
     * Checks player, source item and lifestone validity for augmentation process
     *
     * @param player
     * @param item
     * @param mineralItem
     * @return
     */
    protected static boolean isValid(Player player, Item item, Item mineralItem) {
        if (!isValid(player, item)) {
            return false;
        }

        // Item must belong to owner
        if (mineralItem.getOwnerId() != player.getObjectId()) {
            return false;
        }
        // Lifestone must be located in inventory
        if (mineralItem.getItemLocation() != ItemLocation.INVENTORY) {
            return false;
        }

        return true;
    }

    /**
     * Check both player and source item conditions for augmentation process
     *
     * @param player
     * @param item
     * @return
     */
    protected static boolean isValid(Player player, Item item) {
        if (!isValid(player)) {
            return false;
        }

        // Item must belong to owner
        if (item.getOwnerId() != player.getObjectId()) {
            return false;
        }
        if (item.isAugmented()) {
            return false;
        }
        if (item.isHeroItem()) {
            return false;
        }
        if (item.isCommonItem()) {
            return false;
        }
        if (item.isEtcItem()) {
            return false;
        }
        if (item.isTimeLimitedItem()) {
            return false;
        }
        if (item.isPvp() && !Config.ALT_ALLOW_AUGMENT_PVP_ITEMS) {
            return false;
        }

        // Source item can be equipped or in inventory
        switch (item.getItemLocation()) {
            case INVENTORY:
            case PAPERDOLL: {
                break;
            }
            default: {
                return false;
            }
        }

        if (!(item.getTemplate() instanceof Weapon) && !(item.getTemplate() instanceof Armor)) {
            return false; // neither weapon nor armor ?
        }

        // blacklist check
        if (Arrays.binarySearch(Config.AUGMENTATION_BLACKLIST, item.getId()) >= 0) {
            return false;
        }

        return true;
    }

    /**
     * Check if player's conditions valid for augmentation process
     *
     * @param player
     * @return
     */
    protected static boolean isValid(Player player) {
        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION);
            return false;
        }
        if (player.getActiveTradeList() != null) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_ENGAGED_IN_TRADE_ACTIVITIES);
            return false;
        }
        if (player.isDead()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD);
            return false;
        }
        if (player.hasBlockActions() && player.hasAbnormalType(AbnormalType.PARALYZE)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED);
            return false;
        }
        if (player.isFishing()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING);
            return false;
        }
        if (player.isSitting()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN);
            return false;
        }

        return !player.hasRequest(EnchantItemRequest.class, EnchantItemAttributeRequest.class) && !player.isProcessingTransaction();
    }
}

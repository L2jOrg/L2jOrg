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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.engine.item.ItemTemplate;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.gameserver.network.SystemMessageId.*;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius2D;

public final class RequestDropItem extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDropItem.class);
    private int _objectId;
    private long _count;
    private int _x;
    private int _y;
    private int _z;

    @Override
    public void readImpl() {
        _objectId = readInt();
        _count = readLong();
        _x = readInt();
        _y = readInt();
        _z = readInt();
    }

    @Override
    public void runImpl() {
        if (!client.getFloodProtectors().getDropItem().tryPerformAction("drop item")) {
            return;
        }

        final Player player = client.getPlayer();
        if (player == null || player.isDead()) {
            return;
        }

        final Item item = player.getInventory().getItemByObjectId(_objectId);

        if (!canDropItem(player, item)) {
            return;
        }

        if (item.isEquipped()) {
            var modifiedItems = player.getInventory().unEquipItemInSlotAndRecord(InventorySlot.fromId(item.getLocationSlot()));
            player.sendInventoryUpdate(new InventoryUpdate(modifiedItems));
        }

        final Item droppedItem = player.dropItem("Drop", _objectId, _count, _x, _y, _z, null, false, false);
        if(droppedItem != null) {
            if (player.isGM()) {
                final String target = (player.getTarget() != null ? player.getTarget().getName() : "no-target");
                GMAudit.auditGMAction(player.getName() + " [" + player.getObjectId() + "]", "Drop", target, "(id: " + droppedItem.getId() + " name: " + droppedItem.getName() + " objId: " + droppedItem.getObjectId() + " x: " + player.getX() + " y: " + player.getY() + " z: " + player.getZ() + ")");
            }

            if (droppedItem.getId() == CommonItem.ADENA && droppedItem.getCount() >= 1000000) {
                final String msg = "Character (" + player.getName() + ") has dropped (" + droppedItem.getCount() + ")adena at (" + _x + "," + _y + "," + _z + ")";
                LOGGER.warn(msg);
                AdminData.getInstance().broadcastMessageToGMs(msg);
            }
        }
    }

    private boolean canDropItem(Player player, Item item) {
        if(item == null || player.isFlying() || (item.isQuestItem() && !player.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS))) {
            return false;
        }

        if (!validateDropRequest(player, item)) {
            return false;
        }

        if (!canItemBeDiscarded(player, item)) {
            player.sendPacket(THAT_ITEM_CANNOT_BE_DISCARDED);
            return false;
        }

        if(player.isInsideZone(ZoneType.NO_ITEM_DROP)) {
            player.sendPacket(YOU_CANNOT_DISCARD_THOSE_ITEMS_HERE);
            return false;
        }

        if(item.getItemType() == EtcItemType.PET_COLLAR && player.havePetInvItems()) {
            player.sendPacket(AS_YOUR_PET_IS_CURRENTLY_SUMMONED_YOU_CANNOT_DISCARD_THE_SUMMONING_ITEM);
            return false;
        }

        if (!canPlayerDiscardItem(player)) {
            return false;
        }

        if (!isInsideRadius2D(player, _x, _y, 150) || (Math.abs(_z - player.getZ()) > 50)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISCARD_SOMETHING_THAT_FAR_AWAY_FROM_YOU);
            return false;
        }

        return true;
    }

    private boolean validateDropRequest(Player player, Item item) {
        if (_count < 0) {
            GameUtils.handleIllegalPlayerAction(player, "[RequestDropItem] Character " + player.getName() + " of account " + player.getAccountName() + " tried to drop item with oid " + _objectId + " but has count < 0!");
            return true;
        }

        if (!item.isStackable() && (_count > 1)) {
            GameUtils.handleIllegalPlayerAction(player, "[RequestDropItem] Character " + player.getName() + " of account " + player.getAccountName() + " tried to drop non-stackable item with oid " + _objectId + " but has count > 1!");
            return true;
        }
        return false;
    }

    private boolean canPlayerDiscardItem(Player player) {
        if (Config.JAIL_DISABLE_TRANSACTION && player.isJailed()) {
            player.sendMessage("You cannot drop items in Jail.");
            return false;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
            return false;
        }

        if (player.isProcessingTransaction() || (player.getPrivateStoreType() != PrivateStoreType.NONE)) {
            player.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return false;
        }

        if (player.isFishing()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_SCREEN);
            return false;
        }

        if (player.hasItemRequest()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DESTROY_OR_CRYSTALLIZE_ITEMS_WHILE_ENCHANTING_ATTRIBUTES);
            return false;
        }

        if (player.isCastingNow()) {
            player.sendPacket(THAT_ITEM_CANNOT_BE_DISCARDED);
            return false;
        }
        return true;
    }

    private boolean canItemBeDiscarded(Player player, Item item) {
        if (_count == 0 || !player.validateItemManipulation(_objectId, "drop") || (!GeneralSettings.allowDiscardItem() && !player.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS)) ||
                (!item.isDropable() && !player.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS))) {
            return false;
        }

        if (CharacterSettings.spawnProtection() > 0 && player.isInvulnerable() && !player.isGM()) {
            return false;
        }

        if (_count > item.getCount()) {
            return false;
        }

        if (player.getInventory().isBlocked(item)) {
            return false;
        }

        return (ItemTemplate.TYPE2_QUEST != item.getType2()) || player.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS);
    }
}

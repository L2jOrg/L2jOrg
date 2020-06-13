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
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        final Player player = client.getPlayer();
        if ((player == null) || player.isDead()) {
            return;
        }
        // Flood protect drop to avoid packet lag
        if (!client.getFloodProtectors().getDropItem().tryPerformAction("drop item")) {
            return;
        }

        final Item item = player.getInventory().getItemByObjectId(_objectId);

        if ((item == null) || (_count == 0) || !player.validateItemManipulation(_objectId, "drop") || (!Config.ALLOW_DISCARDITEM && !player.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS)) || (!item.isDropable() && !(player.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS) && Config.GM_TRADE_RESTRICTED_ITEMS)) || ((item.getItemType() == EtcItemType.PET_COLLAR) && player.havePetInvItems()) || player.isInsideZone(ZoneType.NO_ITEM_DROP)) {
            player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }
        if (item.isQuestItem() && !(player.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS) && Config.GM_TRADE_RESTRICTED_ITEMS)) {
            return;
        }

        if (_count > item.getCount()) {
            player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        if ((Config.PLAYER_SPAWN_PROTECTION > 0) && player.isInvul() && !player.isGM()) {
            player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        if (_count < 0) {
            GameUtils.handleIllegalPlayerAction(player, "[RequestDropItem] Character " + player.getName() + " of account " + player.getAccountName() + " tried to drop item with oid " + _objectId + " but has count < 0!");
            return;
        }

        if (!item.isStackable() && (_count > 1)) {
            GameUtils.handleIllegalPlayerAction(player, "[RequestDropItem] Character " + player.getName() + " of account " + player.getAccountName() + " tried to drop non-stackable item with oid " + _objectId + " but has count > 1!");
            return;
        }

        if (Config.JAIL_DISABLE_TRANSACTION && player.isJailed()) {
            player.sendMessage("You cannot drop items in Jail.");
            return;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            player.sendPacket(SystemMessageId.NOTHING_HAPPENED);
            return;
        }

        if (player.isProcessingTransaction() || (player.getPrivateStoreType() != PrivateStoreType.NONE)) {
            player.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }
        if (player.isFishing()) {
            // You can't mount, dismount, break and drop items while fishing
            player.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_SCREEN);
            return;
        }
        if (player.isFlying()) {
            return;
        }

        if (player.hasItemRequest()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DESTROY_OR_CRYSTALLIZE_ITEMS_WHILE_ENCHANTING_ATTRIBUTES);
            return;
        }

        // Cannot discard item that the skill is consuming.
        if (player.isCastingNow()) {
            player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        if ((ItemTemplate.TYPE2_QUEST == item.getTemplate().getType2()) && !player.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS)) {
            player.sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_DISCARDED_OR_EXCHANGED);
            return;
        }

        if (!isInsideRadius2D(player, _x, _y, 150) || (Math.abs(_z - player.getZ()) > 50)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISCARD_SOMETHING_THAT_FAR_AWAY_FROM_YOU);
            return;
        }

        if (player.getInventory().isBlocked(item)) {
            player.sendMessage("You cannot use this item.");
            return;
        }

        if (item.isEquipped()) {
            player.getInventory().unEquipItemInSlot(InventorySlot.fromId(item.getLocationSlot()));
            player.broadcastUserInfo();
            player.sendItemList();
        }

        final Item dropedItem = player.dropItem("Drop", _objectId, _count, _x, _y, _z, null, false, false);

        if (player.isGM()) {
            final String target = (player.getTarget() != null ? player.getTarget().getName() : "no-target");
            GMAudit.auditGMAction(player.getName() + " [" + player.getObjectId() + "]", "Drop", target, "(id: " + dropedItem.getId() + " name: " + dropedItem.getItemName() + " objId: " + dropedItem.getObjectId() + " x: " + player.getX() + " y: " + player.getY() + " z: " + player.getZ() + ")");
        }

        if ((dropedItem != null) && (dropedItem.getId() == CommonItem.ADENA) && (dropedItem.getCount() >= 1000000)) {
            final String msg = "Character (" + player.getName() + ") has dropped (" + dropedItem.getCount() + ")adena at (" + _x + "," + _y + "," + _z + ")";
            LOGGER.warn(msg);
            AdminData.getInstance().broadcastMessageToGMs(msg);
        }
    }
}

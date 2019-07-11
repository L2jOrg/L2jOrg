package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.items.type.ActionType;
import org.l2j.gameserver.model.items.type.EtcItemType;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class ...
 *
 * @version $Revision: 1.11.2.1.2.7 $ $Date: 2005/04/02 21:25:21 $
 */
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
        final Player activeChar = client.getActiveChar();
        if ((activeChar == null) || activeChar.isDead()) {
            return;
        }
        // Flood protect drop to avoid packet lag
        if (!client.getFloodProtectors().getDropItem().tryPerformAction("drop item")) {
            return;
        }

        final L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);

        if ((item == null) || (_count == 0) || !activeChar.validateItemManipulation(_objectId, "drop") || (!Config.ALLOW_DISCARDITEM && !activeChar.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS)) || (!item.isDropable() && !(activeChar.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS) && Config.GM_TRADE_RESTRICTED_ITEMS)) || ((item.getItemType() == EtcItemType.PET_COLLAR) && activeChar.havePetInvItems()) || activeChar.isInsideZone(ZoneId.NO_ITEM_DROP)) {
            activeChar.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }
        if (item.isQuestItem() && !(activeChar.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS) && Config.GM_TRADE_RESTRICTED_ITEMS)) {
            return;
        }

        if (_count > item.getCount()) {
            activeChar.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        if ((Config.PLAYER_SPAWN_PROTECTION > 0) && activeChar.isInvul() && !activeChar.isGM()) {
            activeChar.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        if (_count < 0) {
            GameUtils.handleIllegalPlayerAction(activeChar, "[RequestDropItem] Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tried to drop item with oid " + _objectId + " but has count < 0!", Config.DEFAULT_PUNISH);
            return;
        }

        if (!item.isStackable() && (_count > 1)) {
            GameUtils.handleIllegalPlayerAction(activeChar, "[RequestDropItem] Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tried to drop non-stackable item with oid " + _objectId + " but has count > 1!", Config.DEFAULT_PUNISH);
            return;
        }

        if (Config.JAIL_DISABLE_TRANSACTION && activeChar.isJailed()) {
            activeChar.sendMessage("You cannot drop items in Jail.");
            return;
        }

        if (!activeChar.getAccessLevel().allowTransaction()) {
            activeChar.sendMessage("Transactions are disabled for your Access Level.");
            activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
            return;
        }

        if (activeChar.isProcessingTransaction() || (activeChar.getPrivateStoreType() != PrivateStoreType.NONE)) {
            activeChar.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }
        if (activeChar.isFishing()) {
            // You can't mount, dismount, break and drop items while fishing
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
            return;
        }
        if (activeChar.isFlying()) {
            return;
        }

        if (activeChar.hasItemRequest()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_DESTROY_OR_CRYSTALLIZE_ITEMS_WHILE_ENCHANTING_ATTRIBUTES);
            return;
        }

        // Cannot discard item that the skill is consuming.
        if (activeChar.isCastingNow(s -> (s.getSkill().getItemConsumeId() == item.getId()) && (item.getItem().getDefaultAction() == ActionType.SKILL_REDUCE_ON_SKILL_SUCCESS))) {
            activeChar.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        if ((L2Item.TYPE2_QUEST == item.getItem().getType2()) && !activeChar.canOverrideCond(PcCondOverride.DROP_ALL_ITEMS)) {
            activeChar.sendPacket(SystemMessageId.THAT_ITEM_CANNOT_BE_DISCARDED_OR_EXCHANGED);
            return;
        }

        if (!activeChar.isInsideRadius2D(_x, _y, 0, 150) || (Math.abs(_z - activeChar.getZ()) > 50)) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_DISCARD_SOMETHING_THAT_FAR_AWAY_FROM_YOU);
            return;
        }

        if (!activeChar.getInventory().canManipulateWithItemId(item.getId())) {
            activeChar.sendMessage("You cannot use this item.");
            return;
        }

        if (item.isEquipped()) {
            activeChar.getInventory().unEquipItemInSlot(item.getLocationSlot());
            activeChar.broadcastUserInfo();
            activeChar.sendItemList();
        }

        final L2ItemInstance dropedItem = activeChar.dropItem("Drop", _objectId, _count, _x, _y, _z, null, false, false);

        // activeChar.broadcastUserInfo();

        if (activeChar.isGM()) {
            final String target = (activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target");
            GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", "Drop", target, "(id: " + dropedItem.getId() + " name: " + dropedItem.getItemName() + " objId: " + dropedItem.getObjectId() + " x: " + activeChar.getX() + " y: " + activeChar.getY() + " z: " + activeChar.getZ() + ")");
        }

        if ((dropedItem != null) && (dropedItem.getId() == CommonItem.ADENA) && (dropedItem.getCount() >= 1000000)) {
            final String msg = "Character (" + activeChar.getName() + ") has dropped (" + dropedItem.getCount() + ")adena at (" + _x + "," + _y + "," + _z + ")";
            LOGGER.warn(msg);
            AdminData.getInstance().broadcastMessageToGMs(msg);
        }
    }
}

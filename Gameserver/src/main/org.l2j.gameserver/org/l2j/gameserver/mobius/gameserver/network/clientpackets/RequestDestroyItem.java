package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.mobius.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.mobius.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.PcCondOverride;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.mobius.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * This class ...
 *
 * @version $Revision: 1.7.2.4.2.6 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestDestroyItem extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDestroyItem.class);
    private int _objectId;
    private long _count;

    @Override
    public void readImpl(ByteBuffer packet) {
        _objectId = packet.getInt();
        _count = packet.getLong();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (_count <= 0) {
            if (_count < 0) {
                Util.handleIllegalPlayerAction(activeChar, "[RequestDestroyItem] Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tried to destroy item with oid " + _objectId + " but has count < 0!", Config.DEFAULT_PUNISH);
            }
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("destroy")) {
            activeChar.sendMessage("You are destroying items too fast.");
            return;
        }

        long count = _count;

        if (activeChar.isProcessingTransaction() || (activeChar.getPrivateStoreType() != PrivateStoreType.NONE)) {
            client.sendPacket(SystemMessageId.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (activeChar.hasItemRequest()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_DESTROY_OR_CRYSTALLIZE_ITEMS_WHILE_ENCHANTING_ATTRIBUTES);
            return;
        }

        final L2ItemInstance itemToRemove = activeChar.getInventory().getItemByObjectId(_objectId);

        // if we can't find the requested item, its actually a cheat
        if (itemToRemove == null) {
            // gm can destroy other player items
            if (activeChar.isGM()) {
                final L2Object obj = L2World.getInstance().findObject(_objectId);
                if (obj.isItem()) {
                    if (_count > ((L2ItemInstance) obj).getCount()) {
                        count = ((L2ItemInstance) obj).getCount();
                    }
                    AdminCommandHandler.getInstance().useAdminCommand(activeChar, "admin_delete_item " + _objectId + " " + count, true);
                }
                return;
            }

            client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        // Cannot discard item that the skill is consuming
        if (activeChar.isCastingNow(s -> s.getSkill().getItemConsumeId() == itemToRemove.getId())) {
            client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            return;
        }

        final int itemId = itemToRemove.getId();

        if (!Config.DESTROY_ALL_ITEMS && ((!activeChar.canOverrideCond(PcCondOverride.DESTROY_ALL_ITEMS) && !itemToRemove.isDestroyable()) || CursedWeaponsManager.getInstance().isCursed(itemId))) {
            if (itemToRemove.isHeroItem()) {
                client.sendPacket(SystemMessageId.HERO_WEAPONS_CANNOT_BE_DESTROYED);
            } else {
                client.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_DESTROYED);
            }
            return;
        }

        if (!itemToRemove.isStackable() && (count > 1)) {
            Util.handleIllegalPlayerAction(activeChar, "[RequestDestroyItem] Character " + activeChar.getName() + " of account " + activeChar.getAccountName() + " tried to destroy a non-stackable item with oid " + _objectId + " but has count > 1!", Config.DEFAULT_PUNISH);
            return;
        }

        if (!activeChar.getInventory().canManipulateWithItemId(itemToRemove.getId())) {
            activeChar.sendMessage("You cannot use this item.");
            return;
        }

        if (_count > itemToRemove.getCount()) {
            count = itemToRemove.getCount();
        }

        if (itemToRemove.getItem().isPetItem()) {
            final L2Summon pet = activeChar.getPet();
            if ((pet != null) && (pet.getControlObjectId() == _objectId)) {
                pet.unSummon(activeChar);
            }

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?")) {
                statement.setInt(1, _objectId);
                statement.execute();
            } catch (Exception e) {
                LOGGER.warn("could not delete pet objectid: ", e);
            }
        }
        if (itemToRemove.isTimeLimitedItem()) {
            itemToRemove.endOfLife();
        }

        if (itemToRemove.isEquipped()) {
            if (itemToRemove.getEnchantLevel() > 0) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED);
                sm.addInt(itemToRemove.getEnchantLevel());
                sm.addItemName(itemToRemove);
                client.sendPacket(sm);
            } else {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
                sm.addItemName(itemToRemove);
                client.sendPacket(sm);
            }

            final L2ItemInstance[] unequiped = activeChar.getInventory().unEquipItemInSlotAndRecord(itemToRemove.getLocationSlot());

            final InventoryUpdate iu = new InventoryUpdate();
            for (L2ItemInstance itm : unequiped) {
                iu.addModifiedItem(itm);
            }
            activeChar.sendInventoryUpdate(iu);
        }

        final L2ItemInstance removedItem = activeChar.getInventory().destroyItem("Destroy", itemToRemove, count, activeChar, null);

        if (removedItem == null) {
            return;
        }

        if (!Config.FORCE_INVENTORY_UPDATE) {
            final InventoryUpdate iu = new InventoryUpdate();
            if (removedItem.getCount() == 0) {
                iu.addRemovedItem(removedItem);
            } else {
                iu.addModifiedItem(removedItem);
            }
            activeChar.sendInventoryUpdate(iu);
        } else {
            activeChar.sendItemList();
        }
    }
}

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.model.itemcontainer.ItemContainer;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Migi, DS
 */
public final class RequestPostAttachment extends ClientPacket {
    private int _msgId;

    @Override
    public void readImpl() {
        _msgId = readInt();
    }

    @Override
    public void runImpl() {
        if (!getSettings(GeneralSettings.class).allowMail() || !Config.ALLOW_ATTACHMENTS) {
            return;
        }

        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("getattach")) {
            return;
        }

        if (!activeChar.getAccessLevel().allowTransaction()) {
            activeChar.sendMessage("Transactions are disabled for your Access Level");
            return;
        }

        if (!activeChar.isInsideZone(ZoneType.PEACE)) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }

        if (activeChar.getActiveTradeList() != null) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_DURING_AN_EXCHANGE);
            return;
        }

        if (activeChar.hasItemRequest()) {
            client.sendPacket(SystemMessageId.YOU_CAN_T_RECEIVE_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE_COMBINING_JEWELS_OR_SEALING_UNSEALING_OR_COMBINING);
            return;
        }

        if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }

        final Message msg = MailManager.getInstance().getMessage(_msgId);
        if (msg == null) {
            return;
        }

        if (msg.getReceiverId() != activeChar.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to get not own attachment!");
            return;
        }

        if (!msg.hasAttachments()) {
            return;
        }

        final ItemContainer attachments = msg.getAttachments();
        if (attachments == null) {
            return;
        }

        int weight = 0;
        int slots = 0;

        for (Item item : attachments.getItems()) {
            if (item == null) {
                continue;
            }

            // Calculate needed slots
            if (item.getOwnerId() != msg.getSenderId()) {
                GameUtils.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to get wrong item (ownerId != senderId) from attachment!");
                return;
            }

            if (item.getItemLocation() != ItemLocation.MAIL) {
                GameUtils.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to get wrong item (Location != MAIL) from attachment!");
                return;
            }

            if (item.getLocationSlot() != msg.getId()) {
                GameUtils.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to get items from different attachment!");
                return;
            }

            weight += item.getCount() * item.getTemplate().getWeight();
            if (!item.isStackable()) {
                slots += item.getCount();
            } else if (activeChar.getInventory().getItemByItemId(item.getId()) == null) {
                slots++;
            }
        }

        // Item Max Limit Check
        if (!activeChar.getInventory().validateCapacity(slots)) {
            client.sendPacket(SystemMessageId.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return;
        }

        // Weight limit Check
        if (!activeChar.getInventory().validateWeight(weight)) {
            client.sendPacket(SystemMessageId.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return;
        }

        final long adena = msg.getReqAdena();
        if ((adena > 0) && !activeChar.reduceAdena("PayMail", adena, null, true)) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
            return;
        }

        // Proceed to the transfer
        final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
        for (Item item : attachments.getItems()) {
            if (item == null) {
                continue;
            }

            if (item.getOwnerId() != msg.getSenderId()) {
                GameUtils.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to get item with owner != sender !");
                return;
            }

            final long count = item.getCount();
            final Item newItem = attachments.transferItem(attachments.getName(), item.getObjectId(), item.getCount(), activeChar.getInventory(), activeChar, null);
            if (newItem == null) {
                return;
            }

            if (playerIU != null) {
                if (newItem.getCount() > count) {
                    playerIU.addModifiedItem(newItem);
                } else {
                    playerIU.addNewItem(newItem);
                }
            }
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S2_S1);
            sm.addItemName(item.getId());
            sm.addLong(count);
            client.sendPacket(sm);
        }

        // Send updated item list to the player
        if (playerIU != null) {
            activeChar.sendInventoryUpdate(playerIU);
        } else {
            activeChar.sendItemList();
        }

        msg.removeAttachments();

        SystemMessage sm;
        final Player sender = World.getInstance().findPlayer(msg.getSenderId());
        if (adena > 0) {
            if (sender != null) {
                sender.addAdena("PayMail", adena, activeChar, false);
                sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HAS_MADE_A_PAYMENT_OF_S1_ADENA_PER_YOUR_PAYMENT_REQUEST_MAIL);
                sm.addLong(adena);
                sm.addString(activeChar.getName());
                sender.sendPacket(sm);
            } else {
                final Item paidAdena = ItemEngine.getInstance().createItem("PayMail", CommonItem.ADENA, adena, activeChar, null);
                paidAdena.setOwnerId(msg.getSenderId());
                paidAdena.setItemLocation(ItemLocation.INVENTORY);
                paidAdena.updateDatabase(true);
                World.getInstance().removeObject(paidAdena);
            }
        } else if (sender != null) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_ACQUIRED_THE_ATTACHED_ITEM_TO_YOUR_MAIL);
            sm.addString(activeChar.getName());
            sender.sendPacket(sm);
        }

        client.sendPacket(new ExChangePostState(true, _msgId, Message.READED));
        client.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_RECEIVED);
    }
}

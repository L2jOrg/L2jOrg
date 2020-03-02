package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.model.itemcontainer.ItemContainer;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Migi, DS
 */
public final class RequestCancelPostAttachment extends ClientPacket {
    private int _msgId;

    @Override
    public void readImpl() {
        _msgId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (isNull(activeChar) || !getSettings(GeneralSettings.class).allowMail() || !Config.ALLOW_ATTACHMENTS) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("cancelpost")) {
            return;
        }

        final Message msg = MailManager.getInstance().getMessage(_msgId);
        if (msg == null) {
            return;
        }
        if (msg.getSenderId() != activeChar.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to cancel not own post!");
            return;
        }

        if (!activeChar.isInsideZone(ZoneType.PEACE)) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }

        if (activeChar.getActiveTradeList() != null) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE);
            return;
        }

        if (activeChar.hasItemRequest()) {
            activeChar.sendPacket(SystemMessageId.YOU_CAN_T_CANCEL_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE);
            return;
        }

        if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }

        if (!msg.hasAttachments()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_SENT_MAIL_SINCE_THE_RECIPIENT_RECEIVED_IT);
            return;
        }

        final ItemContainer attachments = msg.getAttachments();
        if ((attachments == null) || (attachments.getSize() == 0)) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_SENT_MAIL_SINCE_THE_RECIPIENT_RECEIVED_IT);
            return;
        }

        int weight = 0;
        int slots = 0;

        for (Item item : attachments.getItems()) {
            if (item == null) {
                continue;
            }

            if (item.getOwnerId() != activeChar.getObjectId()) {
                GameUtils.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to get not own item from cancelled attachment!");
                return;
            }

            if (item.getItemLocation() != ItemLocation.MAIL) {
                GameUtils.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " tried to get items not from mail !");
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

        if (!activeChar.getInventory().validateCapacity(slots)) {
            activeChar.sendPacket(SystemMessageId.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return;
        }

        if (!activeChar.getInventory().validateWeight(weight)) {
            activeChar.sendPacket(SystemMessageId.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return;
        }

        // Proceed to the transfer
        final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
        for (Item item : attachments.getItems()) {
            if (item == null) {
                continue;
            }

            final long count = item.getCount();
            final Item newItem = attachments.transferItem(attachments.getName(), item.getObjectId(), count, activeChar.getInventory(), activeChar, null);
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
            activeChar.sendPacket(sm);
        }

        msg.removeAttachments();

        // Send updated item list to the player
        if (playerIU != null) {
            activeChar.sendInventoryUpdate(playerIU);
        } else {
            activeChar.sendItemList();
        }

        final Player receiver = World.getInstance().findPlayer(msg.getReceiverId());
        if (receiver != null) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANCELED_THE_SENT_MAIL);
            sm.addString(activeChar.getName());
            receiver.sendPacket(sm);
            receiver.sendPacket(new ExChangePostState(true, _msgId, Message.DELETED));
        }

        MailManager.getInstance().deleteMessageInDb(_msgId);

        activeChar.sendPacket(new ExChangePostState(false, _msgId, Message.DELETED));
        activeChar.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_CANCELLED);
    }
}

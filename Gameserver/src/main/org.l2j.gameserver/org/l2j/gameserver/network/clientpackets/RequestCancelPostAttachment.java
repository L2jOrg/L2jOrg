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
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExChangePostState;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Migi, DS
 */
public final class RequestCancelPostAttachment extends ClientPacket {
    private int mailId;

    @Override
    public void readImpl() {
        mailId = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (!getSettings(GeneralSettings.class).allowMail() || !Config.ALLOW_ATTACHMENTS) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("cancelpost")) {
            return;
        }

        final var mail = MailEngine.getInstance().getMail(mailId);
        if (isNull(mail)) {
            return;
        }
        if (mail.getSender() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, "Player " + player + " tried to cancel not own post!");
            return;
        }

        if (!player.isInsideZone(ZoneType.PEACE)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }

        if (player.getActiveTradeList() != null) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE);
            return;
        }

        if (player.hasItemRequest()) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_CANCEL_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE);
            return;
        }

        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }

        if (!mail.hasAttachments()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_SENT_MAIL_SINCE_THE_RECIPIENT_RECEIVED_IT);
            return;
        }

        final ItemContainer attachments = mail.getAttachment();
        if ((attachments == null) || (attachments.getSize() == 0)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_SENT_MAIL_SINCE_THE_RECIPIENT_RECEIVED_IT);
            return;
        }

        int weight = 0;
        int slots = 0;

        for (Item item : attachments.getItems()) {
            if (item == null) {
                continue;
            }

            if (item.getOwnerId() != player.getObjectId()) {
                GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to get not own item from cancelled attachment!");
                return;
            }

            if (item.getItemLocation() != ItemLocation.MAIL) {
                GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to get items not from mail !");
                return;
            }

            if (item.getLocationSlot() != mail.getId()) {
                GameUtils.handleIllegalPlayerAction(player, "Player " + player + " tried to get items from different attachment!");
                return;
            }

            weight += item.getCount() * item.getTemplate().getWeight();
            if (!item.isStackable()) {
                slots += item.getCount();
            } else if (player.getInventory().getItemByItemId(item.getId()) == null) {
                slots++;
            }
        }

        if (!player.getInventory().validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return;
        }

        if (!player.getInventory().validateWeight(weight)) {
            player.sendPacket(SystemMessageId.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return;
        }

        // Proceed to the transfer
        final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
        for (Item item : attachments.getItems()) {
            if (item == null) {
                continue;
            }

            final long count = item.getCount();
            final Item newItem = attachments.transferItem(attachments.getName(), item.getObjectId(), count, player.getInventory(), player, null);
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
            player.sendPacket(sm);
        }

        mail.removeAttachments();

        // Send updated item list to the player
        if (playerIU != null) {
            player.sendInventoryUpdate(playerIU);
        } else {
            player.sendItemList();
        }

        final Player receiver = World.getInstance().findPlayer(mail.getReceiver());
        if (receiver != null) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANCELED_THE_SENT_MAIL);
            sm.addString(player.getName());
            receiver.sendPacket(sm);
            receiver.sendPacket(ExChangePostState.deleted(true, mailId));
        }

        MailEngine.getInstance().deleteMailInDb(mailId);

        player.sendPacket(ExChangePostState.deleted(false, mailId));
        player.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_CANCELLED);
    }
}

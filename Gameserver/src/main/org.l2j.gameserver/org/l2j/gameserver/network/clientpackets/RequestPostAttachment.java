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
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.CommonItem;
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

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Migi, DS
 */
public final class RequestPostAttachment extends ClientPacket {
    private int mailId;

    @Override
    public void readImpl() {
        mailId = readInt();
    }

    @Override
    public void runImpl() {
        if (!getSettings(GeneralSettings.class).allowMail() || !Config.ALLOW_ATTACHMENTS) {
            return;
        }

        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("getattach")) {
            return;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level");
            return;
        }

        if (!player.isInsideZone(ZoneType.PEACE)) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }

        if (player.getActiveTradeList() != null) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_DURING_AN_EXCHANGE);
            return;
        }

        if (player.hasItemRequest()) {
            client.sendPacket(SystemMessageId.YOU_CAN_T_RECEIVE_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE_COMBINING_JEWELS_OR_SEALING_UNSEALING_OR_COMBINING);
            return;
        }

        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }

        final var mail = MailEngine.getInstance().getMail(mailId);
        if (mail == null) {
            return;
        }

        if (mail.getReceiver() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, "Player " + player + " tried to get not own attachment!");
            return;
        }

        if (!mail.hasAttachments()) {
            return;
        }

        final ItemContainer attachments = mail.getAttachment();
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
            if (item.getOwnerId() != mail.getSender()) {
                GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to get wrong item (ownerId != senderId) from attachment!");
                return;
            }

            if (item.getItemLocation() != ItemLocation.MAIL) {
                GameUtils.handleIllegalPlayerAction(player, "Player " + player + " tried to get wrong item (Location != MAIL) from attachment!");
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

        // Item Max Limit Check
        if (!player.getInventory().validateCapacity(slots)) {
            client.sendPacket(SystemMessageId.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return;
        }

        // Weight limit Check
        if (!player.getInventory().validateWeight(weight)) {
            client.sendPacket(SystemMessageId.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return;
        }

        final long adena = mail.getFee();
        if ((adena > 0) && !player.reduceAdena("PayMail", adena, null, true)) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
            return;
        }

        // Proceed to the transfer
        final InventoryUpdate playerIU = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
        for (Item item : attachments.getItems()) {
            if (item == null) {
                continue;
            }

            if (item.getOwnerId() != mail.getSender()) {
                GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to get item with owner != sender !");
                return;
            }

            final long count = item.getCount();
            final Item newItem = attachments.transferItem(attachments.getName(), item.getObjectId(), item.getCount(), player.getInventory(), player, null);
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
            player.sendInventoryUpdate(playerIU);
        } else {
            player.sendItemList();
        }

        mail.removeAttachments();

        SystemMessage sm;
        final Player sender = World.getInstance().findPlayer(mail.getSender());
        if (adena > 0) {
            if (sender != null) {
                sender.addAdena("PayMail", adena, player, false);
                sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HAS_MADE_A_PAYMENT_OF_S1_ADENA_PER_YOUR_PAYMENT_REQUEST_MAIL);
                sm.addLong(adena);
                sm.addString(player.getName());
                sender.sendPacket(sm);
            } else {
                final Item paidAdena = ItemEngine.getInstance().createItem("PayMail", CommonItem.ADENA, adena, player, null);
                paidAdena.setOwnerId(mail.getSender());
                paidAdena.setItemLocation(ItemLocation.INVENTORY);
                paidAdena.updateDatabase(true);
                World.getInstance().removeObject(paidAdena);
            }
        } else if (sender != null) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_ACQUIRED_THE_ATTACHED_ITEM_TO_YOUR_MAIL);
            sm.addString(player.getName());
            sender.sendPacket(sm);
        }

        client.sendPacket(ExChangePostState.reAdded(true, mailId));
        client.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_RECEIVED);
    }
}

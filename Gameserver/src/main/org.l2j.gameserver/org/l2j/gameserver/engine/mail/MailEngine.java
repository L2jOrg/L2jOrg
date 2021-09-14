/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.engine.mail;

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.MailDAO;
import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.AccessLevel;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.gameserver.network.SystemMessageId.THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author Migi, DS
 * @author JoeAlisson
 */
public final class MailEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailEngine.class);

    private static final String PAY_MAIL = "PayMail";

    public static final int MAIL_FEE = 100;
    public static final int MAIL_FEE_PER_SLOT = 1000;
    private static final int INBOX_SIZE = 240;
    private static final int OUTBOX_SIZE = 240;

    private IntMap<MailData> mails = Containers.emptyIntMap();
    private IntMap<List<MailData>> inboxes = Containers.emptyIntMap();
    private IntMap<List<MailData>> outBoxes = Containers.emptyIntMap();

    private MailDeletionScheduler expireTask;

    private MailEngine() {
    }

    private void load() {
        mails = getDAO(MailDAO.class).findAll();
        inboxes = new HashIntMap<>();
        outBoxes = new HashIntMap<>();
        final var currentTime = System.currentTimeMillis();
        mails.values().forEach(m -> {
            if(m.getExpiration() < currentTime) {
                deleteExpiredMail(m);
            } else {
                scheduleExpiration(m);
                addMailToBoxes(m);
            }
        });
    }

    private void scheduleExpiration(MailData mail) {
        if(expireTask == null) {
            expireTask = new MailDeletionScheduler();
        }
        expireTask.add(mail);
    }

    private void addMailToBoxes(MailData mail) {
        if(!mail.isDeletedByReceiver()) {
            inboxes.computeIfAbsent(mail.getReceiver(), i -> new ArrayList<>()).add(mail);
        }

        if(!mail.isDeletedBySender()) {
            outBoxes.computeIfAbsent(mail.getSender(), i -> new ArrayList<>()).add(mail);
        }
    }

    public List<MailData> getInbox(int playerId) {
        return inboxes.getOrDefault(playerId, Collections.emptyList());
    }

    public int getInboxSize(int playerId) {
        return getInbox(playerId).size();
    }

    public List<MailData> getOutbox(int playerId) {
        return outBoxes.getOrDefault(playerId, Collections.emptyList());
    }

    public int getOutboxSize(int playerId) {
        return getOutbox(playerId).size();
    }

    public void sendUnreadCount(Player player) {
        final var unread = getUnreadCount(player);
        if(unread > 0) {
            player.sendPacket(ExNoticePostArrived.valueOf(false));
        }
        player.sendPacket(new ExUnReadMailCount(unread));
    }

    private int getUnreadCount(Player player) {
        var count = 0;
        for (var mail : getInbox(player.getObjectId())) {
            if(mail.isUnread()) {
                count++;
            }
        }
        return count;
    }

    public boolean hasMailInProgress(int playerId) {
        return hasMailInProgress(getInbox(playerId)) || hasMailInProgress(getOutbox(playerId));
    }

    private boolean hasMailInProgress(List<MailData> inbox) {
        for (var mail : inbox) {
            if (mail.getType() == MailType.REGULAR && !mail.isReturned() && mail.hasAttachments()) {
                return true;
            }
        }
        return false;
    }

    public void sendMail(MailData mail) {
        getDAO(MailDAO.class).save(mail);
        mails.put(mail.getId(), mail);
        addMailToBoxes(mail);

        doIfNonNull(World.getInstance().findPlayer(mail.getReceiver()), receiver ->
                receiver.sendPackets(ExNoticePostArrived.valueOf(true), new ExUnReadMailCount(getUnreadCount(receiver))));

        scheduleExpiration(mail);
    }

    public void sendMail(Player sender, String receiver, boolean isPayment, String subject, String content, long requiredAdena, List<ItemHolder> items) {
        var receiverId = PlayerNameTable.getInstance().getIdByName(receiver);

        if(!canSendMail(sender, receiver, receiverId, items, requiredAdena, isPayment) || !chargeFee(sender, items)) {
            return;
        }

        MailData msg = MailData.of(sender.getObjectId(), receiverId, isPayment, subject, content, requiredAdena, MailType.REGULAR);
        if(attach(sender, msg, items)) {
            sendMail(msg);
        }
        sender.sendPacket(ExNoticePostSent.valueOf(true));
        sender.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_SENT);
    }

    private boolean canSendMail(Player player, String receiver, int receiverId, List<ItemHolder> items, long requireAdena, boolean isPayment) {
        if (isBlockedToSendMail(player, items)) return false;

        if ((requireAdena < 0) || (requireAdena > CharacterSettings.maxAdena())) {
            return false;
        }

        if (isPayment) {
            if (requireAdena == 0) {
                player.sendPacket(SystemMessageId.WHEN_NOT_ENTERING_THE_AMOUNT_FOR_THE_PAYMENT_REQUEST_YOU_CANNOT_SEND_ANY_MAIL);
                return false;
            }
            if (items.isEmpty()) {
                player.sendPacket(SystemMessageId.IT_S_A_PAYMENT_REQUEST_TRANSACTION_PLEASE_ATTACH_THE_ITEM);
                return false;
            }
        }

        if (isBlockedReceiver(player, receiver, receiverId)) return false;

        return !isMailBoxFull(player, receiverId);
    }

    private boolean isBlockedToSendMail(Player player, List<ItemHolder> items) {
        if (!player.isInsideZone(ZoneType.PEACE) && !items.isEmpty()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION);
            return true;
        }

        if (player.isJailed() && ((Config.JAIL_DISABLE_TRANSACTION && !items.isEmpty()) || GeneralSettings.disableChatInJail())) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION);
            return true;
        }

        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level.");
            return true;
        }

        if (player.getActiveTradeList() != null) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE);
            return true;
        }

        if (player.hasItemRequest()) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_SEND_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE_COMBINING_JEWELS_OR_SEALING_UNSEALING_OR_COMBINING);
            return true;
        }

        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS);
            return true;
        }
        return false;
    }

    private boolean isBlockedReceiver(Player player, String receiver, int receiverId) {
        if (receiverId <= 0) {
            player.sendPacket(SystemMessageId.WHEN_THE_RECIPIENT_DOESN_T_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE);
            return true;
        }

        if (receiverId == player.getObjectId()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF);
            return true;
        }

        final int level = PlayerNameTable.getInstance().getAccessLevelById(receiverId);
        final AccessLevel accessLevel = AdminData.getInstance().getAccessLevel(level);

        if ((accessLevel != null) && accessLevel.isGM() && !player.getAccessLevel().isGM()) {
            player.sendPacket(getSystemMessage(SystemMessageId.YOUR_MESSAGE_TO_C1_DID_NOT_REACH_ITS_RECIPIENT_YOU_CANNOT_SEND_MAIL_TO_THE_GM_STAFF).addString(receiver));
            return true;
        }

        if (BlockList.isInBlockList(receiverId, player.getObjectId())) {
            player.sendPacket(getSystemMessage(SystemMessageId.C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1).addString(receiver));
            return true;
        }
        return false;
    }

    private boolean isMailBoxFull(Player player, int receiverId) {
        if (MailEngine.getInstance().getOutboxSize(player.getObjectId()) >= OUTBOX_SIZE) {
            player.sendPacket(SystemMessageId.THE_MAIL_LIMIT_240_HAS_BEEN_EXCEEDED_AND_THIS_CANNOT_BE_FORWARDED);
            return true;
        }

        if (MailEngine.getInstance().getInboxSize(receiverId) >= INBOX_SIZE) {
            player.sendPacket(SystemMessageId.THE_MAIL_LIMIT_240_HAS_BEEN_EXCEEDED_AND_THIS_CANNOT_BE_FORWARDED);
            return true;
        }
        return false;
    }

    private boolean attach(Player sender, MailData mail, List<ItemHolder> items) {
        if(isNullOrEmpty(items)) {
            return true;
        }

        final var attachment = new Attachment(mail.getSender(), mail.getId());
        mail.attach(attachment);
        final InventoryUpdate playerIU = new InventoryUpdate();
        for (var item : items) {
            final Item oldItem = sender.checkItemManipulation(item.getId(), item.getCount(), "attach");
            if ((oldItem == null) || !oldItem.isTradeable() || oldItem.isEquipped()) {
                LOGGER.warn("Error adding attachment for player {} (olditem == null)", sender);
                return false;
            }

            final Item newItem = sender.getInventory().transferItem("SendMail", item.getId(), item.getCount(), attachment, sender, mail.getReceiverName() + "[" + mail.getReceiver() + "]");
            if (newItem == null) {
                LOGGER.warn("Error adding attachment for player {} (newitem == null)", sender);
                continue;
            }
            newItem.changeItemLocation(newItem.getItemLocation(), mail.getId());

            if ((oldItem.getCount() > 0) && (oldItem != newItem)) {
                playerIU.addModifiedItem(oldItem);
            } else {
                playerIU.addRemovedItem(oldItem);
            }
        }
        sender.sendInventoryUpdate(playerIU);
        return true;
    }

    private boolean chargeFee(Player player, List<ItemHolder> items) {
        long currentAdena = player.getAdena();
        long fee = MAIL_FEE;

        for (var i : items) {
            final Item item = player.checkItemManipulation(i.getId(), i.getCount(), "attach");
            if ((item == null) || !item.isTradeable() || item.isEquipped()) {
                player.sendPacket(SystemMessageId.THE_ITEM_THAT_YOU_RE_TRYING_TO_SEND_CANNOT_BE_FORWARDED_BECAUSE_IT_ISN_T_PROPER);
                return false;
            }

            fee += MAIL_FEE_PER_SLOT;

            if (item.getId() == CommonItem.ADENA) {
                currentAdena -= i.getCount();
            }
        }

        // Check if enough adena and charge the fee
        if ((currentAdena < fee) || !player.reduceAdena("MailFee", fee, null, false)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_FORWARD_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
            return false;
        }
        return true;
    }

    public void receiveMail(Player player, int mailId) {
        final var mail = mails.get(mailId);
        if (mail == null) {
            return;
        }

        if (mail.getReceiver() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, player + " tried to receive not own post!");
            return;
        }

        if (!player.isInsideZone(ZoneType.PEACE) && mail.hasAttachments()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }

        if (mail.isDeletedByReceiver()) {
            return;
        }

        player.sendPackets(new ExReplyReceivedPost(mail), ExChangePostState.reAdded(true, mailId));
        markAsRead(player, mail);
    }

    private void markAsRead(Player player, MailData mail) {
        if(mail.isUnread()) {
            mail.markAsRead();
            getDAO(MailDAO.class).markAsRead(mail.getId());
            player.sendPacket(new ExUnReadMailCount(getUnreadCount(player)));
        }
    }

    public void deleteSentMails(Player player, int[] mailIds) {
        for (int mailId : mailIds) {
            final var mail =  mails.get(mailId);
            if (mail == null) {
                continue;
            }
            if (mail.getSender() != player.getObjectId()) {
                GameUtils.handleIllegalPlayerAction(player, player + " tried to delete not own post!");
                return;
            }

            if (mail.hasAttachments() || mail.isDeletedBySender()) {
                return;
            }

            if (mail.isDeletedByReceiver()) {
                deleteMail(mail);
            } else {
                mail.setDeletedBySender();
                getOutbox(player.getObjectId()).remove(mail);
                getDAO(MailDAO.class).markAsDeletedBySender(mailId);

            }
        }
        player.sendPacket(ExChangePostState.deleted(false, mailIds));
    }

    private void deleteMail(MailData mail) {
        if(!mail.isDeletedByReceiver()) {
            getInbox(mail.getReceiver()).remove(mail);
        }

        if(!mail.isDeletedBySender()) {
            getOutbox(mail.getSender()).remove(mail);
        }
        mails.remove(mail.getId());

        getDAO(MailDAO.class).deleteById(mail.getId());
        IdFactory.getInstance().releaseId(mail.getId());
    }

    public void deleteReceivedMails(Player player, int[] mailIds) {
        for (int mailId : mailIds) {
            final var mail = mails.get(mailId);
            if (mail == null) {
                continue;
            }
            if (mail.getReceiver() != player.getObjectId()) {
                GameUtils.handleIllegalPlayerAction(player, player + " tried to delete not own post!");
                return;
            }

            if (mail.hasAttachments() || mail.isDeletedByReceiver()) {
                return;
            }

            mail.setDeletedByReceiver();
            if (mail.isDeletedBySender()) {
                deleteMail(mail);
            } else {
                getDAO(MailDAO.class).markAsDeletedByReceiver(mailId);
                getInbox(mail.getReceiver()).remove(mail);
            }
        }
        player.sendPacket(ExChangePostState.deleted(true, mailIds));
    }

    void deleteExpiredMail(MailData mail) {
        if (mail.hasAttachments()) {
            doIfNonNull(mail.getAttachment(), attachment -> {
                final var sender = World.getInstance().findPlayer(mail.getSender());
                if(nonNull(sender)) {
                    attachment.returnToWh(sender.getWarehouse());
                    sender.sendPacket(THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME);
                } else {
                    attachment.returnToWh(null);
                }
                attachment.deleteMe();
            });
            if(mail.removeAttachments()) {
                getDAO(MailDAO.class).deleteAttachment(mail.getId());
            }
            doIfNonNull(World.getInstance().findPlayer(mail.getReceiver()), receiver -> receiver.sendPacket(getSystemMessage(THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME)));
        }
        deleteMail(mail);
    }

    public void cancelMailAttachment(Player player, int mailId) {
        final var mail = mails.get(mailId);
        if (!canCancelPostAttachment(player, mail)) {
            return;
        }

        final var attachments = mail.getAttachment();
        if (attachments == null || attachments.getSize() == 0) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_SENT_MAIL_SINCE_THE_RECIPIENT_RECEIVED_IT);
            return;
        }

        if (!checkInventoryLimits(player, mail, attachments)) {
            return;
        }

        cancelPostAttachment(player, mail, attachments);
    }

    private void cancelPostAttachment(Player player, MailData mail, ItemContainer attachments) {
        final var playerIU =  new InventoryUpdate();
        for (var item : attachments.getItems()) {
            final var count = item.getCount();
            final var newItem = attachments.transferItem(attachments.getName(), item.getObjectId(), count, player.getInventory(), player, null);
            if (newItem == null) {
                return;
            }

            if (newItem.getCount() > count) {
                playerIU.addModifiedItem(newItem);
            } else {
                playerIU.addNewItem(newItem);
            }
            player.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S2_S1).addItemName(item.getId()).addLong(count));
        }

        if(mail.removeAttachments()) {
            getDAO(MailDAO.class).deleteAttachment(mail.getId());
        }
        player.sendInventoryUpdate(playerIU);

        final var receiver = World.getInstance().findPlayer(mail.getReceiver());
        if (receiver != null) {
            receiver.sendPackets(getSystemMessage(SystemMessageId.S1_CANCELED_THE_SENT_MAIL).addString(player.getName()),
                    ExChangePostState.deleted(true, mail.getId()));
        }

        deleteMail(mail);

        player.sendPacket(ExChangePostState.deleted(false, mail.getId()));
        player.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_CANCELLED);
    }

    private boolean checkInventoryLimits(Player player, MailData mail, ItemContainer attachments) {
        int weight = 0;
        int slots = 0;

        for (var item : attachments.getItems()) {
            if (!canBeRetrievedFromMail(player, mail, item)) {
                return false;
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
            return false;
        }

        if (!player.getInventory().validateWeight(weight)) {
            player.sendPacket(SystemMessageId.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return false;
        }
        return true;
    }

    private boolean canBeRetrievedFromMail(Player player, MailData mail, Item item) {
        if (item.getOwnerId() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, player + " tried to get not own item from cancelled attachment!");
            return false;
        }

        if (item.getItemLocation() != ItemLocation.MAIL) {
            GameUtils.handleIllegalPlayerAction(player, player + " tried to get items not from mail !");
            return false;
        }

        if (item.getLocationSlot() != mail.getId()) {
            GameUtils.handleIllegalPlayerAction(player, player + " tried to get items from different attachment!");
            return false;
        }
        return true;
    }

    private boolean canCancelPostAttachment(Player player, MailData mail) {
        if (isNull(mail)) {
            return false;
        }
        if (mail.getSender() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, player + " tried to cancel not own post!");
            return false;
        }

        if (!player.isInsideZone(ZoneType.PEACE)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_IN_A_NON_PEACE_ZONE_LOCATION);
            return false;
        }

        if (player.getActiveTradeList() != null) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE);
            return false;
        }

        if (player.hasItemRequest()) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_CANCEL_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE);
            return false;
        }

        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS);
            return false;
        }

        if (!mail.hasAttachments()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_CANCEL_SENT_MAIL_SINCE_THE_RECIPIENT_RECEIVED_IT);
            return false;
        }
        return true;
    }

    public void retrieveAttachment(Player player, int mailId) {
        if (!canReceiveMailAttachment(player)) {
            return;
        }

        final var mail = mails.get(mailId);
        if (!hasAttachment(player, mail)){
            return;
        }

        final var attachments = mail.getAttachment();
        if (attachments == null) {
            return;
        }

        int weight = 0;
        int slots = 0;

        for (var item : attachments.getItems()) {
            if (!canItemBeAttached(player, mail, item)) {
                return;
            }

            weight += item.getCount() * item.getTemplate().getWeight();
            if (!item.isStackable()) {
                slots += item.getCount();
            } else if (player.getInventory().getItemByItemId(item.getId()) == null) {
                slots++;
            }
        }

        if(!canReceiveAttachments(player, mail, weight, slots)) {
            return;
        }

        receiveAttachments(player, mail, attachments);
    }

    private void receiveAttachments(Player player, MailData mail, ItemContainer attachments) {
        final var playerIU =  new InventoryUpdate();
        for (Item item : attachments.getItems()) {
            if (!receiveAttachment(player, mail, attachments, playerIU, item)){
                return;
            }
        }

        player.sendInventoryUpdate(playerIU);
        if(mail.removeAttachments()) {
            getDAO(MailDAO.class).deleteAttachment(mail.getId());
        }

        chargeAttachmentsFee(player, mail);
        player.sendPacket(ExChangePostState.reAdded(true, mail.getId()));
        player.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_RECEIVED);
    }

    private void chargeAttachmentsFee(Player player, MailData mail) {
        final Player sender = World.getInstance().findPlayer(mail.getSender());
        var adena = mail.getFee();
        if (adena > 0) {
            if (sender != null) {
                sender.addAdena(PAY_MAIL, adena, player, false);
                sender.sendPacket(getSystemMessage(SystemMessageId.S2_HAS_MADE_A_PAYMENT_OF_S1_ADENA_PER_YOUR_PAYMENT_REQUEST_MAIL).addLong(adena).addString(player.getName()));
            } else {
                final var paidAdena = ItemEngine.getInstance().createItem(PAY_MAIL, CommonItem.ADENA, adena, player, null);
                paidAdena.changeOwner(mail.getSender());
                paidAdena.changeItemLocation(ItemLocation.INVENTORY);
                paidAdena.updateDatabase(true);
                World.getInstance().removeObject(paidAdena);
            }
        } else if (sender != null) {
            sender.sendPacket(getSystemMessage(SystemMessageId.S1_ACQUIRED_THE_ATTACHED_ITEM_TO_YOUR_MAIL).addString(player.getName()));
        }
    }

    private boolean canReceiveAttachments(Player player, MailData mail, int weight, int slots) {
        if (!player.getInventory().validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return false;
        }

        if (!player.getInventory().validateWeight(weight)) {
            player.sendPacket(SystemMessageId.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
            return false;
        }

        final long adena = mail.getFee();
        if ((adena > 0) && !player.reduceAdena(PAY_MAIL, adena, null, true)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
            return false;
        }
        return true;
    }

    private boolean receiveAttachment(Player player, MailData mail, ItemContainer attachments, InventoryUpdate playerIU, Item item) {
        if (item.getOwnerId() != mail.getSender()) {
            GameUtils.handleIllegalPlayerAction(player, player + " tried to get item with owner != sender !");
            return false;
        }

        final long count = item.getCount();
        final Item newItem = attachments.transferItem(attachments.getName(), item.getObjectId(), item.getCount(), player.getInventory(), player, null);
        if (newItem == null) {
            return false;
        }

        if (newItem.getCount() > count) {
            playerIU.addModifiedItem(newItem);
        } else {
            playerIU.addNewItem(newItem);
        }
        player.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S2_S1).addItemName(item.getId()).addLong(count));
        return true;
    }

    private boolean canItemBeAttached(Player player, MailData mail, Item item) {
        if (item.getOwnerId() != mail.getSender()) {
            GameUtils.handleIllegalPlayerAction(player, player + " tried to get wrong item (ownerId != senderId) from attachment!");
            return false;
        }

        if (item.getItemLocation() != ItemLocation.MAIL) {
            GameUtils.handleIllegalPlayerAction(player, player + " tried to get wrong item (Location != MAIL) from attachment!");
            return false;
        }

        if (item.getLocationSlot() != mail.getId()) {
            GameUtils.handleIllegalPlayerAction(player, player + " tried to get items from different attachment!");
            return false;
        }
        return true;
    }

    private boolean hasAttachment(Player player, MailData mail) {
        if (mail == null) {
            return false;
        }

        if (mail.getReceiver() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, player + " tried to get not own attachment!");
            return false;
        }

        return mail.hasAttachments();
    }

    private boolean canReceiveMailAttachment(Player player) {
        if (!player.getAccessLevel().allowTransaction()) {
            player.sendMessage("Transactions are disabled for your Access Level");
            return false;
        }

        if (!player.isInsideZone(ZoneType.PEACE)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_IN_A_NON_PEACE_ZONE_LOCATION);
            return false;
        }

        if (player.getActiveTradeList() != null) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_DURING_AN_EXCHANGE);
            return false;
        }

        if (player.hasItemRequest()) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_RECEIVE_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE_COMBINING_JEWELS_OR_SEALING_UNSEALING_OR_COMBINING);
            return false;
        }

        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS);
            return false;
        }
        return true;
    }

    public void rejectMailAttachment(Player player, int mailId) {
        if (!player.isInsideZone(ZoneType.PEACE)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }

        final var mail = mails.get(mailId);
        if (mail == null) {
            return;
        }

        if (mail.getReceiver() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, player + " tried to reject not own attachment!");
            return;
        }

        if (!mail.hasAttachments() || mail.getType() != MailType.REGULAR) {
            return;
        }

        sendMail(mail.asReturned());
        getDAO(MailDAO.class).deleteAttachment(mail.getId());

        player.sendPacket(SystemMessageId.MAIL_SUCCESSFULLY_RETURNED);
        player.sendPacket(ExChangePostState.rejected(true, mailId));

        Util.doIfNonNull(World.getInstance().findPlayer(mail.getSender()), sender -> sender.sendPacket(getSystemMessage(SystemMessageId.S1_RETURNED_THE_MAIL).addString(player.getName())));
    }

    public void replySentMail(Player player, int mailId) {
        final var mail = mails.get(mailId);
        if (mail == null) {
            return;
        }

        if (!player.isInsideZone(ZoneType.PEACE) && mail.hasAttachments()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
            return;
        }

        if (mail.getSender() != player.getObjectId()) {
            GameUtils.handleIllegalPlayerAction(player, player + " tried to read not own post!");
            return;
        }

        if (mail.isDeletedBySender()) {
            return;
        }

        player.sendPacket(new ExReplySentPost(mail));
    }

    public static void init() {
        getInstance().load();
    }

    public static MailEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final MailEngine INSTANCE = new MailEngine();
    }
}

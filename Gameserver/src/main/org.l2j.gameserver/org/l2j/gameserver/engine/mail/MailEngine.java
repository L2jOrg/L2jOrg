/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.engine.mail;

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.dao.MailDAO;
import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.instancemanager.tasks.MessageDeletionTask;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.container.Attachment;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExNoticePostArrived;
import org.l2j.gameserver.network.serverpackets.ExUnReadMailCount;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * @author Migi, DS
 * @author JoeAlisson
 */
public final class MailEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailEngine.class);

    public static final int MAIL_FEE = 100;
    public static final int MAIL_FEE_PER_SLOT = 1000;

    private IntMap<MailData> mails = Containers.emptyIntMap();

    private MailEngine() {
    }

    private void load() {
        mails = getDAO(MailDAO.class).findAll();
        final var currentTime = System.currentTimeMillis();
        mails.values().forEach(m -> {
            if(m.getExpiration() < currentTime) {
                ThreadPool.schedule(new MessageDeletionTask(m.getId()), 10, TimeUnit.SECONDS);
            } else {
                ThreadPool.schedule(new MessageDeletionTask(m.getId()), m.getExpiration() - System.currentTimeMillis());
            }
        });
    }

    public final MailData getMail(int mailId) {
        return mails.get(mailId);
    }

    public final void sendUnreadCount(Player player) {
        final var unread = getUnreadCount(player);
        if(unread > 0) {
            player.sendPacket(ExNoticePostArrived.valueOf(false));
        }
        player.sendPacket(new ExUnReadMailCount((int) unread));
    }

    public final int getInboxSize(int objectId) {
        return (int) inboxStream(objectId).count();
    }

    public final int getOutboxSize(int objectId) {
        int size = 0;
        for (var mail : mails.values()) {
            if (mail.getSender() == objectId && !mail.isDeletedBySender()) {
                size++;
            }
        }
        return size;
    }

    public final List<MailData> getInbox(int objectId) {
        return inboxStream(objectId).collect(Collectors.toList());
    }

    public Stream<MailData> inboxStream(int objectId) {
        return mails.values().stream().filter(m -> m.getReceiver() == objectId && !m.isDeletedByReceiver());
    }

    public final long getUnreadCount(Player player) {
        return inboxStream(player.getObjectId()).filter(MailData::isUnread).count();
    }

    public boolean hasMailInProgress(int objectId) {
        for (var mail : mails.values()) {
            if (mail.getType() == MailType.REGULAR) {
                if ((mail.getReceiver() == objectId) && !mail.isDeletedByReceiver() && !mail.isReturned() && mail.hasAttachments()) {
                    return true;
                } else if ((mail.getSender() == objectId) && !mail.isDeletedBySender() && !mail.isReturned() && mail.hasAttachments()) {
                    return true;
                }
            }
        }
        return false;
    }

    public final List<MailData> getOutbox(int objectId) {
        return mails.values().stream().filter(m -> m.getSender() == objectId && !m.isDeletedBySender()).collect(Collectors.toList());
    }

    public void sendMail(MailData mail) {
        getDAO(MailDAO.class).save(mail);
        mails.put(mail.getId(), mail);

        doIfNonNull(World.getInstance().findPlayer(mail.getReceiver()), receiver -> {
            receiver.sendPacket(ExNoticePostArrived.valueOf(true), new ExUnReadMailCount((int) getUnreadCount(receiver)));
            receiver.sendPacket();
        });

        ThreadPool.schedule(new MessageDeletionTask(mail.getId()), mail.getExpiration() - System.currentTimeMillis());
    }

    public final void markAsRead(Player player, MailData mail) {
        if(mail.isUnread()) {
            mail.markAsRead();
            getDAO(MailDAO.class).markAsRead(mail.getId());
            player.sendPacket(new ExUnReadMailCount((int) getUnreadCount(player)));

        }
    }

    public final void markAsDeletedBySenderInDb(int mailId) {
        getDAO(MailDAO.class).markAsDeletedBySender(mailId);
    }

    public final void markAsDeletedByReceiverInDb(int mailId) {
        getDAO(MailDAO.class).markAsDeletedByReceiver(mailId);
    }

    public final void removeAttachmentsInDb(int mailId) {
        getDAO(MailDAO.class).deleteAttachment(mailId);
    }

    public final void deleteMailInDb(int mailId) {
        getDAO(MailDAO.class).deleteById(mailId);
        mails.remove(mailId);
        IdFactory.getInstance().releaseId(mailId);
    }

    public boolean sendMail(Player sender, int receiverId, boolean isCod, String subject, String content, long reqAdena, List<ItemHolder> items) {
        if(chargeFee(sender, items)) {
            MailData msg = MailData.of(sender.getObjectId(), receiverId, isCod, subject, content, reqAdena, MailType.REGULAR);
            if(attach(sender, msg, items)) {
                sendMail(msg);
            }
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
            newItem.setItemLocation(newItem.getItemLocation(), mail.getId());

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

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
package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.engine.mail.Attachment;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
@Table("mail")
public class MailData {

    private int id;
    private int sender;
    private int receiver;
    private String subject;
    private String content;
    private long expiration;
    private long fee;

    @Column("has_attachment")
    private boolean hasAttachment;
    private boolean unread;

    @Column("sender_deleted")
    private boolean deletedBySender;
    @Column("receiver_deleted")
    private boolean deletedByReceiver;

    private boolean locked;
    private MailType type;
    private boolean returned;
    private int item;
    private int enchant;

    @NonUpdatable
    private Attachment attachment;
    @NonUpdatable
    private String receiverName;

    public int getId() {
        return id;
    }

    public int getSender() {
        return sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public long getExpiration() {
        return expiration;
    }

    public long getFee() {
        return fee;
    }

    public boolean hasAttachments() {
        return hasAttachment;
    }

    public boolean isUnread() {
        return unread;
    }

    public boolean isDeletedBySender() {
        return deletedBySender;
    }

    public boolean isDeletedByReceiver() {
        return deletedByReceiver;
    }

    public boolean isLocked() {
        return locked;
    }

    public MailType getType() {
        return type;
    }

    public boolean isReturned() {
        return returned;
    }

    public int getItem() {
        return item;
    }

    public int getEnchant() {
        return enchant;
    }

    public final synchronized Attachment getAttachment() {
        if (!hasAttachment) {
            return null;
        }

        if (attachment == null) {
            attachment = new Attachment(sender, id);
            attachment.restore();
        }
        return attachment;
    }

    public final boolean removeAttachments() {
        if (nonNull(attachment)) {
            attachment = null;
            hasAttachment = false;
            return true;
        }
        return false;
    }

    public final void setDeletedByReceiver() {
        deletedByReceiver = true;
    }

    public final void setDeletedBySender() {
        deletedBySender = true;
    }

    public final String getSenderName() {
        if(type == MailType.REGULAR) {
            return PlayerNameTable.getInstance().getNameById(sender);
        }
        return "";
    }

    public String getReceiverName() {
        if (receiverName == null) {
            receiverName = PlayerNameTable.getInstance().getNameById(receiver);
            if (receiverName == null) {
                receiverName = "";
            }
        }
        return receiverName;
    }

    public final void markAsRead() {
        unread = false;
    }

    public void attach(Attachment attachment) {
        this.attachment = attachment;
        hasAttachment = true;
    }

    public MailData asReturned() {
        var returned = new MailData();
        returned.id = IdFactory.getInstance().getNextId();
        returned.sender = sender;
        returned.receiver = sender;
        returned.subject = "";
        returned.content = "";
        returned.expiration = Instant.now().plus(15, ChronoUnit.DAYS).toEpochMilli();
        returned.unread = true;
        returned.deletedBySender = true;
        returned.type = MailType.REGULAR;
        returned.returned = true;
        returned.hasAttachment = true;
        returned.attachment = getAttachment();
        removeAttachments();
        return returned;
    }

    public static MailData of(int senderId, int receiverId, boolean isCod, String subject, String content, long reqAdena, MailType type) {
        final var mail = new MailData();
        mail.id = IdFactory.getInstance().getNextId();
        mail.sender = senderId;
        mail.receiver = receiverId;
        mail.subject = subject;
        mail.content = content;
        mail.fee = reqAdena;
        mail.type = type;
        mail.unread = true;

        if(isCod) {
            mail.expiration = Instant.now().plus(12, ChronoUnit.HOURS).toEpochMilli();
        } else {
            mail.expiration = Instant.now().plus(15, ChronoUnit.DAYS).toEpochMilli();
        }
        return mail;
    }

    public static MailData of(int receiver, Item item, MailType type) {
        final var mail = of(-1, receiver, false, "", item.getName(), 0, type);
        mail.deletedBySender = true;

        if (type == MailType.COMMISSION_ITEM_SOLD) {
            mail.item = item.getId();
            mail.enchant = item.getEnchantLevel();
        } else if (type == MailType.COMMISSION_ITEM_RETURNED) {
            mail.attachment = new Attachment(mail.sender, mail.id);
            mail.attachment.addItem("CommissionReturnItem", item, null, null);
            mail.hasAttachment = true;
        }
        return mail;
    }

    public static MailData of(int receiverId, String subject, String content, MailType type) {
        final var mail = of(-1, receiverId, false, subject, content, 0, type);
        mail.deletedBySender = true;
        return mail;
    }
}

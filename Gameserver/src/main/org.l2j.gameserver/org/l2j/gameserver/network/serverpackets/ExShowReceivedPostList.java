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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.List;

import static org.l2j.gameserver.network.SystemMessageId.THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD;
import static org.l2j.gameserver.network.SystemMessageId.THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED;

/**
 * @author Migi, DS
 * @author JoeAlisson
 */
public class ExShowReceivedPostList extends ServerPacket {
    private final List<MailData> inbox;

    public ExShowReceivedPostList(int objectId) {
        inbox = MailEngine.getInstance().getInbox(objectId);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_RECEIVED_POST_LIST);

        writeInt((int) (System.currentTimeMillis() / 1000));
        writeInt(inbox.size());
        inbox.forEach(this::writeMail);
        writeInt(MailEngine.MAIL_FEE);
        writeInt(MailEngine.MAIL_FEE_PER_SLOT);
    }

    private void writeMail(MailData mail) {
        writeInt(mail.getType().ordinal());

        if (mail.getType() == MailType.COMMISSION_ITEM_SOLD) {
            writeInt(THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD.getId());
        } else if (mail.getType() == MailType.COMMISSION_ITEM_RETURNED) {
            writeInt(THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED.getId());
        }

        writeInt(mail.getId());
        writeString(mail.getSubject());
        writeString(mail.getSenderName());
        writeInt(mail.isLocked());
        writeInt((int) (mail.getExpiration() / 1000));
        writeInt(mail.isUnread());
        writeInt(((mail.getType() == MailType.COMMISSION_ITEM_SOLD) || (mail.getType() == MailType.COMMISSION_ITEM_RETURNED)) ? 0 : 1);
        writeInt(mail.hasAttachments());
        writeInt(mail.isReturned());
        writeInt(0x00); // SysString in some case it seems
    }

}

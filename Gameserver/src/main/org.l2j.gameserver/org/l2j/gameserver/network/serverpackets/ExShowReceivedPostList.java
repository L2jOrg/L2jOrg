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

import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.SystemMessageId;

import java.util.List;

/**
 * @author Migi, DS
 */
public class ExShowReceivedPostList extends ServerPacket {
    private static final int MESSAGE_FEE = 100;
    private static final int MESSAGE_FEE_PER_SLOT = 1000;
    private final List<Message> _inbox;

    public ExShowReceivedPostList(int objectId) {
        _inbox = MailManager.getInstance().getInbox(objectId);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_RECEIVED_POST_LIST);

        writeInt((int) (System.currentTimeMillis() / 1000));
        if ((_inbox != null) && (_inbox.size() > 0)) {
            writeInt(_inbox.size());
            for (Message msg : _inbox) {
                writeInt(msg.getMailType().ordinal());
                if (msg.getMailType() == MailType.COMMISSION_ITEM_SOLD) {
                    writeInt(SystemMessageId.THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD.getId());
                } else if (msg.getMailType() == MailType.COMMISSION_ITEM_RETURNED) {
                    writeInt(SystemMessageId.THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED.getId());
                }
                writeInt(msg.getId());
                writeString(msg.getSubject());
                writeString(msg.getSenderName());
                writeInt(msg.isLocked() ? 0x01 : 0x00);
                writeInt(msg.getExpirationSeconds());
                writeInt(msg.isUnread() ? 0x01 : 0x00);
                writeInt(((msg.getMailType() == MailType.COMMISSION_ITEM_SOLD) || (msg.getMailType() == MailType.COMMISSION_ITEM_RETURNED)) ? 0 : 1);
                writeInt(msg.hasAttachments() ? 0x01 : 0x00);
                writeInt(msg.isReturned() ? 0x01 : 0x00);
                writeInt(0x00); // SysString in some case it seems
            }
        } else {
            writeInt(0x00);
        }
        writeInt(MESSAGE_FEE);
        writeInt(MESSAGE_FEE_PER_SLOT);
    }

}

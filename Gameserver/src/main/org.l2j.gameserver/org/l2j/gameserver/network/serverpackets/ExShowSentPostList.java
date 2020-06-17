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
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.List;

/**
 * @author Migi, DS
 * @author JoeAlisson
 */
public class ExShowSentPostList extends ServerPacket {
    private final List<MailData> outbox;

    public ExShowSentPostList(int objectId) {
        outbox = MailEngine.getInstance().getOutbox(objectId);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_SENT_POST_LIST);

        writeInt((int) (System.currentTimeMillis() / 1000));
        writeInt(outbox.size());
        outbox.forEach(this::writeMail);
    }

    private void writeMail(MailData mail) {
        writeInt(mail.getId());
        writeString(mail.getSubject());
        writeString(mail.getReceiverName());
        writeInt(mail.isLocked());
        writeInt((int) (mail.getExpiration() / 1000));
        writeInt(mail.isUnread());
        writeInt(0x01);
        writeInt(mail.hasAttachments());
        writeInt(0x00);
    }

}

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

import org.l2j.gameserver.engine.mail.MailState;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author Migi
 * @author JoeAlisson
 */
public class ExChangePostState extends ServerPacket {
    private final boolean receivedBoard;
    private final int[] changedMailsId;
    private final MailState state;

    private ExChangePostState(boolean receivedBoard, MailState state, int... mailIds) {
        this.receivedBoard = receivedBoard;
        this.changedMailsId = mailIds;
        this.state = state;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CHANGE_POST_STATE);

        writeInt(receivedBoard);
        writeInt(changedMailsId.length);
        for (int mailId : changedMailsId) {
            writeInt(mailId);
            writeInt(state.ordinal());
        }
    }

    public static ExChangePostState deleted(boolean receivedBoard, int... mailsId) {
        return new ExChangePostState(receivedBoard, MailState.DELETED, mailsId);
    }

    public static ExChangePostState rejected(boolean receiveBoard, int mailId) {
        return new ExChangePostState(receiveBoard, MailState.REJECTED, mailId);
    }

    public static ExChangePostState reAdded(boolean receiveBoard, int mailId) {
        return new ExChangePostState(receiveBoard, MailState.RE_ADDED, mailId);
    }

}

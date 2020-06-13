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

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * ConfirmDlg server packet implementation.
 *
 * @author kombat, UnAfraid
 */
public class ConfirmDlg extends AbstractMessagePacket<ConfirmDlg> {
    private int _time;
    private int _requesterId;

    public ConfirmDlg(SystemMessageId smId) {
        super(smId);
    }

    public ConfirmDlg(String text) {
        this(SystemMessageId.S1);
        addString(text);
    }

    public ConfirmDlg addTime(int time) {
        _time = time;
        return this;
    }

    public ConfirmDlg addRequesterId(int id) {
        _requesterId = id;
        return this;
    }

    @Override
    protected void writeParamsSize(int size) {
        writeInt(size);
    }

    @Override
    protected void writeParamType(int type) {
        writeInt(type);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CONFIRM_DLG);

        writeInt(getId());
        writeMe();
        writeInt(_time);
        writeInt(_requesterId);
    }

}

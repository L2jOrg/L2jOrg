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

import org.l2j.gameserver.data.database.data.CrestData;
import org.l2j.gameserver.data.database.data.CrestData.CrestType;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Client packet for setting ally crest.
 */
public final class RequestSetAllyCrest extends ClientPacket {
    private int _length;
    private byte[] _data = null;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        _length = readInt();
        if (_length > 192) {
            throw new InvalidDataPacketException();
        }

        _data = new byte[_length];
        readBytes(_data);
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (_length < 0) {
            activeChar.sendMessage("File transfer error.");
            return;
        }

        if (_length > 192) {
            activeChar.sendPacket(SystemMessageId.PLEASE_ADJUST_THE_IMAGE_SIZE_TO_8X12);
            return;
        }

        if (activeChar.getAllyId() == 0) {
            activeChar.sendPacket(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
            return;
        }

        final Clan leaderClan = ClanTable.getInstance().getClan(activeChar.getAllyId());

        if ((activeChar.getClanId() != leaderClan.getId()) || !activeChar.isClanLeader()) {
            activeChar.sendPacket(SystemMessageId.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
            return;
        }

        if (_length == 0) {
            if (leaderClan.getAllyCrestId() != 0) {
                leaderClan.changeAllyCrest(0, false);
            }
        } else {
            final CrestData crest = CrestTable.getInstance().createCrest(_data, CrestType.ALLY);
            if (crest != null) {
                leaderClan.changeAllyCrest(crest.getId(), false);
                activeChar.sendPacket(SystemMessageId.THE_CREST_WAS_SUCCESSFULLY_REGISTERED);
            }
        }
    }
}

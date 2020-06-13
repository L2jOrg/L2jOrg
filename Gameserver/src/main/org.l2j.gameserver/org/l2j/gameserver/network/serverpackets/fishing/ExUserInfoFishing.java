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
package org.l2j.gameserver.network.serverpackets.fishing;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExUserInfoFishing extends ServerPacket {
    private final Player _activeChar;
    private final boolean _isFishing;
    private final ILocational _baitLocation;

    public ExUserInfoFishing(Player activeChar, boolean isFishing, ILocational baitLocation) {
        _activeChar = activeChar;
        _isFishing = isFishing;
        _baitLocation = baitLocation;
    }

    public ExUserInfoFishing(Player activeChar, boolean isFishing) {
        _activeChar = activeChar;
        _isFishing = isFishing;
        _baitLocation = null;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_USER_INFO_FISHING);

        writeInt(_activeChar.getObjectId());
        writeByte((byte) (_isFishing ? 1 : 0));
        if (_baitLocation == null) {
            writeInt(0);
            writeInt(0);
            writeInt(0);
        } else {
            writeInt(_baitLocation.getX());
            writeInt(_baitLocation.getY());
            writeInt(_baitLocation.getZ());
        }
    }

}

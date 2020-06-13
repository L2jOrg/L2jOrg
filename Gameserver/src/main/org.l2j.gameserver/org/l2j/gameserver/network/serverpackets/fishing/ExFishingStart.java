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
 * @author -Wooden-
 */
public class ExFishingStart extends ServerPacket {
    private final Player _player;
    private final int _fishType;
    private final int _baitType;
    private final ILocational _baitLocation;

    /**
     * @param player
     * @param fishType
     * @param baitType - 0 = newbie, 1 = normal, 2 = night
     * @param baitLocation
     */
    public ExFishingStart(Player player, int fishType, int baitType, ILocational baitLocation) {
        _player = player;
        _fishType = fishType;
        _baitType = baitType;
        _baitLocation = baitLocation;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_FISHING_START);
        writeInt(_player.getObjectId());
        writeByte((byte) _fishType);
        writeInt(_baitLocation.getX());
        writeInt(_baitLocation.getY());
        writeInt(_baitLocation.getZ());
        writeByte((byte) _baitType);
    }

}

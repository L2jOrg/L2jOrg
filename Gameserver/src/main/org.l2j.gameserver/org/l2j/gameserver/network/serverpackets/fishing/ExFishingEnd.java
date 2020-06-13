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
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author -Wooden-
 */
public class ExFishingEnd extends ServerPacket {
    private final Player _player;
    private final FishingEndReason _reason;

    public ExFishingEnd(Player player, FishingEndReason reason) {
        _player = player;
        _reason = reason;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_FISHING_END);
        writeInt(_player.getObjectId());
        writeByte((byte) _reason.getReason());
    }


    public enum FishingEndReason {
        LOSE(0),
        WIN(1),
        STOP(2);

        private final int _reason;

        FishingEndReason(int reason) {
            _reason = reason;
        }

        public int getReason() {
            return _reason;
        }
    }

    public enum FishingEndType {
        PLAYER_STOP,
        PLAYER_CANCEL,
        ERROR;
    }
}

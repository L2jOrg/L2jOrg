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
package org.l2j.gameserver.network.serverpackets.siege;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExMercenarySiegeHUDInfo extends ServerPacket {

    private final int _castleId;

    public ExMercenarySiegeHUDInfo(int castleId)
    {
        _castleId = castleId;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)  {
        writeId(ServerExPacketId.EX_MERCENARY_CASTLEWAR_CASTLE_SIEGE_HUD_INFO, buffer );
        Castle castle = CastleManager.getInstance().getCastleById(_castleId);
        buffer.writeInt(castle.getId());
        if (castle.getSiege().isInProgress())
        {
            buffer.writeInt(0x01);
            buffer.writeInt((int) (System.currentTimeMillis() / 1000));
            buffer.writeInt((int) castle.getSiege().currentStateRemainTimeInSeconds());        }
        else
        {
            buffer.writeInt(0x00);
            buffer.writeInt((int) (System.currentTimeMillis() / 1000));
            buffer.writeInt((int) castle.getSiege().currentStateRemainTimeInSeconds());
        }
    }

}

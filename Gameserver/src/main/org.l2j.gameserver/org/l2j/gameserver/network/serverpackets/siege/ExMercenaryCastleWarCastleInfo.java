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
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.time.ZoneId;

/**
 * @author JoeAlisson
 */
public class ExMercenaryCastleWarCastleInfo extends AbstractCastleInfo {

    public ExMercenaryCastleWarCastleInfo(Castle castle) {
        super(castle);
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)  {
        writeId(ServerExPacketId.EX_MERCENARY_CASTLEWAR_CASTLE_INFO, buffer );

        writeCastleInfo(buffer);

        buffer.writeInt(castle.getTaxPercent(TaxType.BUY));
        buffer.writeLong((long) (castle.getTreasury() * castle.getTaxRate(TaxType.BUY))); // TODO current incoming
        buffer.writeLong((long) (castle.getTreasury() + castle.getTreasury() * castle.getTaxRate(TaxType.BUY))); // TODO total incoming
        buffer.writeInt((int) castle.getSiege().getSiegeDate().atZone(ZoneId.systemDefault()).toEpochSecond());
    }
}

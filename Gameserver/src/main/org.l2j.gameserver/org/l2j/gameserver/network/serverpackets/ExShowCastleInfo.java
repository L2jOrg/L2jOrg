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

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.Collection;

/**
 * @author KenM
 */
public class ExShowCastleInfo extends ServerPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExShowCastleInfo.class);

    public static final ExShowCastleInfo STATIC_PACKET = new ExShowCastleInfo();

    private ExShowCastleInfo() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_CASTLE_INFO);

        final Collection<Castle> castles = CastleManager.getInstance().getCastles();
        writeInt(castles.size());
        for (Castle castle : castles) {
            writeInt(castle.getId());
            if (castle.getOwnerId() > 0) {
                if (ClanTable.getInstance().getClan(castle.getOwnerId()) != null) {
                    writeString(ClanTable.getInstance().getClan(castle.getOwnerId()).getName());
                } else {
                    LOGGER.warn("Castle owner with no name! Castle: " + castle.getName() + " has an OwnerId = " + castle.getOwnerId() + " who does not have a  name!");
                    writeString("");
                }
            } else {
                writeString("");
            }
            writeInt(castle.getTaxPercent(TaxType.BUY));
            writeInt((int) (castle.getSiege().getSiegeDate().atZone(ZoneId.systemDefault()).toEpochSecond()));

            writeByte((byte)( castle.getSiege().isInProgress() ? 0x01 : 0x00)); // Grand Crusade
            writeByte((byte) castle.getSide().ordinal()); // Grand Crusade
        }
    }

}

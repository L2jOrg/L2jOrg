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
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;

import static java.util.Objects.nonNull;

/**
 * Shows the Siege Info<BR>
 * <BR>
 * c = c9<BR>
 * d = CastleID<BR>
 * d = Show Owner Controls (0x00 default || >=0x02(mask?) owner)<BR>
 * d = Owner ClanID<BR>
 * S = Owner ClanName<BR>
 * S = Owner Clan LeaderName<BR>
 * d = Owner AllyID<BR>
 * S = Owner AllyName<BR>
 * d = current time (seconds)<BR>
 * d = Siege time (seconds) (0 for selectable)<BR>
 * d = (UNKNOW) Siege Time Select Related?
 *
 * @author KenM
 */
public class SiegeInfo extends ServerPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiegeInfo.class);

    private final Castle castle;
    private final Player player;

    public SiegeInfo(Castle castle, Player player) {
        this.castle = castle;
        this.player = player;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CASTLE_SIEGE_INFO);

        if (nonNull(castle)) {
            writeInt(castle.getId());

            final int ownerId = castle.getOwnerId();

            writeInt(((ownerId == player.getClanId()) && (player.isClanLeader())) ? 0x01 : 0x00);
            writeInt(ownerId);
            if (ownerId > 0) {
                final Clan owner = ClanTable.getInstance().getClan(ownerId);
                if (owner != null) {
                    writeString(owner.getName()); // Clan Name
                    writeString(owner.getLeaderName()); // Clan Leader Name
                    writeInt(owner.getAllyId()); // Ally ID
                    writeString(owner.getAllyName()); // Ally Name
                } else {
                    LOGGER.warn("Null owner for castle: {}", castle.getName());
                }
            } else {
                writeString(""); // Clan Name
                writeString(""); // Clan Leader Name
                writeInt(0); // Ally ID
                writeString(""); // Ally Name
            }

            writeInt((int) (System.currentTimeMillis() / 1000));

            var siegeDate = castle.getSiegeDate().atZone(ZoneId.systemDefault());
            writeInt((int) siegeDate.toEpochSecond());
            writeInt(0x00);
        }
    }

}

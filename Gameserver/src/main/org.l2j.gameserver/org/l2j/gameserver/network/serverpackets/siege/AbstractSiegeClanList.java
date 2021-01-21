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
package org.l2j.gameserver.network.serverpackets.siege;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.database.data.SiegeParticipant;
import org.l2j.gameserver.engine.siege.Siege;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * @author JoeAlisson
 */
public abstract class AbstractSiegeClanList extends ServerPacket {

    protected final Siege siege;

    protected AbstractSiegeClanList(Siege siege) {
        this.siege = requireNonNull(siege);
    }

    protected void writeHeader(WritableBuffer buffer, int clanAmount, Player player) {
        final var castle = siege.getCastle();
        boolean isClanOwner = nonNull(castle.getOwner()) && castle.getOwner() == player.getClan();

        buffer.writeInt(castle.getId());
        buffer.writeInt(isClanOwner && player.hasClanPrivilege(ClanPrivilege.CS_MANAGE_SIEGE));
        buffer.writeInt(1); // total pages
        buffer.writeInt(0); // page index
        buffer.writeInt(clanAmount); // total count
        buffer.writeInt(clanAmount); // count in the page
    }

    protected void writeClanInfo(WritableBuffer buffer, Clan clan) {
        buffer.writeInt(clan.getId());
        buffer.writeString(clan.getName());
        buffer.writeString(clan.getLeaderName());
        buffer.writeInt(clan.getCrestId());
        buffer.writeInt((int) (System.currentTimeMillis() / 1000)); // register time
    }

    protected void writeMercenaryInfo(WritableBuffer buffer, SiegeParticipant siegeParticipant) {
        buffer.writeInt(siegeParticipant.isRecruitingMercenary());
        buffer.writeLong(siegeParticipant.getMercenaryReward());
        buffer.writeInt(siegeParticipant.getMercenariesCount());
    }

    protected void writeAllianceInfo(WritableBuffer buffer, Clan clan) {
        buffer.writeInt(clan.getAllyId());
        buffer.writeString(clan.getAllyName());
        buffer.writeString("leader name"); // ally leader name
        buffer.writeInt(clan.getAllyCrestId());
    }
}

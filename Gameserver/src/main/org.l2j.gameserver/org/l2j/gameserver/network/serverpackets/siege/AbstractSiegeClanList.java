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
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static java.util.Objects.requireNonNull;

/**
 * @author JoeAlisson
 */
public abstract class AbstractSiegeClanList extends ServerPacket {

    protected final Castle castle;

    protected AbstractSiegeClanList(Castle castle) {
        this.castle = requireNonNull(castle);
    }

    protected void writeHeader(WritableBuffer buffer, int clanAmount) {
        buffer.writeInt(castle.getId());
        buffer.writeInt(0);
        buffer.writeInt(1);
        buffer.writeInt(0);
        buffer.writeInt(clanAmount);
        buffer.writeInt(clanAmount);
    }

    protected void writeClanInfo(WritableBuffer buffer, Clan clan) {
        buffer.writeInt(clan.getId());
        buffer.writeString(clan.getName());
        buffer.writeString(clan.getLeaderName());
        buffer.writeInt(clan.getCrestId());
        buffer.writeInt(0); // register time
    }

    protected void writeAllianceInfo(WritableBuffer buffer, Clan clan) {
        buffer.writeInt(0);
        buffer.writeLong(0);
        buffer.writeInt(0);
        buffer.writeInt(clan.getAllyId());
        buffer.writeString(clan.getAllyName());
        buffer.writeString(""); // ally leader name
        buffer.writeInt(clan.getAllyCrestId());
    }
}

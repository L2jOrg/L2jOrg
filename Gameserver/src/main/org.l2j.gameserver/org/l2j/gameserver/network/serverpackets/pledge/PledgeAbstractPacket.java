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
package org.l2j.gameserver.network.serverpackets.pledge;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public abstract class PledgeAbstractPacket extends ServerPacket {

    protected final Clan clan;

    public PledgeAbstractPacket(Clan clan) {
        this.clan = clan;
    }

    protected void writeClanInfo(WritableBuffer buffer) {
        buffer.writeInt(clan.getCrestId());
        buffer.writeInt(clan.getLevel());
        buffer.writeInt(clan.getCastleId());
        buffer.writeInt(0x00);
        buffer.writeInt(clan.getHideoutId());
        buffer.writeInt(0x00); // fort Id
        buffer.writeInt(0x00); // rank
        buffer.writeInt(clan.getReputationScore());
        buffer.writeInt(0x00); // 0
        buffer.writeInt(0x00); // 0
        buffer.writeInt(clan.getAllyId());
        buffer.writeString(clan.getAllyName());
        buffer.writeInt(clan.getAllyCrestId());
        buffer.writeInt(clan.isAtWar()); // new c3
        buffer.writeInt(0x00); // Territory castle ID
        buffer.writeInt(clan.getMembersCount());
    }
}

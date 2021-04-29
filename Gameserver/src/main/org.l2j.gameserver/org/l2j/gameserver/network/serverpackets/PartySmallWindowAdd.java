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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class PartySmallWindowAdd extends ServerPacket {
    private final Player member;
    private final Party party;

    public PartySmallWindowAdd(Player member, Party party) {
        this.member = member;
        this.party = party;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.PARTY_SMALL_WINDOW_ADD, buffer );

        buffer.writeInt(party.getLeaderObjectId()); // c3
        buffer.writeInt(party.getDistributionType().getId()); // c3
        buffer.writeInt(member.getObjectId());
        buffer.writeString(member.getName());

        buffer.writeInt((int) member.getCurrentCp()); // c4
        buffer.writeInt(member.getMaxCp()); // c4
        buffer.writeInt((int) member.getCurrentHp());
        buffer.writeInt(member.getMaxHp());
        buffer.writeInt((int) member.getCurrentMp());
        buffer.writeInt(member.getMaxMp());
        buffer.writeInt(member.getSayhaGracePoints());
        buffer.writeByte(member.getLevel());
        buffer.writeShort(member.getClassId().getId());
        buffer.writeByte(0x00);
        buffer.writeShort(member.getRace().ordinal());
        buffer.writeInt(0x00);
    }

}

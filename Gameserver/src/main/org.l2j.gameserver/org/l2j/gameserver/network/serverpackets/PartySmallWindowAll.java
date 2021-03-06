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
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import static org.l2j.commons.util.Util.doIfNonNull;

public final class PartySmallWindowAll extends ServerPacket {
    private final Party _party;
    private final Player _exclude;

    public PartySmallWindowAll(Player exclude, Party party) {
        _exclude = exclude;
        _party = party;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.PARTY_SMALL_WINDOW_ALL, buffer );

        buffer.writeInt(_party.getLeaderObjectId());
        buffer.writeByte(_party.getDistributionType().getId());
        buffer.writeByte(_party.getMemberCount() - 1);

        for (Player member : _party.getMembers()) {
            if ((member != null) && (member != _exclude)) {
                buffer.writeInt(member.getObjectId());
                buffer.writeString(member.getName());

                buffer.writeInt((int) member.getCurrentCp()); // c4
                buffer.writeInt(member.getMaxCp()); // c4

                buffer.writeInt((int) member.getCurrentHp());
                buffer.writeInt(member.getMaxHp());
                buffer.writeInt((int) member.getCurrentMp());
                buffer.writeInt(member.getMaxMp());
                buffer.writeInt(member.getVitalityPoints());
                buffer.writeByte(member.getLevel());
                buffer.writeShort(member.getClassId().getId());
                buffer.writeByte(0x01); // Unk
                buffer.writeShort(member.getRace().ordinal());
                buffer.writeInt(0x00);

                final Summon pet = member.getPet();
                buffer.writeInt(member.getServitors().size() + (pet != null ? 1 : 0)); // Summon size, one only atm

                doIfNonNull(pet, summon -> writeSummonStatus(summon, buffer));
                member.getServitors().values().forEach(summon -> writeSummonStatus(summon, buffer));
            }
        }
    }

    private void writeSummonStatus(Summon summon, WritableBuffer buffer) {
        buffer.writeInt(summon.getObjectId());
        buffer.writeInt(summon.getId() + 1000000);
        buffer.writeByte(summon.getSummonType());
        buffer.writeString(summon.getName());
        buffer.writeInt((int) summon.getCurrentHp());
        buffer.writeInt(summon.getMaxHp());
        buffer.writeInt((int) summon.getCurrentMp());
        buffer.writeInt(summon.getMaxMp());
        buffer.writeByte(summon.getLevel());
    }

}

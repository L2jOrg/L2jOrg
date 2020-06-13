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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PARTY_SMALL_WINDOW_ALL);

        writeInt(_party.getLeaderObjectId());
        writeByte((byte) _party.getDistributionType().getId());
        writeByte((byte) (_party.getMemberCount() - 1));

        for (Player member : _party.getMembers()) {
            if ((member != null) && (member != _exclude)) {
                writeInt(member.getObjectId());
                writeString(member.getName());

                writeInt((int) member.getCurrentCp()); // c4
                writeInt(member.getMaxCp()); // c4

                writeInt((int) member.getCurrentHp());
                writeInt(member.getMaxHp());
                writeInt((int) member.getCurrentMp());
                writeInt(member.getMaxMp());
                writeInt(member.getVitalityPoints());
                writeByte(member.getLevel());
                writeShort(member.getClassId().getId());
                writeByte(0x01); // Unk
                writeShort(member.getRace().ordinal());
                writeInt(0x00);

                final Summon pet = member.getPet();
                writeInt(member.getServitors().size() + (pet != null ? 1 : 0)); // Summon size, one only atm

                doIfNonNull(pet, this::writeSummonStatus);
                member.getServitors().values().forEach(this::writeSummonStatus);
            }
        }
    }

    private void writeSummonStatus(Summon summon) {
        writeInt(summon.getObjectId());
        writeInt(summon.getId() + 1000000);
        writeByte(summon.getSummonType());
        writeString(summon.getName());
        writeInt((int) summon.getCurrentHp());
        writeInt(summon.getMaxHp());
        writeInt((int) summon.getCurrentMp());
        writeInt(summon.getMaxMp());
        writeByte(summon.getLevel());
    }

}

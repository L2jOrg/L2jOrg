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
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.util.GameUtils;

import java.util.ArrayList;
import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isPet;

public class PartySpelled extends ServerPacket {
    private final List<BuffInfo> _effects = new ArrayList<>();
    private final List<Skill> _effects2 = new ArrayList<>();
    private final Creature _activeChar;

    public PartySpelled(Creature cha) {
        _activeChar = cha;
    }

    public void addSkill(BuffInfo info) {
        _effects.add(info);
    }

    public void addSkill(Skill skill) {
        _effects2.add(skill);
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.PARTY_SPELLED_INFO, buffer );

        buffer.writeInt(GameUtils.isServitor(_activeChar) ? 2 : isPet(_activeChar) ? 1 : 0);
        buffer.writeInt(_activeChar.getObjectId());
        buffer.writeInt(_effects.size() + _effects2.size());
        for (BuffInfo info : _effects) {
            if ((info != null) && info.isInUse()) {
                buffer.writeInt(info.getSkill().getDisplayId());
                buffer.writeShort(info.getSkill().getDisplayLevel());
                buffer.writeInt(info.getSkill().getAbnormalType().getClientId());
                writeOptionalD(info.getTime(), buffer);
            }
        }
        for (Skill skill : _effects2) {
            if (skill != null) {
                buffer.writeInt(skill.getDisplayId());
                buffer.writeShort(skill.getDisplayLevel());
                buffer.writeInt(skill.getAbnormalType().getClientId());
                buffer.writeShort(-1);
            }
        }
    }
}

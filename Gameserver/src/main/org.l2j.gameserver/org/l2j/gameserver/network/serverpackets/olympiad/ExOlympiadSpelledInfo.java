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
package org.l2j.gameserver.network.serverpackets.olympiad;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author godson
 */
public class ExOlympiadSpelledInfo extends ServerPacket {
    private final int _playerId;
    private final List<BuffInfo> _effects = new ArrayList<>();
    private final List<Skill> _effects2 = new ArrayList<>();

    public ExOlympiadSpelledInfo(Player player) {
        _playerId = player.getObjectId();
    }

    public void addSkill(BuffInfo info) {
        _effects.add(info);
    }

    public void addSkill(Skill skill) {
        _effects2.add(skill);
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_OLYMPIAD_SPELLED_INFO, buffer );

        buffer.writeInt(_playerId);
        buffer.writeInt(_effects.size() + _effects2.size());
        for (BuffInfo info : _effects) {
            if ((info != null) && info.isInUse()) {
                buffer.writeInt(info.getSkill().getDisplayId());
                buffer.writeInt(info.getSkill().getDisplayLevel());
                buffer.writeShort(0x00); // Sub level
                writeOptionalD(info.getSkill().isAura() ? -1 : info.getTime(), buffer);
            }
        }
        for (Skill skill : _effects2) {
            if (skill != null) {
                buffer.writeInt(skill.getDisplayId());
                buffer.writeInt(skill.getDisplayLevel());
                buffer.writeShort(0x00); // Sub level
                buffer.writeShort(-1);
            }
        }
    }

}

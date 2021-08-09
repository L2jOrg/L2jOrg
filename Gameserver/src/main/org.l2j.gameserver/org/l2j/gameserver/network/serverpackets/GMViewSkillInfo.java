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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

public class GMViewSkillInfo extends ServerPacket {
    private final Player _activeChar;
    private final Collection<Skill> _skills;

    public GMViewSkillInfo(Player cha) {
        _activeChar = cha;
        _skills = _activeChar.getSkillList();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.GM_VIEW_SKILL_INFO, buffer );

        buffer.writeString(_activeChar.getName());
        buffer.writeInt(_skills.size());

        final boolean isDisabled = (_activeChar.getClan() != null) && (_activeChar.getClan().getReputationScore() < 0);

        for (Skill skill : _skills) {
            buffer.writeInt(skill.isPassive() ? 1 : 0);
            buffer.writeShort(skill.getDisplayLevel());
            buffer.writeShort(skill.getSubLevel());
            buffer.writeInt(skill.getDisplayId());
            buffer.writeInt(0x00);
            buffer.writeByte(isDisabled && skill.isClanSkill());
            buffer.writeByte(skill.isEnchantable());
        }
    }

}
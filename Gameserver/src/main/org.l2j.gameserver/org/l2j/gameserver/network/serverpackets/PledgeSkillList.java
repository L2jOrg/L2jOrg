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
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author -Wooden-
 */
public class PledgeSkillList extends ServerPacket {
    private final Skill[] _skills;
    private final SubPledgeSkill[] _subSkills;

    public PledgeSkillList(Clan clan) {
        _skills = clan.getAllSkills();
        _subSkills = clan.getAllSubSkills();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_PLEDGE_SKILL_LIST, buffer );

        buffer.writeInt(_skills.length);
        buffer.writeInt(_subSkills.length); // Squad skill length
        for (Skill sk : _skills) {
            buffer.writeInt(sk.getDisplayId());
            buffer.writeShort(sk.getDisplayLevel());
            buffer.writeShort(0x00); // Sub level
        }
        for (SubPledgeSkill sk : _subSkills) {
            buffer.writeInt(sk._subType); // Clan Sub-unit types
            buffer.writeInt(sk._skillId);
            buffer.writeShort(sk._skillLvl);
            buffer.writeShort(0x00); // Sub level
        }
    }


    public static class SubPledgeSkill {
        int _subType;
        int _skillId;
        int _skillLvl;

        public SubPledgeSkill(int subType, int skillId, int skillLvl) {
            _subType = subType;
            _skillId = skillId;
            _skillLvl = skillLvl;
        }
    }
}

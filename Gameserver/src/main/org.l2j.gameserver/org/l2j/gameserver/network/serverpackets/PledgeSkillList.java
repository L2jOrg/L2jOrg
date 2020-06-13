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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PLEDGE_SKILL_LIST);

        writeInt(_skills.length);
        writeInt(_subSkills.length); // Squad skill length
        for (Skill sk : _skills) {
            writeInt(sk.getDisplayId());
            writeShort((short) sk.getDisplayLevel());
            writeShort((short) 0x00); // Sub level
        }
        for (SubPledgeSkill sk : _subSkills) {
            writeInt(sk._subType); // Clan Sub-unit types
            writeInt(sk._skillId);
            writeShort((short) sk._skillLvl);
            writeShort((short) 0x00); // Sub level
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

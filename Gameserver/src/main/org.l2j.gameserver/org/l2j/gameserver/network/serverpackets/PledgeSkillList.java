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

    public PledgeSkillList(Clan clan) {
        _skills = clan.getAllSkills();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_PLEDGE_SKILL_LIST, buffer );

        buffer.writeInt(_skills.length);
        buffer.writeInt(0x00); //sub pledge  Squad skill length
        for (Skill sk : _skills) {
            buffer.writeInt(sk.getDisplayId());
            buffer.writeShort(sk.getDisplayLevel());
            buffer.writeShort(0x00); // Sub level
        }
        // for each sub pledge skill write sub pledge type and skill info
    }
}

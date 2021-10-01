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
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author UnAfraid
 */
public class ExAcquireSkillInfo extends ServerPacket {
    private final int _id;
    private final int _level;
    private final int _dualClassLevel;
    private final long _spCost;
    private final int _minLevel;
    private final List<ItemHolder> _itemReq;
    private final List<Skill> _skillRem;

    /**
     * Special constructor for Alternate Skill Learning system.<br>
     * Sets a custom amount of SP.
     *
     * @param skillLearn the skill learn.
     */
    public ExAcquireSkillInfo(SkillLearn skillLearn) {
        _id = skillLearn.getSkillId();
        _level = skillLearn.getSkillLevel();
        _dualClassLevel = 0;
        _spCost = skillLearn.getLevelUpSp();
        _minLevel = skillLearn.requiredLevel();
        _itemReq = skillLearn.getRequiredItems();
        _skillRem = new ArrayList<>();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_ACQUIRE_SKILL_INFO, buffer );

        buffer.writeInt(_id);
        buffer.writeInt(_level);
        buffer.writeLong(_spCost);
        buffer.writeShort(_minLevel);
        buffer.writeShort(_dualClassLevel);
        buffer.writeInt(_itemReq.size());
        for (ItemHolder holder : _itemReq) {
            buffer.writeInt(holder.getId());
            buffer.writeLong(holder.getCount());
        }

        buffer.writeInt(_skillRem.size());
        for (Skill skill : _skillRem) {
            buffer.writeInt(skill.getId());
            buffer.writeInt(skill.getLevel());
        }
    }

}

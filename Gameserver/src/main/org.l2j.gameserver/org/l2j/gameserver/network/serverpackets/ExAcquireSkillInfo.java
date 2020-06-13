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
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
     * @param player
     * @param skillLearn the skill learn.
     */
    public ExAcquireSkillInfo(Player player, SkillLearn skillLearn) {
        _id = skillLearn.getSkillId();
        _level = skillLearn.getSkillLevel();
        _dualClassLevel = skillLearn.getDualClassLevel();
        _spCost = skillLearn.getLevelUpSp();
        _minLevel = skillLearn.getGetLevel();
        _itemReq = skillLearn.getRequiredItems();
        _skillRem = skillLearn.getRemoveSkills().stream().map(player::getKnownSkill).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_ACQUIRE_SKILL_INFO);

        writeInt(_id);
        writeInt(_level);
        writeLong(_spCost);
        writeShort((short) _minLevel);
        writeShort((short) _dualClassLevel);
        writeInt(_itemReq.size());
        for (ItemHolder holder : _itemReq) {
            writeInt(holder.getId());
            writeLong(holder.getCount());
        }

        writeInt(_skillRem.size());
        for (Skill skill : _skillRem) {
            writeInt(skill.getId());
            writeInt(skill.getLevel());
        }
    }

}

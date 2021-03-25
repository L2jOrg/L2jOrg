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
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

/**
 * @author Sdw, Mobius
 * @version Classic 2.0
 */
public class AcquireSkillList extends ServerPacket {
    final Player player;
    final List<SkillLearn> _learnable;

    public AcquireSkillList(Player player) {
        this.player = player;
        _learnable = SkillTreesData.getInstance().getAvailableSkills(player, player.getClassId(), false, false);
        _learnable.addAll(SkillTreesData.getInstance().getNextAvailableSkills(player, player.getClassId(), false, false));
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.ACQUIRE_SKILL_LIST, buffer );

        buffer.writeShort(_learnable.size());
        for (SkillLearn skill : _learnable) {
            if (skill == null) {
                continue;
            }
            buffer.writeInt(skill.getSkillId());
            buffer.writeShort(skill.getSkillLevel());
            buffer.writeLong(skill.getLevelUpSp());
            buffer.writeByte(skill.getGetLevel());
            buffer.writeShort(0x00); // Salvation: Changed from byte to short.
            if (skill.getRequiredItems().size() > 0) {
                for (ItemHolder item : skill.getRequiredItems()) {
                    buffer.writeByte(0x01);
                    buffer.writeInt(item.getId());
                    buffer.writeLong(item.getCount());
                }
            } else {
                buffer.writeByte( 0x00);
            }
            buffer.writeByte(0x00);
        }
    }

}

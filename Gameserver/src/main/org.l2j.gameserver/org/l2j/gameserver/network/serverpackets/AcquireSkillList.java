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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.ACQUIRE_SKILL_LIST);

        writeShort((short) _learnable.size());
        for (SkillLearn skill : _learnable) {
            if (skill == null) {
                continue;
            }
            writeInt(skill.getSkillId());
            writeShort((short) skill.getSkillLevel());
            writeLong(skill.getLevelUpSp());
            writeByte((byte) skill.getGetLevel());
            writeShort((short) 0x00); // Salvation: Changed from byte to short.
            if (skill.getRequiredItems().size() > 0) {
                for (ItemHolder item : skill.getRequiredItems()) {
                    writeByte((byte) 0x01);
                    writeInt(item.getId());
                    writeLong(item.getCount());
                }
            } else {
                writeByte((byte) 0x00);
            }
            writeByte((byte) 0x00);
        }
    }

}

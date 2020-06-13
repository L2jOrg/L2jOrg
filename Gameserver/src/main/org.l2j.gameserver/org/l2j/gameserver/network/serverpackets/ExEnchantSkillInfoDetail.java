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

import org.l2j.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.EnchantSkillHolder;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Set;

/**
 * @author KenM
 */
public class ExEnchantSkillInfoDetail extends ServerPacket {
    private final SkillEnchantType _type;
    private final int _skillId;
    private final int _skillLvl;
    private final int _skillSubLvl;
    private final EnchantSkillHolder _enchantSkillHolder;

    public ExEnchantSkillInfoDetail(SkillEnchantType type, int skillId, int skillLvl, int skillSubLvl, Player player) {
        _type = type;
        _skillId = skillId;
        _skillLvl = skillLvl;
        _skillSubLvl = skillSubLvl;

        _enchantSkillHolder = null;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_ENCHANT_SKILL_INFO_DETAIL);

        writeInt(_type.ordinal());
        writeInt(_skillId);
        writeShort((short) _skillLvl);
        writeShort((short) _skillSubLvl);
        if (_enchantSkillHolder != null) {
            writeLong(_enchantSkillHolder.getSp(_type));
            writeInt(_enchantSkillHolder.getChance(_type));
            final Set<ItemHolder> holders = _enchantSkillHolder.getRequiredItems(_type);
            writeInt(holders.size());
            holders.forEach(holder ->
            {
                writeInt(holder.getId());
                writeInt((int) holder.getCount());
            });
        }
    }

}

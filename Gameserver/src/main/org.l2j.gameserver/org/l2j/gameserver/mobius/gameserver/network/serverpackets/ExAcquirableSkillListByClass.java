/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.model.L2SkillLearn;
import org.l2j.gameserver.mobius.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.List;

/**
 * @author UnAfraid
 */
public class ExAcquirableSkillListByClass implements IClientOutgoingPacket
{
	final List<L2SkillLearn> _learnable;
	final AcquireSkillType _type;
	
	public ExAcquirableSkillListByClass(List<L2SkillLearn> learnable, AcquireSkillType type)
	{
		_learnable = learnable;
		_type = type;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_ACQUIRABLE_SKILL_LIST_BY_CLASS.writeId(packet);
		
		packet.writeH(_type.getId());
		packet.writeH(_learnable.size());
		for (L2SkillLearn skill : _learnable)
		{
			packet.writeD(skill.getSkillId());
			packet.writeH(skill.getSkillLevel());
			packet.writeH(skill.getSkillLevel());
			packet.writeC(skill.getGetLevel());
			packet.writeQ(skill.getLevelUpSp());
			packet.writeC(skill.getRequiredItems().size());
			if (_type == AcquireSkillType.SUBPLEDGE)
			{
				packet.writeH(0x00);
			}
		}
		return true;
	}
}

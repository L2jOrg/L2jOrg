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

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.data.xml.impl.SkillTreesData;
import com.l2jmobius.gameserver.model.L2SkillLearn;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.ItemHolder;
import com.l2jmobius.gameserver.network.OutgoingPackets;

import java.util.List;

/**
 * @author Sdw, Mobius
 * @version Classic 2.0
 */
public class AcquireSkillList implements IClientOutgoingPacket
{
	final L2PcInstance _activeChar;
	final List<L2SkillLearn> _learnable;
	
	public AcquireSkillList(L2PcInstance activeChar)
	{
		_activeChar = activeChar;
		_learnable = SkillTreesData.getInstance().getAvailableSkills(activeChar, activeChar.getClassId(), false, false);
		_learnable.addAll(SkillTreesData.getInstance().getNextAvailableSkills(activeChar, activeChar.getClassId(), false, false));
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.ACQUIRE_SKILL_LIST.writeId(packet);
		
		packet.writeH(_learnable.size());
		for (L2SkillLearn skill : _learnable)
		{
			if (skill == null)
			{
				continue;
			}
			packet.writeD(skill.getSkillId());
			packet.writeH(skill.getSkillLevel());
			packet.writeQ(skill.getLevelUpSp());
			packet.writeC(skill.getGetLevel());
			packet.writeH(0x00); // Salvation: Changed from byte to short.
			if (skill.getRequiredItems().size() > 0)
			{
				for (ItemHolder item : skill.getRequiredItems())
				{
					packet.writeC(0x01);
					packet.writeD(item.getId());
					packet.writeQ(item.getCount());
				}
			}
			else
			{
				packet.writeC(0x00);
			}
			packet.writeC(0x00);
		}
		return true;
	}
}

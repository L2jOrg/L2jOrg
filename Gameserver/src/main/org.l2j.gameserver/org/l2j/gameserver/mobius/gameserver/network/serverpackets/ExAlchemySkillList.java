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
import com.l2jmobius.gameserver.data.xml.impl.SkillData;
import com.l2jmobius.gameserver.data.xml.impl.SkillTreesData;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.skills.CommonSkill;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.network.OutgoingPackets;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author UnAfraid
 */
public class ExAlchemySkillList implements IClientOutgoingPacket
{
	private final List<Skill> _skills = new ArrayList<>();
	
	public ExAlchemySkillList(L2PcInstance player)
	{
		_skills.addAll(player.getAllSkills().stream().filter(s -> SkillTreesData.getInstance().isAlchemySkill(s.getId(), s.getLevel())).collect(Collectors.toList()));
		_skills.add(SkillData.getInstance().getSkill(CommonSkill.ALCHEMY_CUBE.getId(), 1));
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_ALCHEMY_SKILL_LIST.writeId(packet);
		
		packet.writeD(_skills.size());
		for (Skill skill : _skills)
		{
			packet.writeD(skill.getId());
			packet.writeD(skill.getLevel());
			packet.writeQ(0x00); // Always 0 on Naia, SP i guess?
			packet.writeC(skill.getId() == CommonSkill.ALCHEMY_CUBE.getId() ? 0 : 1); // This is type in flash, visible or not
		}
		return true;
	}
}

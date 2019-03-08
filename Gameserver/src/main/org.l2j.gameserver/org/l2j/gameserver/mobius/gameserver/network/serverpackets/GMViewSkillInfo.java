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
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.Collection;

public class GMViewSkillInfo implements IClientOutgoingPacket
{
	private final L2PcInstance _activeChar;
	private final Collection<Skill> _skills;
	
	public GMViewSkillInfo(L2PcInstance cha)
	{
		_activeChar = cha;
		_skills = _activeChar.getSkillList();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.GM_VIEW_SKILL_INFO.writeId(packet);
		
		packet.writeS(_activeChar.getName());
		packet.writeD(_skills.size());
		
		final boolean isDisabled = (_activeChar.getClan() != null) && (_activeChar.getClan().getReputationScore() < 0);
		
		for (Skill skill : _skills)
		{
			packet.writeD(skill.isPassive() ? 1 : 0);
			packet.writeH(skill.getDisplayLevel());
			packet.writeH(skill.getSubLevel());
			packet.writeD(skill.getDisplayId());
			packet.writeD(0x00);
			packet.writeC(isDisabled && skill.isClanSkill() ? 1 : 0);
			packet.writeC(skill.isEnchantable() ? 1 : 0);
		}
		return true;
	}
}
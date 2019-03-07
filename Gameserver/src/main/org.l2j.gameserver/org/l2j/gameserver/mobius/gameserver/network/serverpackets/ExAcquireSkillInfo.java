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
import com.l2jmobius.gameserver.model.L2SkillLearn;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.ItemHolder;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.network.OutgoingPackets;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author UnAfraid
 */
public class ExAcquireSkillInfo implements IClientOutgoingPacket
{
	private final int _id;
	private final int _level;
	private final int _dualClassLevel;
	private final int _spCost;
	private final int _minLevel;
	private final List<ItemHolder> _itemReq;
	private final List<Skill> _skillRem;
	
	/**
	 * Special constructor for Alternate Skill Learning system.<br>
	 * Sets a custom amount of SP.
	 * @param player
	 * @param skillLearn the skill learn.
	 */
	public ExAcquireSkillInfo(L2PcInstance player, L2SkillLearn skillLearn)
	{
		_id = skillLearn.getSkillId();
		_level = skillLearn.getSkillLevel();
		_dualClassLevel = skillLearn.getDualClassLevel();
		_spCost = skillLearn.getLevelUpSp();
		_minLevel = skillLearn.getGetLevel();
		_itemReq = skillLearn.getRequiredItems();
		_skillRem = skillLearn.getRemoveSkills().stream().map(player::getKnownSkill).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_ACQUIRE_SKILL_INFO.writeId(packet);
		
		packet.writeD(_id);
		packet.writeD(_level);
		packet.writeQ(_spCost);
		packet.writeH(_minLevel);
		packet.writeH(_dualClassLevel);
		packet.writeD(_itemReq.size());
		for (ItemHolder holder : _itemReq)
		{
			packet.writeD(holder.getId());
			packet.writeQ(holder.getCount());
		}
		
		packet.writeD(_skillRem.size());
		for (Skill skill : _skillRem)
		{
			packet.writeD(skill.getId());
			packet.writeD(skill.getLevel());
		}
		return true;
	}
}

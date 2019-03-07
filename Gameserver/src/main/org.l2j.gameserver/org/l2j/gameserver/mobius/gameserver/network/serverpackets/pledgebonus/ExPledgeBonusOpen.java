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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets.pledgebonus;

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.data.xml.impl.ClanRewardData;
import com.l2jmobius.gameserver.enums.ClanRewardType;
import com.l2jmobius.gameserver.model.L2Clan;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.pledge.ClanRewardBonus;
import com.l2jmobius.gameserver.network.OutgoingPackets;
import com.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.util.logging.Logger;

/**
 * @author UnAfraid
 */
public class ExPledgeBonusOpen implements IClientOutgoingPacket
{
	private static final Logger LOGGER = Logger.getLogger(ExPledgeBonusOpen.class.getName());
	
	private final L2PcInstance _player;
	
	public ExPledgeBonusOpen(L2PcInstance player)
	{
		_player = player;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		final L2Clan clan = _player.getClan();
		if (clan == null)
		{
			LOGGER.warning("Player: " + _player + " attempting to write to a null clan!");
			return false;
		}
		
		final ClanRewardBonus highestMembersOnlineBonus = ClanRewardData.getInstance().getHighestReward(ClanRewardType.MEMBERS_ONLINE);
		final ClanRewardBonus highestHuntingBonus = ClanRewardData.getInstance().getHighestReward(ClanRewardType.HUNTING_MONSTERS);
		final ClanRewardBonus membersOnlineBonus = ClanRewardType.MEMBERS_ONLINE.getAvailableBonus(clan);
		final ClanRewardBonus huntingBonus = ClanRewardType.HUNTING_MONSTERS.getAvailableBonus(clan);
		if (highestMembersOnlineBonus == null)
		{
			LOGGER.warning("Couldn't find highest available clan members online bonus!!");
			return false;
		}
		else if (highestHuntingBonus == null)
		{
			LOGGER.warning("Couldn't find highest available clan hunting bonus!!");
			return false;
		}
		else if (highestMembersOnlineBonus.getSkillReward() == null)
		{
			LOGGER.warning("Couldn't find skill reward for highest available members online bonus!!");
			return false;
		}
		else if (highestHuntingBonus.getItemReward() == null)
		{
			LOGGER.warning("Couldn't find item reward for highest available hunting bonus!!");
			return false;
		}
		
		// General OP Code
		OutgoingPackets.EX_PLEDGE_BONUS_OPEN.writeId(packet);
		
		// Members online bonus
		packet.writeD(highestMembersOnlineBonus.getRequiredAmount());
		packet.writeD(clan.getMaxOnlineMembers());
		packet.writeC(0x00); // 140
		packet.writeD(membersOnlineBonus != null ? highestMembersOnlineBonus.getSkillReward().getSkillId() : 0x00);
		packet.writeC(membersOnlineBonus != null ? membersOnlineBonus.getLevel() : 0x00);
		packet.writeC(membersOnlineBonus != null ? 0x01 : 0x00);
		
		// Hunting bonus
		packet.writeD(highestHuntingBonus.getRequiredAmount());
		packet.writeD(clan.getHuntingPoints());
		packet.writeC(0x00); // 140
		packet.writeD(huntingBonus != null ? highestHuntingBonus.getItemReward().getId() : 0x00);
		packet.writeC(huntingBonus != null ? huntingBonus.getLevel() : 0x00);
		packet.writeC(huntingBonus != null ? 0x01 : 0x00);
		return true;
	}
}

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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets.pledgebonus;

import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.enums.ClanRewardType;
import com.l2jmobius.gameserver.model.L2Clan;
import com.l2jmobius.gameserver.model.L2ClanMember;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.ItemHolder;
import com.l2jmobius.gameserver.model.holders.SkillHolder;
import com.l2jmobius.gameserver.model.pledge.ClanRewardBonus;
import com.l2jmobius.gameserver.network.L2GameClient;
import com.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;

/**
 * @author UnAfraid
 */
public class RequestPledgeBonusReward implements IClientIncomingPacket
{
	private int _type;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_type = packet.readC();
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final L2PcInstance player = client.getActiveChar();
		if ((player == null) || (player.getClan() == null))
		{
			return;
		}
		
		if ((_type < 0) || (_type > ClanRewardType.values().length))
		{
			return;
		}
		
		final L2Clan clan = player.getClan();
		final ClanRewardType type = ClanRewardType.values()[_type];
		final L2ClanMember member = clan.getClanMember(player.getObjectId());
		if (clan.canClaimBonusReward(player, type))
		{
			final ClanRewardBonus bonus = type.getAvailableBonus(player.getClan());
			if (bonus != null)
			{
				final ItemHolder itemReward = bonus.getItemReward();
				final SkillHolder skillReward = bonus.getSkillReward();
				if (itemReward != null)
				{
					player.addItem("ClanReward", itemReward.getId(), itemReward.getCount(), player, true);
				}
				else if (skillReward != null)
				{
					skillReward.getSkill().activateSkill(player, player);
				}
				member.setRewardClaimed(type);
			}
			else
			{
				LOGGER.warning(player + " Attempting to claim reward but clan(" + clan + ") doesn't have such!");
			}
		}
	}
}

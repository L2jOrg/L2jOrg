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
import org.l2j.gameserver.mobius.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author Sdw
 */
public class ExPledgeRecruitBoardDetail implements IClientOutgoingPacket
{
	final PledgeRecruitInfo _pledgeRecruitInfo;
	
	public ExPledgeRecruitBoardDetail(PledgeRecruitInfo pledgeRecruitInfo)
	{
		_pledgeRecruitInfo = pledgeRecruitInfo;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_PLEDGE_RECRUIT_BOARD_DETAIL.writeId(packet);
		
		packet.writeD(_pledgeRecruitInfo.getClanId());
		packet.writeD(_pledgeRecruitInfo.getKarma());
		packet.writeS(_pledgeRecruitInfo.getInformation());
		packet.writeS(_pledgeRecruitInfo.getDetailedInformation());
		packet.writeD(_pledgeRecruitInfo.getApplicationType());
		packet.writeD(_pledgeRecruitInfo.getRecruitType());
		return true;
	}
}

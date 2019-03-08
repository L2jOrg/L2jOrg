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
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.List;

/**
 * @author Sdw
 */
public class ExPledgeRecruitBoardSearch implements IClientOutgoingPacket
{
	final List<PledgeRecruitInfo> _clanList;
	private final int _currentPage;
	private final int _totalNumberOfPage;
	private final int _clanOnCurrentPage;
	private final int _startIndex;
	private final int _endIndex;
	
	static final int CLAN_PER_PAGE = 12;
	
	public ExPledgeRecruitBoardSearch(List<PledgeRecruitInfo> clanList, int currentPage)
	{
		_clanList = clanList;
		_currentPage = currentPage;
		_totalNumberOfPage = (int) Math.ceil((double) _clanList.size() / CLAN_PER_PAGE);
		_startIndex = (_currentPage - 1) * CLAN_PER_PAGE;
		_endIndex = (_startIndex + CLAN_PER_PAGE) > _clanList.size() ? _clanList.size() : _startIndex + CLAN_PER_PAGE;
		_clanOnCurrentPage = _endIndex - _startIndex;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_PLEDGE_RECRUIT_BOARD_SEARCH.writeId(packet);
		
		packet.writeD(_currentPage);
		packet.writeD(_totalNumberOfPage);
		packet.writeD(_clanOnCurrentPage);
		
		for (int i = _startIndex; i < _endIndex; i++)
		{
			packet.writeD(_clanList.get(i).getClanId());
			packet.writeD(_clanList.get(i).getClan().getAllyId());
		}
		for (int i = _startIndex; i < _endIndex; i++)
		{
			final L2Clan clan = _clanList.get(i).getClan();
			packet.writeD(clan.getCrestId());
			packet.writeD(clan.getAllyCrestId());
			packet.writeS(clan.getName());
			packet.writeS(clan.getLeaderName());
			packet.writeD(clan.getLevel());
			packet.writeD(clan.getMembersCount());
			packet.writeD(_clanList.get(i).getKarma());
			packet.writeS(_clanList.get(i).getInformation());
			packet.writeD(_clanList.get(i).getApplicationType()); // Helios
			packet.writeD(_clanList.get(i).getRecruitType()); // Helios
		}
		return true;
	}
}

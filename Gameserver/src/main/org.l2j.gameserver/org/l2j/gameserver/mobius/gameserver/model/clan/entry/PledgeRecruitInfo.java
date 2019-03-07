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
package org.l2j.gameserver.mobius.gameserver.model.clan.entry;

import com.l2jmobius.gameserver.data.sql.impl.ClanTable;
import com.l2jmobius.gameserver.model.L2Clan;

/**
 * @author Sdw
 */
public class PledgeRecruitInfo
{
	private int _clanId;
	private int _karma;
	private String _information;
	private String _detailedInformation;
	private final L2Clan _clan;
	private final int _applicationType;
	private final int _recruitType;
	
	public PledgeRecruitInfo(int clanId, int karma, String information, String detailedInformation, int applicationType, int recruitType)
	{
		_clanId = clanId;
		_karma = karma;
		_information = information;
		_detailedInformation = detailedInformation;
		_clan = ClanTable.getInstance().getClan(clanId);
		_applicationType = applicationType;
		_recruitType = recruitType;
	}
	
	public int getClanId()
	{
		return _clanId;
	}
	
	public void setClanId(int clanId)
	{
		_clanId = clanId;
	}
	
	public String getClanName()
	{
		return _clan.getName();
	}
	
	public String getClanLeaderName()
	{
		return _clan.getLeaderName();
	}
	
	public int getClanLevel()
	{
		return _clan.getLevel();
	}
	
	public int getKarma()
	{
		return _karma;
	}
	
	public void setKarma(int karma)
	{
		_karma = karma;
	}
	
	public String getInformation()
	{
		return _information;
	}
	
	public void setInformation(String information)
	{
		_information = information;
	}
	
	public String getDetailedInformation()
	{
		return _detailedInformation;
	}
	
	public void setDetailedInformation(String detailedInformation)
	{
		_detailedInformation = detailedInformation;
	}
	
	public int getApplicationType()
	{
		return _applicationType;
	}
	
	public int getRecruitType()
	{
		return _recruitType;
	}
	
	public L2Clan getClan()
	{
		return _clan;
	}
}

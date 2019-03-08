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
import org.l2j.gameserver.mobius.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.mobius.gameserver.model.ClanInfo;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.RequestAllyInfo;

import java.util.Collection;

/**
 * Sent in response to {@link RequestAllyInfo}, if applicable.<BR>
 * @author afk5min
 */
public class AllianceInfo implements IClientOutgoingPacket
{
	private final String _name;
	private final int _total;
	private final int _online;
	private final String _leaderC;
	private final String _leaderP;
	private final ClanInfo[] _allies;
	
	public AllianceInfo(int allianceId)
	{
		final L2Clan leader = ClanTable.getInstance().getClan(allianceId);
		_name = leader.getAllyName();
		_leaderC = leader.getName();
		_leaderP = leader.getLeaderName();
		
		final Collection<L2Clan> allies = ClanTable.getInstance().getClanAllies(allianceId);
		_allies = new ClanInfo[allies.size()];
		int idx = 0;
		int total = 0;
		int online = 0;
		for (L2Clan clan : allies)
		{
			final ClanInfo ci = new ClanInfo(clan);
			_allies[idx++] = ci;
			total += ci.getTotal();
			online += ci.getOnline();
		}
		
		_total = total;
		_online = online;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.ALLIANCE_INFO.writeId(packet);
		
		packet.writeS(_name);
		packet.writeD(_total);
		packet.writeD(_online);
		packet.writeS(_leaderC);
		packet.writeS(_leaderP);
		
		packet.writeD(_allies.length);
		for (ClanInfo aci : _allies)
		{
			packet.writeS(aci.getClan().getName());
			packet.writeD(0x00);
			packet.writeD(aci.getClan().getLevel());
			packet.writeS(aci.getClan().getLeaderName());
			packet.writeD(aci.getTotal());
			packet.writeD(aci.getOnline());
		}
		return true;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getTotal()
	{
		return _total;
	}
	
	public int getOnline()
	{
		return _online;
	}
	
	public String getLeaderC()
	{
		return _leaderC;
	}
	
	public String getLeaderP()
	{
		return _leaderP;
	}
	
	public ClanInfo[] getAllies()
	{
		return _allies;
	}
}
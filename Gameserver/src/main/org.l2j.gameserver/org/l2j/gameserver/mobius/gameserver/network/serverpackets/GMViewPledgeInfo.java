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
import org.l2j.gameserver.mobius.gameserver.model.L2ClanMember;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public class GMViewPledgeInfo implements IClientOutgoingPacket
{
	private final L2Clan _clan;
	private final L2PcInstance _activeChar;
	
	public GMViewPledgeInfo(L2Clan clan, L2PcInstance activeChar)
	{
		_clan = clan;
		_activeChar = activeChar;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.GM_VIEW_PLEDGE_INFO.writeId(packet);
		
		packet.writeD(0x00);
		packet.writeS(_activeChar.getName());
		packet.writeD(_clan.getId());
		packet.writeD(0x00);
		packet.writeS(_clan.getName());
		packet.writeS(_clan.getLeaderName());
		
		packet.writeD(_clan.getCrestId()); // -> no, it's no longer used (nuocnam) fix by game
		packet.writeD(_clan.getLevel());
		packet.writeD(_clan.getCastleId());
		packet.writeD(_clan.getHideoutId());
		packet.writeD(_clan.getFortId());
		packet.writeD(_clan.getRank());
		packet.writeD(_clan.getReputationScore());
		packet.writeD(0x00);
		packet.writeD(0x00);
		packet.writeD(0x00);
		packet.writeD(_clan.getAllyId()); // c2
		packet.writeS(_clan.getAllyName()); // c2
		packet.writeD(_clan.getAllyCrestId()); // c2
		packet.writeD(_clan.isAtWar() ? 1 : 0); // c3
		packet.writeD(0x00); // T3 Unknown
		
		packet.writeD(_clan.getMembers().size());
		for (L2ClanMember member : _clan.getMembers())
		{
			if (member != null)
			{
				packet.writeS(member.getName());
				packet.writeD(member.getLevel());
				packet.writeD(member.getClassId());
				packet.writeD(member.getSex() ? 1 : 0);
				packet.writeD(member.getRaceOrdinal());
				packet.writeD(member.isOnline() ? member.getObjectId() : 0);
				packet.writeD(member.getSponsor() != 0 ? 1 : 0);
			}
		}
		return true;
	}
}

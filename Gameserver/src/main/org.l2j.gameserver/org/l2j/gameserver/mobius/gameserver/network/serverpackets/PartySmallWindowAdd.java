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
import com.l2jmobius.gameserver.model.L2Party;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.OutgoingPackets;

public final class PartySmallWindowAdd implements IClientOutgoingPacket
{
	private final L2PcInstance _member;
	private final L2Party _party;
	
	public PartySmallWindowAdd(L2PcInstance member, L2Party party)
	{
		_member = member;
		_party = party;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.PARTY_SMALL_WINDOW_ADD.writeId(packet);
		
		packet.writeD(_party.getLeaderObjectId()); // c3
		packet.writeD(_party.getDistributionType().getId()); // c3
		packet.writeD(_member.getObjectId());
		packet.writeS(_member.getName());
		
		packet.writeD((int) _member.getCurrentCp()); // c4
		packet.writeD(_member.getMaxCp()); // c4
		packet.writeD((int) _member.getCurrentHp());
		packet.writeD(_member.getMaxHp());
		packet.writeD((int) _member.getCurrentMp());
		packet.writeD(_member.getMaxMp());
		packet.writeD(_member.getVitalityPoints());
		packet.writeC(_member.getLevel());
		packet.writeH(_member.getClassId().getId());
		packet.writeC(0x00);
		packet.writeH(_member.getRace().ordinal());
		return true;
	}
}

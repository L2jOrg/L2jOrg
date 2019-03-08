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
import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public final class PartySmallWindowAll implements IClientOutgoingPacket
{
	private final L2Party _party;
	private final L2PcInstance _exclude;
	
	public PartySmallWindowAll(L2PcInstance exclude, L2Party party)
	{
		_exclude = exclude;
		_party = party;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.PARTY_SMALL_WINDOW_ALL.writeId(packet);
		
		packet.writeD(_party.getLeaderObjectId());
		packet.writeC(_party.getDistributionType().getId());
		packet.writeC(_party.getMemberCount() - 1);
		
		for (L2PcInstance member : _party.getMembers())
		{
			if ((member != null) && (member != _exclude))
			{
				packet.writeD(member.getObjectId());
				packet.writeS(member.getName());
				
				packet.writeD((int) member.getCurrentCp()); // c4
				packet.writeD(member.getMaxCp()); // c4
				
				packet.writeD((int) member.getCurrentHp());
				packet.writeD(member.getMaxHp());
				packet.writeD((int) member.getCurrentMp());
				packet.writeD(member.getMaxMp());
				packet.writeD(member.getVitalityPoints());
				packet.writeC(member.getLevel());
				packet.writeH(member.getClassId().getId());
				packet.writeC(0x01); // Unk
				packet.writeH(member.getRace().ordinal());
				final L2Summon pet = member.getPet();
				packet.writeD(member.getServitors().size() + (pet != null ? 1 : 0)); // Summon size, one only atm
				if (pet != null)
				{
					packet.writeD(pet.getObjectId());
					packet.writeD(pet.getId() + 1000000);
					packet.writeC(pet.getSummonType());
					packet.writeS(pet.getName());
					packet.writeD((int) pet.getCurrentHp());
					packet.writeD(pet.getMaxHp());
					packet.writeD((int) pet.getCurrentMp());
					packet.writeD(pet.getMaxMp());
					packet.writeC(pet.getLevel());
				}
				member.getServitors().values().forEach(s ->
				{
					packet.writeD(s.getObjectId());
					packet.writeD(s.getId() + 1000000);
					packet.writeC(s.getSummonType());
					packet.writeS(s.getName());
					packet.writeD((int) s.getCurrentHp());
					packet.writeD(s.getMaxHp());
					packet.writeD((int) s.getCurrentMp());
					packet.writeD(s.getMaxMp());
					packet.writeC(s.getLevel());
				});
			}
		}
		return true;
	}
}

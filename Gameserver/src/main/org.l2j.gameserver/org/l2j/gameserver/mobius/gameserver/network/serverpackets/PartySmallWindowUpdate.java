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
import org.l2j.gameserver.mobius.gameserver.enums.PartySmallWindowUpdateType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public final class PartySmallWindowUpdate extends AbstractMaskPacket<PartySmallWindowUpdateType>
{
	private final L2PcInstance _member;
	private int _flags = 0;
	
	public PartySmallWindowUpdate(L2PcInstance member, boolean addAllFlags)
	{
		_member = member;
		if (addAllFlags)
		{
			for (PartySmallWindowUpdateType type : PartySmallWindowUpdateType.values())
			{
				addComponentType(type);
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.PARTY_SMALL_WINDOW_UPDATE.writeId(packet);
		
		packet.writeD(_member.getObjectId());
		packet.writeH(_flags);
		if (containsMask(PartySmallWindowUpdateType.CURRENT_CP))
		{
			packet.writeD((int) _member.getCurrentCp()); // c4
		}
		if (containsMask(PartySmallWindowUpdateType.MAX_CP))
		{
			packet.writeD(_member.getMaxCp()); // c4
		}
		if (containsMask(PartySmallWindowUpdateType.CURRENT_HP))
		{
			packet.writeD((int) _member.getCurrentHp());
		}
		if (containsMask(PartySmallWindowUpdateType.MAX_HP))
		{
			packet.writeD(_member.getMaxHp());
		}
		if (containsMask(PartySmallWindowUpdateType.CURRENT_MP))
		{
			packet.writeD((int) _member.getCurrentMp());
		}
		if (containsMask(PartySmallWindowUpdateType.MAX_MP))
		{
			packet.writeD(_member.getMaxMp());
		}
		if (containsMask(PartySmallWindowUpdateType.LEVEL))
		{
			packet.writeC(_member.getLevel());
		}
		if (containsMask(PartySmallWindowUpdateType.CLASS_ID))
		{
			packet.writeH(_member.getClassId().getId());
		}
		if (containsMask(PartySmallWindowUpdateType.PARTY_SUBSTITUTE))
		{
			packet.writeC(0x00);
		}
		if (containsMask(PartySmallWindowUpdateType.VITALITY_POINTS))
		{
			packet.writeD(_member.getVitalityPoints());
		}
		return true;
	}
	
	@Override
	protected void addMask(int mask)
	{
		_flags |= mask;
	}
	
	@Override
	public boolean containsMask(PartySmallWindowUpdateType component)
	{
		return containsMask(_flags, component);
	}
	
	@Override
	protected byte[] getMasks()
	{
		return new byte[0];
	}
}

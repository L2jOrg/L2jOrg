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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets.appearance;

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.ceremonyofchaos.CeremonyOfChaosMember;
import com.l2jmobius.gameserver.network.OutgoingPackets;
import com.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Sdw
 */
public class ExCuriousHouseMemberUpdate implements IClientOutgoingPacket
{
	public final int _objId;
	public final int _maxHp;
	public final int _maxCp;
	public final int _currentHp;
	public final int _currentCp;
	
	public ExCuriousHouseMemberUpdate(CeremonyOfChaosMember member)
	{
		_objId = member.getObjectId();
		final L2PcInstance player = member.getPlayer();
		if (player != null)
		{
			_maxHp = player.getMaxHp();
			_maxCp = player.getMaxCp();
			_currentHp = (int) player.getCurrentHp();
			_currentCp = (int) player.getCurrentCp();
		}
		else
		{
			_maxHp = 0;
			_maxCp = 0;
			_currentHp = 0;
			_currentCp = 0;
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_CURIOUS_HOUSE_MEMBER_UPDATE.writeId(packet);
		
		packet.writeD(_objId);
		packet.writeD(_maxHp);
		packet.writeD(_maxCp);
		packet.writeD(_currentHp);
		packet.writeD(_currentCp);
		return true;
	}
}

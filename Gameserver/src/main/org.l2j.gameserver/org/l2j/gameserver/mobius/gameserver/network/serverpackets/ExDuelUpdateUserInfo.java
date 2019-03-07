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
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.OutgoingPackets;

/**
 * @author KenM
 */
public class ExDuelUpdateUserInfo implements IClientOutgoingPacket
{
	private final L2PcInstance _activeChar;
	
	public ExDuelUpdateUserInfo(L2PcInstance cha)
	{
		_activeChar = cha;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_DUEL_UPDATE_USER_INFO.writeId(packet);
		
		packet.writeS(_activeChar.getName());
		packet.writeD(_activeChar.getObjectId());
		packet.writeD(_activeChar.getClassId().getId());
		packet.writeD(_activeChar.getLevel());
		packet.writeD((int) _activeChar.getCurrentHp());
		packet.writeD(_activeChar.getMaxHp());
		packet.writeD((int) _activeChar.getCurrentMp());
		packet.writeD(_activeChar.getMaxMp());
		packet.writeD((int) _activeChar.getCurrentCp());
		packet.writeD(_activeChar.getMaxCp());
		return true;
	}
}

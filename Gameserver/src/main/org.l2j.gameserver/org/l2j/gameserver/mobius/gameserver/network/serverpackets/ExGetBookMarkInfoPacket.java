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
import com.l2jmobius.gameserver.model.TeleportBookmark;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.OutgoingPackets;

/**
 * @author ShanSoft
 */
public class ExGetBookMarkInfoPacket implements IClientOutgoingPacket
{
	private final L2PcInstance player;
	
	public ExGetBookMarkInfoPacket(L2PcInstance cha)
	{
		player = cha;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_GET_BOOK_MARK_INFO.writeId(packet);
		
		packet.writeD(0x00); // Dummy
		packet.writeD(player.getBookmarkslot());
		packet.writeD(player.getTeleportBookmarks().size());
		
		for (TeleportBookmark tpbm : player.getTeleportBookmarks())
		{
			packet.writeD(tpbm.getId());
			packet.writeD(tpbm.getX());
			packet.writeD(tpbm.getY());
			packet.writeD(tpbm.getZ());
			packet.writeS(tpbm.getName());
			packet.writeD(tpbm.getIcon());
			packet.writeS(tpbm.getTag());
		}
		return true;
	}
}

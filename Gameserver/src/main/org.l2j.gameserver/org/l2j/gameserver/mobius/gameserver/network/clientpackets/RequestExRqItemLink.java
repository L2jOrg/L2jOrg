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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExRpItemLink;

/**
 * @author KenM
 */
public class RequestExRqItemLink extends IClientIncomingPacket
{
	private int _objectId;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_objectId = packet.getInt();
		return true;
	}
	
	@Override
	public void runImpl()
	{
		final L2Object object = L2World.getInstance().findObject(_objectId);
		if ((object != null) && object.isItem())
		{
			final L2ItemInstance item = (L2ItemInstance) object;
			if (item.isPublished())
			{
				client.sendPacket(new ExRpItemLink(item));
			}
		}
	}
}

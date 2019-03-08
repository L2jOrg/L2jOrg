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
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.UserInfo;

public class RequestRecordInfo extends IClientIncomingPacket
{
	@Override
	public void readImpl(ByteBuffer packet)
	{
		return true;
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		client.sendPacket(new UserInfo(activeChar));
		
		L2World.getInstance().forEachVisibleObject(activeChar, L2Object.class, object ->
		{
			if (object.isVisibleFor(activeChar))
			{
				object.sendInfo(activeChar);
				
				if (object.isCharacter())
				{
					// Update the state of the L2Character object client
					// side by sending Server->Client packet
					// MoveToPawn/CharMoveToLocation and AutoAttackStart to
					// the L2PcInstance
					final L2Character obj = (L2Character) object;
					if (obj.getAI() != null)
					{
						obj.getAI().describeStateToPlayer(activeChar);
					}
				}
			}
		});
	}
}

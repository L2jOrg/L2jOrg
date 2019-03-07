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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets.sayune;

import com.l2jmobius.Config;
import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.data.xml.impl.SayuneData;
import com.l2jmobius.gameserver.enums.CategoryType;
import com.l2jmobius.gameserver.instancemanager.ZoneManager;
import com.l2jmobius.gameserver.model.SayuneEntry;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.actor.request.SayuneRequest;
import com.l2jmobius.gameserver.model.zone.ZoneId;
import com.l2jmobius.gameserver.model.zone.type.L2SayuneZone;
import com.l2jmobius.gameserver.network.L2GameClient;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;

/**
 * @author UnAfraid
 */
public class RequestFlyMoveStart implements IClientIncomingPacket
{
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if ((activeChar == null) || !activeChar.isInsideZone(ZoneId.SAYUNE) || activeChar.hasRequest(SayuneRequest.class) || (!activeChar.isInCategory(CategoryType.SIXTH_CLASS_GROUP) && !Config.FREE_JUMPS_FOR_ALL))
		{
			return;
		}
		
		if (activeChar.hasSummon())
		{
			activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_USE_SAYUNE_WHILE_A_SERVITOR_IS_AROUND);
			return;
		}
		
		if (activeChar.getReputation() < 0)
		{
			activeChar.sendPacket(SystemMessageId.YOU_CANNOT_USE_SAYUNE_WHILE_IN_A_CHAOTIC_STATE);
			return;
		}
		
		if (activeChar.hasRequests())
		{
			activeChar.sendPacket(SystemMessageId.SAYUNE_CANNOT_BE_USED_WHILE_TAKING_OTHER_ACTIONS);
			return;
		}
		
		final L2SayuneZone zone = ZoneManager.getInstance().getZone(activeChar, L2SayuneZone.class);
		if (zone.getMapId() == -1)
		{
			activeChar.sendMessage("That zone is not supported yet!");
			LOGGER.warning(getClass().getSimpleName() + ": " + activeChar + " Requested sayune on zone with no map id set");
			return;
		}
		
		final SayuneEntry map = SayuneData.getInstance().getMap(zone.getMapId());
		if (map == null)
		{
			activeChar.sendMessage("This zone is not handled yet!!");
			LOGGER.warning(getClass().getSimpleName() + ": " + activeChar + " Requested sayune on unhandled map zone " + zone.getName());
			return;
		}
		
		final SayuneRequest request = new SayuneRequest(activeChar, map.getId());
		if (activeChar.addRequest(request))
		{
			request.move(activeChar, 0);
		}
	}
}

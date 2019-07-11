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
package handlers.usercommandhandlers;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.instancemanager.MapRegionManager;
import org.l2j.gameserver.instancemanager.ZoneManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.zone.type.RespawnZone;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * Loc user command.
 */
public class Loc implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		0
	};
	
	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		int region;
		final RespawnZone zone = ZoneManager.getInstance().getZone(activeChar, RespawnZone.class);
		if (zone != null)
		{
			region = MapRegionManager.getInstance().getRestartRegion(activeChar, zone.getAllRespawnPoints().get(Race.HUMAN)).getLocId();
		}
		else
		{
			region = MapRegionManager.getInstance().getMapRegionLocId(activeChar);
		}
		
		SystemMessage sm;
		if (region > 0)
		{
			sm = SystemMessage.getSystemMessage(region);
			if (sm.getSystemMessageId().getParamCount() == 3)
			{
				sm.addInt(activeChar.getX());
				sm.addInt(activeChar.getY());
				sm.addInt(activeChar.getZ());
			}
		}
		else
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.CURRENT_LOCATION_S1);
			sm.addString(activeChar.getX() + ", " + activeChar.getY() + ", " + activeChar.getZ());
		}
		activeChar.sendPacket(sm);
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}

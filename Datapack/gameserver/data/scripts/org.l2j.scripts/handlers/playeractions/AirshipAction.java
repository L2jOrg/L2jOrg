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
package handlers.playeractions;

import org.l2j.gameserver.handler.IPlayerActionHandler;
import org.l2j.gameserver.instancemanager.AirShipManager;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * Airship Action player action handler.
 * @author Nik
 */
public final class AirshipAction implements IPlayerActionHandler
{
	@Override
	public void useAction(Player player, ActionData action, boolean ctrlPressed, boolean shiftPressed)
	{
		if (!player.isInAirShip())
		{
			return;
		}
		
		switch (action.getOptionId())
		{
			case 1: // Steer
			{
				if (player.getAirShip().setCaptain(player))
				{
					player.broadcastUserInfo();
				}
				break;
			}
			case 2: // Cancel Control
			{
				if (player.getAirShip().isCaptain(player))
				{
					if (player.getAirShip().setCaptain(null))
					{
						player.broadcastUserInfo();
					}
				}
				break;
			}
			case 3: // Destination Map
			{
				AirShipManager.getInstance().sendAirShipTeleportList(player);
				break;
			}
			case 4: // Exit Airship
			{
				if (player.getAirShip().isCaptain(player))
				{
					if (player.getAirShip().setCaptain(null))
					{
						player.broadcastUserInfo();
					}
				}
				else if (player.getAirShip().isInDock())
				{
					player.getAirShip().oustPlayer(player);
				}
			}
		}
	}
}

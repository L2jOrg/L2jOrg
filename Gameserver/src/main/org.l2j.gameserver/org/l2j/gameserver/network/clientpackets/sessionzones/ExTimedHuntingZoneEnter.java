/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets.sessionzones;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

import static org.l2j.gameserver.network.SystemMessageId.CANNOT_USE_TIMED_HUNTING_ZONES_WHILE_WAITING_FOR_THE_OLYMPIAD;

/**
 * @author Mobius
 */
public class ExTimedHuntingZoneEnter extends ClientPacket {
	private int _zoneId;

	@Override
	public void readImpl()
	{
		_zoneId = readInt();
	}

	@Override
	public void runImpl()
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}

		if (player.isMounted())
		{
			player.sendMessage("Cannot use time-limited hunting zones while mounted.");
			return;
		}
		if (player.isInDuel())
		{
			player.sendMessage("Cannot use time-limited hunting zones during a duel.");
			return;
		}

		if (Olympiad.getInstance().isRegistered(player)) {
			player.sendPacket(CANNOT_USE_TIMED_HUNTING_ZONES_WHILE_WAITING_FOR_THE_OLYMPIAD);
			return;
		}

		if (player.isOnEvent() || (player.getBlockCheckerArena() > -1))
		{
			player.sendMessage("Cannot use time-limited hunting zones while registered on an event.");
			return;
		}
		if (player.isInInstance())
		{
			player.sendMessage("Cannot use time-limited hunting zones while in an instance.");
			return;
		}

		if ((_zoneId == 2) && (player.getLevel() < 78))
		{
			player.sendMessage("Your level does not correspond the zone equivalent.");
		}

		final long currentTime = System.currentTimeMillis();
		long endTime = player.getHuntingZoneResetTime(_zoneId);
		if ((endTime + Config.TIME_LIMITED_ZONE_RESET_DELAY) < currentTime)
		{
			endTime = currentTime + 3600000;
		}

		if (endTime > currentTime)
		{
			if (player.getAdena() > Config.TIME_LIMITED_ZONE_TELEPORT_FEE)
			{
				player.reduceAdena("TimedHuntingZone", Config.TIME_LIMITED_ZONE_TELEPORT_FEE, player, true);
			}
			else
			{
				player.sendMessage("Not enough adena.");
				return;
			}

			switch (_zoneId)
			{
				case 2: // Ancient Pirates' Tomb
				{
					player.teleToLocation(17613, -76862, -6265);
					break;
				}
			}

			player.setHuntingZoneResetTime(_zoneId, endTime);
			player.startTimedHuntingZone(_zoneId, endTime - currentTime);
		}
		else
		{
			player.sendMessage("You don't have enough time available to enter the hunting zone.");
		}
	}
}

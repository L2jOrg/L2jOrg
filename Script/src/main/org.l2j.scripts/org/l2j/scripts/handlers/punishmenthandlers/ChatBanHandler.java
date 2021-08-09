/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.handlers.punishmenthandlers;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;

/**
 * This class handles chat ban punishment.
 * @author UnAfraid
 */
public class ChatBanHandler extends PunishmentHandler {

	@Override
	protected void applyToPlayer(PunishmentTask task, Player player) {
		final long delay = ((task.getExpirationTime() - System.currentTimeMillis()) / 1000);
		if (delay > 0) {
			player.sendMessage("You've been chat banned for " + (delay > 60 ? ((delay / 60) + " minutes.") : delay + " seconds."));
		} else {
			player.sendMessage("You've been chat banned forever.");
		}
		player.sendPacket(new EtcStatusUpdate(player));
	}

	@Override
	protected void removeFromPlayer(Player player) {
		player.sendMessage("Your Chat ban has been lifted");
		player.sendPacket(new EtcStatusUpdate(player));
	}
	
	@Override
	public PunishmentType getType()
	{
		return PunishmentType.CHAT_BAN;
	}
}

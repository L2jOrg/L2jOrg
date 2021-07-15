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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.tasks.player.TeleportTask;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.world.zone.type.JailZone;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class handles jail punishment.
 * @author UnAfraid
 * @author JoeAlisson
 */
public class JailHandler extends PunishmentHandler {

	public JailHandler() {
		Listeners.Global().addListener(new ConsumerEventListener(Listeners.Global(), EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) this::onPlayerLogin, this));
	}
	
	private void onPlayerLogin(OnPlayerLogin event) {
		var player = event.getPlayer();
		if (player.isJailed() && !player.isInsideZone(ZoneType.JAIL)) {
			applyToPlayer(null, player);
		}
		else if (!player.isJailed() && player.isInsideZone(ZoneType.JAIL) && !player.isGM()) {
			removeFromPlayer(player);
		}
	}

	@Override
	protected void applyToPlayer(PunishmentTask task, Player player) {
		player.setInstance(null);
		
		if (Olympiad.getInstance().isRegistered(player)) {
			Olympiad.getInstance().unregisterPlayer(player);
		}
		
		ThreadPool.schedule(new TeleportTask(player, JailZone.getLocationIn()), 2000);
		
		var msg = new NpcHtmlMessage();
		var content = HtmCache.getInstance().getHtm(player, "data/html/jail_in.htm");
		if (content != null) {
			content = content.replaceAll("%reason%", task != null ? task.getReason() : "");
			content = content.replaceAll("%punishedBy%", task != null ? task.getPunishedBy() : "");
			msg.setHtml(content);
		} else {
			msg.setHtml("<html><body>You have been put in jail by an admin.</body></html>");
		}
		player.sendPacket(msg);
		if (task != null) {
			final long delay = ((task.getExpirationTime() - System.currentTimeMillis()) / 1000);
			if (delay > 0) {
				player.sendMessage("You've been jailed for " + (delay > 60 ? ((delay / 60) + " minutes.") : delay + " seconds."));
			} else {
				player.sendMessage("You've been jailed forever.");
			}
		}
	}

	@Override
	protected void removeFromPlayer(Player player) {
		ThreadPool.schedule(new TeleportTask(player, JailZone.getLocationOut()), 2000);
		
		var msg = new NpcHtmlMessage();
		var content = HtmCache.getInstance().getHtm(player, "data/html/jail_out.htm");
		msg.setHtml(Objects.requireNonNullElse(content, "<html><body>You are free for now, respect server rules!</body></html>"));
		player.sendPacket(msg);
	}
	
	@Override
	public PunishmentType getType() {
		return PunishmentType.JAIL;
	}
}

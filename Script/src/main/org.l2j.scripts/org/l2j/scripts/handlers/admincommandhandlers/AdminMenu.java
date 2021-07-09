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
package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.*;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class AdminMenu implements IAdminCommandHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminMenu.class);
	
	private static final String[] ADMIN_COMMANDS = {
		"admin_char_manage",
		"admin_teleport_character_to_menu",
		"admin_recall_char_menu",
		"admin_recall_party_menu",
		"admin_recall_clan_menu",
		"admin_recall_all",
		"admin_goto_char_menu",
		"admin_kick_menu",
		"admin_kill_menu",
		"admin_ban_menu",
		"admin_unban_menu"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player playerGM)
	{
		if (command.equals("admin_char_manage")) {
			showMainPage(playerGM);
		} else if (command.startsWith("admin_teleport_character_to_menu")) {
			final String[] data = command.split(" ");
			if (data.length == 5) {
				final String playerName = data[1];
				final Player player = World.getInstance().findPlayer(playerName);
				if (player != null) {
					teleportCharacter(player, new Location(Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4])), playerGM, "Admin is teleporting you.");
				}
			}
			showMainPage(playerGM);
		} else if (command.startsWith("admin_recall_char_menu")) {
			try {
				final String targetName = command.substring(23);
				final Player player = World.getInstance().findPlayer(targetName);
				teleportCharacter(player, playerGM.getLocation(), playerGM, "Admin is teleporting you.");
			} catch (StringIndexOutOfBoundsException e) { }
		} else if (command.startsWith("admin_recall_party_menu")) {
			try {
				final String targetName = command.substring(24);
				final Player player = World.getInstance().findPlayer(targetName);
				if (player == null) {
					playerGM.sendPacket(SystemMessageId.INVALID_TARGET);
					return false;
				}
				if (!player.isInParty()) {
					BuilderUtil.sendSysMessage(playerGM, "Player is not in party.");
					teleportCharacter(player, playerGM.getLocation(), playerGM, "Admin is teleporting you.");
					return true;
				}
				for (Player pm : player.getParty().getMembers()) {
					teleportCharacter(pm, playerGM.getLocation(), playerGM, "Your party is being teleported by an Admin.");
				}
			} catch (Exception e) {
				LOGGER.warn("", e);
			}
		} else if (command.startsWith("admin_recall_clan_menu")) {
			try {
				final String targetName = command.substring(23);
				final Player player = World.getInstance().findPlayer(targetName);
				if (player == null) {
					playerGM.sendPacket(SystemMessageId.INVALID_TARGET);
					return false;
				}
				final Clan clan = player.getClan();
				if (clan == null) {
					BuilderUtil.sendSysMessage(playerGM, "Player is not in a clan.");
					teleportCharacter(player, playerGM.getLocation(), playerGM, "Admin is teleporting you.");
					return true;
				}
				
				for (Player member : clan.getOnlineMembers(0)) {
					teleportCharacter(member, playerGM.getLocation(), playerGM, "Your clan is being teleported by an Admin.");
				}
			} catch (Exception e) {
				LOGGER.warn("", e);
			}
		}
		else if(command.startsWith("admin_recall_all")) {
			StringTokenizer tokens = new StringTokenizer(command);
			tokens.nextToken(); // skip command name

			if (tokens.hasMoreTokens()) { // So a radius has been specified
				int radius = Integer.parseInt(tokens.nextToken());
				World.getInstance().forEachPlayer(p -> {
					if(!p.isGM()) {
						p.teleToLocation(playerGM, radius);
					}
				});

				if (tokens.hasMoreTokens()) { // Monster ID has been sent
					int monsterId = Integer.parseInt(tokens.nextToken());
					int monsterCount = tokens.hasMoreTokens() ? Integer.parseInt(tokens.nextToken()) : 10; //Default monster count = 10
					World.getInstance().forEachPlayer(p -> {
						if(!p.isGM()) {
							spawnMonster(playerGM, p, monsterId, 60, monsterCount);
						}
					});
				}
			} else {
			World.getInstance().forEachPlayer(p -> {
				if(!p.isGM()) {
					p.teleToLocation(playerGM, true);
				}
			});
		}
		} else if (command.startsWith("admin_goto_char_menu")) {
			try {
				final Player player = World.getInstance().findPlayer(command.substring(21));
				teleportToCharacter(playerGM, player);
			}
			catch (StringIndexOutOfBoundsException e) {}
		} else if (command.equals("admin_kill_menu")) {
			handleKill(playerGM);
		} else if (command.startsWith("admin_kick_menu")) {
			final StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1) {
				st.nextToken();
				final String player = st.nextToken();
				final Player plyr = World.getInstance().findPlayer(player);
				String text;
				if (plyr != null) {
					Disconnection.of(plyr).logout(false);
					text = "You kicked " + plyr.getName() + " from the game.";
				} else {
					text = "Player " + player + " was not found in the game.";
				}
				playerGM.sendMessage(text);
			}
			showMainPage(playerGM);
		} else if (command.startsWith("admin_ban_menu")) {
			final StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1) {
				final String subCommand = "admin_ban_char";
				AdminCommandHandler.getInstance().useAdminCommand(playerGM, subCommand + command.substring(14), true);
			}
			showMainPage(playerGM);
		} else if (command.startsWith("admin_unban_menu")) {
			final StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1) {
				final String subCommand = "admin_unban_char";
				AdminCommandHandler.getInstance().useAdminCommand(playerGM, subCommand + command.substring(16), true);
			}
			showMainPage(playerGM);
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	private void handleKill(Player activeChar) {
		handleKill(activeChar, null);
	}
	
	private void handleKill(Player activeChar, String player) {
		final WorldObject obj = activeChar.getTarget();
		Creature target = (Creature) obj;
		String filename = "main_menu.htm";
		if (player != null) {
			final Player plyr = World.getInstance().findPlayer(player);
			if (plyr != null) {
				target = plyr;
				BuilderUtil.sendSysMessage(activeChar, "You killed " + plyr.getName());
			}
		}
		if (target != null) {
			if (isPlayer(target)) {
				target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1d, activeChar, null, DamageInfo.DamageType.OTHER);
				filename = "charmanage.htm";
			} else if (Config.CHAMPION_ENABLE && target.isChampion()) {
				target.reduceCurrentHp((target.getMaxHp() * Config.CHAMPION_HP) + 1d, activeChar, null, DamageInfo.DamageType.OTHER);
			} else {
				target.reduceCurrentHp(target.getMaxHp() + 1d, activeChar, null, DamageInfo.DamageType.OTHER);
			}
		} else {
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
		}
		AdminHtml.showAdminHtml(activeChar, filename);
	}
	
	private void teleportCharacter(Player player, Location loc, Player activeChar, String message) {
		if (player != null) {
			player.sendMessage(message);
			player.teleToLocation(loc, true);
		}
		showMainPage(activeChar);
	}
	
	private void teleportToCharacter(Player activeChar, WorldObject target) {
		if (!isPlayer(target)) {
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final Player player = target.getActingPlayer();
		if (player.getObjectId() == activeChar.getObjectId()) {
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
		}
		else {
			activeChar.teleToLocation(player.getLocation(), true, player.getInstanceWorld());
			BuilderUtil.sendSysMessage(activeChar, "You're teleporting yourself to character " + player.getName());
		}
		showMainPage(activeChar);
	}
	
	private void spawnMonster(Player gm, Player target, int monsterId, int respawnTime, int mobCount) {
		final NpcTemplate template1;
		final int monsterTemplate = monsterId;

		template1 = NpcData.getInstance().getTemplate(monsterTemplate);

		try {
			final Spawn spawn = new Spawn(template1);
			spawn.setXYZ(target);
			spawn.setAmount(mobCount);
			spawn.setHeading(target.getHeading());
			spawn.setRespawnDelay(respawnTime);
			if (target.isInInstance())
				spawn.setInstanceId(target.getInstanceId());

			SpawnTable.getInstance().addNewSpawn(spawn, false);
			spawn.init();

			if (respawnTime <= 0)
				spawn.stopRespawn();

			spawn.getLastSpawn().broadcastInfo();
			BuilderUtil.sendSysMessage(gm, "Created " + template1.getName() + " on " + target.getObjectId());
		} catch (Exception e) {
			gm.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
		}
	}

	/**
	 * @param activeChar
	 */
	private void showMainPage(Player activeChar) {
		AdminHtml.showAdminHtml(activeChar, "charmanage.htm");
	}
}

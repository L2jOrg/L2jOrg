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
package handlers.admincommandhandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.enums.AdminTeleportType;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.RaidBoss;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * This class handles following admin commands: - show_moves - show_teleport - teleport_to_character - move_to - teleport_character
 * @version $Revision: 1.3.2.6.2.4 $ $Date: 2005/04/11 10:06:06 $ con.close() change and small typo fix by Zoey76 24/02/2011
 *
 * @author JoeAlisson
 */
public class AdminTeleport implements IAdminCommandHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminTeleport.class);
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_show_moves",
		"admin_show_moves_other",
		"admin_show_teleport",
		"admin_teleport_to_character",
		"admin_teleportto",
		"admin_teleport",
		"admin_move_to",
		"admin_teleport_character",
		"admin_recall",
		"admin_walk",
		"teleportto",
		"recall",
		"admin_recall_npc",
		"admin_gonorth",
		"admin_gosouth",
		"admin_goeast",
		"admin_gowest",
		"admin_goup",
		"admin_godown",
		"admin_tele",
		"admin_teleto",
		"admin_instant_move",
		"admin_sendhome"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_instant_move"))
		{
			BuilderUtil.sendSysMessage(activeChar, "Instant move ready. Click where you want to go.");
			activeChar.setTeleMode(AdminTeleportType.DEMONIC);
		}
		else if (command.equals("admin_teleto charge"))
		{
			BuilderUtil.sendSysMessage(activeChar, "Charge move ready. Click where you want to go.");
			activeChar.setTeleMode(AdminTeleportType.CHARGE);
		}
		else if (command.equals("admin_teleto end"))
		{
			activeChar.setTeleMode(AdminTeleportType.NORMAL);
		}
		else if (command.equals("admin_show_moves"))
		{
			AdminHtml.showAdminHtml(activeChar, "teleports.htm");
		}
		else if (command.equals("admin_show_moves_other"))
		{
			AdminHtml.showAdminHtml(activeChar, "tele/other.html");
		}
		else if (command.equals("admin_show_teleport"))
		{
			showTeleportCharWindow(activeChar);
		}
		else if (command.equals("admin_recall_npc"))
		{
			recallNPC(activeChar);
		}
		else if (command.equals("admin_teleport_to_character"))
		{
			teleportToCharacter(activeChar, activeChar.getTarget());
		}
		else if (command.startsWith("admin_walk"))
		{
			try
			{
				final String val = command.substring(11);
				final StringTokenizer st = new StringTokenizer(val);
				final int x = Integer.parseInt(st.nextToken());
				final int y = Integer.parseInt(st.nextToken());
				final int z = Integer.parseInt(st.nextToken());
				activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(x, y, z, 0));
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("admin_move_to"))
		{
			try
			{
				final String val = command.substring(14);
				teleportTo(activeChar, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of empty or missing coordinates
				AdminHtml.showAdminHtml(activeChar, "teleports.htm");
			}
			catch (NumberFormatException nfe)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //move_to <x> <y> <z>");
				AdminHtml.showAdminHtml(activeChar, "teleports.htm");
			}
		}
		else if (command.startsWith("admin_teleport_character"))
		{
			try
			{
				final String val = command.substring(25);
				
				teleportCharacter(activeChar, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of empty coordinates
				BuilderUtil.sendSysMessage(activeChar, "Wrong or no Coordinates given.");
				showTeleportCharWindow(activeChar); // back to character teleport
			}
		}
		else if (command.startsWith("admin_teleportto "))
		{
			try
			{
				final String targetName = command.substring(17);
				final Player player = World.getInstance().findPlayer(targetName);
				teleportToCharacter(activeChar, player);
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		else if (command.startsWith("admin_teleport"))
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(command, " ");
				st.nextToken();
				final int x = (int) Float.parseFloat(st.nextToken());
				final int y = (int) Float.parseFloat(st.nextToken());
				final int z = st.hasMoreTokens() ? ((int) Float.parseFloat(st.nextToken())) : GeoEngine.getInstance().getHeight(x, y, 10000);
				
				activeChar.teleToLocation(x, y, z);
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Wrong coordinates!");
			}
		}
		else if (command.startsWith("admin_recall "))
		{
			try
			{
				final String[] param = command.split(" ");
				if (param.length != 2)
				{
					BuilderUtil.sendSysMessage(activeChar, "Usage: //recall <playername>");
					return false;
				}
				final String targetName = param[1];
				final Player player = World.getInstance().findPlayer(targetName);
				if (player != null)
				{
					teleportCharacter(player, activeChar.getLocation(), activeChar);
				}
				else
				{
					changeCharacterPosition(activeChar, targetName);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		else if (command.equals("admin_tele"))
		{
			showTeleportWindow(activeChar);
		}
		else if (command.startsWith("admin_go"))
		{
			int intVal = 150;
			int x = activeChar.getX();
			int y = activeChar.getY();
			int z = activeChar.getZ();
			try
			{
				final String val = command.substring(8);
				final StringTokenizer st = new StringTokenizer(val);
				final String dir = st.nextToken();
				if (st.hasMoreTokens())
				{
					intVal = Integer.parseInt(st.nextToken());
				}
				if (dir.equals("east"))
				{
					x += intVal;
				}
				else if (dir.equals("west"))
				{
					x -= intVal;
				}
				else if (dir.equals("north"))
				{
					y -= intVal;
				}
				else if (dir.equals("south"))
				{
					y += intVal;
				}
				else if (dir.equals("up"))
				{
					z += intVal;
				}
				else if (dir.equals("down"))
				{
					z -= intVal;
				}
				activeChar.teleToLocation(new Location(x, y, z));
				showTeleportWindow(activeChar);
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //go<north|south|east|west|up|down> [offset] (default 150)");
			}
		}
		else if (command.startsWith("admin_sendhome"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // Skip command.
			if (st.countTokens() > 1)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //sendhome <playername>");
			}
			else if (st.countTokens() == 1)
			{
				final String name = st.nextToken();
				final Player player = World.getInstance().findPlayer(name);
				if (player == null)
				{
					activeChar.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
					return false;
				}
				teleportHome(player);
			}
			else
			{
				final WorldObject target = activeChar.getTarget();
				if (isPlayer(target))
				{
					teleportHome(target.getActingPlayer());
				}
				else
				{
					activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
				}
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	/**
	 * This method sends a player to it's home town.
	 * @param player the player to teleport.
	 */
	private void teleportHome(Player player) {

		String regionName = switch (player.getRace()) {
			case ELF -> "elf_town";
			case DARK_ELF -> "darkelf_town";
			case ORC -> "orc_town";
			case DWARF -> "dwarf_town";
			case JIN_KAMAEL -> "kamael_town";
			default-> "talking_island_town";
		};
		
		player.teleToLocation(MapRegionManager.getInstance().getMapRegionByName(regionName).getSpawnLoc(), true, null);
	}
	
	private void teleportTo(Player activeChar, String Coords)
	{
		try
		{
			final StringTokenizer st = new StringTokenizer(Coords);
			final int x = Integer.parseInt(st.nextToken());
			final int y = Integer.parseInt(st.nextToken());
			final int z = Integer.parseInt(st.nextToken());
			
			activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			activeChar.teleToLocation(x, y, z, null);
			BuilderUtil.sendSysMessage(activeChar, "You have been teleported to " + Coords);
		}
		catch (NoSuchElementException nsee)
		{
			BuilderUtil.sendSysMessage(activeChar, "Wrong or no Coordinates given.");
		}
	}
	
	private void showTeleportWindow(Player activeChar)
	{
		AdminHtml.showAdminHtml(activeChar, "move.htm");
	}
	
	private void showTeleportCharWindow(Player activeChar)
	{
		final WorldObject target = activeChar.getTarget();
		Player player = null;
		if ((target != null) && isPlayer(target))
		{
			player = (Player) target;
		}
		else
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
		
		final String replyMSG = "<html><title>Teleport Character</title><body>The character you will teleport is " + player.getName() + ".<br>Co-ordinate x<edit var=\"char_cord_x\" width=110>Co-ordinate y<edit var=\"char_cord_y\" width=110>Co-ordinate z<edit var=\"char_cord_z\" width=110><button value=\"Teleport\" action=\"bypass -h admin_teleport_character $char_cord_x $char_cord_y $char_cord_z\" width=60 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><button value=\"Teleport near you\" action=\"bypass -h admin_teleport_character " + activeChar.getX() + " " + activeChar.getY() + " " + activeChar.getZ() + "\" width=115 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></body></html>";
		adminReply.setHtml(replyMSG);
		activeChar.sendPacket(adminReply);
	}
	
	private void teleportCharacter(Player activeChar, String Cords)
	{
		final WorldObject target = activeChar.getTarget();
		Player player = null;
		if ((target != null) && isPlayer(target))
		{
			player = (Player) target;
		}
		else
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		if (player.getObjectId() == activeChar.getObjectId())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
		}
		else
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(Cords);
				final String x1 = st.nextToken();
				final int x = Integer.parseInt(x1);
				final String y1 = st.nextToken();
				final int y = Integer.parseInt(y1);
				final String z1 = st.nextToken();
				final int z = Integer.parseInt(z1);
				teleportCharacter(player, new Location(x, y, z), null);
			}
			catch (NoSuchElementException nsee)
			{
			}
		}
	}
	
	/**
	 * @param player
	 * @param loc
	 * @param activeChar
	 */
	private void teleportCharacter(Player player, Location loc, Player activeChar)
	{
		if (player != null)
		{
			// Check for jail
			if (player.isJailed())
			{
				BuilderUtil.sendSysMessage(activeChar, "Sorry, player " + player.getName() + " is in Jail.");
			}
			else
			{
				BuilderUtil.sendSysMessage(activeChar, "You have recalled " + player.getName());
				player.sendMessage("Admin is teleporting you.");
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				player.teleToLocation(loc, true, activeChar.getInstanceWorld());
			}
		}
	}
	
	private void teleportToCharacter(Player activeChar, WorldObject target)
	{
		if ((target == null) || !isPlayer(target))
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final Player player = target.getActingPlayer();
		if (player.getObjectId() == activeChar.getObjectId())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
		}
		else
		{
			activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			activeChar.teleToLocation(player, true, player.getInstanceWorld());
			BuilderUtil.sendSysMessage(activeChar, "You have teleported to character " + player.getName() + ".");
		}
	}
	
	private void changeCharacterPosition(Player activeChar, String name) {
		final int x = activeChar.getX();
		final int y = activeChar.getY();
		final int z = activeChar.getZ();

		if(getDAO(PlayerDAO.class).updateLocationByName(name, x, y, z)) {
			BuilderUtil.sendSysMessage(activeChar, "Player's [" + name + "] position is now set to (" + x + "," + y + "," + z + ").");
		}
	}
	
	private void recallNPC(Player activeChar)
	{
		final WorldObject obj = activeChar.getTarget();
		if ((obj instanceof Npc) && !((Npc) obj).isMinion() && !(obj instanceof RaidBoss) && !(obj instanceof GrandBoss))
		{
			final Npc target = (Npc) obj;
			
			final int monsterTemplate = target.getTemplate().getId();
			final NpcTemplate template1 = NpcData.getInstance().getTemplate(monsterTemplate);
			if (template1 == null)
			{
				BuilderUtil.sendSysMessage(activeChar, "Incorrect monster template.");
				LOGGER.warn("ERROR: NPC " + target.getObjectId() + " has a 'null' template.");
				return;
			}
			
			Spawn spawn = target.getSpawn();
			if (spawn == null)
			{
				BuilderUtil.sendSysMessage(activeChar, "Incorrect monster spawn.");
				LOGGER.warn("ERROR: NPC " + target.getObjectId() + " has a 'null' spawn.");
				return;
			}
			final int respawnTime = spawn.getRespawnDelay() / 1000;
			
			target.deleteMe();
			spawn.stopRespawn();
			SpawnTable.getInstance().deleteSpawn(spawn, true);
			
			try
			{
				spawn = new Spawn(template1);
				spawn.setXYZ(activeChar);
				spawn.setAmount(1);
				spawn.setHeading(activeChar.getHeading());
				spawn.setRespawnDelay(respawnTime);
				if (activeChar.isInInstance())
				{
					spawn.setInstanceId(activeChar.getInstanceId());
				}
				SpawnTable.getInstance().addNewSpawn(spawn, true);
				spawn.init();

				if (respawnTime <= 0)
				{
					spawn.stopRespawn();
				}
				
				BuilderUtil.sendSysMessage(activeChar, "Created " + template1.getName() + " on " + target.getObjectId() + ".");
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Target is not in game.");
			}
			
		}
		else if (obj instanceof RaidBoss)
		{
			final RaidBoss target = (RaidBoss) obj;
			final Spawn spawn = target.getSpawn();
			final double curHP = target.getCurrentHp();
			final double curMP = target.getCurrentMp();
			if (spawn == null)
			{
				BuilderUtil.sendSysMessage(activeChar, "Incorrect raid spawn.");
				LOGGER.warn("ERROR: NPC Id" + target.getId() + " has a 'null' spawn.");
				return;
			}
			DBSpawnManager.getInstance().deleteSpawn(spawn, true);
			try
			{
				final Spawn spawnDat = new Spawn(target.getId());
				spawnDat.setXYZ(activeChar);
				spawnDat.setAmount(1);
				spawnDat.setHeading(activeChar.getHeading());
				spawnDat.setRespawnMinDelay(43200);
				spawnDat.setRespawnMaxDelay(129600);
				
				DBSpawnManager.getInstance().addNewSpawn(spawnDat, 0, curHP, curMP, true);
			}
			catch (Exception e)
			{
				activeChar.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
			}
		}
		else
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
		}
	}
	
}

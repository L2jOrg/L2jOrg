/*
 * Copyright © 2019 L2J Mobius
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

import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.MobGroup;
import org.l2j.gameserver.model.MobGroupTable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.network.serverpackets.SetupGauge;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.world.World;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author littlecrow Admin commands handler for controllable mobs
 */
public class AdminMobGroup implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_mobmenu",
		"admin_mobgroup_list",
		"admin_mobgroup_create",
		"admin_mobgroup_remove",
		"admin_mobgroup_delete",
		"admin_mobgroup_spawn",
		"admin_mobgroup_unspawn",
		"admin_mobgroup_kill",
		"admin_mobgroup_idle",
		"admin_mobgroup_attack",
		"admin_mobgroup_rnd",
		"admin_mobgroup_return",
		"admin_mobgroup_follow",
		"admin_mobgroup_casting",
		"admin_mobgroup_nomove",
		"admin_mobgroup_attackgrp",
		"admin_mobgroup_invul"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_mobmenu"))
		{
			showMainPage(activeChar, command);
			return true;
		}
		else if (command.equals("admin_mobgroup_list"))
		{
			showGroupList(activeChar);
		}
		else if (command.startsWith("admin_mobgroup_create"))
		{
			createGroup(command, activeChar);
		}
		else if (command.startsWith("admin_mobgroup_delete") || command.startsWith("admin_mobgroup_remove"))
		{
			removeGroup(command, activeChar);
		}
		else if (command.startsWith("admin_mobgroup_spawn"))
		{
			spawnGroup(command, activeChar);
		}
		else if (command.startsWith("admin_mobgroup_unspawn"))
		{
			unspawnGroup(command, activeChar);
		}
		else if (command.startsWith("admin_mobgroup_kill"))
		{
			killGroup(command, activeChar);
		}
		else if (command.startsWith("admin_mobgroup_attackgrp"))
		{
			attackGrp(command, activeChar);
		}
		else if (command.startsWith("admin_mobgroup_attack"))
		{
			if (isCreature(activeChar.getTarget()))
			{
				final Creature target = (Creature) activeChar.getTarget();
				attack(command, activeChar, target);
			}
		}
		else if (command.startsWith("admin_mobgroup_rnd"))
		{
			setNormal(command, activeChar);
		}
		else if (command.startsWith("admin_mobgroup_idle"))
		{
			idle(command, activeChar);
		}
		else if (command.startsWith("admin_mobgroup_return"))
		{
			returnToChar(command, activeChar);
		}
		else if (command.startsWith("admin_mobgroup_follow"))
		{
			follow(command, activeChar, activeChar);
		}
		else if (command.startsWith("admin_mobgroup_casting"))
		{
			setCasting(command, activeChar);
		}
		else if (command.startsWith("admin_mobgroup_nomove"))
		{
			noMove(command, activeChar);
		}
		else if (command.startsWith("admin_mobgroup_invul"))
		{
			invul(command, activeChar);
		}
		else if (command.startsWith("admin_mobgroup_teleport"))
		{
			teleportGroup(command, activeChar);
		}
		showMainPage(activeChar, command);
		return true;
	}
	
	/**
	 * @param activeChar
	 * @param command
	 */
	private void showMainPage(Player activeChar, String command)
	{
		final String filename = "mobgroup.htm";
		AdminHtml.showAdminHtml(activeChar, filename);
	}
	
	private void returnToChar(String command, Player activeChar)
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
			return;
		}
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		group.returnGroup(activeChar);
	}
	
	private void idle(String command, Player activeChar)
	{
		int groupId;
		
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
			return;
		}
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		group.setIdleMode();
	}
	
	private void setNormal(String command, Player activeChar)
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
			return;
		}
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		group.setAttackRandom();
	}
	
	private void attack(String command, Player activeChar, Creature target)
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
			return;
		}
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		group.setAttackTarget(target);
	}
	
	private void follow(String command, Player activeChar, Creature target)
	{
		int groupId;
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
			return;
		}
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		group.setFollowMode(target);
	}
	
	private void createGroup(String command, Player activeChar)
	{
		int groupId;
		int templateId;
		int mobCount;
		
		try
		{
			final String[] cmdParams = command.split(" ");
			
			groupId = Integer.parseInt(cmdParams[1]);
			templateId = Integer.parseInt(cmdParams[2]);
			mobCount = Integer.parseInt(cmdParams[3]);
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_create <group> <npcid> <count>");
			return;
		}
		
		if (MobGroupTable.getInstance().getGroup(groupId) != null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Mob group " + groupId + " already exists.");
			return;
		}
		
		final NpcTemplate template = NpcData.getInstance().getTemplate(templateId);
		
		if (template == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid NPC ID specified.");
			return;
		}
		
		final MobGroup group = new MobGroup(groupId, template, mobCount);
		MobGroupTable.getInstance().addGroup(groupId, group);
		
		BuilderUtil.sendSysMessage(activeChar, "Mob group " + groupId + " created.");
	}
	
	private void removeGroup(String command, Player activeChar)
	{
		int groupId;
		
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_remove <groupId>");
			return;
		}
		
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		
		doAnimation(activeChar);
		group.unspawnGroup();
		
		if (MobGroupTable.getInstance().removeGroup(groupId))
		{
			BuilderUtil.sendSysMessage(activeChar, "Mob group " + groupId + " unspawned and removed.");
		}
	}
	
	private void spawnGroup(String command, Player activeChar)
	{
		int groupId;
		boolean topos = false;
		int posx = 0;
		int posy = 0;
		int posz = 0;
		
		try
		{
			final String[] cmdParams = command.split(" ");
			groupId = Integer.parseInt(cmdParams[1]);
			
			try
			{ // we try to get a position
				posx = Integer.parseInt(cmdParams[2]);
				posy = Integer.parseInt(cmdParams[3]);
				posz = Integer.parseInt(cmdParams[4]);
				topos = true;
			}
			catch (Exception e)
			{
				// no position given
			}
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_spawn <group> [ x y z ]");
			return;
		}
		
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		
		doAnimation(activeChar);
		
		if (topos)
		{
			group.spawnGroup(posx, posy, posz);
		}
		else
		{
			group.spawnGroup(activeChar);
		}
		
		BuilderUtil.sendSysMessage(activeChar, "Mob group " + groupId + " spawned.");
	}
	
	private void unspawnGroup(String command, Player activeChar)
	{
		int groupId;
		
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_unspawn <groupId>");
			return;
		}
		
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		
		doAnimation(activeChar);
		group.unspawnGroup();
		
		BuilderUtil.sendSysMessage(activeChar, "Mob group " + groupId + " unspawned.");
	}
	
	private void killGroup(String command, Player activeChar)
	{
		int groupId;
		
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_kill <groupId>");
			return;
		}
		
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		
		doAnimation(activeChar);
		group.killGroup(activeChar);
	}
	
	private void setCasting(String command, Player activeChar)
	{
		int groupId;
		
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_casting <groupId>");
			return;
		}
		
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		
		group.setCastMode();
	}
	
	private void noMove(String command, Player activeChar)
	{
		int groupId;
		String enabled;
		
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
			enabled = command.split(" ")[2];
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_nomove <groupId> <on|off>");
			return;
		}
		
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		
		if (enabled.equalsIgnoreCase("on") || enabled.equalsIgnoreCase("true"))
		{
			group.setNoMoveMode(true);
		}
		else if (enabled.equalsIgnoreCase("off") || enabled.equalsIgnoreCase("false"))
		{
			group.setNoMoveMode(false);
		}
		else
		{
			BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
		}
	}
	
	private void doAnimation(Player activeChar)
	{
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, 1008, 1, 4000, 0), 1500);
		activeChar.sendPacket(new SetupGauge(activeChar.getObjectId(), 0, 4000));
	}
	
	private void attackGrp(String command, Player activeChar)
	{
		int groupId;
		int othGroupId;
		
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
			othGroupId = Integer.parseInt(command.split(" ")[2]);
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_attackgrp <groupId> <TargetGroupId>");
			return;
		}
		
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		
		final MobGroup othGroup = MobGroupTable.getInstance().getGroup(othGroupId);
		
		if (othGroup == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Incorrect target group.");
			return;
		}
		
		group.setAttackGroup(othGroup);
	}
	
	private void invul(String command, Player activeChar)
	{
		int groupId;
		String enabled;
		
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
			enabled = command.split(" ")[2];
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_invul <groupId> <on|off>");
			return;
		}
		
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		
		if (enabled.equalsIgnoreCase("on") || enabled.equalsIgnoreCase("true"))
		{
			group.setInvul(true);
		}
		else if (enabled.equalsIgnoreCase("off") || enabled.equalsIgnoreCase("false"))
		{
			group.setInvul(false);
		}
		else
		{
			BuilderUtil.sendSysMessage(activeChar, "Incorrect command arguments.");
		}
	}
	
	private void teleportGroup(String command, Player activeChar)
	{
		int groupId;
		String targetPlayerStr = null;
		Player targetPlayer = null;
		
		try
		{
			groupId = Integer.parseInt(command.split(" ")[1]);
			targetPlayerStr = command.split(" ")[2];
			
			if (targetPlayerStr != null)
			{
				targetPlayer = World.getInstance().findPlayer(targetPlayerStr);
			}
			
			if (targetPlayer == null)
			{
				targetPlayer = activeChar;
			}
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Usage: //mobgroup_teleport <groupId> [playerName]");
			return;
		}
		
		final MobGroup group = MobGroupTable.getInstance().getGroup(groupId);
		
		if (group == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Invalid group specified.");
			return;
		}
		
		group.teleportGroup(activeChar);
	}
	
	private void showGroupList(Player activeChar)
	{
		final MobGroup[] mobGroupList = MobGroupTable.getInstance().getGroups();
		
		BuilderUtil.sendSysMessage(activeChar, "======= <Mob Groups> =======");
		
		for (MobGroup mobGroup : mobGroupList)
		{
			activeChar.sendMessage(mobGroup.getGroupId() + ": " + mobGroup.getActiveMobCount() + " alive out of " + mobGroup.getMaxMobCount() + " of NPC ID " + mobGroup.getTemplate().getId() + " (" + mobGroup.getStatus() + ")");
		}
		
		activeChar.sendPacket(SystemMessageId.SEPARATOR_EQUALS);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}

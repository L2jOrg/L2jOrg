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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.model.html.styles.ButtonsStyle;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.gameserver.world.World;

import java.util.*;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class AdminBuffs implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_buff",
		"admin_getbuffs",
		"admin_getbuffs_ps",
		"admin_stopbuff",
		"admin_stopallbuffs",
		"admin_viewblockedeffects",
		"admin_areacancel",
		"admin_removereuse",
		"admin_switch_gm_buffs"
	};
	// Misc
	private static final String FONT_RED1 = "<font color=\"FF0000\">";
	private static final String FONT_RED2 = "</font>";
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_buff"))
		{
			if ( !isCreature(activeChar.getTarget()))
			{
				activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
			
			final StringTokenizer st = new StringTokenizer(command, " ");
			command = st.nextToken();
			if (!st.hasMoreTokens())
			{
				BuilderUtil.sendSysMessage(activeChar, "Skill Id and level are not specified.");
				BuilderUtil.sendSysMessage(activeChar, "Usage: //buff <skillId> <skillLevel>");
				return false;
			}
			
			try
			{
				final int skillId = Integer.parseInt(st.nextToken());
				final int skillLevel = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : SkillEngine.getInstance().getMaxLevel(skillId);
				final Creature target = (Creature) activeChar.getTarget();
				final Skill skill = SkillEngine.getInstance().getSkill(skillId, skillLevel);
				if (skill == null)
				{
					BuilderUtil.sendSysMessage(activeChar, "Skill with id: " + skillId + ", lvl: " + skillLevel + " not found.");
					return false;
				}
				
				BuilderUtil.sendSysMessage(activeChar, "Admin buffing " + skill.getName() + " (" + skillId + "," + skillLevel + ")");
				skill.applyEffects(activeChar, target);
				return true;
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Failed buffing: " + e.getMessage());
				BuilderUtil.sendSysMessage(activeChar, "Usage: //buff <skillId> <skillLevel>");
				return false;
			}
		}
		else if (command.startsWith("admin_getbuffs"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			command = st.nextToken();
			if (st.hasMoreTokens())
			{
				final String playername = st.nextToken();
				final Player player = World.getInstance().findPlayer(playername);
				if (player != null)
				{
					int page = 0;
					if (st.hasMoreTokens())
					{
						page = Integer.parseInt(st.nextToken());
					}
					showBuffs(activeChar, player, page, command.endsWith("_ps"));
					return true;
				}
				BuilderUtil.sendSysMessage(activeChar, "The player " + playername + " is not online.");
				return false;
			}
			else if (isCreature(activeChar.getTarget()))
			{
				showBuffs(activeChar, (Creature) activeChar.getTarget(), 0, command.endsWith("_ps"));
				return true;
			}
			else
			{
				activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
		}
		else if (command.startsWith("admin_stopbuff"))
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(command, " ");
				
				st.nextToken();
				final int objectId = Integer.parseInt(st.nextToken());
				final int skillId = Integer.parseInt(st.nextToken());
				
				removeBuff(activeChar, objectId, skillId);
				return true;
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Failed removing effect: " + e.getMessage());
				BuilderUtil.sendSysMessage(activeChar, "Usage: //stopbuff <objectId> <skillId>");
				return false;
			}
		}
		else if (command.startsWith("admin_stopallbuffs"))
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(command, " ");
				st.nextToken();
				final int objectId = Integer.parseInt(st.nextToken());
				removeAllBuffs(activeChar, objectId);
				return true;
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Failed removing all effects: " + e.getMessage());
				BuilderUtil.sendSysMessage(activeChar, "Usage: //stopallbuffs <objectId>");
				return false;
			}
		}
		else if (command.startsWith("admin_viewblockedeffects"))
		{
			try
			{
				StringTokenizer st = new StringTokenizer(command, " ");
				st.nextToken();
				int objectId = Integer.parseInt(st.nextToken());
				viewBlockedEffects(activeChar, objectId);
				return true;
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Failed viewing blocked effects: " + e.getMessage());
				BuilderUtil.sendSysMessage(activeChar, "Usage: //viewblockedeffects <objectId>");
				return false;
			}
		}
		else if (command.startsWith("admin_areacancel"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			final String val = st.nextToken();
			try
			{
				final int radius = Integer.parseInt(val);
				
				World.getInstance().forEachVisibleObjectInRange(activeChar, Player.class, radius, Creature::stopAllEffects);
				
				BuilderUtil.sendSysMessage(activeChar, "All effects canceled within radius " + radius);
				return true;
			}
			catch (NumberFormatException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //areacancel <radius>");
				return false;
			}
		}
		else if (command.startsWith("admin_removereuse"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			command = st.nextToken();
			
			Player player = null;
			if (st.hasMoreTokens())
			{
				final String playername = st.nextToken();
				
				try
				{
					player = World.getInstance().findPlayer(playername);
				}
				catch (Exception e)
				{
				}
				
				if (player == null)
				{
					BuilderUtil.sendSysMessage(activeChar, "The player " + playername + " is not online.");
					return false;
				}
			}
			else if ((activeChar.getTarget() != null) && isPlayer(activeChar.getTarget()))
			{
				player = activeChar.getTarget().getActingPlayer();
			}
			else
			{
				activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
			
			try
			{
				player.resetTimeStamps();
				player.resetDisabledSkills();
				player.sendPacket(new SkillCoolTime(player));
				BuilderUtil.sendSysMessage(activeChar, "Skill reuse was removed from " + player.getName() + ".");
				return true;
			}
			catch (NullPointerException e)
			{
				return false;
			}
		}
		else if (command.startsWith("admin_switch_gm_buffs"))
		{
			if (Config.GM_GIVE_SPECIAL_SKILLS != Config.GM_GIVE_SPECIAL_AURA_SKILLS)
			{
				final boolean toAuraSkills = activeChar.getKnownSkill(7041) != null;
				switchSkills(activeChar, toAuraSkills);
				activeChar.sendSkillList();
				BuilderUtil.sendSysMessage(activeChar, "You have succefully changed to target " + (toAuraSkills ? "aura" : "one") + " special skills.");
				return true;
			}
			BuilderUtil.sendSysMessage(activeChar, "There is nothing to switch.");
			return false;
		}
		return true;
	}
	
	/**
	 * @param gmchar the player to switch the Game Master skills.
	 * @param toAuraSkills if {@code true} it will remove "GM Aura" skills and add "GM regular" skills, vice versa if {@code false}.
	 */
	private static void switchSkills(Player gmchar, boolean toAuraSkills)
	{
		final Collection<Skill> skills = toAuraSkills ? SkillTreesData.getInstance().getGMSkillTree() : SkillTreesData.getInstance().getGMAuraSkillTree();
		for (Skill skill : skills)
		{
			gmchar.removeSkill(skill, false); // Don't Save GM skills to database
		}
		SkillTreesData.getInstance().addSkills(gmchar, toAuraSkills);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static void showBuffs(Player activeChar, Creature target, int page, boolean passive)
	{
		final List<BuffInfo> effects = new ArrayList<>();
		if (!passive)
		{
			effects.addAll(target.getEffectList().getEffects());
		}
		else
		{
			effects.addAll(target.getEffectList().getPassives());
		}
		
		final String pageLink = "bypass -h admin_getbuffs" + (passive ? "_ps " : " ") + target.getName();
		
		final PageResult result = PageBuilder.newBuilder(effects, 3, pageLink).currentPage(page).style(ButtonsStyle.INSTANCE).bodyHandler((pages, info, sb) ->
		{
			for (AbstractEffect effect : info.getEffects())
			{
				sb.append("<tr><td>");
				sb.append(!info.isInUse() ? FONT_RED1 : "");
				sb.append(info.getSkill().getName());
				sb.append(" Lv ");
				sb.append(info.getSkill().getLevel());
				sb.append(" (");
				sb.append(effect.getClass().getSimpleName());
				sb.append(")");
				sb.append(!info.isInUse() ? FONT_RED2 : "");
				sb.append("</td><td>");
				sb.append(info.getSkill().isToggle() ? "T" : info.getSkill().isPassive() ? "P" : info.getTime() + "s");
				sb.append("</td><td><button value=\"X\" action=\"bypass -h admin_stopbuff ");
				sb.append(target.getObjectId());
				sb.append(" ");
				sb.append(info.getSkill().getId());
				sb.append("\" width=30 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
			}
		}).build();
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
		html.setFile(activeChar, "data/html/admin/getbuffs.htm");
		
		if (result.getPages() > 0)
		{
			html.replace("%pages%", "<table width=280 cellspacing=0><tr>" + result.getPagerTemplate() + "</tr></table>");
		}
		else
		{
			html.replace("%pages%", "");
		}
		
		html.replace("%targetName%", target.getName());
		html.replace("%targetObjId%", target.getObjectId());
		html.replace("%buffs%", result.getBodyTemplate().toString());
		html.replace("%effectSize%", effects.size());
		activeChar.sendPacket(html);
		
		if (getSettings(GeneralSettings.class).auditGM())
		{
			GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", "getbuffs", target.getName() + " (" + target.getObjectId() + ")", "");
		}
	}
	
	private static void removeBuff(Player activeChar, int objId, int skillId)
	{
		Creature target = null;
		try
		{
			target = (Creature) World.getInstance().findObject(objId);
		}
		catch (Exception e)
		{
		}
		
		if ((target != null) && (skillId > 0))
		{
			if (target.isAffectedBySkill(skillId))
			{
				target.stopSkillEffects(true, skillId);
				BuilderUtil.sendSysMessage(activeChar, "Removed skill ID: " + skillId + " effects from " + target.getName() + " (" + objId + ").");
			}
			
			showBuffs(activeChar, target, 0, false);
			if (getSettings(GeneralSettings.class).auditGM())
			{
				GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", "stopbuff", target.getName() + " (" + objId + ")", Integer.toString(skillId));
			}
		}
	}
	
	private static void removeAllBuffs(Player activeChar, int objId)
	{
		Creature target = null;
		try
		{
			target = (Creature) World.getInstance().findObject(objId);
		}
		catch (Exception e)
		{
		}
		
		if (target != null)
		{
			target.stopAllEffects();
			BuilderUtil.sendSysMessage(activeChar, "Removed all effects from " + target.getName() + " (" + objId + ")");
			showBuffs(activeChar, target, 0, false);
			if (getSettings(GeneralSettings.class).auditGM())
			{
				GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", "stopallbuffs", target.getName() + " (" + objId + ")", "");
			}
		}
	}
	
	private static void viewBlockedEffects(Player activeChar, int objId)
	{
		Creature target = null;
		try
		{
			target = (Creature) World.getInstance().findObject(objId);
		}
		catch (Exception e)
		{
			BuilderUtil.sendSysMessage(activeChar, "Target with object id " + objId + " not found.");
			return;
		}
		
		if (target != null)
		{
			final Set<AbnormalType> blockedAbnormals = target.getEffectList().getBlockedAbnormalTypes();
			final int blockedAbnormalsSize = blockedAbnormals != null ? blockedAbnormals.size() : 0;
			final StringBuilder html = new StringBuilder(500 + (blockedAbnormalsSize * 50));
			html.append("<html><table width=\"100%\"><tr><td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td width=180><center><font color=\"LEVEL\">Blocked effects of ");
			html.append(target.getName());
			html.append("</font></td><td width=45><button value=\"Back\" action=\"bypass -h admin_getbuffs" + (isPlayer(target) ? (" " + target.getName()) : "") + "\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table><br>");
			
			if ((blockedAbnormals != null) && !blockedAbnormals.isEmpty())
			{
				html.append("<br>Blocked buff slots: ");
				for (AbnormalType slot : blockedAbnormals)
				{
					html.append("<br>").append(slot.toString());
				}
			}
			
			html.append("</html>");
			
			// Send the packet
			activeChar.sendPacket(new NpcHtmlMessage(0, 1, html.toString()));
			
			if (getSettings(GeneralSettings.class).auditGM())
			{
				GMAudit.auditGMAction(activeChar.getName() + " [" + activeChar.getObjectId() + "]", "viewblockedeffects", target.getName() + " (" + Integer.toString(target.getObjectId()) + ")", "");
			}
		}
	}
}

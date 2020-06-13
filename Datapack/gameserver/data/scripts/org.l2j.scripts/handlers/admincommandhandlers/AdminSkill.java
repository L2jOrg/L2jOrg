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

import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.AcquireSkillList;
import org.l2j.gameserver.network.serverpackets.PledgeSkillList;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.StringTokenizer;

import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * This class handles following admin commands:
 * <ul>
 * <li>show_skills</li>
 * <li>remove_skills</li>
 * <li>skill_list</li>
 * <li>skill_index</li>
 * <li>add_skill</li>
 * <li>remove_skill</li>
 * <li>get_skills</li>
 * <li>reset_skills</li>
 * <li>give_all_skills</li>
 * <li>give_all_skills_fs</li>
 * <li>admin_give_all_clan_skills</li>
 * <li>remove_all_skills</li>
 * <li>add_clan_skills</li>
 * <li>admin_setskill</li>
 * </ul>
 * @version 2012/02/26 Small fixes by Zoey76 05/03/2011
 */
public class AdminSkill implements IAdminCommandHandler
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminSkill.class);
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_show_skills",
		"admin_remove_skills",
		"admin_skill_list",
		"admin_skill_index",
		"admin_add_skill",
		"admin_remove_skill",
		"admin_get_skills",
		"admin_reset_skills",
		"admin_give_all_skills",
		"admin_give_all_skills_fs",
		"admin_give_clan_skills",
		"admin_give_all_clan_skills",
		"admin_remove_all_skills",
		"admin_add_clan_skill",
		"admin_setskill",
		"admin_cast",
		"admin_castnow"
	};
	
	private static Skill[] adminSkills;
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_show_skills"))
		{
			showMainPage(activeChar);
		}
		else if (command.startsWith("admin_remove_skills"))
		{
			try
			{
				final String val = command.substring(20);
				removeSkillsPage(activeChar, Integer.parseInt(val));
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		else if (command.startsWith("admin_skill_list"))
		{
			AdminHtml.showAdminHtml(activeChar, "skills.htm");
		}
		else if (command.startsWith("admin_skill_index"))
		{
			try
			{
				final String val = command.substring(18);
				AdminHtml.showAdminHtml(activeChar, "skills/" + val + ".htm");
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		else if (command.startsWith("admin_add_skill"))
		{
			try
			{
				final String val = command.substring(15);
				adminAddSkill(activeChar, val);
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //add_skill <skill_id> <level>");
			}
		}
		else if (command.startsWith("admin_remove_skill"))
		{
			try
			{
				final String id = command.substring(19);
				final int idval = Integer.parseInt(id);
				adminRemoveSkill(activeChar, idval);
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //remove_skill <skill_id>");
			}
		}
		else if (command.equals("admin_get_skills"))
		{
			adminGetSkills(activeChar);
		}
		else if (command.equals("admin_reset_skills"))
		{
			adminResetSkills(activeChar);
		}
		else if (command.equals("admin_give_all_skills"))
		{
			adminGiveAllSkills(activeChar, false);
		}
		else if (command.equals("admin_give_all_skills_fs"))
		{
			adminGiveAllSkills(activeChar, true);
		}
		else if (command.equals("admin_give_clan_skills"))
		{
			adminGiveClanSkills(activeChar, false);
		}
		else if (command.equals("admin_give_all_clan_skills"))
		{
			adminGiveClanSkills(activeChar, true);
		}
		else if (command.equals("admin_remove_all_skills")) {
			final WorldObject target = activeChar.getTarget();
			if (!isPlayer(target)) {
				activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
				return false;
			}
			final Player player = target.getActingPlayer();
			for (Skill skill : player.getAllSkills()) {
				player.removeSkill(skill);
			}
			BuilderUtil.sendSysMessage(activeChar, "You have removed all skills from " + player.getName() + ".");
			player.sendMessage("Admin removed all skills from you.");
			player.sendSkillList();
			player.broadcastUserInfo();
			player.sendPacket(new AcquireSkillList(player));
		}
		else if (command.startsWith("admin_add_clan_skill"))
		{
			try
			{
				final String[] val = command.split(" ");
				adminAddClanSkill(activeChar, Integer.parseInt(val[1]), Integer.parseInt(val[2]));
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Usage: //add_clan_skill <skill_id> <level>");
			}
		}
		else if (command.startsWith("admin_setskill"))
		{
			final String[] split = command.split(" ");
			final int id = Integer.parseInt(split[1]);
			final int lvl = Integer.parseInt(split[2]);
			final Skill skill = SkillEngine.getInstance().getSkill(id, lvl);
			if (skill != null)
			{
				activeChar.addSkill(skill);
				activeChar.sendSkillList();
				BuilderUtil.sendSysMessage(activeChar, "You added yourself skill " + skill.getName() + "(" + id + ") level " + lvl);
				activeChar.sendPacket(new AcquireSkillList(activeChar));
			}
			else
			{
				BuilderUtil.sendSysMessage(activeChar, "No such skill found. Id: " + id + " Level: " + lvl);
			}
		}
		else if (command.startsWith("admin_cast"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			command = st.nextToken();
			if (!st.hasMoreTokens())
			{
				BuilderUtil.sendSysMessage(activeChar, "Skill Id and level are not specified.");
				BuilderUtil.sendSysMessage(activeChar, "Usage: //cast <skillId> <skillLevel>");
				return false;
			}
			
			try
			{
				final int skillId = Integer.parseInt(st.nextToken());
				final int skillLevel = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : SkillEngine.getInstance().getMaxLevel(skillId);
				final Skill skill = SkillEngine.getInstance().getSkill(skillId, skillLevel);
				if (skill == null)
				{
					BuilderUtil.sendSysMessage(activeChar, "Skill with id: " + skillId + ", lvl: " + skillLevel + " not found.");
					return false;
				}
				
				if (command.equalsIgnoreCase("admin_castnow"))
				{
					BuilderUtil.sendSysMessage(activeChar, "Admin instant casting " + skill.getName() + " (" + skillId + "," + skillLevel + ")");
					final WorldObject target = skill.getTarget(activeChar, true, false, true);
					if (target != null)
					{
						skill.forEachTargetAffected(activeChar, target, o ->
						{
							if (isCreature(o))
							{
								skill.activateSkill(activeChar, o);
							}
						});
					}
				}
				else
				{
					BuilderUtil.sendSysMessage(activeChar, "Admin casting " + skill.getName() + " (" + skillId + "," + skillLevel + ")");
					activeChar.doCast(skill);
				}
				
				return true;
			}
			catch (Exception e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Failed casting: " + e.getMessage());
				BuilderUtil.sendSysMessage(activeChar, "Usage: //cast <skillId> <skillLevel>");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This function will give all the skills that the target can learn at his/her level
	 * @param activeChar the active char
	 * @param includedByFs if {@code true} Forgotten Scroll skills will be delivered.
	 */
	private void adminGiveAllSkills(Player activeChar, boolean includedByFs)
	{
		final WorldObject target = activeChar.getTarget();
		if (!isPlayer(target))
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		final Player player = target.getActingPlayer();
		// Notify player and admin
		BuilderUtil.sendSysMessage(activeChar, "You gave " + player.giveAvailableSkills(includedByFs, true) + " skills to " + player.getName());
		player.sendSkillList();
		player.sendPacket(new AcquireSkillList(player));
	}
	
	/**
	 * This function will give all the skills that the target's clan can learn at it's level.<br>
	 * If the target is not the clan leader, a system message will be sent to the Game Master.
	 * @param activeChar the active char, probably a Game Master.
	 * @param includeSquad if Squad skills is included
	 */
	private void adminGiveClanSkills(Player activeChar, boolean includeSquad)
	{
		final WorldObject target = activeChar.getTarget();
		if ((target == null) || !isPlayer(target))
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final Player player = target.getActingPlayer();
		final Clan clan = player.getClan();
		
		if (clan == null)
		{
			activeChar.sendPacket(SystemMessageId.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
			return;
		}
		
		if (!player.isClanLeader())
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER);
			sm.addString(player.getName());
			activeChar.sendPacket(sm);
		}
		
		final Map<Integer, SkillLearn> skills = SkillTreesData.getInstance().getMaxPledgeSkills(clan, includeSquad);
		for (SkillLearn s : skills.values())
		{
			clan.addNewSkill(SkillEngine.getInstance().getSkill(s.getSkillId(), s.getSkillLevel()));
		}
		
		// Notify target and active char
		clan.broadcastToOnlineMembers(new PledgeSkillList(clan));
		for (Player member : clan.getOnlineMembers(0))
		{
			member.sendSkillList();
		}
		
		BuilderUtil.sendSysMessage(activeChar, "You gave " + skills.size() + " skills to " + player.getName() + "'s clan " + clan.getName() + ".");
		player.sendMessage("Your clan received " + skills.size() + " skills.");
	}
	
	/**
	 * TODO: Externalize HTML
	 * @param activeChar the active Game Master.
	 * @param page
	 */
	private void removeSkillsPage(Player activeChar, int page)
	{
		final WorldObject target = activeChar.getTarget();
		if ((target == null) || !isPlayer(target))
		{
			activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
			return;
		}
		
		final Player player = target.getActingPlayer();
		final Skill[] skills = player.getAllSkills().toArray(new Skill[player.getAllSkills().size()]);
		
		final int maxSkillsPerPage = 10;
		int maxPages = skills.length / maxSkillsPerPage;
		if (skills.length > (maxSkillsPerPage * maxPages))
		{
			maxPages++;
		}
		
		if (page > maxPages)
		{
			page = maxPages;
		}
		
		final int skillsStart = maxSkillsPerPage * page;
		int skillsEnd = skills.length;
		if ((skillsEnd - skillsStart) > maxSkillsPerPage)
		{
			skillsEnd = skillsStart + maxSkillsPerPage;
		}
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
		final StringBuilder replyMSG = new StringBuilder(500 + (maxPages * 50) + (((skillsEnd - skillsStart) + 1) * 50));
		replyMSG.append("<html><body><table width=260><tr><td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td width=180><center>Character Selection Menu</center></td><td width=40><button value=\"Back\" action=\"bypass -h admin_show_skills\" width=40 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table><br><br><center>Editing <font color=\"LEVEL\">" + player.getName() + "</font></center><br><table width=270><tr><td>Lv: " + player.getLevel() + " " + ClassListData.getInstance().getClass(player.getClassId()).getClientCode() + "</td></tr></table><br><table width=270><tr><td>Note: Dont forget that modifying players skills can</td></tr><tr><td>ruin the game...</td></tr></table><br><center>Click on the skill you wish to remove:</center><br><center><table width=270><tr>");
		
		for (int x = 0; x < maxPages; x++)
		{
			final int pagenr = x + 1;
			replyMSG.append("<td><a action=\"bypass -h admin_remove_skills " + x + "\">Page " + pagenr + "</a></td>");
		}
		
		replyMSG.append("</tr></table></center><br><table width=270><tr><td width=80>Name:</td><td width=60>Level:</td><td width=40>Id:</td></tr>");
		
		for (int i = skillsStart; i < skillsEnd; i++)
		{
			replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_remove_skill " + skills[i].getId() + "\">" + skills[i].getName() + "</a></td><td width=60>" + skills[i].getLevel() + "</td><td width=40>" + skills[i].getId() + "</td></tr>");
		}
		
		replyMSG.append("</table><br><center><table>Remove skill by ID :<tr><td>Id: </td><td><edit var=\"id_to_remove\" width=110></td></tr></table></center><center><button value=\"Remove skill\" action=\"bypass -h admin_remove_skill $id_to_remove\" width=110 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center><br><center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></body></html>");
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	/**
	 * @param activeChar the active Game Master.
	 */
	private void showMainPage(Player activeChar)
	{
		final WorldObject target = activeChar.getTarget();
		if ((target == null) || !isPlayer(target))
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		final Player player = target.getActingPlayer();
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
		adminReply.setFile(activeChar, "data/html/admin/charskills.htm");
		adminReply.replace("%name%", player.getName());
		adminReply.replace("%level%", String.valueOf(player.getLevel()));
		adminReply.replace("%class%", ClassListData.getInstance().getClass(player.getClassId()).getClientCode());
		activeChar.sendPacket(adminReply);
	}
	
	/**
	 * @param activeChar the active Game Master.
	 */
	private void adminGetSkills(Player activeChar)
	{
		final WorldObject target = activeChar.getTarget();
		if ((target == null) || !isPlayer(target))
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		final Player player = target.getActingPlayer();
		if (player.getName().equals(activeChar.getName()))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
		}
		else
		{
			final Skill[] skills = player.getAllSkills().toArray(new Skill[player.getAllSkills().size()]);
			adminSkills = activeChar.getAllSkills().toArray(new Skill[activeChar.getAllSkills().size()]);
			for (Skill skill : adminSkills)
			{
				activeChar.removeSkill(skill);
			}
			for (Skill skill : skills)
			{
				activeChar.addSkill(skill, true);
			}
			BuilderUtil.sendSysMessage(activeChar, "You now have all the skills of " + player.getName() + ".");
			activeChar.sendSkillList();
		}
		showMainPage(activeChar);
	}
	
	/**
	 * @param activeChar the active Game Master.
	 */
	private void adminResetSkills(Player activeChar)
	{
		final WorldObject target = activeChar.getTarget();
		if ((target == null) || !isPlayer(target))
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		final Player player = target.getActingPlayer();
		if (adminSkills == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "You must get the skills of someone in order to do this.");
		}
		else
		{
			final Skill[] skills = player.getAllSkills().toArray(new Skill[player.getAllSkills().size()]);
			for (Skill skill : skills)
			{
				player.removeSkill(skill);
			}
			for (Skill skill : activeChar.getAllSkills())
			{
				player.addSkill(skill, true);
			}
			for (Skill skill : skills)
			{
				activeChar.removeSkill(skill);
			}
			for (Skill skill : adminSkills)
			{
				activeChar.addSkill(skill, true);
			}
			player.sendMessage("[GM]" + activeChar.getName() + " updated your skills.");
			BuilderUtil.sendSysMessage(activeChar, "You now have all your skills back.");
			adminSkills = null;
			activeChar.sendSkillList();
			player.sendSkillList();
		}
		showMainPage(activeChar);
	}
	
	/**
	 * @param activeChar the active Game Master.
	 * @param val
	 */
	private void adminAddSkill(Player activeChar, String val)
	{
		final WorldObject target = activeChar.getTarget();
		if ((target == null) || !isPlayer(target))
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			showMainPage(activeChar);
			return;
		}
		final Player player = target.getActingPlayer();
		final StringTokenizer st = new StringTokenizer(val);
		if ((st.countTokens() != 1) && (st.countTokens() != 2))
		{
			showMainPage(activeChar);
		}
		else
		{
			Skill skill = null;
			try
			{
				final String id = st.nextToken();
				final String level = st.countTokens() == 1 ? st.nextToken() : null;
				final int idval = Integer.parseInt(id);
				final int levelval = level == null ? 1 : Integer.parseInt(level);
				skill = SkillEngine.getInstance().getSkill(idval, levelval);
			}
			catch (Exception e)
			{
				LOGGER.warn("", e);
			}
			if (skill != null)
			{
				final String name = skill.getName();
				// Player's info.
				player.sendMessage("Admin gave you the skill " + name + ".");
				player.addSkill(skill, true);
				player.sendSkillList();
				// Admin info.
				BuilderUtil.sendSysMessage(activeChar, "You gave the skill " + name + " to " + player.getName() + ".");
				activeChar.sendSkillList();
			}
			else
			{
				BuilderUtil.sendSysMessage(activeChar, "Error: there is no such skill.");
			}
			showMainPage(activeChar); // Back to start
		}
	}
	
	/**
	 * @param activeChar the active Game Master.
	 * @param idval
	 */
	private void adminRemoveSkill(Player activeChar, int idval)
	{
		final WorldObject target = activeChar.getTarget();
		if ((target == null) || !isPlayer(target))
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		final Player player = target.getActingPlayer();
		final Skill skill = SkillEngine.getInstance().getSkill(idval, player.getSkillLevel(idval));
		if (skill != null)
		{
			final String skillname = skill.getName();
			player.sendMessage("Admin removed the skill " + skillname + " from your skills list.");
			player.removeSkill(skill);
			// Admin information
			BuilderUtil.sendSysMessage(activeChar, "You removed the skill " + skillname + " from " + player.getName() + ".");
			activeChar.sendSkillList();
		}
		else
		{
			BuilderUtil.sendSysMessage(activeChar, "Error: there is no such skill.");
		}
		removeSkillsPage(activeChar, 0); // Back to previous page
	}
	
	/**
	 * @param activeChar the active Game Master.
	 * @param id
	 * @param level
	 */
	private void adminAddClanSkill(Player activeChar, int id, int level)
	{
		final WorldObject target = activeChar.getTarget();
		if ((target == null) || !isPlayer(target))
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			showMainPage(activeChar);
			return;
		}
		final Player player = target.getActingPlayer();
		if (!player.isClanLeader())
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER);
			sm.addString(player.getName());
			activeChar.sendPacket(sm);
			showMainPage(activeChar);
			return;
		}
		if ((id < 370) || (id > 391) || (level < 1) || (level > 3))
		{
			BuilderUtil.sendSysMessage(activeChar, "Usage: //add_clan_skill <skill_id> <level>");
			showMainPage(activeChar);
			return;
		}
		
		final Skill skill = SkillEngine.getInstance().getSkill(id, level);
		if (skill == null)
		{
			BuilderUtil.sendSysMessage(activeChar, "Error: there is no such skill.");
			return;
		}
		
		final String skillname = skill.getName();
		final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED);
		sm.addSkillName(skill);
		player.sendPacket(sm);
		final Clan clan = player.getClan();
		clan.broadcastToOnlineMembers(sm);
		clan.addNewSkill(skill);
		BuilderUtil.sendSysMessage(activeChar, "You gave the Clan Skill: " + skillname + " to the clan " + clan.getName() + ".");
		
		clan.broadcastToOnlineMembers(new PledgeSkillList(clan));
		for (Player member : clan.getOnlineMembers(0))
		{
			member.sendSkillList();
		}
		
		showMainPage(activeChar);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}

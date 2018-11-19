package l2s.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SkillCoolTimePacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.SkillEntry;
import l2s.gameserver.stats.Calculator;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.Log;

public class AdminSkill implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_show_skills,
		admin_remove_skills,
		admin_remove_all_skills,
		admin_skill_list,
		admin_skill_index,
		admin_add_skill,
		admin_remove_skill,
		admin_get_skills,
		admin_reset_skills,
		admin_give_all_skills,
		admin_show_effects,
		admin_debug_stats,
		admin_remove_cooldown,
		admin_buff,
		admin_callskill,
		admin_use_skill
	}

	private static SkillEntry[] adminSkills;

	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditChar)
			return false;

		switch(command)
		{
			case admin_show_skills:
				showSkillsPage(activeChar);
				break;
			case admin_show_effects:
				showEffects(activeChar);
				break;
			case admin_remove_skills:
				removeSkillsPage(activeChar);
				break;
			case admin_remove_all_skills:
				removeAllSkills(activeChar);
				break;
			case admin_skill_list:
				activeChar.sendPacket(new HtmlMessage(5).setFile("admin/skills.htm"));
				break;
			case admin_skill_index:
				if(wordList.length > 1)
					activeChar.sendPacket(new HtmlMessage(5).setFile("admin/skills/" + wordList[1] + ".htm"));
				break;
			case admin_add_skill:
				adminAddSkill(activeChar, wordList);
				break;
			case admin_remove_skill:
				adminRemoveSkill(activeChar, wordList);
				break;
			case admin_get_skills:
				adminGetSkills(activeChar);
				break;
			case admin_reset_skills:
				adminResetSkills(activeChar);
				break;
			case admin_give_all_skills:
				adminGiveAllSkills(activeChar);
				break;
			case admin_debug_stats:
				debug_stats(activeChar);
				break;
			case admin_remove_cooldown:
				GameObject target = activeChar.getTarget();
				Player player = null;
				if(target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
					player = (Player) target;
				else
				{
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
					return false;
				}			
				player.resetReuse();
				player.sendPacket(new SkillCoolTimePacket(activeChar));
				player.sendMessage("The reuse delay of all skills has been reseted.");

				showSkillsPage(activeChar);
				break;
			case admin_buff:
				for(int i = 7041; i <= 7064; i++)
					activeChar.addSkill(SkillHolder.getInstance().getSkillEntry(i, 1));
				activeChar.sendSkillList();
				break;
			case admin_use_skill:
			case admin_callskill:
				adminCallSkill(activeChar, wordList);
				break;
		}

		return true;
	}

	private void debug_stats(Player activeChar)
	{
		GameObject target_obj = activeChar.getTarget();
		if(!target_obj.isCreature())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		Creature target = (Creature) target_obj;

		Calculator[] calculators = target.getCalculators();

		String log_str = "--- Debug for " + target.getName() + " ---\r\n";

		for(Calculator calculator : calculators)
		{
			if(calculator == null)
				continue;
			Env env = new Env(target, activeChar, null);
			env.value = calculator.getBase();
			log_str += "Stat: " + calculator._stat.getValue() + ", prevValue: " + calculator.getLast() + "\r\n";
			Func[] funcs = calculator.getFunctions();
			for(int i = 0; i < funcs.length; i++)
			{
				String order = Integer.toHexString(funcs[i].order).toUpperCase();
				if(order.length() == 1)
					order = "0" + order;
				log_str += "\tFunc #" + i + "@ [0x" + order + "]" + funcs[i].getClass().getSimpleName() + "\t" + env.value;
				if(funcs[i].getCondition() == null || funcs[i].getCondition().test(env))
					funcs[i].calc(env);
				log_str += " -> " + env.value + (funcs[i].owner != null ? "; owner: " + funcs[i].owner.toString() : "; no owner") + "\r\n";
			}
		}

		Log.add(log_str, "debug_stats");
	}

	/**
	 * This function will give all the skills that the gm target can have at its
	 * level to the traget
	 *
	 * @param activeChar: the gm char
	 */
	private void adminGiveAllSkills(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player = null;
		if(target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (Player) target;
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}
		int skillCounter = player.rewardSkills(true, true, true, false);
		player.sendMessage("Admin gave you " + skillCounter + " skills.");
		activeChar.sendMessage("You gave " + skillCounter + " skills to " + player.getName());
	}

	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void removeSkillsPage(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player;
		if(target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (Player) target;
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		Collection<SkillEntry> skills = player.getAllSkills();

		HtmlMessage adminReply = new HtmlMessage(5);
		StringBuilder replyMSG = new StringBuilder("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_skills\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<center>Editing character: " + player.getName());
		replyMSG.append("<br>Level: " + player.getLevel() + " " + HtmlUtils.htmlClassName(player.getClassId().getId()) + "</center>");
		replyMSG.append("<br><center>Click on the skill you wish to remove:</center>");
		replyMSG.append("<br><table width=270>");
		replyMSG.append("<tr><td width=80>Name:</td><td width=60>Level:</td><td width=40>Id:</td></tr>");
		for(SkillEntry element : skills)
			replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_remove_skill " + element.getId() + "\">" + element.getName(activeChar) + "</a></td><td width=60>" + element.getLevel() + "</td><td width=40>" + element.getId() + "</td></tr>");
		replyMSG.append("</table>");
		replyMSG.append("<br><center><table>");
		replyMSG.append("Remove custom skill:");
		replyMSG.append("<tr><td>Id: </td>");
		replyMSG.append("<td><edit var=\"id_to_remove\" width=110></td></tr>");
		replyMSG.append("</table></center>");
		replyMSG.append("<center><button value=\"Remove skill\" action=\"bypass -h admin_remove_skill $id_to_remove\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");
		replyMSG.append("<br><center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void removeAllSkills(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player;
		if(target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (Player) target;
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		Collection<SkillEntry> skills = player.getAllSkills();
		for(SkillEntry skillEntry : skills)
		{
			if(skillEntry != null)
				player.removeSkill(skillEntry, true);
		}

		player.sendSkillList();

		activeChar.sendMessage("You removed all skills from target: " + player.getName() + ".");
		showSkillsPage(activeChar);
	}

	private void showSkillsPage(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player;
		if(target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (Player) target;
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		HtmlMessage adminReply = new HtmlMessage(5);

		StringBuilder replyMSG = new StringBuilder("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<center>Editing character: " + player.getName());
		replyMSG.append("<br>Level: " + player.getLevel() + " " + HtmlUtils.htmlClassName(player.getClassId().getId()) + "</center>");
		replyMSG.append("<br><center><table>");
		replyMSG.append("<tr><td><button value=\"Add skills\" action=\"bypass -h admin_skill_list\" width=100 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Get skills\" action=\"bypass -h admin_get_skills\" width=100 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"Delete skills\" action=\"bypass -h admin_remove_skills\" width=100 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Delete all skills\" action=\"bypass -h admin_remove_all_skills\" width=100 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"Reset skills\" action=\"bypass -h admin_reset_skills\" width=100 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Reset reuse\" action=\"bypass -h admin_remove_cooldown\" width=100 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"Give All Skills\" action=\"bypass -h admin_give_all_skills\" width=100 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("</table></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void showEffects(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player;
		if(target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (Player) target;
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		HtmlMessage adminReply = new HtmlMessage(5);

		StringBuilder replyMSG = new StringBuilder("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<center>Editing character: " + player.getName() + "</center>");

		replyMSG.append("<br><center><button value=\"");
		replyMSG.append(player.isLangRus() ? "Обновить" : "Refresh");
		replyMSG.append("\" action=\"bypass -h admin_show_effects\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center>");
		replyMSG.append("<br>");

		for(Abnormal e : player.getAbnormalList())
			replyMSG.append(e.getSkill().getName(activeChar)).append(" ").append(e.getSkill().getLevel()).append(" - ").append(e.getSkill().isToggle() ? "Infinity" : (e.getTimeLeft() + " seconds")).append("<br1>");
		replyMSG.append("<br></body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void adminGetSkills(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player;
		if(target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (Player) target;
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		if(player.getName().equals(activeChar.getName()))
			player.sendMessage("There is no point in doing it on your character.");
		else
		{
			Collection<SkillEntry> skills = player.getAllSkills();
			adminSkills = activeChar.getAllSkillsArray();
			for(SkillEntry element : adminSkills)
				activeChar.removeSkill(element, true);
			for(SkillEntry element : skills)
				activeChar.addSkill(element, true);
			activeChar.sendMessage("You now have all the skills of  " + player.getName() + ".");
		}

		showSkillsPage(activeChar);
	}

	private void adminResetSkills(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player = null;
		if(target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (Player) target;
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		//Skill[] skills = player.getAllSkillsArray();
		int counter = 0;
		//TODO WtF?
		/*for(L2Skill element : skills)
			if(!element.isCommon() && !SkillTreeTable.getInstance().isSkillPossible(player, element.getId(), element.getLevel()))
			{
				player.removeSkill(element, true);
				counter++;
			}*/
		player.checkSkills();
		player.sendSkillList();
		player.sendMessage("[GM]" + activeChar.getName() + " has updated your skills.");
		activeChar.sendMessage(counter + " skills removed.");

		showSkillsPage(activeChar);
	}

	private void adminAddSkill(Player activeChar, String[] wordList)
	{
		GameObject target = activeChar.getTarget();
		Player player;
		if(target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (Player) target;
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		if(wordList.length >= 2)
		{
			int id = Integer.parseInt(wordList[1]);
			int level = 1;
			if(wordList.length >= 3)
				level = Integer.parseInt(wordList[2]);
			SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(id, level);
			if(skillEntry != null)
			{
				player.addSkill(skillEntry, true);
				player.sendSkillList();
				player.getPlayer().sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skillEntry.getId(), skillEntry.getLevel()));
				activeChar.sendMessage("You gave the skill " + skillEntry.getName(activeChar) + " to " + player.getName() + ".");
			}
			else
				activeChar.sendMessage("Error: there is no such skill.");
		}

		showSkillsPage(activeChar);
	}

	private void adminRemoveSkill(Player activeChar, String[] wordList)
	{
		GameObject target = activeChar.getTarget();
		Player player = null;
		if(target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
			player = (Player) target;
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		if(wordList.length == 2)
		{
			int id = Integer.parseInt(wordList[1]);
			int level = player.getSkillLevel(id);
			SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(id, level);
			if(skillEntry != null)
			{
				player.sendMessage("Admin removed the skill " + skillEntry.getName(player) + ".");
				player.removeSkill(skillEntry, true);
				player.sendSkillList();
				activeChar.sendMessage("You removed the skill " + skillEntry.getName(activeChar) + " from " + player.getName() + ".");
			}
			else
				activeChar.sendMessage("Error: there is no such skill.");
		}

		removeSkillsPage(activeChar);
	}

	private void adminCallSkill(Player player, String[] wordList)
	{
		GameObject target = player.getTarget();
		if(target == null)
			target = player;

		if(!target.isPlayer())
			return;

		Skill skill = SkillHolder.getInstance().getSkill(Integer.parseInt(wordList[1]), Integer.parseInt(wordList[2]));
		if(skill == null)
			return;

		List<Creature> list = new ArrayList<Creature>();
		list.add(target.getPlayer());
		player.callSkill(skill, list, false, false);
	}
}
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

import org.l2j.gameserver.data.database.dao.QuestDAO;
import org.l2j.gameserver.data.database.data.QuestData;
import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExShowQuestMark;
import org.l2j.gameserver.network.serverpackets.QuestList;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.world.World;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.SPACE;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * TODO: Rework and cleanup. extract to htm file
 * @author Korvin, Zoey76
 * @author JoeAlisson
 */
public class AdminShowQuests implements IAdminCommandHandler {
	
	private static final String[] ADMIN_COMMANDS = {
		"admin_charquestmenu",
		"admin_setcharquest"
	};
	
	private static final String[] _states = {
		"CREATED",
		"STARTED",
		"COMPLETED"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player player) {
		final String[] cmdParams = command.split(" ");

		Player target = null;
		WorldObject targetObject;
		final String[] val = new String[4];
		
		if (cmdParams.length > 2) {
			var playerName = cmdParams[1];

			target = World.getInstance().findPlayer(playerName);
			val[0] = "var";

			switch (cmdParams[2]) {
				case "0" -> val[1] = "Start";
				case "1" -> val[1] = "Started";
				case "2" -> val[1] = "Completed";
				case "3" -> val[0] = "full";
				default -> {
					if(cmdParams[2].contains("_")) {
						val[0] = "name";
						val[1] = cmdParams[2];
					}  else if (cmdParams.length > 3 && cmdParams[3].equals("custom")) {
						val[0] = "custom";
						val[1] = cmdParams[2];
					}
				}
			}
		} else {
			targetObject = player.getTarget();
			
			if (isPlayer(targetObject)) {
				target = targetObject.getActingPlayer();
			}
		}
		
		if (isNull(target)) {
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		
		if (command.startsWith("admin_charquestmenu")) {
			if (nonNull(val[0])) {
				showQuestMenu(target, player, val);
			} else {
				showFirstQuestMenu(target, player);
			}
		}
		else if (command.startsWith("admin_setcharquest")) {
			if (cmdParams.length >= 5) {
				val[0] = cmdParams[2];
				val[1] = cmdParams[3];
				val[2] = cmdParams[4];
				if (cmdParams.length == 6) {
					val[3] = cmdParams[5];
				}
				setQuestVar(target, player, val);
			} else {
				return false;
			}
		}
		return true;
	}
	
	private static void showFirstQuestMenu(Player target, Player actor) {
		final StringBuilder replyMSG = new StringBuilder("<html><body><table width=270><tr><td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td width=180><center>Player: " + target.getName() + "</center></td><td width=45><button value=\"Back\" action=\"bypass -h admin_admin6\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table>");
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
		final int ID = target.getObjectId();
		
		replyMSG.append("Quest Menu for <font color=\"LEVEL\">").append(target.getName()).append("</font> (ID:").append(ID).append(")<br><center>");
		replyMSG.append("<table width=250><tr><td><button value=\"CREATED\" action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(" 0\" width=85 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"STARTED\" action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(" 1\" width=85 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"COMPLETED\" action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(" 2\" width=85 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><br><button value=\"All\" action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(" 3\" width=85 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("<tr><td><br><br>Manual Edit by Quest number:<br></td></tr>");
		replyMSG.append("<tr><td><edit var=\"qn\" width=50 height=15><br><button value=\"Edit\" action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(" $qn custom\" width=50 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("</table></center></body></html>");
		adminReply.setHtml(replyMSG.toString());
		actor.sendPacket(adminReply);
	}
	
	private void showQuestMenu(Player target, Player actor, String[] val) {
		final StringBuilder replyMSG = new StringBuilder("<html><body>");
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);

		switch (val[0]) {
			case "full" -> fullQuestList(target, replyMSG);
			case "name" -> questListByName(target, val[1], replyMSG);
			case "var" -> questByStateValue(target, val[1], replyMSG);
			case "custom" -> questCustomList(target, val[1], replyMSG);
		}
		adminReply.setHtml(replyMSG.toString());
		actor.sendPacket(adminReply);
	}

	private void questCustomList(Player target, String questId, StringBuilder replyMSG) {
		boolean questFound = true;
		boolean questStatFound = true;
		final int qnumber = Integer.parseInt(questId);
		String state;
		String qname = null;
		QuestState qs = null;

		final Quest quest = QuestManager.getInstance().getQuest(qnumber);
		if (nonNull(quest)) {
			qname = quest.getName();
			qs = target.getQuestState(qname);
		} else {
			questFound = false;
		}

		if (nonNull(qs)) {
			state = _states[qs.getState()];
		} else {
			questStatFound = false;
			state = "N/A";
		}

		if (questFound) {
			replyMSG.append("Character: <font color=\"LEVEL\">").append(target.getName()).append("</font><br>Quest: <font color=\"LEVEL\">").append(qname).append("</font><br>State: <font color=\"LEVEL\">").append(state).append("</font><br><br>");
			if (questStatFound) {
				replyMSG.append("<center><table width=250><tr><td>Var</td><td>Value</td><td>New Value</td><td>&nbsp;</td></tr>");

				var id = target.getObjectId();
				for (QuestData questData : getDAO(QuestDAO.class).findByPlayerAndNameExcludeState(id, qname)) {
					questDataToEditLine(target, qname, replyMSG, questData);
				}

				replyMSG.append("</table><br><br><table width=250><tr><td>Repeatable quest:</td><td>Unrepeatable quest:</td></tr>");
				replyMSG.append("<tr><td><button value=\"Quest Complete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(SPACE).append(qname).append(" state COMPLETED 1\" width=100 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
				replyMSG.append("<td><button value=\"Quest Complete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(SPACE).append(qname).append(" state COMPLETED 0\" width=100 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
				replyMSG.append("</table><br><br><font color=\"ff0000\">Delete Quest from DB:</font><br><button value=\"Quest Delete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(SPACE).append(qname).append(" state DELETE\" width=100 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			} else {
				replyMSG.append("<center>Start this Quest for player:<br>");
				replyMSG.append("<button value=\"Create Quest\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(SPACE).append(qnumber).append(" state CREATE\" width=100 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br><br>");
				replyMSG.append("<font color=\"ee0000\">Only for Unrepeateble quests:</font><br>");
				replyMSG.append("<button value=\"Create & Complete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(SPACE).append(qnumber).append(" state CC\" width=130 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br><br>");
			}
			replyMSG.append("</center></body></html>");
		} else {
			replyMSG.append("<center><font color=\"ee0000\">Quest with number </font><font color=\"LEVEL\">").append(qnumber).append("</font><font color=\"ee0000\"> doesn't exist!</font></center></body></html>");
		}
	}

	private void questByStateValue(Player target, String value, StringBuilder replyMSG) {
		replyMSG.append("Character: <font color=\"LEVEL\">").append(target.getName()).append("</font><br>Quests with state: <font color=\"LEVEL\">").append(value).append("</font><br>");
		replyMSG.append("<table width=250>");

		getDAO(QuestDAO.class).findQuestNameByPlayerAndStateValue(target.getObjectId(), value).forEach(questName ->
				replyMSG.append("<tr><td><a action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(SPACE).append(questName).append("\">").append(questName).append("</a></td></tr>"));

		replyMSG.append("</table></body></html>");
	}

	private void fullQuestList(Player target, StringBuilder replyMSG) {
		var id = target.getObjectId();
		replyMSG.append("<table width=250><tr><td>Full Quest List for <font color=\"LEVEL\">").append(target.getName()).append("</font> (ID:").append(id).append(")</td></tr>");
		getDAO(QuestDAO.class).findQuestNameByPlayerAndState(id).forEach(questName ->
				replyMSG.append("<tr><td><a action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(SPACE).append(questName).append("\">").append(questName).append("</a></td></tr>"));
		replyMSG.append("</table></body></html>");
	}

	private void questListByName(Player target, String name, StringBuilder replyMSG) {
		final QuestState qs = target.getQuestState(name);
		final String state = nonNull(qs) ? _states[qs.getState()] : "CREATED";
		var id = target.getObjectId();

		replyMSG.append("Player: <font color=\"LEVEL\">").append(target.getName()).append("</font><br>Quest: <font color=\"LEVEL\">").append(name).append("</font><br>State: <font color=\"LEVEL\">").append(state).append("</font><br><br>");
		replyMSG.append("<center><table width=250><tr><td>Var</td><td>Value</td><td>New Value</td><td>&nbsp;</td></tr>");

		getDAO(QuestDAO.class).findByPlayerAndNameExcludeState(id, name).forEach(questData -> questDataToEditLine(target, name, replyMSG, questData));

		replyMSG.append("</table><br><br><table width=250><tr><td>Repeatable quest:</td><td>Unrepeatable quest:</td></tr>");
		replyMSG.append("<tr><td><button value=\"Quest Complete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(SPACE).append(name).append(" state COMPLETED 1\" width=120 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td><button value=\"Quest Complete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(SPACE).append(name).append(" state COMPLETED 0\" width=120 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		replyMSG.append("</table><br><br><font color=\"ff0000\">Delete Quest from DB:</font><br><button value=\"Quest Delete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(SPACE).append(name).append(" state DELETE\" width=120 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		replyMSG.append("</center></body></html>");
	}

	private void questDataToEditLine(Player target, String name, StringBuilder replyMSG, QuestData questData) {
		replyMSG.append("<tr><td>").append(questData.getVar()).append("</td><td>")
				.append(questData.getValue()).append("</td><td><edit var=\"var").append(questData.getVar())
				.append("\" width=80 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(SPACE).append(name).append(SPACE).append(questData.getVar()).append(" $var").append(questData.getVar())
				.append("\" width=30 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"Del\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(SPACE).append(name).append(SPACE).append(questData.getVar()).append(" delete\" width=30 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
	}

	private void setQuestVar(Player target, Player actor, String[] val) {
		QuestState qs = target.getQuestState(val[0]);
		final String[] outval = new String[3];
		qs.setSimulated(false);
		
		if (val[1].equals("state")) {
			switch (val[2]) {
				case "COMPLETED" -> qs.exitQuest((val[3].equals("1")) ? QuestType.REPEATABLE : QuestType.ONE_TIME);
				case "DELETE" -> {
					Quest.deleteQuestInDb(qs, true);
					qs.exitQuest(QuestType.REPEATABLE);
					target.sendPacket(new QuestList(target));
					target.sendPacket(new ExShowQuestMark(qs.getQuest().getId(), qs.getCond()));
				}
				case "CREATE" -> {
					qs = QuestManager.getInstance().getQuest(Integer.parseInt(val[0])).newQuestState(target);
					qs.setState(State.STARTED);
					qs.set("cond", "1");
					target.sendPacket(new QuestList(target));
					target.sendPacket(new ExShowQuestMark(qs.getQuest().getId(), qs.getCond()));
					val[0] = qs.getQuest().getName();
				}
				case "CC" -> {
					qs = QuestManager.getInstance().getQuest(Integer.parseInt(val[0])).newQuestState(target);
					qs.exitQuest(QuestType.ONE_TIME);
					target.sendPacket(new QuestList(target));
					target.sendPacket(new ExShowQuestMark(qs.getQuest().getId(), qs.getCond()));
					val[0] = qs.getQuest().getName();
				}
			}
		} else {
			if (val[2].equals("delete")) {
				qs.unset(val[1]);
			}
			else {
				qs.set(val[1], val[2]);
			}
			target.sendPacket(new QuestList(target));
			target.sendPacket(new ExShowQuestMark(qs.getQuest().getId(), qs.getCond()));
		}
		actor.sendMessage("");
		outval[0] = "name";
		outval[1] = val[0];
		showQuestMenu(target, actor, outval);
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}

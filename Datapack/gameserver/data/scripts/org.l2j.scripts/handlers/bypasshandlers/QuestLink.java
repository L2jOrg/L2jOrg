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
package handlers.bypasshandlers;

import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.STRING_EMPTY;
import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * @author JoeAlisson
 */
public class QuestLink implements IBypassHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(QuestLink.class);

	private static final String[] COMMANDS = {
		"Quest"
	};
	
	@Override
	public boolean useBypass(String command, Player player, Creature target) {
		String quest = "";
		try {
			quest = command.substring(5).trim();
		} catch (IndexOutOfBoundsException e) {
			LOGGER.warn(e.getMessage(), e);
		}

		if (isNullOrEmpty(quest)) {
			showQuestsWindow(player, (Npc) target);
		}
		else {
			final int questNameEnd = quest.indexOf(" ");
			if (questNameEnd == -1) {
				showQuestWindow(player, (Npc) target, quest);
			} else {
				player.processQuestEvent(quest.substring(0, questNameEnd), quest.substring(questNameEnd).trim());
			}
		}
		return true;
	}

	/**
	 * Collect awaiting quests/start points and display a QuestChooseWindow (if several available) or QuestWindow.
	 * @param player the Player that talk with the {@code npc}.
	 * @param npc the Folk that chats with the {@code player}.
	 */
	private void showQuestsWindow(Player player, Npc npc) {
		//@formatter:off
		final Set<Quest> quests = npc.getListeners(EventType.ON_NPC_TALK).stream()
				.map(AbstractEventListener::getOwner)
				.filter(Quest.class::isInstance)
				.map(Quest.class::cast)
				.filter(quest -> quest.getId() > 0)
				.collect(Collectors.toSet());
		//@formatter:on

		if (quests.size() > 1) {
			showQuestChooseWindow(player, npc, quests);
		} else if (quests.size() == 1) {
			showQuestWindow(player, npc, quests.iterator().next().getName());
		} else {
			showQuestWindow(player, npc, "");
		}
	}
	
	/**
	 * Open a choose quest window on client with all quests available of the Folk.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <li>Send a Server->Client NpcHtmlMessage containing the text of the Folk to the Player</li>
	 * @param player The Player that talk with the Folk
	 * @param npc The table containing quests of the Folk
	 */
	private void showQuestChooseWindow(Player player, Npc npc, Collection<Quest> quests) {
		final var sbStarted = new StringBuilder(128);
		final var sbCanStart = new StringBuilder(128);
		final var sbCompleted = new StringBuilder(128);

		for (var quest : quests) {
			final var questState =  quest.getQuestState(player, false);

			if (isNull(questState) || questState.isCreated() || (questState.isCompleted() && questState.isNowAvailable())) {
				if(quest.canStartQuest(player)) {
					sbCanStart.append("<font color=\"bbaa88\">");
					createQuestButton(npc, quest, sbCanStart, STRING_EMPTY, "01</fstring>");
				}
			} else if (questState.isStarted()) {
				sbStarted.append("<font color=\"ffdd66\">");
				createQuestButton(npc, quest, sbStarted, " (In Progress)", "02</fstring>");
			}
			else if (questState.isCompleted()) {
				sbCompleted.append("<font color=\"787878\">");
				createQuestButton(npc, quest, sbCompleted," (Complete) ", "03</fstring>");
			}
		}
		
		String content;
		if ((sbStarted.length() > 0) || (sbCanStart.length() > 0) || (sbCompleted.length() > 0)) {
			content = "<html><body>" +
					sbStarted.toString() +
					sbCanStart.toString() +
					sbCompleted.toString() +
					"</body></html>";
		}
		else {
			content = Quest.getNoQuestMsg(player);
		}

		content = content.replaceAll("%objectId%", String.valueOf(npc.getObjectId()));
		player.sendPacket(new NpcHtmlMessage(npc.getObjectId(), content));
	}

	private void createQuestButton(Npc npc, Quest quest, StringBuilder stringBuilder, String customSuffix, String npcStringIdSuffix) {
		stringBuilder.append("<button icon=\"quest\" align=\"left\" action=\"bypass -h npc_").append(npc.getObjectId()).append("_Quest ").append(quest.getName()).append("\">").
				append(quest.isCustomQuest() ? quest.getPath() + customSuffix : "<fstring>" + quest.getNpcStringId() + npcStringIdSuffix).
				append("</button></font>");
	}

	/**
	 * Open a quest window on client with the text of the Folk.<br>
	 * <b><u>Actions</u>:</b><br>
	 * <ul>
	 * <li>Get the text of the quest state in the folder data/scripts/quests/questId/stateId.htm</li>
	 * <li>Send a Server->Client NpcHtmlMessage containing the text of the Folk to the Player</li>
	 * <li>Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet</li>
	 * </ul>
	 * @param player the Player that talk with the {@code npc}
	 * @param npc the Folk that chats with the {@code player}
	 * @param questId the Id of the quest to display the message
	 */
	private static void showQuestWindow(Player player, Npc npc, String questId) {
		String content = null;
		
		final var quest = QuestManager.getInstance().getQuest(questId);

		if (nonNull(quest)) {

			if (player.getWeightPenalty() >= 3 || !player.isInventoryUnder80(true)) {
				player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
				return;
			}

			final QuestState qs =  player.getQuestState(questId);
			
			if (isNull(qs)) {
				if (quest.getId() >= 1) {
					if (player.getAllActiveQuests().size() > 40) {
						final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
						html.setFile(player, "data/html/fullquest.html");
						player.sendPacket(html);
						return;
					}
				}
			}
			quest.notifyTalk(npc, player);
		} else {
			content = Quest.getNoQuestMsg(player); // no quests found
		}

		if (nonNull(content)) {
			content = content.replaceAll("%objectId%", String.valueOf(npc.getObjectId()));
			player.sendPacket(new NpcHtmlMessage(npc.getObjectId(), content));
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}

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
package org.l2j.scripts.quests.hunting.q10967;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.scripts.quests.hunting.MonsterHunting;

import static java.util.Objects.isNull;

/**
 * @author RobikBobik
 * @author JoeAlisson
 * Notee: Fixes and Debugging by Bru7aLMike.
 */
public class CulturedAdventurer extends MonsterHunting {

	private static final int CAPTAIN_BATHIS = 30332;

	private static final int OL_MAHUM_SHOOTER = 20063;
	private static final int OL_MAHUM_SERGEANT = 20439;
	private static final int OL_MAHUM_OFFICER = 20066;
	private static final int OL_MAHUM_GENERAL = 20438;
	private static final int OL_MAHUM_COMMANDER = 20076;

	private static final ItemHolder ADVENTURERS_BROOCH = new ItemHolder(91932, 1);
	private static final ItemHolder ADVENTURERS_BROOCH_GEMS = new ItemHolder(91936, 1);

	private static final int MAX_LEVEL = 30;
	private static final int MIN_LEVEL = 25;

	public CulturedAdventurer() {
		super(10967, OL_MAHUM_SHOOTER, OL_MAHUM_SERGEANT, OL_MAHUM_OFFICER, OL_MAHUM_GENERAL, OL_MAHUM_COMMANDER);
		addStartNpc(CAPTAIN_BATHIS);
		addTalkId(CAPTAIN_BATHIS);
		setQuestNameNpcStringId(NpcStringId.LV_25_30_MORE_EXPERIENCE);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "no_lvl.html");
	}

	@Override
	public String onTalk(Npc npc, Player player) {
		var qs = getQuestState(player, true);
		var htmlText = getNoQuestMsg(player);

		if(isNull(qs)) {
			return htmlText;
		}
		if (qs.isCreated()) {
			htmlText = "30332.htm";
		} else if (qs.isStarted()) {
			if (npc.getId() == CAPTAIN_BATHIS) {
				if (qs.isCond(2)) {
					htmlText = "30332-04.html";
				}
			}
		} else if (qs.isCompleted()) {
			if (npc.getId() == CAPTAIN_BATHIS) {
				htmlText = getAlreadyCompletedMsg(player);
			}
		}
		return htmlText;
	}


	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		var qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}

		String htmlText = null;
		switch (event) {
			case "30332-01.htm" -> htmlText = event;
			case "30332-02.htm" -> htmlText = event;
			case "30332-03.htm" -> {
				qs.startQuest();
				htmlText = event;
			}
			case "30332-05.html" -> {
				if (qs.isStarted()) {
					player.sendPacket(new ExShowScreenMessage("You've obtained Adventurer's Brooch and Adventurer's Gem Fragment.#Check the tutorial to equip the gems.", 5000));
					addExpAndSp(player, 2000000, 50000);
					giveItems(player, ADVENTURERS_BROOCH);
					giveItems(player, ADVENTURERS_BROOCH_GEMS);
					qs.exitQuest(false, true);
					htmlText = event;
				}
			}
		}
		return htmlText;
	}

	@Override
	protected boolean hasHuntCondition(Player killer, Npc npc, QuestState qs) {
		return qs.isCond(1);
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_ABANDONED_CAMP;
	}

	@Override
	protected int huntingAmount(Player killer, QuestState qs) {
		return 300;
	}

	@Override
	protected void onCompleteHunting(Player killer, QuestState qs) {
		qs.setCond(2, true);
		showOnScreenMsg(killer, NpcStringId.MONSTERS_OF_THE_ABANDONED_CAMP_ARE_KILLED_NUSE_THE_TELEPORT_TO_GET_TO_BATHIS_IN_GLUDIO, ExShowScreenMessage.TOP_CENTER, 5000);
	}
}

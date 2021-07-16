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
package org.l2j.scripts.quests.hunting.q10966;

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.scripts.quests.hunting.MonsterHunting;

import static java.util.Objects.isNull;

/**
 * @author RobikBobik
 * @author JoeAlisson
 * Notee: Debugging by Bru7aLMike.
 */
public class ATripBegins extends MonsterHunting {

	private static final int CAPTAIN_BATHIS = 30332;
	private static final int BELLA = 30256;

	private static final ItemHolder SOE_TO_CAPTAIN_BATHIS = new ItemHolder(91651, 1);
	private static final ItemHolder SOE_NOVICE = new ItemHolder(10650, 10);
	private static final ItemHolder TALISMAN_OF_ADEN = new ItemHolder(91745, 1);
	private static final ItemHolder SCROLL_OF_ENCHANT_TALISMAN_OF_ADEN = new ItemHolder(91756, 1);
	private static final ItemHolder ADVENTURERS_BRACELET = new ItemHolder(91934, 1);

	private static final int ARACHNID_PREDATOR = 20926;
	private static final int SKELETON_BOWMAN = 20051;
	private static final int RUIN_SPARTOI = 20054;
	private static final int RAGING_SPARTOI = 20060;
	private static final int TUMRAN_BUGBEAR = 20062;
	private static final int TUMRAN_BUGBEAR_WARRIOR = 20064;

	private static final int MIN_LEVEL = 20;
	private static final int MAX_LEVEL = 25;

	public ATripBegins() {
		super(10966, ARACHNID_PREDATOR, SKELETON_BOWMAN, RUIN_SPARTOI, RAGING_SPARTOI, RAGING_SPARTOI, TUMRAN_BUGBEAR, TUMRAN_BUGBEAR_WARRIOR);
		addStartNpc(CAPTAIN_BATHIS);
		addTalkId(CAPTAIN_BATHIS, BELLA);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "no_lvl.html");
		setQuestNameNpcStringId(NpcStringId.LV_20_25_A_TRIP_BEGINS);
	}

	@Override
	public String onTalk(Npc npc, Player player) {
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);

		if(isNull(qs)) {
			return htmltext;
		}

		if (qs.isCreated()) {
			htmltext = "30332.htm";
		} else if (qs.isStarted()) {
			int id = npc.getId();
			if (id == CAPTAIN_BATHIS) {
				if (qs.isCond(1)) {
					htmltext = "30332-03.htm";
				} else if (qs.isCond(3)) {
					htmltext = "30332-04.html";
				}
			} else if (id == BELLA) {
				if (qs.isCond(1)) {
					htmltext = "30256.html";
				}
			}
		} else if (qs.isCompleted()) {
			if (npc.getId() == CAPTAIN_BATHIS) {
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}

	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		var qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}

		String htmlText = null;
		switch (event) {
			case "30332-01.htm", "30332-02.htm" -> htmlText = event;
			case "30332-03.htm" -> {
				qs.startQuest();
				npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.TALK_TO_BELLA));
				htmlText = event;
			}
			case "30256-01.html" -> {
				qs.setCond(2, true);
				htmlText = event;
			}
			case "30332-05.html" -> {
				if (qs.isStarted()) {
					addExpAndSp(player, 500000, 12500);
					giveItems(player, SOE_NOVICE);
					giveItems(player, TALISMAN_OF_ADEN);
					giveItems(player, SCROLL_OF_ENCHANT_TALISMAN_OF_ADEN);
					giveItems(player, ADVENTURERS_BRACELET);
					qs.exitQuest(false, true);
					htmlText = event;
				}
			}
		}
		return htmlText;
	}

	@Override
	protected boolean hasHuntCondition(Player killer, Npc npc, QuestState qs) {
		return qs.isCond(2);
	}

	@Override
	protected void onCompleteHunting(Player killer, QuestState qs) {
		qs.setCond(3, true);
		showOnScreenMsg(killer, NpcStringId.YOU_VE_MADE_THE_FIRST_STEPS_ON_THE_ADVENTURER_S_PATH_RETURN_TO_BATHIS_TO_GET_YOUR_REWARD, ExShowScreenMessage.TOP_CENTER, 5000);
		giveItems(killer, SOE_TO_CAPTAIN_BATHIS);
	}

	@Override
	protected int huntingAmount(Player killer, QuestState qs) {
		return 15;
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_RUINS_OF_AGONY;
	}
}
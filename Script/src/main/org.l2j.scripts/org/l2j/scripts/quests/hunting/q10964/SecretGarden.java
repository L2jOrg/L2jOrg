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
package org.l2j.scripts.quests.hunting.q10964;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.scripts.quests.hunting.MonsterHunting;

import static java.util.Objects.isNull;

/**
 * @author RobikBobik
 * @author JoeAlisson
 */
public class SecretGarden extends MonsterHunting {

	private static final int CAPTAIN_BATHIS = 30332;
	private static final int RAYMOND = 30289;

	private static final int HARPY = 20145;
	private static final int MEDUSA = 20158;
	private static final int WYRM = 20176;
	private static final int TURAK_BUGBEAR = 20248;
	private static final int TURAK_BUGBEAR_WARRIOR = 20249;

	private static final int MAX_LEVEL = 34;
	private static final int MIN_LEVEL = 30;
	
	public SecretGarden() {
		super(10964, HARPY, MEDUSA, WYRM, TURAK_BUGBEAR, TURAK_BUGBEAR_WARRIOR);
		addStartNpc(CAPTAIN_BATHIS);
		addTalkId(CAPTAIN_BATHIS, RAYMOND);
		setQuestNameNpcStringId(NpcStringId.LV_30_34_SECRET_GARDEN);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "no_lvl.html");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		var qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}
		
		String htmlText = null;
		switch (event) {
			case "30332-01.htm" -> {
				qs.startQuest();
				htmlText = event;
			}
			case "30289-01.html" -> {
				qs.setCond(2, true);
				htmlText = event;
			}
			case "30289-02.html" -> htmlText = event;
			case "30289-03.html" -> {
				if (qs.isStarted()) {
					player.sendPacket(new ExShowScreenMessage(NpcStringId.THE_MISSION_ADVENTURER_S_JOURNEY_II_IS_NOW_AVAILABLE_NCLICK_THE_YELLOW_QUESTION_MARK_IN_THE_RIGHT_BOTTOM_CORNER_OF_YOUR_SCREEN_TO_SEE_THE_QUEST_S_INFO, 2, 5000));
					addExpAndSp(player, 2500000, 75000);
					qs.exitQuest(false, true);
					htmlText = event;
				}
			}
		}
		return htmlText;
	}

	@Override
	public String onTalk(Npc npc, Player player) {
		var qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);

		if(isNull(qs)) {
			return htmltext;
		}

		if (qs.isCreated()) {
			htmltext = "30332.htm";
		}
		else if (qs.isStarted()) {
			int id = npc.getId();
			if (id == CAPTAIN_BATHIS) {
				if (qs.isCond(1)) {
					htmltext = "30332-01.htm";
				}
			} else if (id == RAYMOND) {
				if (qs.isCond(1)) {
					htmltext = "30289.html";
				} else if (qs.isCond(3)) {
					htmltext = "30289-02.html";
				}
			}
		}
		else if (qs.isCompleted()) {
			if (npc.getId() == CAPTAIN_BATHIS) {
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}

	@Override
	protected boolean hasHuntCondition(Player killer, Npc npc, QuestState qs) {
		return qs.isCond(2);
	}

	@Override
	protected int huntingAmount(Player killer, QuestState qs) {
		return 500;
	}

	@Override
	protected void onCompleteHunting(Player killer, QuestState qs) {
		qs.setCond(3, true);
		killer.sendPacket(new ExShowScreenMessage(NpcStringId.MONSTERS_OF_THE_GORGON_FLOWER_GARDEN_ARE_KILLED_NUSE_THE_TELEPORT_TO_GET_TO_HIGH_PRIEST_RAYMOND_IN_GLUDIO, 2, 5000));
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_GORGON_FLOWER_GARDEN;
	}

}
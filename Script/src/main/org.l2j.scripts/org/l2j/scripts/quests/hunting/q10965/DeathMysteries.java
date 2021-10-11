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
package org.l2j.scripts.quests.hunting.q10965;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.scripts.quests.hunting.MonsterHunting;

import static java.util.Objects.isNull;

/**
 * Death Mysteries (10965)
 * @author RobikBobik
 * @author JoeAlisson
 * Notee: Debugging by Bru7aLMike.
 */
public class DeathMysteries extends MonsterHunting {

	private static final int RAYMOND = 30289;
	private static final int MAXIMILLIAN = 30120;

	private static final int WYRM = 20176;
	private static final int GUARDIAN_BASILISK = 20550;
	private static final int ROAD_SCAVENGER = 20551;
	private static final int FETTERED_SOUL = 20552;
	private static final int WINDUS = 20553;
	private static final int GRANDIS = 20554;

	private static final ItemHolder ADVENTURERS_AGATHION_BRACELET = new ItemHolder(91933, 1);
	private static final ItemHolder ADVENTURERS_AGATHION_GRIFIN = new ItemHolder(91935, 1);

	private static final int MAX_LEVEL = 40;
	private static final int MIN_LEVEL = 37;

	public DeathMysteries() {
		super(10965, WYRM, GUARDIAN_BASILISK, ROAD_SCAVENGER, FETTERED_SOUL, WINDUS, GRANDIS);
		addStartNpc(RAYMOND);
		addTalkId(RAYMOND, MAXIMILLIAN);
		setQuestNameNpcStringId(NpcStringId.LV_37_40_DEATH_MYSTERIES);
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
			htmlText = "30289.htm";
		}
		else if (qs.isStarted()) {
			int id = npc.getId();
			if (id == RAYMOND) {
				if (qs.isCond(1)) {
					htmlText = "30289-01.htm";
				}
			} else if (id == MAXIMILLIAN) {
				if (qs.isCond(1)) {
					htmlText = "30120.html";
				} else if (qs.isCond(3)) {
					htmlText = "30120-04.html";
				}
			}
		} else if (qs.isCompleted()) {
			if (npc.getId() == RAYMOND) {
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
			case "TELEPORT_TO_MAXIMILIAN" -> player.teleToLocation(86845, 148626, -3402);
			case "30289-01.htm", "30289-02.htm", "30120-01.html", "30120-02.html" -> htmlText = event;
			case "30289-03.htm" -> {
				qs.startQuest();
				htmlText = event;
			}
			case "30120-03.html" -> {
				qs.setCond(2, true);
				htmlText = event;
			}
			case "30120-05.html" -> {
				if (qs.isStarted()) {
					player.sendPacket(new ExShowScreenMessage(NpcStringId.YOU_VE_GOT_ADVENTURER_S_AGATHION_BRACELET_AND_ADVENTURER_S_AGATHION_GRIFFIN_NCOMPLETE_THE_TUTORIAL_AND_TRY_TO_USE_THE_AGATHION, 2, 5000));
					addExpAndSp(player, 3000000, 75000);
					giveItems(player, ADVENTURERS_AGATHION_BRACELET);
					giveItems(player, ADVENTURERS_AGATHION_GRIFIN);
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
	protected int huntingAmount(Player killer, QuestState qs) {
		return 500;
	}

	@Override
	protected void onCompleteHunting(Player killer, QuestState qs) {
		qs.setCond(3, true);
		killer.sendPacket(new ExShowScreenMessage(NpcStringId.MONSTERS_OF_THE_DEATH_PASS_ARE_KILLED_NUSE_THE_TELEPORT_OR_THE_SCROLL_OF_ESCAPE_TO_GET_TO_HIGH_PRIEST_MAXIMILIAN_IN_GIRAN, 2, 5000));
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_DEATH_PASS;
	}
}
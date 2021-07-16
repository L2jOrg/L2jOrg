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
package org.l2j.scripts.quests.hunting.q10963;

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
public class ExploringTheAntNest extends MonsterHunting {

	private static final int RAYMOND = 30289;

	private static final int ANT_LARVA = 20075;
	private static final int ANT = 20079;
	private static final int ANT_CAPTAIN = 20080;
	private static final int ANT_OVERSEER = 20081;
	private static final int ANT_RECRUIT = 20082;
	private static final int ANT_PATROL = 20084;
	private static final int ANT_GUARD = 20086;
	private static final int ANT_SOLDIER = 20087;
	private static final int ANT_WARRIOR_CAPTAIN = 20088;
	private static final int ANT_NOBLE = 20089;
	private static final int ANT_NOBLE_CAPTAIN = 20090;

	private static final int MIN_LEVEL = 34;
	private static final int MAX_LEVEL = 37;
	
	public ExploringTheAntNest() {
		super(10963, ANT_LARVA, ANT, ANT_CAPTAIN, ANT_OVERSEER, ANT_RECRUIT, ANT_PATROL, ANT_GUARD, ANT_SOLDIER, ANT_WARRIOR_CAPTAIN, ANT_NOBLE, ANT_NOBLE_CAPTAIN);
		addStartNpc(RAYMOND);
		addTalkId(RAYMOND);
		setQuestNameNpcStringId(NpcStringId.LV_34_37_EXPLORING_THE_ANT_NEST);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "no_lvl.html");
	}

	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		var qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "30289-01.htm", "30289-02.htm" -> htmltext = event;
			case "30289-03.htm" -> {
				qs.startQuest();
				htmltext = event;
			}
			case "30289-05.html" -> {
				if (qs.isStarted()) {
					addExpAndSp(player, 3000000, 75000);
					qs.exitQuest(false, true);
					htmltext = event;
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(Npc npc, Player player) {
		var qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);

		if(isNull(qs)) {
			return htmltext;
		}

		if (qs.isCreated()) {
			htmltext = "30289.htm";
		}
		else if (qs.isStarted()) {
			if (npc.getId() == RAYMOND) {
				if (qs.isCond(2)) {
					htmltext = "30289-04.html";
				}
			}
		} else if (qs.isCompleted()) {
			if (npc.getId() == RAYMOND) {
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}

	@Override
	protected NpcStringId questHuntingProgressName() {
		return NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_ANT_NEST;
	}

	@Override
	protected void onCompleteHunting(Player killer, QuestState qs) {
		qs.setCond(2, true);
		killer.sendPacket(new ExShowScreenMessage(NpcStringId.MONSTERS_OF_THE_ANT_NEST_ARE_KILLED_NUSE_THE_TELEPORT_TO_GET_TO_HIGH_PRIEST_RAYMOND_IN_GLUDIO, 2, 5000));
	}

	@Override
	protected int huntingAmount(Player killer, QuestState qs) {
		return 500;
	}

	@Override
	protected boolean hasHuntCondition(Player killer, Npc npc, QuestState qs) {
		return qs.isCond(1);
	}

}
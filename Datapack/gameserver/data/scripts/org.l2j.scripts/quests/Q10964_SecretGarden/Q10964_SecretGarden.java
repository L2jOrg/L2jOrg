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
package quests.Q10964_SecretGarden;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;


/**
 * Secret Garden (10964)
 * @author RobikBobik
 * @Note: Based on NA server September 2019
 */
public class Q10964_SecretGarden extends Quest
{
	// NPC
	private static final int CAPTAIN_BATHIS = 30332;
	private static final int RAYMOND = 30289;
	// Monsters
	private static final int HARPY = 20145;
	private static final int MEDUSA = 20158;
	private static final int WYRM = 20176;
	private static final int TURAK_BUGBEAR = 20248;
	private static final int TURAK_BUGBEAR_WARRIOR = 20249;
	// Misc
	private static final String KILL_COUNT_VAR = "KillCount";
	private static final int MAX_LEVEL = 34;
	private static final int MIN_LEVEL = 30;
	
	public Q10964_SecretGarden()
	{
		super(10964);
		addStartNpc(CAPTAIN_BATHIS);
		addTalkId(CAPTAIN_BATHIS, RAYMOND);
		addKillId(HARPY, MEDUSA, WYRM, TURAK_BUGBEAR, TURAK_BUGBEAR_WARRIOR);
		setQuestNameNpcStringId(NpcStringId.LV_30_34_SECRET_GARDEN);
		addCondMinLevel(MIN_LEVEL, "no_lvl.html");
		addCondMaxLevel(MAX_LEVEL, "no_lvl.html");
	}
	
	@Override
	public boolean checkPartyMember(Player member, Npc npc)
	{
		final QuestState qs = getQuestState(member, false);
		return ((qs != null) && qs.isStarted());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30332-01.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30289-01.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "30289-02.html":
			{
				htmltext = event;
				break;
			}
			case "30289-03.html":
			{
				if (qs.isStarted())
				{
					player.sendPacket(new ExShowScreenMessage(NpcStringId.THE_MISSION_ADVENTURER_S_JOURNEY_II_IS_NOW_AVAILABLE_NCLICK_THE_YELLOW_QUESTION_MARK_IN_THE_RIGHT_BOTTOM_CORNER_OF_YOUR_SCREEN_TO_SEE_THE_QUEST_S_INFO, 2, 5000));
					addExpAndSp(player, 2500000, 75000);
					qs.exitQuest(false, true);
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(2))
		{
			final int killCount = qs.getInt(KILL_COUNT_VAR) + 1;
			if (killCount < 500)
			{
				qs.set(KILL_COUNT_VAR, killCount);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				sendNpcLogList(killer);
			}
			else
			{
				qs.setCond(3, true);
				qs.unset(KILL_COUNT_VAR);
				killer.sendPacket(new ExShowScreenMessage(NpcStringId.MONSTERS_OF_THE_GORGON_FLOWER_GARDEN_ARE_KILLED_NUSE_THE_TELEPORT_TO_GET_TO_HIGH_PRIEST_RAYMOND_IN_GLUDIO, 2, 5000));
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(Player player)
	{
		
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(2))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_GORGON_FLOWER_GARDEN.getId(), true, qs.getInt(KILL_COUNT_VAR)));
			return holder;
		}
		
		return super.getNpcLogList(player);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);

		if(isNull(qs)) {
			return htmltext;
		}

		if (qs.isCreated())
		{
			htmltext = "30332.htm";
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case CAPTAIN_BATHIS:
				{
					if (qs.isCond(1))
					{
						htmltext = "30332-01.htm";
					}
					break;
				}
				case RAYMOND:
				{
					if (qs.isCond(1))
					{
						htmltext = "30289.html";
					}
					else if (qs.isCond(3))
					{
						htmltext = "30289-02.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == CAPTAIN_BATHIS)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		
		return htmltext;
	}
}
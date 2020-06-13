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
package quests.Q10965_DeathMysteries;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;


/**
 * Death Mysteries (10965)
 * @author RobikBobik
 * @Note: Based on NA server September 2019
 */
public class Q10965_DeathMysteries extends Quest
{
	// NPC
	private static final int RAYMOND = 30289;
	private static final int MAXIMILLIAN = 30120;
	// Monsters
	private static final int WYRM = 20176;
	private static final int GUARDIAN_BASILISK = 20550;
	private static final int ROAD_SCAVENGER = 20551;
	private static final int FETTERED_SOUL = 20552;
	private static final int WINDUS = 20553;
	private static final int GRANDIS = 20554;
	// Items
	private static final ItemHolder ADVENTURERS_AGATHION_BRACELET = new ItemHolder(91933, 1);
	private static final ItemHolder ADVENTURERS_AGATHION_GRIFIN = new ItemHolder(91935, 1);
	// Misc
	private static final String KILL_COUNT_VAR = "KillCount";
	private static final int MAX_LEVEL = 40;
	private static final int MIN_LEVEL = 37;
	
	public Q10965_DeathMysteries()
	{
		super(10965);
		addStartNpc(RAYMOND);
		addTalkId(RAYMOND, MAXIMILLIAN);
		addKillId(WYRM, GUARDIAN_BASILISK, ROAD_SCAVENGER, FETTERED_SOUL, WINDUS, GRANDIS);
		setQuestNameNpcStringId(NpcStringId.LV_37_40_DEATH_MYSTERIES);
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
			case "TELEPORT_TO_MAXIMILLIAN":
			{
				player.teleToLocation(86845, 148626, -3402);
				break;
			}
			case "30289-01.htm":
			{
				htmltext = event;
				break;
			}
			case "30289-02.htm":
			{
				htmltext = event;
				break;
			}
			case "30289-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30120-01.html":
			{
				htmltext = event;
				break;
			}
			case "30120-02.html":
			{
				htmltext = event;
				break;
			}
			case "30120-03.html":
			{
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "30120-05.html":
			{
				if (qs.isStarted())
				{
					player.sendPacket(new ExShowScreenMessage(NpcStringId.YOU_VE_GOT_ADVENTURER_S_AGATHION_BRACELET_AND_ADVENTURER_S_AGATHION_GRIFFIN_NCOMPLETE_THE_TUTORIAL_AND_TRY_TO_USE_THE_AGATHION, 2, 5000));
					addExpAndSp(player, 3000000, 75000);
					giveItems(player, ADVENTURERS_AGATHION_BRACELET);
					giveItems(player, ADVENTURERS_AGATHION_GRIFIN);
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
				killer.sendPacket(new ExShowScreenMessage(NpcStringId.MONSTERS_OF_THE_DEATH_PASS_ARE_KILLED_NUSE_THE_TELEPORT_OR_THE_SCROLL_OF_ESCAPE_TO_GET_TO_HIGH_PRIEST_MAXIMILIAN_IN_GIRAN, 2, 5000));
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
			holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_DEATH_PASS.getId(), true, qs.getInt(KILL_COUNT_VAR)));
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
			htmltext = "30289.htm";
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case RAYMOND:
				{
					if (qs.isCond(1))
					{
						htmltext = "30289-01.htm";
					}
					break;
				}
				case MAXIMILLIAN:
				{
					if (qs.isCond(1))
					{
						htmltext = "30120.html";
					}
					else if (qs.isCond(3))
					{
						htmltext = "30120-04.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == RAYMOND)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		
		return htmltext;
	}
}
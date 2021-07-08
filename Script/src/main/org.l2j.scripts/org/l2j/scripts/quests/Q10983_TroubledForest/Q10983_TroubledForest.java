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
package org.l2j.scripts.quests.Q10983_TroubledForest;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.util.MathUtil;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;


/**
 * Troubled Forest (10983)
 * @author RobikBobik
 * @Notee: Debugging by Bru7aLMike. // Based on EU-classic server November 2020
 * TODO: OnKill optimisation
 */
public class Q10983_TroubledForest extends Quest
{
	// NPCs
	private static final int NEWBIE_GUIDE = 30599;
	private static final int HERBIEL = 30150;
	// Monsters
	private static final int GOBLIN_RAIDER = 20325;
	private static final int KABOO_ORC = 20468;
	// Items
	private static final ItemHolder SOE_TO_HERBIEL = new ItemHolder(91647, 1);
	private static final ItemHolder SOE_NOVICE = new ItemHolder(10650, 10);
	private static final ItemHolder RING_NOVICE = new ItemHolder(49041, 2);
	private static final ItemHolder EARRING_NOVICE = new ItemHolder(49040, 2);
	private static final ItemHolder NECKLACE_NOVICE = new ItemHolder(49039, 1);
	// Misc
	private static final int MAX_LEVEL = 20;
	private static final String KILL_COUNT_VAR = "KillCount";

	public Q10983_TroubledForest()
	{
		super(10983);
		addStartNpc(NEWBIE_GUIDE);
		addTalkId(NEWBIE_GUIDE, HERBIEL);
		addKillId(GOBLIN_RAIDER, KABOO_ORC);
		addCondMaxLevel(MAX_LEVEL, "no_lvl.html");
		setQuestNameNpcStringId(NpcStringId.LV_2_20_TROUBLED_FOREST);
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
			case "TELEPORT_TO_HUNTING_GROUND":
			{
				player.teleToLocation(52746, 49932, -3480);
				break;
			}
			case "30599-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30150-02.htm":
			{
				if (qs.isStarted())
				{
					addExpAndSp(player, 260000, 6000);
					giveItems(player, SOE_NOVICE);
					giveItems(player, RING_NOVICE);
					giveItems(player, EARRING_NOVICE);
					giveItems(player, NECKLACE_NOVICE);
					qs.exitQuest(false, true);
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		if (player.getParty() != null)
		{
			for (Player partyMember : player.getParty().getMembers())
			{
				if (MathUtil.isInsideRadius3D(npc, player, 1200))
				{
					final QuestState qs = getQuestState(partyMember, false);
					if ((qs != null) && qs.isCond(1))
					{
						final int killCount = qs.getInt(KILL_COUNT_VAR) + 1;
						if (killCount <= 19)
						{
							qs.set(KILL_COUNT_VAR, killCount);
							playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							sendNpcLogList(partyMember);
						}
						else
						{
							qs.setCond(2, true);
							qs.unset(KILL_COUNT_VAR);
							partyMember.sendPacket(new ExShowScreenMessage("You hunted all monsters.#Use the Scroll of Escape from your inventory to return to Trader Herbiel.", 5000));
							giveItems(partyMember, SOE_TO_HERBIEL);
						}
					}

				}
				else if (!MathUtil.isInsideRadius3D(npc, player, 1200))
				{
					return null;
				}
			}
		}
		else
		{
			final QuestState qs = getQuestState(player, false);
			if (qs == null)
			{
				return null;
			}
			else if ((qs != null) && qs.isCond(1))
			{
				final int killCount = qs.getInt(KILL_COUNT_VAR) + 1;
				if (killCount <= 19)
				{
					qs.set(KILL_COUNT_VAR, killCount);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					sendNpcLogList(player);
				}
				else
				{
					qs.setCond(2, true);
					qs.unset(KILL_COUNT_VAR);
					player.sendPacket(new ExShowScreenMessage("You hunted all monsters.#Use the Scroll of Escape from your inventory to return to Trader Herbiel.", 5000));
					giveItems(player, SOE_TO_HERBIEL);
				}
			}

		}
		return super.onKill(npc, player, isSummon);
	}

	@Override
	public Set<NpcLogListHolder> getNpcLogList(Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(NpcStringId.KILL_ORCS_AND_GOBLINS.getId(), true, qs.getInt(KILL_COUNT_VAR)));
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
			htmltext = "30599-01.htm";
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case NEWBIE_GUIDE:
				{
					if (qs.isCond(1))
					{
						htmltext = "30599-02.htm";
					}
					break;
				}
				case HERBIEL:
				{
					if (qs.isCond(2))
					{
						htmltext = "30150.htm";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == NEWBIE_GUIDE)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}
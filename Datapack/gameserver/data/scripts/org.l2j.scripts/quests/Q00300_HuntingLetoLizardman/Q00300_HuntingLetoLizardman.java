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
package quests.Q00300_HuntingLetoLizardman;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

/**
 * Hunting Leto Lizardman (300)
 * @author ivantotov
 */
public final class Q00300_HuntingLetoLizardman extends Quest
{
	// NPCs
	private static final int RATH = 30126;
	// Items
	private static final int BRACELET_OF_LIZARDMAN = 7139;
	private static final ItemHolder REWARD_ADENA = new ItemHolder(CommonItem.ADENA, 5000);
	private static final ItemHolder REWARD_ANIMAL_BONE = new ItemHolder(1872, 50);
	private static final ItemHolder REWARD_ANIMAL_SKIN = new ItemHolder(1867, 50);
	// Misc
	private static final int MIN_LEVEL = 34;
	private static final int REQUIRED_BRACELET_COUNT = 60;
	// Monsters
	private static final Map<Integer, Integer> MOBS_SAC = new HashMap<>();
	static
	{
		MOBS_SAC.put(20577, 360); // Leto Lizardman
		MOBS_SAC.put(20578, 390); // Leto Lizardman Archer
		MOBS_SAC.put(20579, 410); // Leto Lizardman Soldier
		MOBS_SAC.put(20580, 790); // Leto Lizardman Warrior
		MOBS_SAC.put(20582, 890); // Leto Lizardman Overlord
	}
	
	public Q00300_HuntingLetoLizardman()
	{
		super(300);
		addStartNpc(RATH);
		addTalkId(RATH);
		addKillId(MOBS_SAC.keySet());
		registerQuestItems(BRACELET_OF_LIZARDMAN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, false);
		if (isNull(st)) {
			return null;
		}
		String htmltext = null;
		switch (event)
		{
			case "30126-03.htm":
			{
				if (st.isCreated())
				{
					st.startQuest();
					htmltext = event;
				}
				break;
			}
			case "30126-06.html":
			{
				if (getQuestItemsCount(player, BRACELET_OF_LIZARDMAN) >= REQUIRED_BRACELET_COUNT)
				{
					takeItems(player, BRACELET_OF_LIZARDMAN, -1);
					int rand = getRandom(1000);
					if (rand < 500)
					{
						giveItems(player, REWARD_ADENA);
					}
					else if (rand < 750)
					{
						giveItems(player, REWARD_ANIMAL_SKIN);
					}
					else if (rand < 1000)
					{
						giveItems(player, REWARD_ANIMAL_BONE);
					}
					st.exitQuest(true, true);
					htmltext = event;
				}
				else
				{
					htmltext = "30126-07.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final Player partyMember = getRandomPartyMember(player, 1);
		if (partyMember != null)
		{
			final QuestState st = getQuestState(partyMember, false);
			if (st.isCond(1) && (getRandom(1000) < MOBS_SAC.get(npc.getId())))
			{
				giveItems(player, BRACELET_OF_LIZARDMAN, 1);
				if (getQuestItemsCount(player, BRACELET_OF_LIZARDMAN) == REQUIRED_BRACELET_COUNT)
				{
					st.setCond(2, true);
				}
				else
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() >= MIN_LEVEL) ? "30126-01.htm" : "30126-02.htm";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "30126-04.html";
						break;
					}
					case 2:
					{
						if (getQuestItemsCount(player, BRACELET_OF_LIZARDMAN) >= REQUIRED_BRACELET_COUNT)
						{
							htmltext = "30126-05.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
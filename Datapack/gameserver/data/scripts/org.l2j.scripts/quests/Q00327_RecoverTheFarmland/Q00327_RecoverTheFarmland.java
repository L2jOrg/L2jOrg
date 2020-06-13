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
package quests.Q00327_RecoverTheFarmland;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

import java.util.HashMap;
import java.util.Map;

/**
 * Recover the Farmland (327).
 * @author GKR
 */

public final class Q00327_RecoverTheFarmland extends Quest
{
	// NPCs
	private static final int IRIS = 30034;
	private static final int ASHA = 30313;
	private static final int NESTLE = 30314;
	private static final int LEIKAN = 30382;
	private static final int PIOTUR = 30597;
	private static final int TUREK_ORK_WARLORD = 20495;
	private static final int TUREK_ORK_ARCHER = 20496;
	private static final int TUREK_ORK_SKIRMISHER = 20497;
	private static final int TUREK_ORK_SUPPLIER = 20498;
	private static final int TUREK_ORK_FOOTMAN = 20499;
	private static final int TUREK_ORK_SENTINEL = 20500;
	private static final int TUREK_ORK_SHAMAN = 20501;
	
	// Items
	private static final int TUREK_DOG_TAG = 1846;
	private static final int TUREK_MEDALLION = 1847;
	private static final int LEIKANS_LETTER = 5012;
	private static final int CLAY_URN_FRAGMENT = 1848;
	private static final int BRASS_TRINKET_PIECE = 1849;
	private static final int BRONZE_MIRROR_PIECE = 1850;
	private static final int JADE_NECKLACE_BEAD = 1851;
	private static final int ANCIENT_CLAY_URN = 1852;
	private static final int ANCIENT_BRASS_TIARA = 1853;
	private static final int ANCIENT_BRONZE_MIRROR = 1854;
	private static final int ANCIENT_JADE_NECKLACE = 1855;
	private static final int QUICK_STEP_POTION = 734;
	private static final int SWIFT_ATTACK_POTION = 735;
	private static final int SCROLL_OF_ESCAPE = 736;
	private static final int SCROLL_OF_RESURRECTION = 737;
	private static final int HEALING_POTION = 1061;
	private static final int SOULSHOT_D = 1463;
	private static final int SPIRITSHOT_D = 2510;
	
	// Misc
	private static final int MIN_LVL = 25;
	private static final Map<String, ItemHolder> FRAGMENTS_REWARD_DATA = new HashMap<>(4);
	private static final Map<Integer, Integer> FRAGMENTS_DROP_PROB = new HashMap<>(7);
	private static final ItemHolder[] FULL_REWARD_DATA =
	{
		new ItemHolder(ANCIENT_CLAY_URN, 2766),
		new ItemHolder(ANCIENT_BRASS_TIARA, 3227),
		new ItemHolder(ANCIENT_BRONZE_MIRROR, 3227),
		new ItemHolder(ANCIENT_JADE_NECKLACE, 3919)
	};
	
	static
	{
		FRAGMENTS_REWARD_DATA.put("30034-03.html", new ItemHolder(CLAY_URN_FRAGMENT, 307));
		FRAGMENTS_REWARD_DATA.put("30034-04.html", new ItemHolder(BRASS_TRINKET_PIECE, 368));
		FRAGMENTS_REWARD_DATA.put("30034-05.html", new ItemHolder(BRONZE_MIRROR_PIECE, 368));
		FRAGMENTS_REWARD_DATA.put("30034-06.html", new ItemHolder(JADE_NECKLACE_BEAD, 430));
		
		FRAGMENTS_DROP_PROB.put(TUREK_ORK_ARCHER, 21);
		FRAGMENTS_DROP_PROB.put(TUREK_ORK_FOOTMAN, 19);
		FRAGMENTS_DROP_PROB.put(TUREK_ORK_SENTINEL, 18);
		FRAGMENTS_DROP_PROB.put(TUREK_ORK_SHAMAN, 22);
		FRAGMENTS_DROP_PROB.put(TUREK_ORK_SKIRMISHER, 21);
		FRAGMENTS_DROP_PROB.put(TUREK_ORK_SUPPLIER, 20);
		FRAGMENTS_DROP_PROB.put(TUREK_ORK_WARLORD, 26);
	}
	
	public Q00327_RecoverTheFarmland()
	{
		super(327);
		addStartNpc(LEIKAN, PIOTUR);
		addTalkId(LEIKAN, PIOTUR, IRIS, ASHA, NESTLE);
		addKillId(TUREK_ORK_WARLORD, TUREK_ORK_ARCHER, TUREK_ORK_SKIRMISHER, TUREK_ORK_SUPPLIER, TUREK_ORK_FOOTMAN, TUREK_ORK_SENTINEL, TUREK_ORK_SHAMAN);
		registerQuestItems(TUREK_DOG_TAG, TUREK_MEDALLION, LEIKANS_LETTER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String html = null;
		
		switch (event)
		{
			case "30034-01.html":
			case "30313-01.html":
			case "30314-02.html":
			case "30314-08.html":
			case "30314-09.html":
			case "30382-05a.html":
			case "30382-05b.html":
			case "30597-03.html":
			case "30597-07.html":
			{
				html = event;
				break;
			}
			case "30382-03.htm":
			{
				st.startQuest();
				giveItems(player, LEIKANS_LETTER, 1);
				st.setCond(2);
				html = event;
				break;
			}
			case "30597-03.htm":
			{
				st.startQuest();
				html = event;
				break;
			}
			case "30597-06.html":
			{
				st.exitQuest(true, true);
				html = event;
				break;
			}
			case "30034-03.html":
			case "30034-04.html":
			case "30034-05.html":
			case "30034-06.html":
			{
				final ItemHolder item = FRAGMENTS_REWARD_DATA.get(event);
				if (!hasQuestItems(player, item.getId()))
				{
					html = "30034-02.html";
				}
				else
				{
					addExpAndSp(player, getQuestItemsCount(player, item.getId()) * item.getCount(), 0);
					takeItems(player, item.getId(), -1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					html = event;
				}
				break;
			}
			case "30034-07.html":
			{
				boolean rewarded = false;
				for (ItemHolder it : FULL_REWARD_DATA)
				{
					if (hasQuestItems(player, it.getId()))
					{
						addExpAndSp(player, getQuestItemsCount(player, it.getId()) * it.getCount(), 0);
						takeItems(player, it.getId(), -1);
						playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						rewarded = true;
					}
				}
				html = rewarded ? event : "30034-02.html";
				break;
			}
			case "30313-03.html":
			{
				if (getQuestItemsCount(player, CLAY_URN_FRAGMENT) < 5)
				{
					html = "30313-02.html";
				}
				else
				{
					takeItems(player, CLAY_URN_FRAGMENT, 5);
					if (getRandom(6) < 5)
					{
						giveItems(player, ANCIENT_CLAY_URN, 1);
						html = event;
					}
					else
					{
						html = "30313-10.html";
					}
				}
				break;
			}
			case "30313-05.html":
			{
				if (getQuestItemsCount(player, BRASS_TRINKET_PIECE) < 5)
				{
					html = "30313-04.html";
				}
				else
				{
					takeItems(player, BRASS_TRINKET_PIECE, 5);
					if (getRandom(7) < 6)
					{
						giveItems(player, ANCIENT_BRASS_TIARA, 1);
						html = event;
					}
					else
					{
						html = "30313-10.html";
					}
				}
				break;
			}
			case "30313-07.html":
			{
				if (getQuestItemsCount(player, BRONZE_MIRROR_PIECE) < 5)
				{
					html = "30313-06.html";
				}
				else
				{
					takeItems(player, BRONZE_MIRROR_PIECE, 5);
					if (getRandom(7) < 6)
					{
						giveItems(player, ANCIENT_BRONZE_MIRROR, 1);
						html = event;
					}
					else
					{
						html = "30313-10.html";
					}
				}
				break;
			}
			case "30313-09.html":
			{
				if (getQuestItemsCount(player, JADE_NECKLACE_BEAD) < 5)
				{
					html = "30313-08.html";
				}
				else
				{
					takeItems(player, JADE_NECKLACE_BEAD, 5);
					if (getRandom(8) < 7)
					{
						giveItems(player, ANCIENT_JADE_NECKLACE, 1);
						html = event;
					}
					else
					{
						html = "30313-10.html";
					}
				}
				break;
			}
			case "30314-03.html":
			{
				if (!hasQuestItems(player, ANCIENT_CLAY_URN))
				{
					html = "30314-07.html";
				}
				else
				{
					rewardItems(player, SOULSHOT_D, getRandom(70, 110));
					takeItems(player, ANCIENT_CLAY_URN, 1);
					html = event;
				}
				break;
			}
			case "30314-04.html":
			{
				if (!hasQuestItems(player, ANCIENT_BRASS_TIARA))
				{
					html = "30314-07.html";
				}
				else
				{
					final int rnd = getRandom(100);
					if (rnd < 40)
					{
						rewardItems(player, HEALING_POTION, 1);
					}
					else if (rnd < 84)
					{
						rewardItems(player, QUICK_STEP_POTION, 1);
					}
					else
					{
						rewardItems(player, SWIFT_ATTACK_POTION, 1);
					}
					takeItems(player, ANCIENT_BRASS_TIARA, 1);
					html = event;
				}
				break;
			}
			case "30314-05.html":
			{
				if (!hasQuestItems(player, ANCIENT_BRONZE_MIRROR))
				{
					html = "30314-07.html";
				}
				else
				{
					rewardItems(player, (getRandom(100) < 59) ? SCROLL_OF_ESCAPE : SCROLL_OF_RESURRECTION, 1);
					takeItems(player, ANCIENT_BRONZE_MIRROR, 1);
					html = event;
				}
				break;
			}
			case "30314-06.html":
			{
				if (!hasQuestItems(player, ANCIENT_JADE_NECKLACE))
				{
					html = "30314-07.html";
				}
				else
				{
					rewardItems(player, SPIRITSHOT_D, getRandom(50, 90));
					takeItems(player, ANCIENT_JADE_NECKLACE, 1);
					html = event;
				}
				break;
			}
		}
		
		return html;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		if (st != null)
		{
			if ((npc.getId() == TUREK_ORK_SHAMAN) || (npc.getId() == TUREK_ORK_WARLORD))
			{
				giveItems(killer, TUREK_MEDALLION, 1);
			}
			else
			{
				giveItems(killer, TUREK_DOG_TAG, 1);
			}
			
			if (getRandom(100) < FRAGMENTS_DROP_PROB.get(npc.getId()))
			{
				giveItems(killer, getRandom(CLAY_URN_FRAGMENT, JADE_NECKLACE_BEAD), 1);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, true);
		String html = getNoQuestMsg(player);
		
		switch (npc.getId())
		{
			case LEIKAN:
			{
				if (st.isCreated())
				{
					html = ((player.getLevel() >= MIN_LVL) ? "30382-02.htm" : "30382-01.htm");
				}
				else if (st.isStarted())
				{
					if (hasQuestItems(player, LEIKANS_LETTER))
					{
						html = "30382-04.html";
					}
					else
					{
						html = "30382-05.html";
						st.setCond(5, true);
					}
				}
				break;
			}
			case PIOTUR:
			{
				if (st.isCreated())
				{
					html = ((player.getLevel() >= MIN_LVL) ? "30597-02.htm" : "30597-01.htm");
				}
				else if (st.isStarted())
				{
					if (hasQuestItems(player, LEIKANS_LETTER))
					{
						html = "30597-03a.htm";
						takeItems(player, LEIKANS_LETTER, -1);
						st.setCond(3, true);
					}
					else
					{
						if (!hasQuestItems(player, TUREK_DOG_TAG) && !hasQuestItems(player, TUREK_MEDALLION))
						{
							html = "30597-04.html";
						}
						else
						{
							html = "30597-05.html";
							final long dogTags = getQuestItemsCount(player, TUREK_DOG_TAG);
							final long medallions = getQuestItemsCount(player, TUREK_MEDALLION);
							final long rewardCount = (dogTags * 8) + (medallions * 8) + (((dogTags + medallions) >= 10) ? 1000 : 0);
							giveAdena(player, rewardCount, true);
							takeItems(player, TUREK_DOG_TAG, -1);
							takeItems(player, TUREK_MEDALLION, -1);
							st.setCond(4, true);
						}
					}
				}
				break;
			}
			case IRIS:
			{
				if (st.isStarted())
				{
					html = "30034-01.html";
				}
				break;
			}
			case ASHA:
			{
				if (st.isStarted())
				{
					html = "30313-01.html";
				}
				break;
			}
			case NESTLE:
			{
				if (st.isStarted())
				{
					html = "30314-01.html";
				}
				break;
			}
		}
		
		return html;
	}
}
/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q00325_GrimCollector;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.QuestItemHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.util.GameUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Grim Collector (325)
 * @author ivantotov
 */
public final class Q00325_GrimCollector extends Quest
{
	// NPCs
	private static final int GUARD_CURTIZ = 30336;
	private static final int VARSAK = 30342;
	private static final int SAMED = 30434;
	// Items
	private static final int ANATOMY_DIAGRAM = 1349;
	private static final int ZOMBIE_HEAD = 1350;
	private static final int ZOMBIE_HEART = 1351;
	private static final int ZOMBIE_LIVER = 1352;
	private static final int SKULL = 1353;
	private static final int RIB_BONE = 1354;
	private static final int SPINE = 1355;
	private static final int ARM_BONE = 1356;
	private static final int THIGH_BONE = 1357;
	private static final int COMPLETE_SKELETON = 1358;
	// Misc
	private static final int MIN_LEVEL = 15;
	// Monsters
	private static final Map<Integer, List<QuestItemHolder>> MONSTER_DROPS = new HashMap<>();
	static
	{
		MONSTER_DROPS.put(20026, Arrays.asList(new QuestItemHolder(ZOMBIE_HEAD, 30), new QuestItemHolder(ZOMBIE_HEART, 50), new QuestItemHolder(ZOMBIE_LIVER, 75)));
		MONSTER_DROPS.put(20029, Arrays.asList(new QuestItemHolder(ZOMBIE_HEAD, 30), new QuestItemHolder(ZOMBIE_HEART, 52), new QuestItemHolder(ZOMBIE_LIVER, 75)));
		MONSTER_DROPS.put(20035, Arrays.asList(new QuestItemHolder(SKULL, 5), new QuestItemHolder(RIB_BONE, 15), new QuestItemHolder(SPINE, 29), new QuestItemHolder(THIGH_BONE, 79)));
		MONSTER_DROPS.put(20042, Arrays.asList(new QuestItemHolder(SKULL, 6), new QuestItemHolder(RIB_BONE, 19), new QuestItemHolder(ARM_BONE, 69), new QuestItemHolder(THIGH_BONE, 86)));
		MONSTER_DROPS.put(20045, Arrays.asList(new QuestItemHolder(SKULL, 9), new QuestItemHolder(SPINE, 59), new QuestItemHolder(ARM_BONE, 77), new QuestItemHolder(THIGH_BONE, 97)));
		MONSTER_DROPS.put(20051, Arrays.asList(new QuestItemHolder(SKULL, 9), new QuestItemHolder(RIB_BONE, 59), new QuestItemHolder(SPINE, 79), new QuestItemHolder(ARM_BONE, 100)));
		MONSTER_DROPS.put(20457, Arrays.asList(new QuestItemHolder(ZOMBIE_HEAD, 40), new QuestItemHolder(ZOMBIE_HEART, 60), new QuestItemHolder(ZOMBIE_LIVER, 80)));
		MONSTER_DROPS.put(20458, Arrays.asList(new QuestItemHolder(ZOMBIE_HEAD, 40), new QuestItemHolder(ZOMBIE_HEART, 70), new QuestItemHolder(ZOMBIE_LIVER, 100)));
		MONSTER_DROPS.put(20514, Arrays.asList(new QuestItemHolder(SKULL, 6), new QuestItemHolder(RIB_BONE, 21), new QuestItemHolder(SPINE, 30), new QuestItemHolder(ARM_BONE, 31), new QuestItemHolder(THIGH_BONE, 64)));
		MONSTER_DROPS.put(20515, Arrays.asList(new QuestItemHolder(SKULL, 5), new QuestItemHolder(RIB_BONE, 20), new QuestItemHolder(SPINE, 31), new QuestItemHolder(ARM_BONE, 33), new QuestItemHolder(THIGH_BONE, 69)));
	}
	
	public Q00325_GrimCollector()
	{
		super(325);
		addStartNpc(GUARD_CURTIZ);
		addTalkId(GUARD_CURTIZ, VARSAK, SAMED);
		addKillId(MONSTER_DROPS.keySet());
		registerQuestItems(ANATOMY_DIAGRAM, ZOMBIE_HEAD, ZOMBIE_HEART, ZOMBIE_LIVER, SKULL, RIB_BONE, SPINE, ARM_BONE, THIGH_BONE, COMPLETE_SKELETON);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		String htmltext = null;
		switch (event)
		{
			case "30336-03.htm":
			{
				if (st.isCreated())
				{
					st.startQuest();
					htmltext = event;
				}
				break;
			}
			case "assembleSkeleton":
			{
				if (!hasQuestItems(player, SPINE, ARM_BONE, SKULL, RIB_BONE, THIGH_BONE))
				{
					htmltext = "30342-02.html";
				}
				else
				{
					takeItems(player, 1, SPINE, ARM_BONE, SKULL, RIB_BONE, THIGH_BONE);
					
					if (getRandom(5) < 4)
					{
						giveItems(player, COMPLETE_SKELETON, 1);
						htmltext = "30342-03.html";
					}
					else
					{
						htmltext = "30342-04.html";
					}
				}
				break;
			}
			case "30434-02.htm":
			{
				htmltext = event;
				break;
			}
			case "30434-03.html":
			{
				giveItems(player, ANATOMY_DIAGRAM, 1);
				htmltext = event;
				break;
			}
			case "30434-06.html":
			case "30434-07.html":
			{
				if (hasQuestItems(player, getRegisteredItemIds()))
				{
					final long head = getQuestItemsCount(player, ZOMBIE_HEAD);
					final long heart = getQuestItemsCount(player, ZOMBIE_HEART);
					final long liver = getQuestItemsCount(player, ZOMBIE_LIVER);
					final long skull = getQuestItemsCount(player, SKULL);
					final long rib = getQuestItemsCount(player, RIB_BONE);
					final long spine = getQuestItemsCount(player, SPINE);
					final long arm = getQuestItemsCount(player, ARM_BONE);
					final long thigh = getQuestItemsCount(player, THIGH_BONE);
					final long complete = getQuestItemsCount(player, COMPLETE_SKELETON);
					final long totalCount = (head + heart + liver + skull + rib + spine + arm + thigh + complete);
					if (totalCount > 0)
					{
						long sum = ((head * 8) + (heart * 5) + (liver * 5) + (skull * 25) + (rib * 5) + (spine * 5) + (arm * 5) + (thigh * 5));
						
						if (totalCount >= 10)
						{
							sum += 1629;
						}
						
						if (complete > 0)
						{
							sum += 543 + (complete * 341);
						}
						
						giveAdena(player, sum, true);
					}
					
					takeItems(player, -1, getRegisteredItemIds());
				}
				
				if (event.equals("30434-06.html"))
				{
					st.exitQuest(true, true);
				}
				
				htmltext = event;
				break;
			}
			case "30434-09.html":
			{
				final long complete = getQuestItemsCount(player, COMPLETE_SKELETON);
				if (complete > 0)
				{
					giveAdena(player, ((complete * 341) + 543), true);
					takeItems(player, COMPLETE_SKELETON, -1);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs == null) || !qs.isStarted())
		{
			return super.onKill(npc, killer, isSummon);
		}
		
		if (!GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, killer, npc, true) || !hasQuestItems(killer, ANATOMY_DIAGRAM))
		{
			return super.onKill(npc, killer, isSummon);
		}
		
		final int rnd = getRandom(100);
		for (QuestItemHolder drop : MONSTER_DROPS.get(npc.getId()))
		{
			if (rnd < drop.getChance())
			{
				giveItemRandomly(killer, npc, drop.getId(), 1, 0, 1.0, true);
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (npc.getId())
		{
			case GUARD_CURTIZ:
			{
				switch (st.getState())
				{
					case State.CREATED:
					{
						htmltext = (player.getLevel() >= MIN_LEVEL) ? "30336-02.htm" : "30336-01.htm";
						break;
					}
					case State.STARTED:
					{
						htmltext = hasQuestItems(player, ANATOMY_DIAGRAM) ? "30336-05.html" : "30336-04.html";
						break;
					}
				}
				break;
			}
			case VARSAK:
			{
				if (st.isStarted() && hasQuestItems(player, ANATOMY_DIAGRAM))
				{
					htmltext = "30342-01.html";
				}
				break;
			}
			case SAMED:
			{
				if (st.isStarted())
				{
					if (!hasQuestItems(player, ANATOMY_DIAGRAM))
					{
						htmltext = "30434-01.html";
					}
					else
					{
						if (!hasAtLeastOneQuestItem(player, getRegisteredItemIds()))
						{
							htmltext = "30434-04.html";
						}
						else if (!hasQuestItems(player, COMPLETE_SKELETON))
						{
							htmltext = "30434-05.html";
						}
						else
						{
							htmltext = "30434-08.html";
						}
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
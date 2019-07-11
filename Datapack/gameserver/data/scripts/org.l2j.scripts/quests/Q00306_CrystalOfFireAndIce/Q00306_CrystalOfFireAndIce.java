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
package quests.Q00306_CrystalOfFireAndIce;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.util.GameUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Crystals of Fire and Ice (306)
 * @author ivantotov
 */
public final class Q00306_CrystalOfFireAndIce extends Quest
{
	// NPC
	private static final int KATERINA = 30004;
	// Items
	private static final int FLAME_SHARD = 1020;
	private static final int ICE_SHARD = 1021;
	// Misc
	private static final int MIN_LEVEL = 17;
	// Monsters
	private static final int UNDINE_NOBLE = 20115;
	private static final Map<Integer, ItemHolder> MONSTER_DROPS = new HashMap<>();
	static
	{
		MONSTER_DROPS.put(20109, new ItemHolder(FLAME_SHARD, 925)); // Salamander
		MONSTER_DROPS.put(20110, new ItemHolder(ICE_SHARD, 900)); // Undine
		MONSTER_DROPS.put(20112, new ItemHolder(FLAME_SHARD, 900)); // Salamander Elder
		MONSTER_DROPS.put(20113, new ItemHolder(ICE_SHARD, 925)); // Undine Elder
		MONSTER_DROPS.put(20114, new ItemHolder(FLAME_SHARD, 925)); // Salamander Noble
		MONSTER_DROPS.put(UNDINE_NOBLE, new ItemHolder(ICE_SHARD, 950)); // Undine Noble
	}
	
	public Q00306_CrystalOfFireAndIce()
	{
		super(306);
		addStartNpc(KATERINA);
		addTalkId(KATERINA);
		addKillId(MONSTER_DROPS.keySet());
		registerQuestItems(FLAME_SHARD, ICE_SHARD);
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
			case "30004-04.htm":
			{
				if (st.isCreated())
				{
					st.startQuest();
					htmltext = event;
				}
				break;
			}
			case "30004-08.html":
			{
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "30004-09.html":
			{
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs;
		if (npc.getId() == UNDINE_NOBLE) // Undine Noble gives quest drops only for the killer
		{
			qs = getQuestState(killer, false);
			if ((qs != null) && qs.isStarted())
			{
				giveKillReward(killer, npc);
			}
		}
		else
		{
			qs = getRandomPartyMemberState(killer, -1, 3, npc);
			if (qs != null)
			{
				giveKillReward(qs.getPlayer(), npc);
			}
		}
		return super.onKill(npc, killer, isSummon);
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
				htmltext = player.getLevel() >= MIN_LEVEL ? "30004-03.htm" : "30004-02.htm";
				break;
			}
			case State.STARTED:
			{
				if (hasAtLeastOneQuestItem(player, getRegisteredItemIds()))
				{
					final long flame = getQuestItemsCount(player, FLAME_SHARD);
					final long ice = getQuestItemsCount(player, ICE_SHARD);
					giveAdena(player, ((flame * 15) + (ice * 15) + ((flame + ice) >= 10 ? 5000 : 0)), true);
					takeItems(player, -1, getRegisteredItemIds());
					htmltext = "30004-07.html";
				}
				else
				{
					htmltext = "30004-05.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	private static void giveKillReward(Player player, Npc npc)
	{
		if (GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, player, false))
		{
			final ItemHolder item = MONSTER_DROPS.get(npc.getId());
			giveItemRandomly(player, npc, item.getId(), 1, 0, 1000.0 / item.getCount(), true);
		}
	}
}
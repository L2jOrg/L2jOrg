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
package quests.Q11019_TribalBenefit;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Tribal Benefit (11019)
 * @author Stayway
 */
public class Q11019_TribalBenefit extends Quest
{
	// NPCs
	private static final int NEWBIE_GUIDE = 30602;
	private static final int TIKU = 30582;
	// Items
	private static final int KASHA_WOLF_FUR = 90262;
	private static final int ASHES_OF_ANCESTORS = 90263;
	private static final int IMP_NECKLACE = 90264;
	private static final int MOUNTAIN_FUNGUS_SPORES = 90265;
	private static final int MARAKU_WEREWOLF_CLAW = 90266;
	private static final int EYE_OF_SEER_TEARS = 90267;
	private static final int TRIBAL_CHRONICLE = 90261;
	// Rewards
	private static final int BUTCHERS_SWORD = 49052;
	private static final int RING_NOVICE = 29497;
	private static final int NECKLACE_NOVICE = 49039;
	// Monsters
	private static final int KASHA_WOLF = 20475;
	private static final int KASHA_FOREST_WOLF = 20477;
	private static final int GOBLIN_TOMB_RAIDER = 20319;
	private static final int RAKECLAW_IMP_HUNTER = 20312;
	private static final int MOUNTAIN_FUNGUS = 20365;
	private static final int MARAKU_WEREWOLF = 20363;
	private static final int EYE_OF_SEER = 20426;
	// Misc
	private static final int MIN_LVL = 2;
	private static final int MAX_LVL = 20;
	
	public Q11019_TribalBenefit()
	{
		super(11019);
		addStartNpc(NEWBIE_GUIDE);
		addTalkId(NEWBIE_GUIDE, TIKU);
		addKillId(KASHA_WOLF, KASHA_FOREST_WOLF, GOBLIN_TOMB_RAIDER, RAKECLAW_IMP_HUNTER, MOUNTAIN_FUNGUS, MARAKU_WEREWOLF, EYE_OF_SEER);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html");
		addCondRace(Race.ORC, "no-race.html");
		registerQuestItems(TRIBAL_CHRONICLE, KASHA_WOLF_FUR, ASHES_OF_ANCESTORS, IMP_NECKLACE, MOUNTAIN_FUNGUS_SPORES, MARAKU_WEREWOLF_CLAW, EYE_OF_SEER_TEARS);
		setQuestNameNpcStringId(NpcStringId.LV_2_20_TRIBAL_BENEFIT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30602-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(7))
				{
					takeItems(player, TRIBAL_CHRONICLE, 1);
					takeItems(player, KASHA_WOLF_FUR, 10);
					takeItems(player, ASHES_OF_ANCESTORS, 10);
					takeItems(player, IMP_NECKLACE, 10);
					takeItems(player, MOUNTAIN_FUNGUS_SPORES, 10);
					takeItems(player, MARAKU_WEREWOLF_CLAW, 10);
					giveItems(player, BUTCHERS_SWORD, 1);
					giveItems(player, RING_NOVICE, 2);
					giveItems(player, NECKLACE_NOVICE, 1);
					addExpAndSp(player, 70000, 0);
					qs.exitQuest(false, true);
					htmltext = "30582-03.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (qs != null)
		{
			switch (npc.getId())
			{
				case KASHA_WOLF:
				case KASHA_FOREST_WOLF:
				{
					if ((qs.isCond(2) && (getQuestItemsCount(killer, KASHA_WOLF_FUR) < 10)))
					{
						giveItems(killer, KASHA_WOLF_FUR, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, KASHA_WOLF_FUR) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_KASHA_WOLVES_AND_KASHA_BEARS_N_GO_HUNTING_AND_KILL_GOBLIN_TOMB_RAIDERS_AND_RAKECLAW_IMP_HUNTERS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(3);
						}
					}
					break;
				}
				case GOBLIN_TOMB_RAIDER:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, ASHES_OF_ANCESTORS) < 10))
					{
						giveItems(killer, ASHES_OF_ANCESTORS, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if ((getQuestItemsCount(killer, ASHES_OF_ANCESTORS) >= 10) && (getQuestItemsCount(killer, IMP_NECKLACE) >= 10))
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_GOBLIN_TOMB_RAIDERS_AND_RAKECLAW_IMP_HUNTERS_N_GO_HUNTING_AND_KILL_MOUNTAIN_FUNGUS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(4);
						}
					}
					break;
				}
				case RAKECLAW_IMP_HUNTER:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, IMP_NECKLACE) < 10))
					{
						giveItems(killer, IMP_NECKLACE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if ((getQuestItemsCount(killer, ASHES_OF_ANCESTORS) >= 10) && (getQuestItemsCount(killer, IMP_NECKLACE) >= 10))
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_GOBLIN_TOMB_RAIDERS_AND_RAKECLAW_IMP_HUNTERS_N_GO_HUNTING_AND_KILL_MOUNTAIN_FUNGUS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(4);
						}
					}
					break;
				}
				case MOUNTAIN_FUNGUS:
				{
					if (qs.isCond(4) && (getQuestItemsCount(killer, MOUNTAIN_FUNGUS_SPORES) < 10))
					{
						giveItems(killer, MOUNTAIN_FUNGUS_SPORES, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, MOUNTAIN_FUNGUS_SPORES) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_MOUNTAIN_FUNGUS_N_GO_HUNTING_AND_KILL_MARAKU_WEREWOLVES, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(5);
						}
					}
					break;
				}
				case MARAKU_WEREWOLF:
				{
					if (qs.isCond(5) && (getQuestItemsCount(killer, MARAKU_WEREWOLF_CLAW) < 10))
					{
						giveItems(killer, MARAKU_WEREWOLF_CLAW, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, MARAKU_WEREWOLF_CLAW) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_MARAKU_WEREWOLVES_N_GO_HUNTING_AND_KILL_EYES_OF_SEER, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(6);
						}
					}
					break;
				}
				case EYE_OF_SEER:
				{
					if (qs.isCond(6) && (getQuestItemsCount(killer, EYE_OF_SEER_TEARS) < 10))
					{
						giveItems(killer, EYE_OF_SEER_TEARS, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, EYE_OF_SEER_TEARS) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_EYES_OF_SEER_NRETURN_TO_CENTURION_TIKU, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(7);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == NEWBIE_GUIDE)
				{
					htmltext = "30602-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == NEWBIE_GUIDE)
				{
					if (qs.isCond(1))
					{
						htmltext = "30602-02a.html";
					}
					break;
				}
				else if (npc.getId() == TIKU)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30582-01.htm";
							qs.setCond(2, true);
							showOnScreenMsg(talker, NpcStringId.GO_HUNTING_AND_KILL_KASHA_WOLF_AND_KASHA_FOREST_WOLF, ExShowScreenMessage.TOP_CENTER, 10000);
							giveItems(talker, TRIBAL_CHRONICLE, 1);
							break;
						}
						case 2:
						{
							htmltext = "30582-01a.html";
							break;
						}
						case 7:
						{
							htmltext = "30582-02.html";
							break;
						}
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(talker);
				break;
			}
		}
		return htmltext;
	}
}
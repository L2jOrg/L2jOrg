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
package quests.Q11013_ShilensHunt;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.quest.State;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Shilen's Hunt (11013)
 * @author Stayway
 */
public class Q11013_ShilensHunt extends Quest
{
	// NPCs
	private static final int NEWBIE_GUIDE = 30600;
	private static final int TALOS = 30141;
	// Items
	private static final int WOLF_TAIL = 90238;
	private static final int GOBLIN_POISONOUS_DART = 90239;
	private static final int IMP_WING = 90240;
	private static final int FUNGUS_JUICE = 90241;
	private static final int BALOR_ORC_FANG = 90242;
	private static final int HUNTING_PLAN = 90237;
	// Rewards
	private static final int BLOOD_SABER = 49050;
	private static final int ELDRITCH_DAGGER = 49049;
	private static final int RING_NOVICE = 29497;
	private static final int NECKLACE_NOVICE = 49039;
	// Monsters
	private static final int ASHEN_WOLVES = 20456;
	private static final int GOBLIN = 20003;
	private static final int IMP = 20004;
	private static final int IMP_ELDER = 20005;
	private static final int GREN_FUNGUS = 20007;
	private static final int BALOR_ORC_WARRIOR = 20386;
	private static final int BALOR_ORC_CAPTAIN = 20387;
	private static final int BALOR_ORC_LIEUTENANTS = 20388;
	// Misc
	private static final int MIN_LVL = 2;
	private static final int MAX_LVL = 20;
	
	public Q11013_ShilensHunt()
	{
		super(11013);
		addStartNpc(NEWBIE_GUIDE);
		addTalkId(NEWBIE_GUIDE, TALOS);
		addKillId(ASHEN_WOLVES, GOBLIN, IMP, IMP_ELDER, GREN_FUNGUS, BALOR_ORC_WARRIOR, BALOR_ORC_CAPTAIN, BALOR_ORC_LIEUTENANTS);
		addCondLevel(MIN_LVL, MAX_LVL, "");
		addCondRace(Race.DARK_ELF, "");
		registerQuestItems(HUNTING_PLAN, WOLF_TAIL, GOBLIN_POISONOUS_DART, IMP_WING, FUNGUS_JUICE, BALOR_ORC_FANG);
		setQuestNameNpcStringId(NpcStringId.LV_2_20_SHILEN_S_HUNT);
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
			case "30600-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(7))
				{
					takeItems(player, HUNTING_PLAN, 1);
					takeItems(player, WOLF_TAIL, 10);
					takeItems(player, GOBLIN_POISONOUS_DART, 10);
					takeItems(player, IMP_WING, 10);
					takeItems(player, FUNGUS_JUICE, 10);
					takeItems(player, BALOR_ORC_FANG, 10);
					giveItems(player, BLOOD_SABER, 1);
					giveItems(player, RING_NOVICE, 2);
					giveItems(player, NECKLACE_NOVICE, 1);
					addExpAndSp(player, 70000, 0);
					qs.exitQuest(false, true);
					htmltext = "30141-03.html";
				}
				break;
			}
			case "reward2":
			{
				if (qs.isCond(7))
				{
					takeItems(player, HUNTING_PLAN, 1);
					takeItems(player, WOLF_TAIL, 10);
					takeItems(player, GOBLIN_POISONOUS_DART, 10);
					takeItems(player, IMP_WING, 10);
					takeItems(player, FUNGUS_JUICE, 10);
					takeItems(player, BALOR_ORC_FANG, 10);
					giveItems(player, ELDRITCH_DAGGER, 1);
					giveItems(player, RING_NOVICE, 2);
					giveItems(player, NECKLACE_NOVICE, 1);
					addExpAndSp(player, 70000, 0);
					qs.exitQuest(false, true);
					htmltext = "30141-03.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == NEWBIE_GUIDE)
				{
					htmltext = "30600-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == NEWBIE_GUIDE)
				{
					if (qs.isCond(1))
					{
						htmltext = "30600-02a.html";
					}
					break;
				}
				else if (npc.getId() == TALOS)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30141-01.htm";
							qs.setCond(2, true);
							showOnScreenMsg(talker, NpcStringId.GO_HUNTING_AND_KILL_ASHEN_WOLVES, ExShowScreenMessage.TOP_CENTER, 10000);
							giveItems(talker, HUNTING_PLAN, 1);
							break;
						}
						case 2:
						{
							htmltext = "30141-01a.html";
							break;
						}
						case 7:
						{
							htmltext = "30141-02.html";
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
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (qs != null)
		{
			switch (npc.getId())
			{
				case ASHEN_WOLVES:
				{
					if (qs.isCond(2))
					{
						giveItems(killer, WOLF_TAIL, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, WOLF_TAIL) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_ASHEN_WOLVES_N_GO_HUNTING_AND_KILL_GOBLINS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(3);
						}
					}
					break;
				}
				case GOBLIN:
				{
					if (qs.isCond(3))
					{
						giveItems(killer, GOBLIN_POISONOUS_DART, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, GOBLIN_POISONOUS_DART) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_GOBLINS_N_GO_HUNTING_AND_KILL_IMPS_AND_IMP_ELDERS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(4);
						}
					}
					break;
				}
				case IMP:
				case IMP_ELDER:
				{
					if (qs.isCond(4))
					{
						giveItems(killer, IMP_WING, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, IMP_WING) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_IMPS_AND_IMP_ELDERS_N_GO_HUNTING_AND_KILL_GREEN_FUNGUS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(5);
						}
					}
					break;
				}
				case GREN_FUNGUS:
				{
					if (qs.isCond(5))
					{
						giveItems(killer, FUNGUS_JUICE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, FUNGUS_JUICE) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_GREEN_FUNGUS_N_GO_HUNTING_AND_KILL_BALOR_ORC_WARRIORS_BALOR_ORC_WARRIOR_CAPTAINS_AND_BALOR_ORC_WARRIOR_LIEUTENANTS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(6);
						}
					}
					break;
				}
				case BALOR_ORC_WARRIOR:
				case BALOR_ORC_CAPTAIN:
				case BALOR_ORC_LIEUTENANTS:
				{
					if (qs.isCond(6))
					{
						giveItems(killer, BALOR_ORC_FANG, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						if (getQuestItemsCount(killer, BALOR_ORC_FANG) >= 10)
						{
							showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_BALOR_ORC_WARRIORS_BALOR_ORC_WARRIOR_CAPTAINS_AND_BALOR_ORC_WARRIOR_LIEUTENANTS_NRETURN_TO_HIERARCH_TALOS, ExShowScreenMessage.TOP_CENTER, 10000);
							qs.setCond(7);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
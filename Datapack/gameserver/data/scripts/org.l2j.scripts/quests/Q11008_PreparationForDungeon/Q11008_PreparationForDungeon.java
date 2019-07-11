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
package quests.Q11008_PreparationForDungeon;

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
 * Preparation for Dungeon (11008)
 * @author Stayway
 */
public class Q11008_PreparationForDungeon extends Quest
{
	// NPCs
	private static final int KENDELL = 30218;
	private static final int STARDEN = 30220;
	// Items
	private static final int ORCS_BANDAGE = 90223;
	private static final int DRYADS_CRIMSON_HERB = 90224;
	private static final int SPIDER_VENOM = 90225;
	private static final int NOTE_ABOUT_REQUIRED_INGREDIENTS = 90222; // Need finish htm
	// Rewards
	private static final int WARRIORS_ARMOR = 90306;
	private static final int WARRIORS_GAITERS = 90307;
	private static final int MEDIUMS_TUNIC = 90308;
	private static final int MEDIUMS_STOCKINGS = 90309;
	private static final int RING_NOVICE = 29497;
	// Monsters
	private static final int KABOO_ORC_WARRIOR_CAPTAIN = 20472;
	private static final int KABOO_ORC_WARRIOR_LIEUTENANT = 20473;
	private static final int KABOO_ORC_WARRIOR = 20471;
	private static final int DRYAD = 20013;
	private static final int DRYAD_ELDER = 20019;
	private static final int HOOK_SPIDER = 20308;
	private static final int CRIMSON_SPIDER = 20460;
	private static final int PINCER_SPIDER = 20466;
	// Misc
	private static final int MIN_LVL = 11;
	private static final int MAX_LVL = 20;
	
	public Q11008_PreparationForDungeon()
	{
		super(11008);
		addStartNpc(KENDELL);
		addTalkId(KENDELL, STARDEN);
		addKillId(KABOO_ORC_WARRIOR, KABOO_ORC_WARRIOR_CAPTAIN, KABOO_ORC_WARRIOR_LIEUTENANT, DRYAD, DRYAD_ELDER, HOOK_SPIDER, CRIMSON_SPIDER, PINCER_SPIDER);
		addCondLevel(MIN_LVL, MAX_LVL, "no-level.html"); // Custom
		addCondRace(Race.ELF, "no-race.html"); // Custom
		registerQuestItems(NOTE_ABOUT_REQUIRED_INGREDIENTS, ORCS_BANDAGE, DRYADS_CRIMSON_HERB, SPIDER_VENOM);
		setQuestNameNpcStringId(NpcStringId.LV_11_20_PREPARATION_FOR_DUNGEON);
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
			case "30218-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "reward1":
			{
				if (qs.isCond(5))
				{
					takeItems(player, NOTE_ABOUT_REQUIRED_INGREDIENTS, 1);
					takeItems(player, ORCS_BANDAGE, 20);
					takeItems(player, DRYADS_CRIMSON_HERB, 20);
					takeItems(player, SPIDER_VENOM, 20);
					giveItems(player, WARRIORS_ARMOR, 1);
					giveItems(player, WARRIORS_GAITERS, 1);
					giveItems(player, RING_NOVICE, 2);
					addExpAndSp(player, 80000, 0);
					qs.exitQuest(false, true);
					htmltext = "30220-03.html";
				}
				break;
			}
			case "reward2":
			{
				if (qs.isCond(5))
				{
					takeItems(player, NOTE_ABOUT_REQUIRED_INGREDIENTS, 1);
					takeItems(player, ORCS_BANDAGE, 20);
					takeItems(player, DRYADS_CRIMSON_HERB, 20);
					takeItems(player, SPIDER_VENOM, 20);
					giveItems(player, MEDIUMS_TUNIC, 1);
					giveItems(player, MEDIUMS_STOCKINGS, 1);
					giveItems(player, RING_NOVICE, 2);
					addExpAndSp(player, 80000, 0);
					qs.exitQuest(false, true);
					htmltext = "30220-04.html"; // Custom
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
				if (npc.getId() == KENDELL)
				{
					htmltext = "30218-01.html";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == KENDELL)
				{
					if (qs.isCond(1))
					{
						htmltext = "30218-02a.html";
					}
					break;
				}
				else if (npc.getId() == STARDEN)
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30220-01.htm";
							qs.setCond(2, true);
							showOnScreenMsg(talker, NpcStringId.GO_HUNTING_AND_KILL_KABOO_ORC_WARRIOR_LIEUTENANTS_AND_KABOO_ORC_WARRIOR_CAPTAINS, ExShowScreenMessage.TOP_CENTER, 10000);
							giveItems(talker, NOTE_ABOUT_REQUIRED_INGREDIENTS, 1);
							break;
						}
						case 2:
						{
							htmltext = "30220-01a.html";
							break;
						}
						case 5:
						{
							htmltext = "30220-02.html";
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
				case KABOO_ORC_WARRIOR:
				case KABOO_ORC_WARRIOR_CAPTAIN:
				case KABOO_ORC_WARRIOR_LIEUTENANT:
				{
					if (qs.isCond(2) && (getQuestItemsCount(killer, ORCS_BANDAGE) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, ORCS_BANDAGE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, ORCS_BANDAGE) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_KABOO_ORC_WARRIOR_LIEUTENANTS_AND_KABOO_ORC_WARRIOR_CAPTAINS_N_GO_HUNTING_AND_KILL_DRYADS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(3);
							}
						}
					}
					break;
				}
				case DRYAD:
				case DRYAD_ELDER:
				{
					if (qs.isCond(3) && (getQuestItemsCount(killer, DRYADS_CRIMSON_HERB) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, DRYADS_CRIMSON_HERB, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, DRYADS_CRIMSON_HERB) >= 20)
							{
								showOnScreenMsg(killer, NpcStringId.YOU_HAVE_KILLED_ENOUGH_DRYADS_N_GO_HUNTING_AND_KILL_CRIMSON_SPIDERS_HOOK_SPIDERS_AND_PINCER_SPIDERS, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(4);
							}
						}
					}
					break;
				}
				case HOOK_SPIDER:
				case CRIMSON_SPIDER:
				case PINCER_SPIDER:
				{
					if (qs.isCond(4) && (getQuestItemsCount(killer, SPIDER_VENOM) < 20))
					{
						if (getRandom(100) < 90)
						{
							giveItems(killer, SPIDER_VENOM, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, SPIDER_VENOM) >= 20))
							{
								showOnScreenMsg(killer, NpcStringId.RETURN_TO_SENTINEL_STARDEN, ExShowScreenMessage.TOP_CENTER, 10000);
								qs.setCond(5);
							}
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
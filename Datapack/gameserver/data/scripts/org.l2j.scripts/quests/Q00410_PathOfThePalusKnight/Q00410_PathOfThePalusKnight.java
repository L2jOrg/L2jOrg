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
package quests.Q00410_PathOfThePalusKnight;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Path Of The Palus Knight (410)
 * @author ivantotov
 */
public final class Q00410_PathOfThePalusKnight extends Quest
{
	// NPCs
	private static final int MASTER_VIRGIL = 30329;
	private static final int KALINTA = 30422;
	// Items
	private static final int PALLUS_TALISMAN = 1237;
	private static final int LYCANTHROPE_SKULL = 1238;
	private static final int VIRGILS_LETTER = 1239;
	private static final int MORTE_TALISMAN = 1240;
	private static final int VENOMOUS_SPIDERS_CARAPACE = 1241;
	private static final int ARACHNID_TRACKER_SILK = 1242;
	private static final int COFFIN_OF_ETERNAL_REST = 1243;
	// Reward
	private static final int GAZE_OF_ABYSS = 1244;
	// Monster
	private static final int VENOMOUS_SPIDER = 20038;
	private static final int ARACHNID_TRACKER = 20043;
	private static final int LYCANTHROPE = 20049;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q00410_PathOfThePalusKnight()
	{
		super(410);
		addStartNpc(MASTER_VIRGIL);
		addTalkId(MASTER_VIRGIL, KALINTA);
		addKillId(VENOMOUS_SPIDER, ARACHNID_TRACKER, LYCANTHROPE);
		registerQuestItems(PALLUS_TALISMAN, LYCANTHROPE_SKULL, VIRGILS_LETTER, MORTE_TALISMAN, VENOMOUS_SPIDERS_CARAPACE, ARACHNID_TRACKER_SILK, COFFIN_OF_ETERNAL_REST);
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
			case "ACCEPT":
			{
				if (player.getClassId() == ClassId.DARK_FIGHTER)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, GAZE_OF_ABYSS))
						{
							htmltext = "30329-04.htm";
						}
						else
						{
							htmltext = "30329-05.htm";
						}
					}
					else
					{
						htmltext = "30329-02.htm";
					}
				}
				else if (player.getClassId() == ClassId.PALUS_KNIGHT)
				{
					htmltext = "30329-02a.htm";
				}
				else
				{
					htmltext = "30329-03.htm";
				}
				break;
			}
			case "30329-06.htm":
			{
				qs.startQuest();
				giveItems(player, PALLUS_TALISMAN, 1);
				htmltext = event;
				break;
			}
			case "30329-10.html":
			{
				if (hasQuestItems(player, PALLUS_TALISMAN, LYCANTHROPE_SKULL))
				{
					takeItems(player, PALLUS_TALISMAN, 1);
					takeItems(player, LYCANTHROPE_SKULL, -1);
					giveItems(player, VIRGILS_LETTER, 1);
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30422-02.html":
			{
				if (hasQuestItems(player, VIRGILS_LETTER))
				{
					takeItems(player, VIRGILS_LETTER, 1);
					giveItems(player, MORTE_TALISMAN, 1);
					qs.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "30422-06.html":
			{
				if (hasQuestItems(player, MORTE_TALISMAN, ARACHNID_TRACKER_SILK, VENOMOUS_SPIDERS_CARAPACE))
				{
					takeItems(player, MORTE_TALISMAN, 1);
					takeItems(player, VENOMOUS_SPIDERS_CARAPACE, 1);
					takeItems(player, ARACHNID_TRACKER_SILK, -1);
					giveItems(player, COFFIN_OF_ETERNAL_REST, 1);
					qs.setCond(6, true);
					htmltext = event;
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
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case VENOMOUS_SPIDER:
				{
					if (hasQuestItems(killer, MORTE_TALISMAN) && (getQuestItemsCount(killer, VENOMOUS_SPIDERS_CARAPACE) < 1))
					{
						giveItems(killer, VENOMOUS_SPIDERS_CARAPACE, 1);
						if (getQuestItemsCount(killer, ARACHNID_TRACKER_SILK) >= 5)
						{
							qs.setCond(5, true);
						}
					}
					break;
				}
				case ARACHNID_TRACKER:
				{
					if (hasQuestItems(killer, MORTE_TALISMAN) && (getQuestItemsCount(killer, ARACHNID_TRACKER_SILK) < 5))
					{
						giveItems(killer, ARACHNID_TRACKER_SILK, 1);
						if (getQuestItemsCount(killer, ARACHNID_TRACKER_SILK) == 5)
						{
							if ((getQuestItemsCount(killer, ARACHNID_TRACKER_SILK) >= 4) && hasQuestItems(killer, VENOMOUS_SPIDERS_CARAPACE))
							{
								qs.setCond(5, true);
							}
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case LYCANTHROPE:
				{
					if (hasQuestItems(killer, PALLUS_TALISMAN) && (getQuestItemsCount(killer, LYCANTHROPE_SKULL) < 13))
					{
						giveItems(killer, LYCANTHROPE_SKULL, 1);
						if (getQuestItemsCount(killer, LYCANTHROPE_SKULL) == 13)
						{
							qs.setCond(2, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == MASTER_VIRGIL)
			{
				htmltext = "30329-01.htm";
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == MASTER_VIRGIL)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case MASTER_VIRGIL:
				{
					if (hasQuestItems(player, PALLUS_TALISMAN))
					{
						if (!hasQuestItems(player, LYCANTHROPE_SKULL))
						{
							htmltext = "30329-07.html";
						}
						else if (hasQuestItems(player, LYCANTHROPE_SKULL) && (getQuestItemsCount(player, LYCANTHROPE_SKULL) < 13))
						{
							htmltext = "30329-08.html";
						}
						else
						{
							htmltext = "30329-09.html";
						}
					}
					else if (hasQuestItems(player, COFFIN_OF_ETERNAL_REST))
					{
						giveItems(player, GAZE_OF_ABYSS, 1);
						final int level = player.getLevel();
						if (level >= 20)
						{
							addExpAndSp(player, 80314, 5087);
						}
						else if (level == 19)
						{
							addExpAndSp(player, 80314, 5087);
						}
						else
						{
							addExpAndSp(player, 80314, 5087);
						}
						qs.exitQuest(false, true);
						player.sendPacket(new SocialAction(player.getObjectId(), 3));
						htmltext = "30329-11.html";
					}
					else if (hasAtLeastOneQuestItem(player, VIRGILS_LETTER, MORTE_TALISMAN))
					{
						htmltext = "30329-12.html";
					}
					break;
				}
				case KALINTA:
				{
					if (hasQuestItems(player, VIRGILS_LETTER))
					{
						htmltext = "30422-01.html";
					}
					else if (hasQuestItems(player, MORTE_TALISMAN))
					{
						if (!hasQuestItems(player, ARACHNID_TRACKER_SILK, VENOMOUS_SPIDERS_CARAPACE))
						{
							htmltext = "30422-03.html";
						}
						else if (!hasQuestItems(player, ARACHNID_TRACKER_SILK) && hasQuestItems(player, VENOMOUS_SPIDERS_CARAPACE))
						{
							htmltext = "30422-04.html";
						}
						else if ((getQuestItemsCount(player, ARACHNID_TRACKER_SILK) >= 5) && hasQuestItems(player, VENOMOUS_SPIDERS_CARAPACE))
						{
							htmltext = "30422-05.html";
						}
						else if (hasQuestItems(player, ARACHNID_TRACKER_SILK, VENOMOUS_SPIDERS_CARAPACE))
						{
							htmltext = "30422-04.html";
						}
					}
					else if (hasQuestItems(player, COFFIN_OF_ETERNAL_REST))
					{
						htmltext = "30422-06.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}
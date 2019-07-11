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
package quests.Q00412_PathOfTheDarkWizard;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Path Of The Dark Wizard (412)
 * @author ivantotov
 */
public final class Q00412_PathOfTheDarkWizard extends Quest
{
	// NPCs
	private static final int CHARKEREN = 30415;
	private static final int ANNIKA = 30418;
	private static final int ARKENIA = 30419;
	private static final int VARIKA = 30421;
	// Items
	private static final int SEEDS_OF_ANGER = 1253;
	private static final int SEEDS_OF_DESPAIR = 1254;
	private static final int SEEDS_OF_HORROR = 1255;
	private static final int SEEDS_OF_LUNACY = 1256;
	private static final int FAMILYS_REMAINS = 1257;
	private static final int KNEE_BONE = 1259;
	private static final int HEART_OF_LUNACY = 1260;
	private static final int LUCKY_KEY = 1277;
	private static final int CANDLE = 1278;
	private static final int HUB_SCENT = 1279;
	// Reward
	private static final int JEWEL_OF_DARKNESS = 1261;
	// Monster
	private static final int MARSH_ZOMBIE = 20015;
	private static final int MISERY_SKELETON = 20022;
	private static final int SKELETON_SCOUT = 20045;
	private static final int SKELETON_HUNTER = 20517;
	private static final int SKELETON_HUNTER_ARCHER = 20518;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q00412_PathOfTheDarkWizard()
	{
		super(412);
		addStartNpc(VARIKA);
		addTalkId(VARIKA, CHARKEREN, ANNIKA, ARKENIA);
		addKillId(MARSH_ZOMBIE, MISERY_SKELETON, SKELETON_SCOUT, SKELETON_HUNTER, SKELETON_HUNTER_ARCHER);
		registerQuestItems(SEEDS_OF_ANGER, SEEDS_OF_DESPAIR, SEEDS_OF_HORROR, SEEDS_OF_LUNACY, FAMILYS_REMAINS, KNEE_BONE, HEART_OF_LUNACY, LUCKY_KEY, CANDLE, HUB_SCENT);
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
			case "ACCEPT":
			{
				if (player.getClassId() == ClassId.DARK_MAGE)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, JEWEL_OF_DARKNESS))
						{
							htmltext = "30421-04.htm";
						}
						else
						{
							qs.startQuest();
							giveItems(player, SEEDS_OF_DESPAIR, 1);
							htmltext = "30421-05.htm";
						}
					}
					else
					{
						htmltext = "30421-02.htm";
					}
				}
				else if (player.getClassId() == ClassId.DARK_WIZARD)
				{
					htmltext = "30421-02a.htm";
				}
				else
				{
					htmltext = "30421-03.htm";
				}
				break;
			}
			case "30421-06.html":
			{
				if (hasQuestItems(player, SEEDS_OF_ANGER))
				{
					htmltext = event;
				}
				else
				{
					htmltext = "30421-07.html";
				}
				break;
			}
			case "30421-09.html":
			{
				if (hasQuestItems(player, SEEDS_OF_HORROR))
				{
					htmltext = event;
				}
				else
				{
					htmltext = "30421-10.html";
				}
				break;
			}
			case "30421-11.html":
			{
				if (hasQuestItems(player, SEEDS_OF_LUNACY))
				{
					htmltext = event;
				}
				else if (!hasQuestItems(player, SEEDS_OF_LUNACY) && hasQuestItems(player, SEEDS_OF_DESPAIR))
				{
					htmltext = "30421-12.html";
				}
				break;
			}
			case "30421-08.html":
			case "30415-02.html":
			{
				htmltext = event;
				break;
			}
			case "30415-03.html":
			{
				giveItems(player, LUCKY_KEY, 1);
				htmltext = event;
				break;
			}
			case "30418-02.html":
			{
				giveItems(player, CANDLE, 1);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case MARSH_ZOMBIE:
				{
					if (hasQuestItems(killer, LUCKY_KEY) && (getQuestItemsCount(killer, FAMILYS_REMAINS) < 3))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, FAMILYS_REMAINS, 1);
							if (getQuestItemsCount(killer, FAMILYS_REMAINS) == 3)
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							}
							else
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
					}
					break;
				}
				case MISERY_SKELETON:
				case SKELETON_HUNTER:
				case SKELETON_HUNTER_ARCHER:
				{
					if (hasQuestItems(killer, CANDLE) && (getQuestItemsCount(killer, KNEE_BONE) < 2))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, KNEE_BONE, 1);
							if (getQuestItemsCount(killer, KNEE_BONE) == 2)
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							}
							else
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
					}
					break;
				}
				case SKELETON_SCOUT:
				{
					if (hasQuestItems(killer, HUB_SCENT) && (getQuestItemsCount(killer, HEART_OF_LUNACY) < 3))
					{
						if (getRandom(2) == 0)
						{
							giveItems(killer, HEART_OF_LUNACY, 1);
							if (getQuestItemsCount(killer, HEART_OF_LUNACY) == 3)
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							}
							else
							{
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == VARIKA)
			{
				if (!hasQuestItems(player, JEWEL_OF_DARKNESS))
				{
					htmltext = "30421-01.htm";
				}
				else
				{
					htmltext = "30421-04.htm";
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == VARIKA)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case VARIKA:
				{
					if (hasQuestItems(player, SEEDS_OF_DESPAIR, SEEDS_OF_HORROR, SEEDS_OF_LUNACY, SEEDS_OF_ANGER))
					{
						giveItems(player, JEWEL_OF_DARKNESS, 1);
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
						htmltext = "30421-13.html";
					}
					else if (hasQuestItems(player, SEEDS_OF_DESPAIR))
					{
						if (!hasAtLeastOneQuestItem(player, FAMILYS_REMAINS, LUCKY_KEY, CANDLE, HUB_SCENT, KNEE_BONE, HEART_OF_LUNACY))
						{
							htmltext = "30421-14.html";
						}
						else if (!hasQuestItems(player, SEEDS_OF_ANGER))
						{
							htmltext = "30421-08.html";
						}
						else if (!hasQuestItems(player, SEEDS_OF_HORROR))
						{
							htmltext = "30421-15.html";
						}
						else if (!hasQuestItems(player, SEEDS_OF_LUNACY))
						{
							htmltext = "30421-12.html";
						}
					}
					break;
				}
				case CHARKEREN:
				{
					if (!hasQuestItems(player, SEEDS_OF_ANGER) && hasQuestItems(player, SEEDS_OF_DESPAIR))
					{
						if (!hasAtLeastOneQuestItem(player, FAMILYS_REMAINS, LUCKY_KEY))
						{
							htmltext = "30415-01.html";
						}
						else if (hasQuestItems(player, LUCKY_KEY) && (getQuestItemsCount(player, FAMILYS_REMAINS) < 3))
						{
							htmltext = "30415-04.html";
						}
						else
						{
							giveItems(player, SEEDS_OF_ANGER, 1);
							takeItems(player, FAMILYS_REMAINS, -1);
							takeItems(player, LUCKY_KEY, 1);
							htmltext = "30415-05.html";
						}
					}
					else
					{
						htmltext = "30415-06.html";
					}
					break;
				}
				case ANNIKA:
				{
					if (!hasQuestItems(player, SEEDS_OF_HORROR) && hasQuestItems(player, SEEDS_OF_DESPAIR))
					{
						if (!hasAtLeastOneQuestItem(player, CANDLE, KNEE_BONE))
						{
							htmltext = "30418-01.html";
						}
						else if (hasQuestItems(player, CANDLE) && (getQuestItemsCount(player, KNEE_BONE) < 2))
						{
							htmltext = "30418-03.html";
						}
						else
						{
							giveItems(player, SEEDS_OF_HORROR, 1);
							takeItems(player, KNEE_BONE, -1);
							takeItems(player, CANDLE, 1);
							htmltext = "30418-04.html";
						}
					}
					break;
				}
				case ARKENIA:
				{
					if (!hasQuestItems(player, SEEDS_OF_LUNACY))
					{
						if (!hasAtLeastOneQuestItem(player, HUB_SCENT, HEART_OF_LUNACY))
						{
							giveItems(player, HUB_SCENT, 1);
							htmltext = "30419-01.html";
						}
						else if (hasQuestItems(player, HUB_SCENT) && (getQuestItemsCount(player, HEART_OF_LUNACY) < 3))
						{
							htmltext = "30419-02.html";
						}
						else
						{
							giveItems(player, SEEDS_OF_LUNACY, 1);
							takeItems(player, HEART_OF_LUNACY, -1);
							takeItems(player, HUB_SCENT, 1);
							htmltext = "30419-03.html";
						}
					}
					break;
				}
			}
		}
		return htmltext;
	}
}
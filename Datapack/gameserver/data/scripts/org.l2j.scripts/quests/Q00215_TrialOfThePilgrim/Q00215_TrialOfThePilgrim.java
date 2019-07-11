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
package quests.Q00215_TrialOfThePilgrim;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Trial Of The Pilgrim (215)
 * @author ivantotov
 */
public final class Q00215_TrialOfThePilgrim extends Quest
{
	// NPCs
	private static final int PRIEST_PETRON = 30036;
	private static final int PRIEST_PRIMOS = 30117;
	private static final int ANDELLIA = 30362;
	private static final int GAURI_TWINKLEROCK = 30550;
	private static final int SEER_TANAPI = 30571;
	private static final int ELDER_CASIAN = 30612;
	private static final int HERMIT_SANTIAGO = 30648;
	private static final int ANCESTOR_MARTANKUS = 30649;
	private static final int PRIEST_OF_THE_EARTH_GERALD = 30650;
	private static final int WANDERER_DORF = 30651;
	private static final int URUHA = 30652;
	// Items
	private static final int ADENA = 57;
	private static final int BOOK_OF_SAGE = 2722;
	private static final int VOUCHER_OF_TRIAL = 2723;
	private static final int SPIRIT_OF_FLAME = 2724;
	private static final int ESSENSE_OF_FLAME = 2725;
	private static final int BOOK_OF_GERALD = 2726;
	private static final int GREY_BADGE = 2727;
	private static final int PICTURE_OF_NAHIR = 2728;
	private static final int HAIR_OF_NAHIR = 2729;
	private static final int STATUE_OF_EINHASAD = 2730;
	private static final int BOOK_OF_DARKNESS = 2731;
	private static final int DEBRIS_OF_WILLOW = 2732;
	private static final int TAG_OF_RUMOR = 2733;
	// Reward
	private static final int MARK_OF_PILGRIM = 2721;
	// Quest Monster
	private static final int LAVA_SALAMANDER = 27116;
	private static final int NAHIR = 27117;
	private static final int BLACK_WILLOW = 27118;
	// Misc
	private static final int MIN_LVL = 35;
	
	public Q00215_TrialOfThePilgrim()
	{
		super(215);
		addStartNpc(HERMIT_SANTIAGO);
		addTalkId(HERMIT_SANTIAGO, PRIEST_PETRON, PRIEST_PRIMOS, ANDELLIA, GAURI_TWINKLEROCK, SEER_TANAPI, ELDER_CASIAN, ANCESTOR_MARTANKUS, PRIEST_OF_THE_EARTH_GERALD, WANDERER_DORF, URUHA);
		addKillId(LAVA_SALAMANDER, NAHIR, BLACK_WILLOW);
		registerQuestItems(BOOK_OF_SAGE, VOUCHER_OF_TRIAL, SPIRIT_OF_FLAME, ESSENSE_OF_FLAME, BOOK_OF_GERALD, GREY_BADGE, PICTURE_OF_NAHIR, HAIR_OF_NAHIR, STATUE_OF_EINHASAD, BOOK_OF_DARKNESS, DEBRIS_OF_WILLOW, TAG_OF_RUMOR);
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
				if (qs.isCreated())
				{
					qs.startQuest();
					qs.setMemoState(1);
					giveItems(player, VOUCHER_OF_TRIAL, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				}
				break;
			}
			case "30648-05.html":
			case "30648-06.html":
			case "30648-07.html":
			case "30648-08.html":
			{
				htmltext = event;
				break;
			}
			case "30362-05.html":
			{
				if (qs.isMemoState(15) && hasQuestItems(player, BOOK_OF_DARKNESS))
				{
					takeItems(player, BOOK_OF_DARKNESS, 1);
					qs.setMemoState(16);
					qs.setCond(16, true);
					htmltext = event;
				}
				break;
			}
			case "30362-04.html":
			{
				if (qs.isMemoState(15) && hasQuestItems(player, BOOK_OF_DARKNESS))
				{
					qs.setMemoState(16);
					qs.setCond(16, true);
					htmltext = event;
				}
				break;
			}
			case "30649-04.html":
			{
				if (qs.isMemoState(4) && hasQuestItems(player, ESSENSE_OF_FLAME))
				{
					giveItems(player, SPIRIT_OF_FLAME, 1);
					takeItems(player, ESSENSE_OF_FLAME, 1);
					qs.setMemoState(5);
					qs.setCond(5, true);
					htmltext = event;
				}
				break;
			}
			case "30650-02.html":
			{
				if (qs.isMemoState(6) && hasQuestItems(player, TAG_OF_RUMOR))
				{
					if (getQuestItemsCount(player, ADENA) >= 100000)
					{
						giveItems(player, BOOK_OF_GERALD, 1);
						takeItems(player, ADENA, 100000);
						qs.setMemoState(7);
						htmltext = event;
					}
					else
					{
						htmltext = "30650-03.html";
					}
				}
				break;
			}
			case "30650-03.html":
			{
				if (qs.isMemoState(6) && hasQuestItems(player, TAG_OF_RUMOR))
				{
					htmltext = event;
				}
				break;
			}
			case "30652-02.html":
			{
				if (qs.isMemoState(14) && hasQuestItems(player, DEBRIS_OF_WILLOW))
				{
					giveItems(player, BOOK_OF_DARKNESS, 1);
					takeItems(player, DEBRIS_OF_WILLOW, 1);
					qs.setMemoState(15);
					qs.setCond(15, true);
					htmltext = event;
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
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case LAVA_SALAMANDER:
				{
					if (qs.isMemoState(3) && !hasQuestItems(killer, ESSENSE_OF_FLAME))
					{
						qs.setMemoState(4);
						qs.setCond(4, true);
						giveItems(killer, ESSENSE_OF_FLAME, 1);
					}
					break;
				}
				case NAHIR:
				{
					if (qs.isMemoState(10) && !hasQuestItems(killer, HAIR_OF_NAHIR))
					{
						qs.setMemoState(11);
						qs.setCond(11, true);
						giveItems(killer, HAIR_OF_NAHIR, 1);
					}
					break;
				}
				case BLACK_WILLOW:
				{
					if (qs.isMemoState(13) && !hasQuestItems(killer, DEBRIS_OF_WILLOW))
					{
						qs.setMemoState(14);
						qs.setCond(14, true);
						giveItems(killer, DEBRIS_OF_WILLOW, 1);
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
		final int memoState = qs.getMemoState();
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == HERMIT_SANTIAGO)
			{
				if (!player.isInCategory(CategoryType.HEAL_GROUP))
				{
					htmltext = "30648-02.html";
				}
				else if (player.getLevel() < MIN_LVL)
				{
					htmltext = "30648-01.html";
				}
				else
				{
					htmltext = "30648-03.htm";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case HERMIT_SANTIAGO:
				{
					if (memoState >= 1)
					{
						if (!hasQuestItems(player, BOOK_OF_SAGE))
						{
							htmltext = "30648-09.html";
						}
						else
						{
							giveAdena(player, 229298, true);
							giveItems(player, MARK_OF_PILGRIM, 1);
							addExpAndSp(player, 1258250, 81606);
							qs.exitQuest(false, true);
							player.sendPacket(new SocialAction(player.getObjectId(), 3));
							htmltext = "30648-10.html";
						}
					}
					break;
				}
				case PRIEST_PETRON:
				{
					if (memoState == 9)
					{
						giveItems(player, PICTURE_OF_NAHIR, 1);
						qs.setMemoState(10);
						qs.setCond(10, true);
						htmltext = "30036-01.html";
					}
					else if (memoState == 10)
					{
						htmltext = "30036-02.html";
					}
					else if (memoState == 11)
					{
						takeItems(player, PICTURE_OF_NAHIR, 1);
						takeItems(player, HAIR_OF_NAHIR, 1);
						giveItems(player, STATUE_OF_EINHASAD, 1);
						qs.setMemoState(12);
						qs.setCond(12, true);
						htmltext = "30036-03.html";
					}
					else if (memoState == 12)
					{
						if (hasQuestItems(player, STATUE_OF_EINHASAD))
						{
							htmltext = "30036-04.html";
						}
					}
					break;
				}
				case PRIEST_PRIMOS:
				{
					if (memoState == 8)
					{
						qs.setMemoState(9);
						qs.setCond(9, true);
						htmltext = "30117-01.html";
					}
					else if (memoState == 9)
					{
						qs.setMemoState(9);
						qs.setCond(9, true);
						htmltext = "30117-02.html";
					}
					break;
				}
				case ANDELLIA:
				{
					if (memoState == 12)
					{
						if (player.getLevel() >= 0)
						{
							qs.setMemoState(13);
							qs.setCond(13, true);
							htmltext = "30362-01.html";
						}
						else
						{
							htmltext = "30362-01a.html";
						}
					}
					else if (memoState == 13)
					{
						htmltext = "30362-02.html";
					}
					else if (memoState == 14)
					{
						htmltext = "30362-02a.html";
					}
					else if (memoState == 15)
					{
						if (hasQuestItems(player, BOOK_OF_DARKNESS))
						{
							htmltext = "30362-03.html";
						}
						else
						{
							htmltext = "30362-07.html";
						}
					}
					else if (memoState == 16)
					{
						htmltext = "30362-06.html";
					}
					break;
				}
				case GAURI_TWINKLEROCK:
				{
					if (memoState == 5)
					{
						if (hasQuestItems(player, SPIRIT_OF_FLAME))
						{
							takeItems(player, SPIRIT_OF_FLAME, 1);
							giveItems(player, TAG_OF_RUMOR, 1);
							qs.setMemoState(6);
							qs.setCond(7, true);
							htmltext = "30550-01.html";
						}
					}
					else if (memoState == 6)
					{
						htmltext = "30550-02.html";
					}
					break;
				}
				case SEER_TANAPI:
				{
					if (memoState == 1)
					{
						if (hasQuestItems(player, VOUCHER_OF_TRIAL))
						{
							takeItems(player, VOUCHER_OF_TRIAL, 1);
							qs.setMemoState(2);
							qs.setCond(2, true);
							htmltext = "30571-01.html";
						}
					}
					else if (memoState == 2)
					{
						htmltext = "30571-02.html";
					}
					else if (memoState == 5)
					{
						if (hasQuestItems(player, SPIRIT_OF_FLAME))
						{
							qs.setCond(6, true);
							htmltext = "30571-03.html";
						}
					}
					break;
				}
				case ELDER_CASIAN:
				{
					if (memoState == 16)
					{
						qs.setMemoState(17);
						if (!hasQuestItems(player, BOOK_OF_SAGE))
						{
							giveItems(player, BOOK_OF_SAGE, 1);
						}
						takeItems(player, GREY_BADGE, 1);
						takeItems(player, SPIRIT_OF_FLAME, 1);
						takeItems(player, STATUE_OF_EINHASAD, 1);
						if (hasQuestItems(player, BOOK_OF_DARKNESS))
						{
							addExpAndSp(player, 5000, 500);
							takeItems(player, BOOK_OF_DARKNESS, 1);
						}
						htmltext = "30612-01.html";
						
					}
					else if (memoState == 17)
					{
						qs.setCond(17, true);
						htmltext = "30612-02.html";
					}
					break;
				}
				case ANCESTOR_MARTANKUS:
				{
					if (memoState == 2)
					{
						qs.setMemoState(3);
						qs.setCond(3, true);
						htmltext = "30649-01.html";
					}
					else if (memoState == 3)
					{
						htmltext = "30649-02.html";
					}
					else if (memoState == 4)
					{
						if (hasQuestItems(player, ESSENSE_OF_FLAME))
						{
							htmltext = "30649-03.html";
						}
					}
					break;
				}
				case PRIEST_OF_THE_EARTH_GERALD:
				{
					if (memoState == 6)
					{
						if (hasQuestItems(player, TAG_OF_RUMOR))
						{
							htmltext = "30650-01.html";
						}
					}
					else if (hasQuestItems(player, GREY_BADGE, BOOK_OF_GERALD))
					{
						giveAdena(player, 100000, true);
						takeItems(player, BOOK_OF_GERALD, 1);
						htmltext = "30650-04.html";
					}
					break;
				}
				case WANDERER_DORF:
				{
					if (memoState == 6)
					{
						if (hasQuestItems(player, TAG_OF_RUMOR))
						{
							giveItems(player, GREY_BADGE, 1);
							takeItems(player, TAG_OF_RUMOR, 1);
							qs.setMemoState(8);
							htmltext = "30651-01.html";
						}
					}
					else if (memoState == 7)
					{
						if (hasQuestItems(player, TAG_OF_RUMOR))
						{
							giveItems(player, GREY_BADGE, 1);
							takeItems(player, TAG_OF_RUMOR, 1);
							qs.setMemoState(8);
							htmltext = "30651-02.html";
						}
					}
					else if (memoState == 8)
					{
						qs.setCond(8, true);
						htmltext = "30651-03.html";
					}
					break;
				}
				case URUHA:
				{
					if (memoState == 14)
					{
						if (hasQuestItems(player, DEBRIS_OF_WILLOW))
						{
							htmltext = "30652-01.html";
						}
					}
					else if (memoState == 15)
					{
						if (hasQuestItems(player, BOOK_OF_DARKNESS))
						{
							htmltext = "30652-03.html";
						}
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == HERMIT_SANTIAGO)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}

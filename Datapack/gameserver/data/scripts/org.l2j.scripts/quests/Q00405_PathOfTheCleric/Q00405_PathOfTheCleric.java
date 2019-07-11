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
package quests.Q00405_PathOfTheCleric;

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
 * Path Of The Cleric (405)
 * @author ivantotov
 */
public final class Q00405_PathOfTheCleric extends Quest
{
	// NPCs
	private static final int GALLINT = 30017;
	private static final int ZIGAUNT = 30022;
	private static final int VIVYAN = 30030;
	private static final int TRADER_SIMPLON = 30253;
	private static final int GUARD_PRAGA = 30333;
	private static final int LIONEL = 30408;
	// Items
	private static final int LETTER_OF_ORDER_1ST = 1191;
	private static final int LETTER_OF_ORDER_2ND = 1192;
	private static final int LIONELS_BOOK = 1193;
	private static final int BOOK_OF_VIVYAN = 1194;
	private static final int BOOK_OF_SIMPLON = 1195;
	private static final int BOOK_OF_PRAGA = 1196;
	private static final int CERTIFICATE_OF_GALLINT = 1197;
	private static final int PENDANT_OF_MOTHER = 1198;
	private static final int NECKLACE_OF_MOTHER = 1199;
	private static final int LEMONIELLS_COVENANT = 1200;
	// Reward
	private static final int MARK_OF_FAITH = 1201;
	// Monster
	private static final int RUIN_ZOMBIE = 20026;
	private static final int RUIN_ZOMBIE_LEADER = 20029;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q00405_PathOfTheCleric()
	{
		super(405);
		addStartNpc(ZIGAUNT);
		addTalkId(ZIGAUNT, GALLINT, VIVYAN, TRADER_SIMPLON, GUARD_PRAGA, LIONEL);
		addKillId(RUIN_ZOMBIE, RUIN_ZOMBIE_LEADER);
		registerQuestItems(LETTER_OF_ORDER_1ST, LETTER_OF_ORDER_2ND, LIONELS_BOOK, BOOK_OF_VIVYAN, BOOK_OF_SIMPLON, BOOK_OF_PRAGA, CERTIFICATE_OF_GALLINT, PENDANT_OF_MOTHER, NECKLACE_OF_MOTHER, LEMONIELLS_COVENANT);
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
				if (player.getClassId() == ClassId.MAGE)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, MARK_OF_FAITH))
						{
							htmltext = "30022-04.htm";
						}
						else
						{
							qs.startQuest();
							giveItems(player, LETTER_OF_ORDER_1ST, 1);
							htmltext = "30022-05.htm";
						}
					}
					else
					{
						htmltext = "30022-03.htm";
					}
				}
				else if (player.getClassId() == ClassId.CLERIC)
				{
					htmltext = "30022-02a.htm";
				}
				else
				{
					htmltext = "30022-02.htm";
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
			if (hasQuestItems(killer, NECKLACE_OF_MOTHER) && !hasQuestItems(killer, PENDANT_OF_MOTHER))
			{
				giveItems(killer, PENDANT_OF_MOTHER, 1);
				playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_MIDDLE);
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
			if (npc.getId() == ZIGAUNT)
			{
				if (!hasQuestItems(player, MARK_OF_FAITH))
				{
					htmltext = "30022-01.htm";
				}
				else
				{
					htmltext = "30022-04.htm";
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == ZIGAUNT)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case ZIGAUNT:
				{
					if (!hasQuestItems(player, LEMONIELLS_COVENANT) && hasQuestItems(player, LETTER_OF_ORDER_2ND))
					{
						htmltext = "30022-07.html";
					}
					else if (hasQuestItems(player, LETTER_OF_ORDER_2ND, LEMONIELLS_COVENANT))
					{
						takeItems(player, LETTER_OF_ORDER_2ND, 1);
						takeItems(player, LEMONIELLS_COVENANT, 1);
						giveItems(player, MARK_OF_FAITH, 1);
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
						htmltext = "30022-09.html";
					}
					else if (hasQuestItems(player, LETTER_OF_ORDER_1ST))
					{
						if (hasQuestItems(player, BOOK_OF_VIVYAN, BOOK_OF_SIMPLON, BOOK_OF_PRAGA))
						{
							takeItems(player, LETTER_OF_ORDER_1ST, 1);
							giveItems(player, LETTER_OF_ORDER_2ND, 1);
							takeItems(player, BOOK_OF_VIVYAN, 1);
							takeItems(player, BOOK_OF_SIMPLON, -1);
							takeItems(player, BOOK_OF_PRAGA, 1);
							qs.setCond(3, true);
							htmltext = "30022-08.html";
						}
						else
						{
							htmltext = "30022-06.html";
						}
					}
					break;
				}
				case GALLINT:
				{
					if (!hasQuestItems(player, LEMONIELLS_COVENANT) && hasQuestItems(player, LETTER_OF_ORDER_2ND))
					{
						if (!hasQuestItems(player, CERTIFICATE_OF_GALLINT) && hasQuestItems(player, LIONELS_BOOK))
						{
							takeItems(player, LIONELS_BOOK, 1);
							giveItems(player, CERTIFICATE_OF_GALLINT, 1);
							qs.setCond(5, true);
							htmltext = "30017-01.html";
						}
						else
						{
							htmltext = "30017-02.html";
						}
					}
					break;
				}
				case VIVYAN:
				{
					if (hasQuestItems(player, LETTER_OF_ORDER_1ST))
					{
						if (!hasQuestItems(player, BOOK_OF_VIVYAN))
						{
							giveItems(player, BOOK_OF_VIVYAN, 1);
							if ((getQuestItemsCount(player, BOOK_OF_SIMPLON) >= 3) && (getQuestItemsCount(player, BOOK_OF_VIVYAN) >= 0) && (getQuestItemsCount(player, BOOK_OF_PRAGA) >= 1))
							{
								qs.setCond(2, true);
							}
							htmltext = "30030-01.html";
						}
						else
						{
							htmltext = "30030-02.html";
						}
					}
					break;
				}
				case TRADER_SIMPLON:
				{
					if (hasQuestItems(player, LETTER_OF_ORDER_1ST))
					{
						if (!hasQuestItems(player, BOOK_OF_SIMPLON))
						{
							giveItems(player, BOOK_OF_SIMPLON, 3);
							if ((getQuestItemsCount(player, BOOK_OF_SIMPLON) >= 0) && (getQuestItemsCount(player, BOOK_OF_VIVYAN) >= 1) && (getQuestItemsCount(player, BOOK_OF_PRAGA) >= 1))
							{
								qs.setCond(2, true);
							}
							htmltext = "30253-01.html";
						}
						else
						{
							htmltext = "30253-02.html";
						}
					}
					break;
				}
				case GUARD_PRAGA:
				{
					if (hasQuestItems(player, LETTER_OF_ORDER_1ST))
					{
						if (!hasAtLeastOneQuestItem(player, BOOK_OF_PRAGA, NECKLACE_OF_MOTHER))
						{
							giveItems(player, NECKLACE_OF_MOTHER, 1);
							htmltext = "30333-01.html";
						}
						else if (!hasAtLeastOneQuestItem(player, BOOK_OF_PRAGA, PENDANT_OF_MOTHER) && hasQuestItems(player, NECKLACE_OF_MOTHER))
						{
							htmltext = "30333-02.html";
						}
						else if (!hasQuestItems(player, BOOK_OF_PRAGA) && hasQuestItems(player, NECKLACE_OF_MOTHER, PENDANT_OF_MOTHER))
						{
							giveItems(player, BOOK_OF_PRAGA, 1);
							takeItems(player, PENDANT_OF_MOTHER, 1);
							takeItems(player, NECKLACE_OF_MOTHER, 1);
							if ((getQuestItemsCount(player, BOOK_OF_SIMPLON) >= 3) && (getQuestItemsCount(player, BOOK_OF_VIVYAN) >= 1) && (getQuestItemsCount(player, BOOK_OF_PRAGA) >= 0))
							{
								qs.setCond(2, true);
							}
							htmltext = "30333-03.html";
						}
						else if (hasQuestItems(player, BOOK_OF_PRAGA))
						{
							htmltext = "30333-04.html";
						}
					}
					break;
				}
				case LIONEL:
				{
					if (!hasQuestItems(player, LETTER_OF_ORDER_2ND))
					{
						htmltext = "30408-02.html";
					}
					else if (!hasAtLeastOneQuestItem(player, LIONELS_BOOK, LEMONIELLS_COVENANT, CERTIFICATE_OF_GALLINT) && hasQuestItems(player, LETTER_OF_ORDER_2ND))
					{
						giveItems(player, LIONELS_BOOK, 1);
						qs.setCond(4, true);
						htmltext = "30408-01.html";
					}
					else if (!hasAtLeastOneQuestItem(player, LEMONIELLS_COVENANT, CERTIFICATE_OF_GALLINT) && hasQuestItems(player, LETTER_OF_ORDER_2ND, LIONELS_BOOK))
					{
						htmltext = "30408-03.html";
					}
					else if (!hasAtLeastOneQuestItem(player, LIONELS_BOOK, LEMONIELLS_COVENANT) && hasQuestItems(player, LETTER_OF_ORDER_2ND, CERTIFICATE_OF_GALLINT))
					{
						takeItems(player, CERTIFICATE_OF_GALLINT, 1);
						giveItems(player, LEMONIELLS_COVENANT, 1);
						qs.setCond(6, true);
						htmltext = "30408-04.html";
					}
					else if (!hasAtLeastOneQuestItem(player, LIONELS_BOOK, CERTIFICATE_OF_GALLINT) && hasQuestItems(player, LETTER_OF_ORDER_2ND, LEMONIELLS_COVENANT))
					{
						htmltext = "30408-05.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}
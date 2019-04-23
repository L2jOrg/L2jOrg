/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q00418_PathOfTheArtisan;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Util;

/**
 * Path Of The Artisan (418)
 * @author ivantotov
 */
public final class Q00418_PathOfTheArtisan extends Quest
{
	// NPCs
	private static final int BLACKSMITH_SILVERA = 30527;
	private static final int BLACKSMITH_PINTER = 30298;
	private static final int BLACKSMITH_KLUTO = 30317;
	private static final int IRON_GATES_LOCKIRIN = 30531;
	// Items
	private static final int SILVERYS_RING = 1632;
	private static final int PASS_1ST_CERTIFICATE = 1633;
	private static final int PASS_2ND_CERTIFICATE = 1634;
	private static final int BOOGLE_RATMAN_TOOTH = 1636;
	private static final int BOOGLE_RATMAN_LEADERS_TOOTH = 1637;
	private static final int KLUTOS_LETTER = 1638;
	private static final int FOOTPRINT_OF_THIEF = 1639;
	private static final int STOLEN_SECRET_BOX = 1640;
	private static final int SECRET_BOX = 1641;
	// Reward
	private static final int FINAL_PASS_CERTIFICATE = 1635;
	// Monster
	private static final int VUKU_ORC_FIGHTER = 20017;
	private static final int BOOGLE_RATMAN = 20389;
	private static final int BOOGLE_RATMAN_LEADER = 20390;
	// Misc
	private static final int MIN_LEVEL = 19;
	
	public Q00418_PathOfTheArtisan()
	{
		super(418);
		addStartNpc(BLACKSMITH_SILVERA);
		addTalkId(BLACKSMITH_SILVERA, BLACKSMITH_PINTER, BLACKSMITH_KLUTO, IRON_GATES_LOCKIRIN);
		addKillId(VUKU_ORC_FIGHTER, BOOGLE_RATMAN, BOOGLE_RATMAN_LEADER);
		registerQuestItems(SILVERYS_RING, PASS_1ST_CERTIFICATE, PASS_2ND_CERTIFICATE, BOOGLE_RATMAN_TOOTH, BOOGLE_RATMAN_LEADERS_TOOTH, KLUTOS_LETTER, FOOTPRINT_OF_THIEF, STOLEN_SECRET_BOX, SECRET_BOX);
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
				if (player.getClassId() == ClassId.DWARVEN_FIGHTER)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, FINAL_PASS_CERTIFICATE))
						{
							htmltext = "30527-04.htm";
						}
						else
						{
							htmltext = "30527-05.htm";
						}
					}
					else
					{
						htmltext = "30527-03.htm";
					}
				}
				else if (player.getClassId() == ClassId.ARTISAN)
				{
					htmltext = "30527-02a.htm";
				}
				else
				{
					htmltext = "30527-02.htm";
				}
				break;
			}
			case "30527-06.htm":
			{
				qs.startQuest();
				giveItems(player, SILVERYS_RING, 1);
				htmltext = event;
				break;
			}
			case "30527-08b.html":
			{
				takeItems(player, SILVERYS_RING, 1);
				takeItems(player, BOOGLE_RATMAN_TOOTH, -1);
				takeItems(player, BOOGLE_RATMAN_LEADERS_TOOTH, -1);
				giveItems(player, PASS_1ST_CERTIFICATE, 1);
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			case "30527-08c.html":
			{
				takeItems(player, SILVERYS_RING, 1);
				takeItems(player, BOOGLE_RATMAN_TOOTH, -1);
				takeItems(player, BOOGLE_RATMAN_LEADERS_TOOTH, -1);
				qs.setMemoState(10);
				qs.setCond(8, true);
				htmltext = event;
				break;
			}
			case "30298-02.html":
			case "30317-02.html":
			case "30317-03.html":
			case "30317-05.html":
			case "30317-06.html":
			case "30317-11.html":
			case "30531-02.html":
			case "30531-03.html":
			case "30531-04.html":
			case "31956-02.html":
			case "31956-03.html":
			case "32052-02.html":
			case "32052-03.html":
			case "32052-04.html":
			case "32052-05.html":
			case "32052-06.html":
			case "32052-10.html":
			case "32052-11.html":
			case "32052-12.html":
			{
				htmltext = event;
				break;
			}
			case "30298-03.html":
			{
				if (hasQuestItems(player, KLUTOS_LETTER))
				{
					takeItems(player, KLUTOS_LETTER, 1);
					giveItems(player, FOOTPRINT_OF_THIEF, 1);
					qs.setCond(5, true);
					htmltext = event;
				}
				break;
			}
			case "30298-06.html":
			{
				if (hasQuestItems(player, FOOTPRINT_OF_THIEF, STOLEN_SECRET_BOX))
				{
					giveItems(player, PASS_2ND_CERTIFICATE, 1);
					takeItems(player, FOOTPRINT_OF_THIEF, 1);
					takeItems(player, STOLEN_SECRET_BOX, 1);
					giveItems(player, SECRET_BOX, 1);
					qs.setCond(7, true);
					htmltext = event;
				}
				break;
			}
			case "30317-04.html":
			{
				giveItems(player, KLUTOS_LETTER, 1);
				qs.setCond(4, true);
				htmltext = event;
				break;
			}
			case "30317-07.html":
			{
				giveItems(player, KLUTOS_LETTER, 1);
				qs.setCond(4);
				htmltext = event;
				break;
			}
			case "30317-10.html":
			{
				if (hasQuestItems(player, PASS_2ND_CERTIFICATE, SECRET_BOX))
				{
					giveItems(player, FINAL_PASS_CERTIFICATE, 1);
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
					htmltext = event;
				}
				break;
			}
			case "30317-12.html":
			{
				if (hasQuestItems(player, PASS_2ND_CERTIFICATE, SECRET_BOX))
				{
					giveItems(player, FINAL_PASS_CERTIFICATE, 1);
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
					htmltext = event;
				}
				break;
			}
			case "30531-05.html":
			{
				if (qs.isMemoState(101))
				{
					giveItems(player, FINAL_PASS_CERTIFICATE, 1);
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
					htmltext = event;
				}
				break;
			}
			case "31956-04.html":
			{
				if (qs.isMemoState(201))
				{
					giveItems(player, FINAL_PASS_CERTIFICATE, 1);
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
					htmltext = event;
				}
				break;
			}
			case "31963-02.html":
			case "31963-06.html":
			{
				if (qs.isMemoState(100))
				{
					htmltext = event;
				}
				break;
			}
			case "31963-03.html":
			{
				if (qs.isMemoState(100))
				{
					qs.setMemoState(101);
					qs.setCond(10, true);
					htmltext = event;
				}
				break;
			}
			case "31963-05.html":
			{
				if (qs.isMemoState(100))
				{
					qs.setMemoState(102);
					qs.setCond(11, true);
					htmltext = event;
				}
				break;
			}
			case "31963-07.html":
			{
				if (qs.isMemoState(100))
				{
					qs.setMemoState(201);
					qs.setCond(12, true);
					htmltext = event;
				}
				break;
			}
			case "31963-09.html":
			{
				if (qs.isMemoState(100))
				{
					qs.setMemoState(202);
					htmltext = event;
				}
				break;
			}
			case "31963-10.html":
			{
				if (qs.isMemoState(202))
				{
					giveItems(player, FINAL_PASS_CERTIFICATE, 1);
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
					htmltext = event;
				}
				break;
			}
			case "32052-07.html":
			{
				if (qs.isMemoState(10))
				{
					qs.setMemoState(100);
					qs.setCond(9, true);
					htmltext = event;
				}
				break;
			}
			case "32052-13.html":
			{
				if (qs.isMemoState(102))
				{
					giveItems(player, FINAL_PASS_CERTIFICATE, 1);
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
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case VUKU_ORC_FIGHTER:
				{
					if (hasQuestItems(killer, FOOTPRINT_OF_THIEF) && !hasQuestItems(killer, STOLEN_SECRET_BOX))
					{
						if (getRandom(10) < 2)
						{
							giveItems(killer, STOLEN_SECRET_BOX, 1);
							qs.setCond(6, true);
						}
					}
					break;
				}
				case BOOGLE_RATMAN:
				{
					if (hasQuestItems(killer, SILVERYS_RING) && (getQuestItemsCount(killer, BOOGLE_RATMAN_TOOTH) < 10))
					{
						if (getRandom(10) < 7)
						{
							if (getQuestItemsCount(killer, BOOGLE_RATMAN_TOOTH) == 9)
							{
								giveItems(killer, BOOGLE_RATMAN_TOOTH, 1);
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
								if (getQuestItemsCount(killer, BOOGLE_RATMAN_LEADERS_TOOTH) >= 2)
								{
									qs.setCond(2);
								}
							}
							else
							{
								giveItems(killer, BOOGLE_RATMAN_TOOTH, 1);
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
					}
					break;
				}
				case BOOGLE_RATMAN_LEADER:
				{
					if (hasQuestItems(killer, SILVERYS_RING) && (getQuestItemsCount(killer, BOOGLE_RATMAN_LEADERS_TOOTH) < 2))
					{
						if (getRandom(10) < 5)
						{
							if (getQuestItemsCount(killer, BOOGLE_RATMAN_LEADERS_TOOTH) == 1)
							{
								giveItems(killer, BOOGLE_RATMAN_LEADERS_TOOTH, 1);
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
								if (getQuestItemsCount(killer, BOOGLE_RATMAN_TOOTH) >= 10)
								{
									qs.setCond(2);
								}
							}
						}
						else
						{
							giveItems(killer, BOOGLE_RATMAN_LEADERS_TOOTH, 1);
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
			if (npc.getId() == BLACKSMITH_SILVERA)
			{
				htmltext = "30527-01.htm";
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == BLACKSMITH_SILVERA)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case BLACKSMITH_SILVERA:
				{
					if (hasQuestItems(player, SILVERYS_RING) && ((getQuestItemsCount(player, BOOGLE_RATMAN_TOOTH) + getQuestItemsCount(player, BOOGLE_RATMAN_LEADERS_TOOTH)) < 12))
					{
						htmltext = "30527-07.html";
					}
					else if (hasQuestItems(player, SILVERYS_RING) && (getQuestItemsCount(player, BOOGLE_RATMAN_TOOTH) >= 10) && (getQuestItemsCount(player, BOOGLE_RATMAN_LEADERS_TOOTH) >= 2))
					{
						htmltext = "30527-08a.html";
					}
					else if (hasQuestItems(player, PASS_1ST_CERTIFICATE))
					{
						htmltext = "30527-09.html";
					}
					else if (!hasQuestItems(player, PASS_1ST_CERTIFICATE) && qs.isMemoState(10))
					{
						htmltext = "30527-09a.html";
					}
					break;
				}
				case BLACKSMITH_PINTER:
				{
					if (hasQuestItems(player, PASS_1ST_CERTIFICATE, KLUTOS_LETTER))
					{
						htmltext = "30298-01.html";
					}
					else if (hasQuestItems(player, PASS_1ST_CERTIFICATE, FOOTPRINT_OF_THIEF) && !hasQuestItems(player, STOLEN_SECRET_BOX))
					{
						htmltext = "30298-04.html";
					}
					else if (hasQuestItems(player, PASS_1ST_CERTIFICATE, FOOTPRINT_OF_THIEF, STOLEN_SECRET_BOX))
					{
						htmltext = "30298-05.html";
					}
					else if (hasQuestItems(player, PASS_1ST_CERTIFICATE, PASS_2ND_CERTIFICATE, SECRET_BOX))
					{
						htmltext = "30298-07.html";
					}
					break;
				}
				case BLACKSMITH_KLUTO:
				{
					if (hasQuestItems(player, PASS_1ST_CERTIFICATE) && !hasAtLeastOneQuestItem(player, FOOTPRINT_OF_THIEF, KLUTOS_LETTER, PASS_2ND_CERTIFICATE, SECRET_BOX))
					{
						htmltext = "30317-01.html";
					}
					else if (hasQuestItems(player, PASS_1ST_CERTIFICATE) && hasAtLeastOneQuestItem(player, KLUTOS_LETTER, FOOTPRINT_OF_THIEF))
					{
						htmltext = "30317-08.html";
					}
					else if (hasQuestItems(player, PASS_1ST_CERTIFICATE, PASS_2ND_CERTIFICATE, SECRET_BOX))
					{
						htmltext = "30317-09.html";
					}
					break;
				}
				case IRON_GATES_LOCKIRIN:
				{
					if (qs.isMemoState(101))
					{
						htmltext = "30531-01.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}
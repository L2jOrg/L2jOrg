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
package quests.Q00223_TestOfTheChampion;

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
 * Test Of The Champion (223)
 * @author ivantotov
 */
public final class Q00223_TestOfTheChampion extends Quest
{
	// NPCs
	private static final int TRADER_GROOT = 30093;
	private static final int CAPTAIN_MOUEN = 30196;
	private static final int VETERAN_ASCALON = 30624;
	private static final int MASON = 30625;
	// Items
	private static final int ASCALONS_1ST_LETTER = 3277;
	private static final int MASONS_LETTER = 3278;
	private static final int IRON_ROSE_RING = 3279;
	private static final int ASCALONS_2ND_LETTER = 3280;
	private static final int WHITE_ROSE_INSIGNIA = 3281;
	private static final int GROOTS_LETTER = 3282;
	private static final int ASCALONS_3RD_LETTER = 3283;
	private static final int MOUENS_1ST_ORDER = 3284;
	private static final int MOUENS_2ND_ORDER = 3285;
	private static final int MOUENS_LETTER = 3286;
	private static final int HARPYS_EGG = 3287;
	private static final int MEDUSA_VENOM = 3288;
	private static final int WINDSUS_BILE = 3289;
	private static final int BLOODY_AXE_HEAD = 3290;
	private static final int ROAD_RATMAN_HEAD = 3291;
	private static final int LETO_LIZARDMAN_FANG = 3292;
	// Reward
	private static final int MARK_OF_CHAMPION = 3276;
	// Monster
	private static final int HARPY = 20145;
	private static final int MEDUSA = 20158;
	private static final int ROAD_SCAVENGER = 20551;
	private static final int WINDSUS = 20553;
	private static final int LETO_LIZARDMAN = 20577;
	private static final int LETO_LIZARDMAN_ARCHER = 20578;
	private static final int LETO_LIZARDMAN_SOLDIER = 20579;
	private static final int LETO_LIZARDMAN_WARRIOR = 20580;
	private static final int LETO_LIZARDMAN_SHAMAN = 20581;
	private static final int LETO_LIZARDMAN_OCERLORD = 20582;
	private static final int BLOODY_AXE_ELITE = 20780;
	// Quest Monster
	private static final int HARPY_MATRIARCH = 27088;
	private static final int ROAD_COLLECTOR = 27089;
	// Misc
	private static final int MIN_LEVEL = 39;
	
	public Q00223_TestOfTheChampion()
	{
		super(223);
		addStartNpc(VETERAN_ASCALON);
		addTalkId(VETERAN_ASCALON, TRADER_GROOT, CAPTAIN_MOUEN, MASON);
		addKillId(HARPY, MEDUSA, WINDSUS, ROAD_SCAVENGER, LETO_LIZARDMAN, LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OCERLORD, BLOODY_AXE_ELITE, HARPY_MATRIARCH, ROAD_COLLECTOR);
		addAttackId(HARPY, ROAD_SCAVENGER, BLOODY_AXE_ELITE);
		registerQuestItems(ASCALONS_1ST_LETTER, MASONS_LETTER, IRON_ROSE_RING, ASCALONS_2ND_LETTER, WHITE_ROSE_INSIGNIA, GROOTS_LETTER, ASCALONS_3RD_LETTER, MOUENS_1ST_ORDER, MOUENS_2ND_ORDER, MOUENS_LETTER, HARPYS_EGG, MEDUSA_VENOM, WINDSUS_BILE, BLOODY_AXE_HEAD, ROAD_RATMAN_HEAD, LETO_LIZARDMAN_FANG);
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
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					giveItems(player, ASCALONS_1ST_LETTER, 1);
				}
				break;
			}
			case "30624-05.htm":
			case "30196-02.html":
			case "30625-02.html":
			{
				htmltext = event;
				break;
			}
			case "30624-10.html":
			{
				if (hasQuestItems(player, MASONS_LETTER))
				{
					takeItems(player, MASONS_LETTER, 1);
					giveItems(player, ASCALONS_2ND_LETTER, 1);
					qs.setCond(5, true);
					htmltext = event;
				}
				break;
			}
			case "30624-14.html":
			{
				if (hasQuestItems(player, GROOTS_LETTER))
				{
					takeItems(player, GROOTS_LETTER, 1);
					giveItems(player, ASCALONS_3RD_LETTER, 1);
					qs.setCond(9, true);
					htmltext = event;
				}
				break;
			}
			case "30093-02.html":
			{
				if (hasQuestItems(player, ASCALONS_2ND_LETTER))
				{
					takeItems(player, ASCALONS_2ND_LETTER, 1);
					giveItems(player, WHITE_ROSE_INSIGNIA, 1);
					qs.setCond(6, true);
					htmltext = event;
				}
				break;
			}
			case "30196-03.html":
			{
				if (hasQuestItems(player, ASCALONS_3RD_LETTER))
				{
					takeItems(player, ASCALONS_3RD_LETTER, 1);
					giveItems(player, MOUENS_1ST_ORDER, 1);
					qs.setCond(10, true);
					htmltext = event;
				}
				break;
			}
			case "30196-06.html":
			{
				if (getQuestItemsCount(player, ROAD_RATMAN_HEAD) >= 10)
				{
					takeItems(player, MOUENS_1ST_ORDER, 1);
					giveItems(player, MOUENS_2ND_ORDER, 1);
					takeItems(player, ROAD_RATMAN_HEAD, -1);
					qs.setCond(12, true);
					htmltext = event;
				}
				break;
			}
			case "30625-03.html":
			{
				if (hasQuestItems(player, ASCALONS_1ST_LETTER))
				{
					takeItems(player, ASCALONS_1ST_LETTER, 1);
					giveItems(player, IRON_ROSE_RING, 1);
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		final QuestState qs = getQuestState(attacker, false);
		if ((qs != null) && qs.isStarted())
		{
			switch (npc.getId())
			{
				case HARPY:
				{
					switch (npc.getScriptValue())
					{
						case 0:
						{
							npc.getVariables().set("lastAttacker", attacker.getObjectId());
							if (hasQuestItems(attacker, WHITE_ROSE_INSIGNIA) && (getQuestItemsCount(attacker, HARPYS_EGG) < 30))
							{
								if (getRandomBoolean())
								{
									if (getRandom(10) < 7)
									{
										addAttackPlayerDesire(addSpawn(HARPY_MATRIARCH, npc, true, 0, false), attacker);
									}
									else
									{
										addAttackPlayerDesire(addSpawn(HARPY_MATRIARCH, npc, true, 0, false), attacker);
										addAttackPlayerDesire(addSpawn(HARPY_MATRIARCH, npc, true, 0, false), attacker);
									}
								}
							}
							npc.setScriptValue(1);
							break;
						}
						case 1:
						{
							npc.setScriptValue(2);
							break;
						}
					}
					break;
				}
				case ROAD_SCAVENGER:
				{
					switch (npc.getScriptValue())
					{
						case 0:
						{
							npc.getVariables().set("lastAttacker", attacker.getObjectId());
							if (hasQuestItems(attacker, MOUENS_1ST_ORDER) && (getQuestItemsCount(attacker, ROAD_RATMAN_HEAD) < 10))
							{
								if (getRandomBoolean())
								{
									if (getRandom(10) < 7)
									{
										addAttackPlayerDesire(addSpawn(ROAD_COLLECTOR, npc, true, 0, false), attacker);
									}
									else
									{
										addAttackPlayerDesire(addSpawn(ROAD_COLLECTOR, npc, true, 0, false), attacker);
										addAttackPlayerDesire(addSpawn(ROAD_COLLECTOR, npc, true, 0, false), attacker);
									}
								}
							}
							npc.setScriptValue(1);
							break;
						}
						case 1:
						{
							npc.setScriptValue(2);
							break;
						}
					}
					break;
				}
				case BLOODY_AXE_ELITE:
				{
					switch (npc.getScriptValue())
					{
						case 0:
						{
							npc.getVariables().set("lastAttacker", attacker.getObjectId());
							if (hasQuestItems(attacker, IRON_ROSE_RING) && (getQuestItemsCount(attacker, BLOODY_AXE_HEAD) < 10))
							{
								if (getRandomBoolean())
								{
									addAttackPlayerDesire(addSpawn(BLOODY_AXE_ELITE, npc, true, 0, false), attacker);
								}
							}
							npc.setScriptValue(1);
							break;
						}
						case 1:
						{
							npc.setScriptValue(2);
							break;
						}
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case HARPY:
				case HARPY_MATRIARCH:
				{
					if (hasQuestItems(killer, WHITE_ROSE_INSIGNIA) && (getQuestItemsCount(killer, HARPYS_EGG) < 30))
					{
						if (getQuestItemsCount(killer, HARPYS_EGG) >= 28)
						{
							giveItems(killer, HARPYS_EGG, 2);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, MEDUSA_VENOM) >= 30) && (getQuestItemsCount(killer, WINDSUS_BILE) >= 30))
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, HARPYS_EGG, 2);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MEDUSA:
				{
					if (hasQuestItems(killer, WHITE_ROSE_INSIGNIA) && (getQuestItemsCount(killer, MEDUSA_VENOM) < 30))
					{
						if (getQuestItemsCount(killer, MEDUSA_VENOM) >= 27)
						{
							giveItems(killer, MEDUSA_VENOM, 3);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, HARPYS_EGG) >= 30) && (getQuestItemsCount(killer, WINDSUS_BILE) >= 30))
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, MEDUSA_VENOM, 3);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case WINDSUS:
				{
					if (hasQuestItems(killer, WHITE_ROSE_INSIGNIA) && (getQuestItemsCount(killer, WINDSUS_BILE) < 30))
					{
						if (getQuestItemsCount(killer, WINDSUS_BILE) >= 27)
						{
							giveItems(killer, WINDSUS_BILE, 3);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, HARPYS_EGG) >= 30) && (getQuestItemsCount(killer, MEDUSA_VENOM) >= 30))
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, WINDSUS_BILE, 3);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case ROAD_SCAVENGER:
				case ROAD_COLLECTOR:
				{
					if (hasQuestItems(killer, MOUENS_1ST_ORDER) && (getQuestItemsCount(killer, ROAD_RATMAN_HEAD) < 10))
					{
						if (getQuestItemsCount(killer, ROAD_RATMAN_HEAD) >= 9)
						{
							giveItems(killer, ROAD_RATMAN_HEAD, 1);
							qs.setCond(11, true);
						}
						else
						{
							giveItems(killer, ROAD_RATMAN_HEAD, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case LETO_LIZARDMAN:
				case LETO_LIZARDMAN_ARCHER:
				case LETO_LIZARDMAN_SOLDIER:
				case LETO_LIZARDMAN_WARRIOR:
				case LETO_LIZARDMAN_SHAMAN:
				case LETO_LIZARDMAN_OCERLORD:
				{
					if (hasQuestItems(killer, MOUENS_2ND_ORDER) && (getQuestItemsCount(killer, LETO_LIZARDMAN_FANG) < 10))
					{
						if (getQuestItemsCount(killer, LETO_LIZARDMAN_FANG) >= 9)
						{
							giveItems(killer, LETO_LIZARDMAN_FANG, 1);
							qs.setCond(13, true);
						}
						else
						{
							giveItems(killer, LETO_LIZARDMAN_FANG, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case BLOODY_AXE_ELITE:
				{
					if (hasQuestItems(killer, IRON_ROSE_RING) && (getQuestItemsCount(killer, BLOODY_AXE_HEAD) < 10))
					{
						if (getQuestItemsCount(killer, BLOODY_AXE_HEAD) >= 9)
						{
							giveItems(killer, BLOODY_AXE_HEAD, 1);
							qs.setCond(3, true);
						}
						else
						{
							giveItems(killer, BLOODY_AXE_HEAD, 1);
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
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == VETERAN_ASCALON)
			{
				if ((player.getClassId() == ClassId.WARRIOR) || (player.getClassId() == ClassId.ORC_RAIDER))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (player.getClassId() == ClassId.WARRIOR)
						{
							htmltext = "30624-03.htm";
						}
						else
						{
							htmltext = "30624-04.html";
						}
					}
					else
					{
						htmltext = "30624-01.html";
					}
				}
				else
				{
					htmltext = "30624-02.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case VETERAN_ASCALON:
				{
					if (hasQuestItems(player, ASCALONS_1ST_LETTER))
					{
						htmltext = "30624-07.html";
					}
					else if (hasQuestItems(player, IRON_ROSE_RING))
					{
						htmltext = "30624-08.html";
					}
					else if (hasQuestItems(player, MASONS_LETTER))
					{
						htmltext = "30624-09.html";
					}
					else if (hasQuestItems(player, ASCALONS_2ND_LETTER))
					{
						htmltext = "30624-11.html";
					}
					else if (hasQuestItems(player, WHITE_ROSE_INSIGNIA))
					{
						htmltext = "30624-12.html";
					}
					else if (hasQuestItems(player, GROOTS_LETTER))
					{
						htmltext = "30624-13.html";
					}
					else if (hasQuestItems(player, ASCALONS_3RD_LETTER))
					{
						htmltext = "30624-15.html";
					}
					else if (hasAtLeastOneQuestItem(player, MOUENS_1ST_ORDER, MOUENS_2ND_ORDER))
					{
						htmltext = "30624-16.html";
					}
					else if (hasQuestItems(player, MOUENS_LETTER))
					{
						giveAdena(player, 229764, true);
						giveItems(player, MARK_OF_CHAMPION, 1);
						addExpAndSp(player, 1270742, 87200);
						qs.exitQuest(false, true);
						player.sendPacket(new SocialAction(player.getObjectId(), 3));
						htmltext = "30624-17.html";
					}
					break;
				}
				case TRADER_GROOT:
				{
					if (hasQuestItems(player, ASCALONS_2ND_LETTER))
					{
						htmltext = "30093-01.html";
					}
					else if (hasQuestItems(player, WHITE_ROSE_INSIGNIA))
					{
						if ((getQuestItemsCount(player, HARPYS_EGG) >= 30) && (getQuestItemsCount(player, MEDUSA_VENOM) >= 30) && (getQuestItemsCount(player, WINDSUS_BILE) >= 30))
						{
							takeItems(player, WHITE_ROSE_INSIGNIA, 1);
							giveItems(player, GROOTS_LETTER, 1);
							takeItems(player, HARPYS_EGG, -1);
							takeItems(player, MEDUSA_VENOM, -1);
							takeItems(player, WINDSUS_BILE, -1);
							qs.setCond(8, true);
							htmltext = "30093-04.html";
						}
						else
						{
							htmltext = "30093-03.html";
						}
					}
					else if (hasQuestItems(player, GROOTS_LETTER))
					{
						htmltext = "30093-05.html";
					}
					else if (hasAtLeastOneQuestItem(player, ASCALONS_3RD_LETTER, MOUENS_1ST_ORDER, MOUENS_2ND_ORDER, MOUENS_LETTER))
					{
						htmltext = "30093-06.html";
					}
					break;
				}
				case CAPTAIN_MOUEN:
				{
					if (hasQuestItems(player, ASCALONS_3RD_LETTER))
					{
						htmltext = "30196-01.html";
					}
					else if (hasQuestItems(player, MOUENS_1ST_ORDER))
					{
						if (getQuestItemsCount(player, ROAD_RATMAN_HEAD) < 10)
						{
							htmltext = "30196-04.html";
						}
						else
						{
							htmltext = "30196-05.html";
						}
					}
					else if (hasQuestItems(player, MOUENS_2ND_ORDER))
					{
						if (getQuestItemsCount(player, LETO_LIZARDMAN_FANG) < 10)
						{
							htmltext = "30196-07.html";
						}
						else
						{
							takeItems(player, MOUENS_2ND_ORDER, 1);
							giveItems(player, MOUENS_LETTER, 1);
							takeItems(player, LETO_LIZARDMAN_FANG, -1);
							qs.setCond(14, true);
							htmltext = "30196-08.html";
						}
					}
					else if (hasQuestItems(player, MOUENS_LETTER))
					{
						htmltext = "30196-09.html";
					}
					break;
				}
				case MASON:
				{
					if (hasQuestItems(player, ASCALONS_1ST_LETTER))
					{
						htmltext = "30625-01.html";
					}
					else if (hasQuestItems(player, IRON_ROSE_RING))
					{
						if (getQuestItemsCount(player, BLOODY_AXE_HEAD) < 10)
						{
							htmltext = "30625-04.html";
						}
						else
						{
							giveItems(player, MASONS_LETTER, 1);
							takeItems(player, IRON_ROSE_RING, 1);
							takeItems(player, BLOODY_AXE_HEAD, -1);
							qs.setCond(4, true);
							htmltext = "30625-05.html";
						}
					}
					else if (hasQuestItems(player, MASONS_LETTER))
					{
						htmltext = "30625-06.html";
					}
					else if (hasAtLeastOneQuestItem(player, ASCALONS_2ND_LETTER, WHITE_ROSE_INSIGNIA, GROOTS_LETTER, ASCALONS_3RD_LETTER, MOUENS_1ST_ORDER, MOUENS_2ND_ORDER, MOUENS_LETTER))
					{
						htmltext = "30625-07.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == VETERAN_ASCALON)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}
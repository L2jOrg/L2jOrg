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
package quests.Q00417_PathOfTheScavenger;

import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Util;

/**
 * Path Of The Scavenger (417)
 * @author ivantotov
 */
public final class Q00417_PathOfTheScavenger extends Quest
{
	// NPCs
	private static final int WAREHOUSE_KEEPER_RAUT = 30316;
	private static final int TRADER_SHARI = 30517;
	private static final int TRADER_MION = 30519;
	private static final int COLLECTOR_PIPI = 30524;
	private static final int HEAD_BLACKSMITH_BRONK = 30525;
	private static final int PRIEST_OF_THE_EARTH_ZIMENF = 30538;
	private static final int MASTER_TOMA = 30556;
	private static final int TORAI = 30557;
	// Items
	private static final int PIPPIS_LETTER_OF_RECOMMENDATION = 1643;
	private static final int ROUTS_TELEPORT_SCROLL = 1644;
	private static final int SUCCUBUS_UNDIES = 1645;
	private static final int MIONS_LETTER = 1646;
	private static final int BRONKS_INGOT = 1647;
	private static final int SHARIS_AXE = 1648;
	private static final int ZIMENFS_POTION = 1649;
	private static final int BRONKS_PAY = 1650;
	private static final int SHARIS_PAY = 1651;
	private static final int ZIMENFS_PAY = 1652;
	private static final int BEAR_PICTURE = 1653;
	private static final int TARANTULA_PICTURE = 1654;
	private static final int HONEY_JAR = 1655;
	private static final int BEAD = 1656;
	private static final int BEAD_PARCEL = 1657;
	private static final int BEAD_PARCEL2 = 8543;
	// Reward
	private static final int RING_OF_RAVEN = 1642;
	// Monster
	private static final int HUNTER_TARANTULA = 20403;
	private static final int PLUNDER_TARANTULA = 20508;
	private static final int HUNTER_BEAR = 20777;
	// Quest Monster
	private static final int HONEY_BEAR = 27058;
	// Misc
	private static final int MIN_LEVEL = 19;
	private static final String FIRST_ATTACKER = "FIRST_ATTACKER";
	private static final String FLAG = "FLAG";
	
	public Q00417_PathOfTheScavenger()
	{
		super(417);
		addStartNpc(COLLECTOR_PIPI);
		addTalkId(COLLECTOR_PIPI, WAREHOUSE_KEEPER_RAUT, TRADER_MION, TRADER_SHARI, HEAD_BLACKSMITH_BRONK, PRIEST_OF_THE_EARTH_ZIMENF, MASTER_TOMA, TORAI);
		addAttackId(HUNTER_TARANTULA, PLUNDER_TARANTULA, HUNTER_BEAR, HONEY_BEAR);
		addKillId(HUNTER_TARANTULA, PLUNDER_TARANTULA, HUNTER_BEAR, HONEY_BEAR);
		registerQuestItems(PIPPIS_LETTER_OF_RECOMMENDATION, ROUTS_TELEPORT_SCROLL, SUCCUBUS_UNDIES, MIONS_LETTER, BRONKS_INGOT, SHARIS_AXE, ZIMENFS_POTION, BRONKS_PAY, SHARIS_PAY, ZIMENFS_PAY, BEAR_PICTURE, TARANTULA_PICTURE, HONEY_JAR, BEAD, BEAD_PARCEL, BEAD_PARCEL2);
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
						if (hasQuestItems(player, RING_OF_RAVEN))
						{
							htmltext = "30524-04.htm";
						}
						else
						{
							qs.startQuest();
							qs.setMemoStateEx(1, 0);
							giveItems(player, PIPPIS_LETTER_OF_RECOMMENDATION, 1);
							htmltext = "30524-05.htm";
						}
					}
					else
					{
						htmltext = "30524-02.htm";
					}
				}
				else if (player.getClassId() == ClassId.SCAVENGER)
				{
					htmltext = "30524-02a.htm";
				}
				else
				{
					htmltext = "30524-08.htm";
				}
				break;
			}
			case "30524-03.html":
			case "30557-02.html":
			case "30519-06.html":
			{
				htmltext = event;
				break;
			}
			case "reply_1":
			{
				if (hasQuestItems(player, PIPPIS_LETTER_OF_RECOMMENDATION))
				{
					takeItems(player, PIPPIS_LETTER_OF_RECOMMENDATION, 1);
					switch (getRandom(3))
					{
						case 0:
						{
							giveItems(player, ZIMENFS_POTION, 1);
							htmltext = "30519-02.html";
							break;
						}
						case 1:
						{
							giveItems(player, SHARIS_AXE, 1);
							htmltext = "30519-03.html";
							break;
						}
						case 2:
						{
							giveItems(player, BRONKS_INGOT, 1);
							htmltext = "30519-04.html";
							break;
						}
					}
				}
				break;
			}
			case "30519-07.html":
			{
				qs.setMemoStateEx(1, qs.getMemoStateEx(1) + 1);
				htmltext = event;
				break;
			}
			case "reply_2":
			{
				switch (getRandom(2))
				{
					case 0:
					{
						htmltext = "30519-06.html";
						break;
					}
					case 1:
					{
						htmltext = "30519-11.html";
						break;
					}
				}
				break;
			}
			case "reply_3":
			{
				if ((qs.getMemoStateEx(1) % 10) < 2)
				{
					qs.setMemoStateEx(1, qs.getMemoStateEx(1) + 1);
					htmltext = "30519-07.html";
				}
				else if (((qs.getMemoStateEx(1) % 10) == 2) && qs.isMemoState(0))
				{
					htmltext = "30519-07.html";
				}
				else if (((qs.getMemoStateEx(1) % 10) == 2) && qs.isMemoState(1))
				{
					qs.setMemoStateEx(1, qs.getMemoStateEx(1) + 1);
					htmltext = "30519-09.html";
				}
				else if (((qs.getMemoStateEx(1) % 10) >= 3) && qs.isMemoState(1))
				{
					giveItems(player, MIONS_LETTER, 1);
					takeItems(player, SHARIS_AXE, 1);
					takeItems(player, ZIMENFS_POTION, 1);
					takeItems(player, BRONKS_INGOT, 1);
					qs.setCond(4, true);
					htmltext = "30519-10.html";
				}
				break;
			}
			case "reply_4":
			{
				takeItems(player, ZIMENFS_PAY, 1);
				takeItems(player, SHARIS_PAY, 1);
				takeItems(player, BRONKS_PAY, 1);
				switch (getRandom(3))
				{
					case 0:
					{
						giveItems(player, ZIMENFS_POTION, 1);
						htmltext = "30519-02.html";
						break;
					}
					case 1:
					{
						giveItems(player, SHARIS_AXE, 1);
						htmltext = "30519-03.html";
						break;
					}
					case 2:
					{
						giveItems(player, BRONKS_INGOT, 1);
						htmltext = "30519-04.html";
						break;
					}
				}
				break;
			}
			case "30556-05b.html":
			{
				if (hasQuestItems(player, TARANTULA_PICTURE) && (getQuestItemsCount(player, BEAD) >= 20))
				{
					takeItems(player, TARANTULA_PICTURE, 1);
					takeItems(player, BEAD, -1);
					giveItems(player, BEAD_PARCEL, 1);
					qs.setCond(9, true);
					htmltext = event;
				}
				break;
			}
			case "30556-06b.html":
			{
				if (hasQuestItems(player, TARANTULA_PICTURE) && (getQuestItemsCount(player, BEAD) >= 20))
				{
					takeItems(player, TARANTULA_PICTURE, 1);
					takeItems(player, BEAD, -1);
					giveItems(player, BEAD_PARCEL2, 1);
					qs.setMemoState(2);
					qs.setCond(12, true);
					htmltext = event;
				}
				break;
			}
			case "30316-02.html":
			{
				if (hasQuestItems(player, BEAD_PARCEL))
				{
					takeItems(player, BEAD_PARCEL, 1);
					giveItems(player, ROUTS_TELEPORT_SCROLL, 1);
					qs.setCond(10, true);
					htmltext = event;
				}
				break;
			}
			case "30316-03.html":
			{
				if (hasQuestItems(player, BEAD_PARCEL))
				{
					giveItems(player, ROUTS_TELEPORT_SCROLL, 1);
					takeItems(player, BEAD_PARCEL, 1);
					qs.setCond(10, true);
					htmltext = event;
				}
				break;
			}
			case "30557-03.html":
			{
				if (hasQuestItems(player, ROUTS_TELEPORT_SCROLL))
				{
					takeItems(player, ROUTS_TELEPORT_SCROLL, 1);
					giveItems(player, SUCCUBUS_UNDIES, 1);
					qs.setCond(11, true);
					npc.deleteMe();
					htmltext = event;
				}
				break;
			}
			case "31958-02.html":
			{
				if (qs.isMemoState(2) && hasQuestItems(player, BEAD_PARCEL2))
				{
					giveItems(player, RING_OF_RAVEN, 1);
					final int level = player.getLevel();
					if (level >= 20)
					{
						addExpAndSp(player, 80314, 5087);
					}
					else if (level >= 19)
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
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, Skill skill)
	{
		final QuestState qs = getQuestState(attacker, false);
		if ((qs != null) && qs.isStarted())
		{
			switch (npc.getId())
			{
				
				case HUNTER_BEAR:
				{
					switch (npc.getScriptValue())
					{
						case 0:
						{
							npc.setScriptValue(1);
							npc.getVariables().set(FIRST_ATTACKER, attacker.getObjectId());
							break;
						}
						case 1:
						{
							if (npc.getVariables().getInt(FIRST_ATTACKER) != attacker.getObjectId())
							{
								npc.setScriptValue(2);
							}
							break;
						}
					}
					break;
				}
				case HUNTER_TARANTULA:
				case PLUNDER_TARANTULA:
				case HONEY_BEAR:
				{
					if (npc.isScriptValue(0))
					{
						npc.setScriptValue(1);
						npc.getVariables().set(FIRST_ATTACKER, attacker.getObjectId());
					}
					
					if (((L2MonsterInstance) npc).getSpoilerObjectId() == attacker.getObjectId())
					{
						npc.setScriptValue(2);
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(1500, npc, killer, true) && npc.isAttackable())
		{
			final boolean firstAttacker = (killer.getObjectId() == npc.getVariables().getInt(FIRST_ATTACKER));
			switch (npc.getId())
			{
				case HUNTER_BEAR:
				{
					if (firstAttacker && hasQuestItems(killer, BEAR_PICTURE) && (getQuestItemsCount(killer, HONEY_JAR) < 5))
					{
						final int flag = qs.getInt(FLAG);
						if ((flag > 0) && (getRandom(100) < (20 * flag)))
						{
							addSpawn(HONEY_BEAR, npc, true, 0, true);
							qs.set(FLAG, 0);
						}
						else
						{
							qs.set(FLAG, flag + 1);
						}
					}
					break;
				}
				case HONEY_BEAR:
				{
					if (firstAttacker && ((L2Attackable) npc).isSpoiled() && hasQuestItems(killer, BEAR_PICTURE))
					{
						if (giveItemRandomly(killer, npc, HONEY_JAR, 1, 5, 50.0, true))
						{
							qs.setCond(6);
						}
					}
					break;
				}
				case HUNTER_TARANTULA:
				case PLUNDER_TARANTULA:
				{
					if (firstAttacker && ((L2Attackable) npc).isSpoiled() && hasQuestItems(killer, TARANTULA_PICTURE))
					{
						if (giveItemRandomly(killer, npc, BEAD, 1, 20, 50.0, true))
						{
							qs.setCond(8);
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
			if (npc.getId() == COLLECTOR_PIPI)
			{
				htmltext = "30524-01.htm";
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == COLLECTOR_PIPI)
			{
				return htmltext;
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case COLLECTOR_PIPI:
				{
					if (hasQuestItems(player, PIPPIS_LETTER_OF_RECOMMENDATION))
					{
						htmltext = "30524-06.html";
					}
					else
					{
						htmltext = "30524-07.html";
					}
					break;
				}
				case TRADER_MION:
				{
					if (hasQuestItems(player, PIPPIS_LETTER_OF_RECOMMENDATION))
					{
						qs.setCond(2, true);
						htmltext = "30519-01.html";
					}
					else if ((getQuestItemsCount(player, SHARIS_AXE) + getQuestItemsCount(player, BRONKS_INGOT) + getQuestItemsCount(player, ZIMENFS_POTION)) == 1)
					{
						if ((qs.getMemoStateEx(1) % 10) == 0)
						{
							htmltext = "30519-05.html";
						}
						else if ((qs.getMemoStateEx(1) % 10) > 0)
						{
							htmltext = "30519-08.html";
						}
					}
					else if ((getQuestItemsCount(player, SHARIS_PAY) + getQuestItemsCount(player, BRONKS_PAY) + getQuestItemsCount(player, ZIMENFS_PAY)) == 1)
					{
						if (qs.getMemoStateEx(1) < 50)
						{
							htmltext = "30519-12.html";
						}
						else
						{
							giveItems(player, MIONS_LETTER, 1);
							takeItems(player, SHARIS_PAY, 1);
							takeItems(player, ZIMENFS_PAY, 1);
							takeItems(player, BRONKS_PAY, 1);
							qs.setCond(4, true);
							htmltext = "30519-15.html";
						}
					}
					else if (hasQuestItems(player, MIONS_LETTER))
					{
						htmltext = "30519-13.html";
					}
					else if (hasAtLeastOneQuestItem(player, BEAR_PICTURE, TARANTULA_PICTURE, BEAD_PARCEL, ROUTS_TELEPORT_SCROLL, SUCCUBUS_UNDIES))
					{
						htmltext = "30519-14.html";
					}
					break;
				}
				case TRADER_SHARI:
				{
					if (hasQuestItems(player, SHARIS_AXE))
					{
						if (qs.getMemoStateEx(1) < 20)
						{
							takeItems(player, SHARIS_AXE, 1);
							giveItems(player, SHARIS_PAY, 1);
							qs.setMemoStateEx(1, qs.getMemoStateEx(1) + 10);
							htmltext = "30517-01.html";
						}
						else
						{
							takeItems(player, SHARIS_AXE, 1);
							giveItems(player, SHARIS_PAY, 1);
							qs.setMemoState(1);
							qs.setMemoStateEx(1, qs.getMemoStateEx(1) + 10);
							qs.setCond(3, true);
							htmltext = "30517-02.html";
						}
					}
					else if (hasQuestItems(player, SHARIS_PAY))
					{
						htmltext = "30517-03.html";
					}
					break;
				}
				case HEAD_BLACKSMITH_BRONK:
				{
					if (hasQuestItems(player, BRONKS_INGOT))
					{
						if (qs.getMemoStateEx(1) < 20)
						{
							takeItems(player, BRONKS_INGOT, 1);
							giveItems(player, BRONKS_PAY, 1);
							qs.setMemoStateEx(1, qs.getMemoStateEx(1) + 10);
							htmltext = "30525-01.html";
						}
						else
						{
							takeItems(player, BRONKS_INGOT, 1);
							giveItems(player, BRONKS_PAY, 1);
							qs.setMemoState(1);
							qs.setMemoStateEx(1, qs.getMemoStateEx(1) + 10);
							qs.setCond(3, true);
							htmltext = "30525-02.html";
						}
					}
					else if (hasQuestItems(player, BRONKS_PAY))
					{
						htmltext = "30525-03.html";
					}
					break;
				}
				case PRIEST_OF_THE_EARTH_ZIMENF:
				{
					if (hasQuestItems(player, ZIMENFS_POTION))
					{
						if (qs.getMemoStateEx(1) < 20)
						{
							takeItems(player, ZIMENFS_POTION, 1);
							giveItems(player, ZIMENFS_PAY, 1);
							qs.setMemoStateEx(1, qs.getMemoStateEx(1) + 10);
							htmltext = "30538-01.html";
						}
						else
						{
							takeItems(player, ZIMENFS_POTION, 1);
							giveItems(player, ZIMENFS_PAY, 1);
							qs.setMemoState(1);
							qs.setMemoStateEx(1, qs.getMemoStateEx(1) + 10);
							qs.setCond(3, true);
							htmltext = "30538-02.html";
						}
					}
					else if (hasQuestItems(player, ZIMENFS_PAY))
					{
						htmltext = "30538-03.html";
					}
					break;
				}
				case MASTER_TOMA:
				{
					if (hasQuestItems(player, MIONS_LETTER))
					{
						takeItems(player, MIONS_LETTER, 1);
						giveItems(player, BEAR_PICTURE, 1);
						qs.setCond(5, true);
						qs.set(FLAG, 0);
						htmltext = "30556-01.html";
					}
					else if (hasQuestItems(player, BEAR_PICTURE))
					{
						if (getQuestItemsCount(player, HONEY_JAR) < 5)
						{
							htmltext = "30556-02.html";
						}
						else
						{
							takeItems(player, BEAR_PICTURE, 1);
							giveItems(player, TARANTULA_PICTURE, 1);
							takeItems(player, HONEY_JAR, -1);
							qs.setCond(7, true);
							htmltext = "30556-03.html";
						}
					}
					else if (hasQuestItems(player, TARANTULA_PICTURE))
					{
						if (getQuestItemsCount(player, BEAD) < 20)
						{
							htmltext = "30556-04.html";
						}
						else
						{
							htmltext = "30556-05a.html";
						}
					}
					else if (hasQuestItems(player, BEAD_PARCEL) && !hasQuestItems(player, BEAD_PARCEL2))
					{
						htmltext = "30556-06a.html";
					}
					else if (hasQuestItems(player, BEAD_PARCEL2) && !hasQuestItems(player, BEAD_PARCEL) && qs.isMemoState(2))
					{
						htmltext = "30556-06c.html";
					}
					else if (hasAtLeastOneQuestItem(player, ROUTS_TELEPORT_SCROLL, SUCCUBUS_UNDIES))
					{
						htmltext = "30556-07.html";
					}
					break;
				}
				case WAREHOUSE_KEEPER_RAUT:
				{
					if (hasQuestItems(player, BEAD_PARCEL))
					{
						htmltext = "30316-01.html";
					}
					else if (hasQuestItems(player, ROUTS_TELEPORT_SCROLL))
					{
						htmltext = "30316-04.html";
					}
					else if (hasQuestItems(player, SUCCUBUS_UNDIES))
					{
						giveItems(player, RING_OF_RAVEN, 1);
						final int level = player.getLevel();
						if (level >= 20)
						{
							addExpAndSp(player, 80314, 5087);
						}
						else if (level >= 19)
						{
							addExpAndSp(player, 80314, 5087);
						}
						else
						{
							addExpAndSp(player, 80314, 5087);
						}
						qs.exitQuest(false, true);
						player.sendPacket(new SocialAction(player.getObjectId(), 3));
						htmltext = "30316-05.html";
					}
					break;
				}
				case TORAI:
				{
					if (hasQuestItems(player, ROUTS_TELEPORT_SCROLL))
					{
						htmltext = "30557-01.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}
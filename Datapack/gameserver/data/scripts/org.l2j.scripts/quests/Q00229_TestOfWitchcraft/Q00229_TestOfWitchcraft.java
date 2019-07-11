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
package quests.Q00229_TestOfWitchcraft;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Test Of Witchcraft (229)
 * @author ivantotov
 */
public final class Q00229_TestOfWitchcraft extends Quest
{
	// NPCs
	private static final int GROCER_LARA = 30063;
	private static final int TRADER_ALEXANDRIA = 30098;
	private static final int MAGISTER_IKER = 30110;
	private static final int PRIEST_VADIN = 30188;
	private static final int TRADER_NESTLE = 30314;
	private static final int SIR_KLAUS_VASPER = 30417;
	private static final int LEOPOLD = 30435;
	private static final int MAGISTER_KAIRA = 30476;
	private static final int SHADOW_ORIM = 30630;
	private static final int WARDEN_RODERIK = 30631;
	private static final int WARDEN_ENDRIGO = 30632;
	private static final int FISHER_EVERT = 30633;
	// Items
	private static final int SWORD_OF_BINDING = 3029;
	private static final int ORIMS_DIAGRAM = 3308;
	private static final int ALEXANDRIAS_BOOK = 3309;
	private static final int IKERS_LIST = 3310;
	private static final int DIRE_WYRM_FANG = 3311;
	private static final int LETO_LIZARDMAN_CHARM = 3312;
	private static final int ENCHANTED_STONE_GOLEM_HEARTSTONE = 3313;
	private static final int LARAS_MEMO = 3314;
	private static final int NESTLES_MEMO = 3315;
	private static final int LEOPOLDS_JOURNAL = 3316;
	private static final int AKLANTOTH_1ST_GEM = 3317;
	private static final int AKLANTOTH_2ND_GEM = 3318;
	private static final int AKLANTOTH_3RD_GEM = 3319;
	private static final int AKLANTOTH_4TH_GEM = 3320;
	private static final int AKLANTOTH_5TH_GEM = 3321;
	private static final int AKLANTOTH_6TH_GEM = 3322;
	private static final int BRIMSTONE_1ST = 3323;
	private static final int ORIMS_INSTRUCTIONS = 3324;
	private static final int ORIMS_1ST_LETTER = 3325;
	private static final int ORIMS_2ND_LETTER = 3326;
	private static final int SIR_VASPERS_LETTER = 3327;
	private static final int VADINS_CRUCIFIX = 3328;
	private static final int TAMLIN_ORC_AMULET = 3329;
	private static final int VADINS_SANCTIONS = 3330;
	private static final int IKERS_AMULET = 3331;
	private static final int SOULTRAP_CRYSTAL = 3332;
	private static final int PURGATORY_KEY = 3333;
	private static final int ZERUEL_BIND_CRYSTAL = 3334;
	private static final int BRIMSTONE_2ND = 3335;
	// Reward
	private static final int MARK_OF_WITCHCRAFT = 3307;
	// Monster
	private static final int DIRE_WYRM = 20557;
	private static final int ENCHANTED_STONE_GOLEM = 20565;
	private static final int LETO_LIZARDMAN = 20577;
	private static final int LETO_LIZARDMAN_ARCHER = 20578;
	private static final int LETO_LIZARDMAN_SOLDIER = 20579;
	private static final int LETO_LIZARDMAN_WARRIOR = 20580;
	private static final int LETO_LIZARDMAN_SHAMAN = 20581;
	private static final int LETO_LIZARDMAN_OVERLORD = 20582;
	private static final int TAMLIN_ORC = 20601;
	private static final int TAMLIN_ORC_ARCHER = 20602;
	// Quest Monster
	private static final int NAMELESS_REVENANT = 27099;
	private static final int SKELETAL_MERCENARY = 27100;
	private static final int DREVANUL_PRINCE_ZERUEL = 27101;
	// Misc
	private static final int MIN_LEVEL = 39;
	// Locations
	private static final Location DREVANUL_PRINCE_ZERUEL_SPAWN = new Location(13395, 169807, -3708);
	
	public Q00229_TestOfWitchcraft()
	{
		super(229);
		{
			addStartNpc(SHADOW_ORIM);
			addTalkId(SHADOW_ORIM, GROCER_LARA, TRADER_ALEXANDRIA, MAGISTER_IKER, PRIEST_VADIN, TRADER_NESTLE, SIR_KLAUS_VASPER, LEOPOLD, MAGISTER_KAIRA, WARDEN_RODERIK, WARDEN_ENDRIGO, FISHER_EVERT);
			addKillId(DIRE_WYRM, ENCHANTED_STONE_GOLEM, LETO_LIZARDMAN, LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD, TAMLIN_ORC, TAMLIN_ORC_ARCHER, NAMELESS_REVENANT, SKELETAL_MERCENARY, DREVANUL_PRINCE_ZERUEL);
			addAttackId(NAMELESS_REVENANT, SKELETAL_MERCENARY, DREVANUL_PRINCE_ZERUEL);
			registerQuestItems(SWORD_OF_BINDING, ORIMS_DIAGRAM, ALEXANDRIAS_BOOK, IKERS_LIST, DIRE_WYRM_FANG, LETO_LIZARDMAN_CHARM, ENCHANTED_STONE_GOLEM_HEARTSTONE, LARAS_MEMO, NESTLES_MEMO, LEOPOLDS_JOURNAL, AKLANTOTH_1ST_GEM, AKLANTOTH_2ND_GEM, AKLANTOTH_3RD_GEM, AKLANTOTH_4TH_GEM, AKLANTOTH_5TH_GEM, AKLANTOTH_6TH_GEM, BRIMSTONE_1ST, ORIMS_INSTRUCTIONS, ORIMS_1ST_LETTER, ORIMS_2ND_LETTER, SIR_VASPERS_LETTER, VADINS_CRUCIFIX, TAMLIN_ORC_AMULET, VADINS_SANCTIONS, IKERS_AMULET, SOULTRAP_CRYSTAL, PURGATORY_KEY, ZERUEL_BIND_CRYSTAL, BRIMSTONE_2ND);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, Player player)
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
					giveItems(player, ORIMS_DIAGRAM, 1);
				}
				break;
			}
			case "30630-04.htm":
			case "30630-06.htm":
			case "30630-07.htm":
			case "30630-12.htm":
			case "30630-13.htm":
			case "30630-20.htm":
			case "30630-21.htm":
			case "30098-02.htm":
			case "30110-02.htm":
			case "30417-02.htm":
			{
				htmltext = event;
				break;
			}
			case "30630-14.htm":
			{
				if (hasQuestItems(player, ALEXANDRIAS_BOOK))
				{
					takeItems(player, ALEXANDRIAS_BOOK, 1);
					takeItems(player, AKLANTOTH_1ST_GEM, 1);
					takeItems(player, AKLANTOTH_2ND_GEM, 1);
					takeItems(player, AKLANTOTH_3RD_GEM, 1);
					takeItems(player, AKLANTOTH_4TH_GEM, 1);
					takeItems(player, AKLANTOTH_5TH_GEM, 1);
					takeItems(player, AKLANTOTH_6TH_GEM, 1);
					giveItems(player, BRIMSTONE_1ST, 1);
					qs.setCond(4, true);
					addSpawn(DREVANUL_PRINCE_ZERUEL, npc, true, 0, false);
					htmltext = event;
				}
				break;
			}
			case "30630-16.htm":
			{
				if (hasQuestItems(player, BRIMSTONE_1ST))
				{
					takeItems(player, BRIMSTONE_1ST, 1);
					giveItems(player, ORIMS_INSTRUCTIONS, 1);
					giveItems(player, ORIMS_1ST_LETTER, 1);
					giveItems(player, ORIMS_2ND_LETTER, 1);
					qs.setCond(6, true);
					htmltext = event;
				}
				break;
			}
			case "30630-22.htm":
			{
				if (hasQuestItems(player, ZERUEL_BIND_CRYSTAL))
				{
					giveAdena(player, 372154, true);
					giveItems(player, MARK_OF_WITCHCRAFT, 1);
					addExpAndSp(player, 2058244, 141240);
					qs.exitQuest(false, true);
					player.sendPacket(new SocialAction(player.getObjectId(), 3));
					htmltext = event;
				}
				break;
			}
			case "30063-02.htm":
			{
				giveItems(player, LARAS_MEMO, 1);
				htmltext = event;
				break;
			}
			case "30098-03.htm":
			{
				if (hasQuestItems(player, ORIMS_DIAGRAM))
				{
					takeItems(player, ORIMS_DIAGRAM, 1);
					giveItems(player, ALEXANDRIAS_BOOK, 1);
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "30110-03.htm":
			{
				giveItems(player, IKERS_LIST, 1);
				htmltext = event;
				break;
			}
			case "30110-08.htm":
			{
				takeItems(player, ORIMS_2ND_LETTER, 1);
				giveItems(player, IKERS_AMULET, 1);
				giveItems(player, SOULTRAP_CRYSTAL, 1);
				if (hasQuestItems(player, SWORD_OF_BINDING))
				{
					qs.setCond(7, true);
				}
				htmltext = event;
				break;
			}
			case "30314-02.htm":
			{
				giveItems(player, NESTLES_MEMO, 1);
				htmltext = event;
				break;
			}
			case "30417-03.htm":
			{
				if (hasQuestItems(player, ORIMS_1ST_LETTER))
				{
					takeItems(player, ORIMS_1ST_LETTER, 1);
					giveItems(player, SIR_VASPERS_LETTER, 1);
					htmltext = event;
				}
				break;
			}
			case "30435-02.htm":
			{
				if (hasQuestItems(player, NESTLES_MEMO))
				{
					takeItems(player, NESTLES_MEMO, 1);
					giveItems(player, LEOPOLDS_JOURNAL, 1);
					htmltext = event;
				}
				break;
			}
			case "30476-02.htm":
			{
				giveItems(player, AKLANTOTH_2ND_GEM, 1);
				if (hasQuestItems(player, AKLANTOTH_1ST_GEM, AKLANTOTH_3RD_GEM, AKLANTOTH_4TH_GEM, AKLANTOTH_5TH_GEM, AKLANTOTH_6TH_GEM))
				{
					qs.setCond(3, true);
				}
				htmltext = event;
				break;
			}
			case "30633-02.htm":
			{
				giveItems(player, BRIMSTONE_2ND, 1);
				qs.setCond(9, true);
				if (npc.getSummonedNpcCount() < 1)
				{
					addSpawn(npc, DREVANUL_PRINCE_ZERUEL, DREVANUL_PRINCE_ZERUEL_SPAWN, false, 0);
				}
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, Player attacker, int damage, boolean isSummon)
	{
		final QuestState qs = getQuestState(attacker, false);
		if ((qs != null) && qs.isStarted())
		{
			switch (npc.getId())
			{
				case NAMELESS_REVENANT:
				{
					if (npc.isScriptValue(0) && hasQuestItems(attacker, ALEXANDRIAS_BOOK, LARAS_MEMO) && !hasQuestItems(attacker, AKLANTOTH_3RD_GEM))
					{
						npc.setScriptValue(1);
						npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.I_ABSOLUTELY_CANNOT_GIVE_IT_TO_YOU_IT_IS_MY_PRECIOUS_JEWEL));
					}
					break;
				}
				case SKELETAL_MERCENARY:
				{
					if (npc.isScriptValue(0) && hasQuestItems(attacker, LEOPOLDS_JOURNAL) && !hasQuestItems(attacker, AKLANTOTH_4TH_GEM, AKLANTOTH_5TH_GEM, AKLANTOTH_6TH_GEM))
					{
						npc.setScriptValue(1);
						npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.I_ABSOLUTELY_CANNOT_GIVE_IT_TO_YOU_IT_IS_MY_PRECIOUS_JEWEL));
					}
					break;
				}
				case DREVANUL_PRINCE_ZERUEL:
				{
					if (hasQuestItems(attacker, BRIMSTONE_1ST))
					{
						npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.I_LL_TAKE_YOUR_LIVES_LATER));
						npc.deleteMe();
						qs.setCond(5, true);
					}
					else if (hasQuestItems(attacker, ORIMS_INSTRUCTIONS, BRIMSTONE_2ND, SWORD_OF_BINDING, SOULTRAP_CRYSTAL))
					{
						if (npc.isScriptValue(0) && checkWeapon(attacker))
						{
							npc.setScriptValue(1);
							npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.THAT_SWORD_IS_REALLY));
						}
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case DIRE_WYRM:
				{
					if (hasQuestItems(killer, ALEXANDRIAS_BOOK, IKERS_LIST))
					{
						if (getQuestItemsCount(killer, DIRE_WYRM_FANG) < 20)
						{
							giveItems(killer, DIRE_WYRM_FANG, 1);
							if (getQuestItemsCount(killer, DIRE_WYRM_FANG) >= 20)
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
				case ENCHANTED_STONE_GOLEM:
				{
					if (hasQuestItems(killer, ALEXANDRIAS_BOOK, IKERS_LIST))
					{
						if (getQuestItemsCount(killer, ENCHANTED_STONE_GOLEM_HEARTSTONE) < 20)
						{
							giveItems(killer, ENCHANTED_STONE_GOLEM_HEARTSTONE, 1);
							if (getQuestItemsCount(killer, ENCHANTED_STONE_GOLEM_HEARTSTONE) >= 20)
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
				case LETO_LIZARDMAN:
				case LETO_LIZARDMAN_ARCHER:
				case LETO_LIZARDMAN_SOLDIER:
				case LETO_LIZARDMAN_WARRIOR:
				case LETO_LIZARDMAN_SHAMAN:
				case LETO_LIZARDMAN_OVERLORD:
				{
					if (hasQuestItems(killer, ALEXANDRIAS_BOOK, IKERS_LIST))
					{
						if (getQuestItemsCount(killer, LETO_LIZARDMAN_CHARM) < 20)
						{
							giveItems(killer, LETO_LIZARDMAN_CHARM, 1);
							if (getQuestItemsCount(killer, LETO_LIZARDMAN_CHARM) >= 20)
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
				case TAMLIN_ORC:
				case TAMLIN_ORC_ARCHER:
				{
					if (hasQuestItems(killer, VADINS_CRUCIFIX))
					{
						if ((getRandom(100) < 50) && (getQuestItemsCount(killer, TAMLIN_ORC_AMULET) < 20))
						{
							giveItems(killer, TAMLIN_ORC_AMULET, 1);
							if (getQuestItemsCount(killer, TAMLIN_ORC_AMULET) >= 20)
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
				case NAMELESS_REVENANT:
				{
					if (hasQuestItems(killer, ALEXANDRIAS_BOOK, LARAS_MEMO) && !hasQuestItems(killer, AKLANTOTH_3RD_GEM))
					{
						takeItems(killer, LARAS_MEMO, 1);
						giveItems(killer, AKLANTOTH_3RD_GEM, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						if (hasQuestItems(killer, AKLANTOTH_1ST_GEM, AKLANTOTH_2ND_GEM, AKLANTOTH_4TH_GEM, AKLANTOTH_5TH_GEM, AKLANTOTH_6TH_GEM))
						{
							qs.setCond(3);
						}
					}
					break;
				}
				case SKELETAL_MERCENARY:
				{
					if (hasQuestItems(killer, LEOPOLDS_JOURNAL) && !hasQuestItems(killer, AKLANTOTH_4TH_GEM, AKLANTOTH_5TH_GEM, AKLANTOTH_6TH_GEM))
					{
						if (!hasQuestItems(killer, AKLANTOTH_4TH_GEM))
						{
							giveItems(killer, AKLANTOTH_4TH_GEM, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
						else if (!hasQuestItems(killer, AKLANTOTH_5TH_GEM))
						{
							giveItems(killer, AKLANTOTH_5TH_GEM, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
						else if (!hasQuestItems(killer, AKLANTOTH_6TH_GEM))
						{
							takeItems(killer, LEOPOLDS_JOURNAL, 1);
							giveItems(killer, AKLANTOTH_6TH_GEM, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							if (hasQuestItems(killer, AKLANTOTH_1ST_GEM, AKLANTOTH_2ND_GEM, AKLANTOTH_3RD_GEM))
							{
								qs.setCond(3);
							}
						}
					}
					break;
				}
				case DREVANUL_PRINCE_ZERUEL:
				{
					if (hasQuestItems(killer, ORIMS_INSTRUCTIONS, BRIMSTONE_2ND, SWORD_OF_BINDING, SOULTRAP_CRYSTAL))
					{
						if (npc.getKillingBlowWeapon() == SWORD_OF_BINDING)
						{
							npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.NO_I_HAVEN_T_COMPLETELY_FINISHED_THE_COMMAND_FOR_DESTRUCTION_AND_SLAUGHTER_YET));
							takeItems(killer, SOULTRAP_CRYSTAL, 1);
							giveItems(killer, PURGATORY_KEY, 1);
							giveItems(killer, ZERUEL_BIND_CRYSTAL, 1);
							takeItems(killer, BRIMSTONE_2ND, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							qs.setCond(10);
						}
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == SHADOW_ORIM)
			{
				if ((player.getClassId() == ClassId.WIZARD) || (player.getClassId() == ClassId.KNIGHT) || (player.getClassId() == ClassId.PALUS_KNIGHT))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (player.getClassId() == ClassId.WIZARD)
						{
							htmltext = "30630-03.htm";
						}
						else
						{
							htmltext = "30630-05.htm";
						}
					}
					else
					{
						htmltext = "30630-02.htm";
					}
				}
				else
				{
					htmltext = "30630-01.htm";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case SHADOW_ORIM:
				{
					if (hasQuestItems(player, ORIMS_DIAGRAM))
					{
						htmltext = "30630-09.htm";
					}
					else if (hasQuestItems(player, ALEXANDRIAS_BOOK))
					{
						if (hasQuestItems(player, AKLANTOTH_1ST_GEM, AKLANTOTH_2ND_GEM, AKLANTOTH_3RD_GEM, AKLANTOTH_4TH_GEM, AKLANTOTH_5TH_GEM, AKLANTOTH_6TH_GEM))
						{
							htmltext = "30630-11.htm";
						}
						else
						{
							htmltext = "30630-10.htm";
						}
					}
					else if (hasQuestItems(player, BRIMSTONE_1ST))
					{
						htmltext = "30630-15.htm";
					}
					else if (hasQuestItems(player, ORIMS_INSTRUCTIONS) && !hasAtLeastOneQuestItem(player, SWORD_OF_BINDING, SOULTRAP_CRYSTAL))
					{
						htmltext = "30630-17.htm";
					}
					if (hasQuestItems(player, SWORD_OF_BINDING, SOULTRAP_CRYSTAL))
					{
						qs.setCond(8, true);
						htmltext = "30630-18.htm";
					}
					else if (hasQuestItems(player, SWORD_OF_BINDING, ZERUEL_BIND_CRYSTAL))
					{
						htmltext = "30630-19.htm";
					}
					break;
				}
				case GROCER_LARA:
				{
					if (hasQuestItems(player, ALEXANDRIAS_BOOK))
					{
						if (!hasAtLeastOneQuestItem(player, LARAS_MEMO, AKLANTOTH_3RD_GEM))
						{
							htmltext = "30063-01.htm";
						}
						else if (!hasQuestItems(player, AKLANTOTH_3RD_GEM) && hasQuestItems(player, LARAS_MEMO))
						{
							htmltext = "30063-03.htm";
						}
						else if (!hasQuestItems(player, LARAS_MEMO) && hasQuestItems(player, AKLANTOTH_3RD_GEM))
						{
							htmltext = "30063-04.htm";
						}
					}
					else if (hasAtLeastOneQuestItem(player, BRIMSTONE_1ST, ORIMS_INSTRUCTIONS))
					{
						htmltext = "30063-05.htm";
					}
					break;
				}
				case TRADER_ALEXANDRIA:
				{
					if (hasQuestItems(player, ORIMS_DIAGRAM))
					{
						htmltext = "30098-01.htm";
					}
					else if (hasQuestItems(player, ALEXANDRIAS_BOOK))
					{
						htmltext = "30098-04.htm";
					}
					else if (hasQuestItems(player, ORIMS_INSTRUCTIONS, BRIMSTONE_1ST))
					{
						htmltext = "30098-05.htm";
					}
					break;
				}
				case MAGISTER_IKER:
				{
					if (hasQuestItems(player, ALEXANDRIAS_BOOK))
					{
						if (!hasAtLeastOneQuestItem(player, IKERS_LIST, AKLANTOTH_1ST_GEM))
						{
							htmltext = "30110-01.htm";
						}
						else if (hasQuestItems(player, IKERS_LIST))
						{
							if ((getQuestItemsCount(player, DIRE_WYRM_FANG) >= 20) && (getQuestItemsCount(player, LETO_LIZARDMAN_CHARM) >= 20) && (getQuestItemsCount(player, ENCHANTED_STONE_GOLEM_HEARTSTONE) >= 20))
							{
								takeItems(player, IKERS_LIST, 1);
								takeItems(player, DIRE_WYRM_FANG, -1);
								takeItems(player, LETO_LIZARDMAN_CHARM, -1);
								takeItems(player, ENCHANTED_STONE_GOLEM_HEARTSTONE, -1);
								giveItems(player, AKLANTOTH_1ST_GEM, 1);
								if (hasQuestItems(player, AKLANTOTH_2ND_GEM, AKLANTOTH_3RD_GEM, AKLANTOTH_4TH_GEM, AKLANTOTH_5TH_GEM, AKLANTOTH_6TH_GEM))
								{
									qs.setCond(3, true);
								}
								htmltext = "30110-05.htm";
							}
							else
							{
								htmltext = "30110-04.htm";
							}
						}
						else if (!hasQuestItems(player, IKERS_LIST) && hasQuestItems(player, AKLANTOTH_1ST_GEM))
						{
							htmltext = "30110-06.htm";
						}
					}
					else if (hasQuestItems(player, ORIMS_INSTRUCTIONS))
					{
						if (!hasAtLeastOneQuestItem(player, SOULTRAP_CRYSTAL, ZERUEL_BIND_CRYSTAL))
						{
							htmltext = "30110-07.htm";
						}
						else if (!hasQuestItems(player, ZERUEL_BIND_CRYSTAL) && hasQuestItems(player, SOULTRAP_CRYSTAL))
						{
							htmltext = "30110-09.htm";
						}
						else if (!hasQuestItems(player, SOULTRAP_CRYSTAL) && hasQuestItems(player, ZERUEL_BIND_CRYSTAL))
						{
							htmltext = "30110-10.htm";
						}
					}
					break;
				}
				case PRIEST_VADIN:
				{
					if (hasQuestItems(player, ORIMS_INSTRUCTIONS, SIR_VASPERS_LETTER))
					{
						takeItems(player, SIR_VASPERS_LETTER, 1);
						giveItems(player, VADINS_CRUCIFIX, 1);
						htmltext = "30188-01.htm";
					}
					else if (hasQuestItems(player, VADINS_CRUCIFIX))
					{
						if (getQuestItemsCount(player, TAMLIN_ORC_AMULET) < 20)
						{
							htmltext = "30188-02.htm";
						}
						else
						{
							takeItems(player, VADINS_CRUCIFIX, 1);
							takeItems(player, TAMLIN_ORC_AMULET, -1);
							giveItems(player, VADINS_SANCTIONS, 1);
							htmltext = "30188-03.htm";
						}
					}
					else if (hasQuestItems(player, ORIMS_INSTRUCTIONS))
					{
						if (hasQuestItems(player, VADINS_SANCTIONS))
						{
							htmltext = "30188-04.htm";
						}
						else if (hasQuestItems(player, SWORD_OF_BINDING))
						{
							htmltext = "30188-05.htm";
						}
					}
					break;
				}
				case TRADER_NESTLE:
				{
					if (hasQuestItems(player, ALEXANDRIAS_BOOK))
					{
						if (!hasAtLeastOneQuestItem(player, LEOPOLDS_JOURNAL, NESTLES_MEMO, AKLANTOTH_4TH_GEM, AKLANTOTH_5TH_GEM, AKLANTOTH_6TH_GEM))
						{
							htmltext = "30314-01.htm";
						}
						else if (hasQuestItems(player, NESTLES_MEMO) && !hasQuestItems(player, LEOPOLDS_JOURNAL))
						{
							htmltext = "30314-03.htm";
						}
						else if (!hasQuestItems(player, NESTLES_MEMO) && hasAtLeastOneQuestItem(player, LEOPOLDS_JOURNAL, AKLANTOTH_4TH_GEM, AKLANTOTH_5TH_GEM, AKLANTOTH_6TH_GEM))
						{
							htmltext = "30314-04.htm";
						}
					}
					break;
				}
				case SIR_KLAUS_VASPER:
				{
					if (hasQuestItems(player, ORIMS_INSTRUCTIONS))
					{
						if (hasQuestItems(player, ORIMS_1ST_LETTER))
						{
							htmltext = "30417-01.htm";
						}
						else if (hasQuestItems(player, SIR_VASPERS_LETTER))
						{
							htmltext = "30417-04.htm";
						}
						else if (hasQuestItems(player, VADINS_SANCTIONS))
						{
							giveItems(player, SWORD_OF_BINDING, 1);
							takeItems(player, VADINS_SANCTIONS, 1);
							if (hasQuestItems(player, SOULTRAP_CRYSTAL))
							{
								qs.setCond(7, true);
							}
							htmltext = "30417-05.htm";
						}
						else if (hasQuestItems(player, SWORD_OF_BINDING))
						{
							htmltext = "30417-06.htm";
						}
					}
					break;
				}
				case LEOPOLD:
				{
					if (hasQuestItems(player, ALEXANDRIAS_BOOK))
					{
						if (hasQuestItems(player, NESTLES_MEMO) && !hasQuestItems(player, LEOPOLDS_JOURNAL))
						{
							htmltext = "30435-01.htm";
						}
						else if (hasQuestItems(player, LEOPOLDS_JOURNAL) && !hasQuestItems(player, NESTLES_MEMO))
						{
							htmltext = "30435-03.htm";
						}
						else if (hasQuestItems(player, AKLANTOTH_4TH_GEM, AKLANTOTH_5TH_GEM, AKLANTOTH_6TH_GEM))
						{
							htmltext = "30435-04.htm";
						}
					}
					else if (hasAtLeastOneQuestItem(player, BRIMSTONE_1ST, ORIMS_INSTRUCTIONS))
					{
						htmltext = "30435-05.htm";
					}
					break;
				}
				case MAGISTER_KAIRA:
				{
					if (hasQuestItems(player, ALEXANDRIAS_BOOK))
					{
						if (!hasQuestItems(player, AKLANTOTH_2ND_GEM))
						{
							htmltext = "30476-01.htm";
						}
						else
						{
							htmltext = "30476-03.htm";
						}
					}
					else if (hasAtLeastOneQuestItem(player, BRIMSTONE_1ST, ORIMS_INSTRUCTIONS))
					{
						htmltext = "30476-04.htm";
					}
					break;
				}
				case WARDEN_RODERIK:
				{
					if (hasQuestItems(player, ALEXANDRIAS_BOOK) && hasAtLeastOneQuestItem(player, LARAS_MEMO, AKLANTOTH_3RD_GEM))
					{
						htmltext = "30631-01.htm";
					}
					break;
				}
				case WARDEN_ENDRIGO:
				{
					if (hasQuestItems(player, ALEXANDRIAS_BOOK) && hasAtLeastOneQuestItem(player, LARAS_MEMO, AKLANTOTH_3RD_GEM))
					{
						htmltext = "30632-01.htm";
					}
					break;
				}
				case FISHER_EVERT:
				{
					if (hasQuestItems(player, ORIMS_INSTRUCTIONS))
					{
						if (hasQuestItems(player, SOULTRAP_CRYSTAL, SWORD_OF_BINDING) && !hasQuestItems(player, BRIMSTONE_2ND))
						{
							htmltext = "30633-01.htm";
						}
						else if (hasQuestItems(player, SOULTRAP_CRYSTAL, BRIMSTONE_2ND) && !hasQuestItems(player, ZERUEL_BIND_CRYSTAL))
						{
							if (npc.getSummonedNpcCount() < 1)
							{
								addSpawn(npc, DREVANUL_PRINCE_ZERUEL, DREVANUL_PRINCE_ZERUEL_SPAWN, false, 0);
							}
							htmltext = "30633-02.htm";
						}
						else if (hasQuestItems(player, ZERUEL_BIND_CRYSTAL) && !hasAtLeastOneQuestItem(player, SOULTRAP_CRYSTAL, BRIMSTONE_2ND))
						{
							htmltext = "30633-03.htm";
						}
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == SHADOW_ORIM)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
	
	private boolean checkWeapon(Player player)
	{
		Item weapon = player.getActiveWeaponInstance();
		return ((weapon != null) && ((weapon.getId() == SWORD_OF_BINDING)));
	}
}
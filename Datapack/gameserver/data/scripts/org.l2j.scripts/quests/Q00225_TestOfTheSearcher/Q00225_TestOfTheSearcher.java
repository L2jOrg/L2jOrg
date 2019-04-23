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
package quests.Q00225_TestOfTheSearcher;

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
 * Test Of The Searcher (225)
 * @author ivantotov
 */
public final class Q00225_TestOfTheSearcher extends Quest
{
	// NPCs
	private static final int CAPTAIN_ALEX = 30291;
	private static final int TYRA = 30420;
	private static final int TREE = 30627;
	private static final int STRONG_WOODEN_CHEST = 30628;
	private static final int MASTER_LUTHER = 30690;
	private static final int MILITIAMAN_LEIRYNN = 30728;
	private static final int DRUNKARD_BORYS = 30729;
	private static final int BODYGUARD_JAX = 30730;
	// Items
	private static final int LUTHERS_LETTER = 2784;
	private static final int ALEXS_WARRANT = 2785;
	private static final int LEIRYNNS_1ST_ORDER = 2786;
	private static final int DELU_TOTEM = 2787;
	private static final int LEIRYNNS_2ND_ORDER = 2788;
	private static final int CHIEF_KALKIS_FANG = 2789;
	private static final int LEIRYNNS_REPORT = 2790;
	private static final int STRINGE_MAP = 2791;
	private static final int LAMBERTS_MAP = 2792;
	private static final int ALEXS_LETTER = 2793;
	private static final int ALEXS_ORDER = 2794;
	private static final int WINE_CATALOG = 2795;
	private static final int TYRAS_CONTRACT = 2796;
	private static final int RED_SPORE_DUST = 2797;
	private static final int MALRUKIAN_WINE = 2798;
	private static final int OLD_ORDER = 2799;
	private static final int JAXS_DIARY = 2800;
	private static final int TORN_MAP_PIECE_1ST = 2801;
	private static final int TORN_MAP_PIECE_2ND = 2802;
	private static final int SOLTS_MAP = 2803;
	private static final int MAKELS_MAP = 2804;
	private static final int COMBINED_MAP = 2805;
	private static final int RUSTED_KEY = 2806;
	private static final int GOLD_BAR = 2807;
	private static final int ALEXS_RECOMMEND = 2808;
	// Reward
	private static final int MARK_OF_SEARCHER = 2809;
	// Monster
	private static final int HANGMAN_TREE = 20144;
	private static final int ROAD_SCAVENGER = 20551;
	private static final int GIANT_FUNGUS = 20555;
	private static final int DELU_lIZARDMAN_SHAMAN = 20781;
	// Quest Monster
	private static final int NEER_BODYGUARD = 27092;
	private static final int DELU_CHIEF_KALKIS = 27093;
	// Misc
	private static final int MIN_LEVEL = 39;
	
	public Q00225_TestOfTheSearcher()
	{
		super(225);
		addStartNpc(MASTER_LUTHER);
		addTalkId(MASTER_LUTHER, CAPTAIN_ALEX, TYRA, TREE, STRONG_WOODEN_CHEST, MILITIAMAN_LEIRYNN, DRUNKARD_BORYS, BODYGUARD_JAX);
		addKillId(HANGMAN_TREE, ROAD_SCAVENGER, GIANT_FUNGUS, DELU_lIZARDMAN_SHAMAN, NEER_BODYGUARD, DELU_CHIEF_KALKIS);
		addAttackId(DELU_lIZARDMAN_SHAMAN);
		registerQuestItems(LUTHERS_LETTER, ALEXS_WARRANT, LEIRYNNS_1ST_ORDER, DELU_TOTEM, LEIRYNNS_2ND_ORDER, CHIEF_KALKIS_FANG, LEIRYNNS_REPORT, STRINGE_MAP, LAMBERTS_MAP, ALEXS_LETTER, ALEXS_ORDER, WINE_CATALOG, TYRAS_CONTRACT, RED_SPORE_DUST, MALRUKIAN_WINE, OLD_ORDER, JAXS_DIARY, TORN_MAP_PIECE_1ST, TORN_MAP_PIECE_2ND, SOLTS_MAP, MAKELS_MAP, COMBINED_MAP, RUSTED_KEY, GOLD_BAR, ALEXS_RECOMMEND);
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
				if (qs.isCreated())
				{
					qs.startQuest();
					qs.setMemoState(1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					giveItems(player, LUTHERS_LETTER, 1);
				}
				break;
			}
			case "30291-05.html":
			case "30291-01t.html":
			case "30291-06.html":
			case "30730-01a.html":
			case "30730-01b.html":
			case "30730-01c.html":
			case "30730-02.html":
			case "30730-02a.html":
			case "30730-02b.html":
			{
				htmltext = event;
				break;
			}
			case "30291-07.html":
			{
				if (hasQuestItems(player, LEIRYNNS_REPORT, STRINGE_MAP))
				{
					takeItems(player, LEIRYNNS_REPORT, 1);
					takeItems(player, STRINGE_MAP, 1);
					giveItems(player, LAMBERTS_MAP, 1);
					giveItems(player, ALEXS_LETTER, 1);
					giveItems(player, ALEXS_ORDER, 1);
					qs.setCond(8, true);
					htmltext = event;
				}
				break;
			}
			case "30420-01a.html":
			{
				if (hasQuestItems(player, WINE_CATALOG))
				{
					takeItems(player, WINE_CATALOG, 1);
					giveItems(player, TYRAS_CONTRACT, 1);
					qs.setCond(10, true);
					htmltext = event;
				}
				break;
			}
			case "30627-01a.html":
			{
				if (npc.getSummonedNpcCount() < 5)
				{
					giveItems(player, RUSTED_KEY, 1);
					addSpawn(npc, STRONG_WOODEN_CHEST, npc, true, 0);
					qs.setCond(17, true);
					htmltext = event;
				}
				break;
			}
			case "30628-01a.html":
			{
				takeItems(player, RUSTED_KEY, 1);
				giveItems(player, GOLD_BAR, 20);
				qs.setCond(18, true);
				npc.deleteMe();
				htmltext = event;
				break;
			}
			case "30730-01d.html":
			{
				if (hasQuestItems(player, OLD_ORDER))
				{
					takeItems(player, OLD_ORDER, 1);
					giveItems(player, JAXS_DIARY, 1);
					qs.setCond(14, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		final QuestState qs = getQuestState(attacker, false);
		if ((qs != null) && qs.isStarted())
		{
			if (npc.isScriptValue(0) && hasQuestItems(attacker, LEIRYNNS_1ST_ORDER))
			{
				npc.setScriptValue(1);
				addAttackPlayerDesire(addSpawn(NEER_BODYGUARD, npc, true, 200000), attacker);
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case HANGMAN_TREE:
				{
					if (hasQuestItems(killer, JAXS_DIARY) && !hasQuestItems(killer, MAKELS_MAP) && (getQuestItemsCount(killer, TORN_MAP_PIECE_2ND) < 4))
					{
						if (getQuestItemsCount(killer, TORN_MAP_PIECE_2ND) < 3)
						{
							if (getRandom(100) < 50)
							{
								giveItems(killer, TORN_MAP_PIECE_2ND, 1);
								playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
							}
						}
						else if (getRandom(100) < 50)
						{
							takeItems(killer, TORN_MAP_PIECE_2ND, -1);
							giveItems(killer, MAKELS_MAP, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, SOLTS_MAP) >= 1)
							{
								qs.setCond(15);
							}
						}
					}
					break;
				}
				case ROAD_SCAVENGER:
				{
					if (hasQuestItems(killer, JAXS_DIARY) && !hasQuestItems(killer, SOLTS_MAP) && (getQuestItemsCount(killer, TORN_MAP_PIECE_1ST) < 4))
					{
						if (getQuestItemsCount(killer, TORN_MAP_PIECE_1ST) < 3)
						{
							giveItems(killer, TORN_MAP_PIECE_1ST, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
						else
						{
							takeItems(killer, TORN_MAP_PIECE_1ST, -1);
							giveItems(killer, SOLTS_MAP, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, MAKELS_MAP) >= 1)
							{
								qs.setCond(15);
							}
						}
					}
					break;
				}
				case GIANT_FUNGUS:
				{
					if (hasQuestItems(killer, TYRAS_CONTRACT) && (getQuestItemsCount(killer, RED_SPORE_DUST) < 10))
					{
						giveItems(killer, RED_SPORE_DUST, 1);
						if (getQuestItemsCount(killer, RED_SPORE_DUST) >= 10)
						{
							qs.setCond(11, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case DELU_lIZARDMAN_SHAMAN:
				{
					if (hasQuestItems(killer, LEIRYNNS_1ST_ORDER) && (getQuestItemsCount(killer, DELU_TOTEM) < 10))
					{
						giveItems(killer, DELU_TOTEM, 1);
						if (getQuestItemsCount(killer, RED_SPORE_DUST) >= 10)
						{
							qs.setCond(4, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case DELU_CHIEF_KALKIS:
				{
					if (hasQuestItems(killer, LEIRYNNS_2ND_ORDER) && !hasAtLeastOneQuestItem(killer, CHIEF_KALKIS_FANG, STRINGE_MAP))
					{
						giveItems(killer, CHIEF_KALKIS_FANG, 1);
						giveItems(killer, STRINGE_MAP, 1);
						qs.setCond(6, true);
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
			if (npc.getId() == MASTER_LUTHER)
			{
				if ((player.getClassId() == ClassId.ROGUE) || (player.getClassId() == ClassId.ELVEN_SCOUT) || (player.getClassId() == ClassId.ASSASSIN) || (player.getClassId() == ClassId.SCAVENGER))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (player.getClassId() == ClassId.SCAVENGER)
						{
							htmltext = "30690-04.htm";
						}
						else
						{
							htmltext = "30690-03.htm";
						}
					}
					else
					{
						htmltext = "30690-02.html";
					}
				}
				else
				{
					htmltext = "30690-01.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case MASTER_LUTHER:
				{
					if (hasQuestItems(player, LUTHERS_LETTER) && !hasQuestItems(player, ALEXS_RECOMMEND))
					{
						htmltext = "30690-06.html";
					}
					else if (!hasAtLeastOneQuestItem(player, LUTHERS_LETTER, ALEXS_RECOMMEND))
					{
						htmltext = "30690-07.html";
					}
					else if (!hasQuestItems(player, LUTHERS_LETTER) && hasQuestItems(player, ALEXS_RECOMMEND))
					{
						giveAdena(player, 161806, true);
						giveItems(player, MARK_OF_SEARCHER, 1);
						addExpAndSp(player, 894888, 61408);
						qs.exitQuest(false, true);
						player.sendPacket(new SocialAction(player.getObjectId(), 3));
						htmltext = "30690-08.html";
					}
					break;
				}
				case CAPTAIN_ALEX:
				{
					if (hasQuestItems(player, LUTHERS_LETTER))
					{
						takeItems(player, LUTHERS_LETTER, 1);
						giveItems(player, ALEXS_WARRANT, 1);
						qs.setCond(2, true);
						htmltext = "30291-01.html";
					}
					else if (hasQuestItems(player, ALEXS_WARRANT))
					{
						htmltext = "30291-02.html";
					}
					else if (hasAtLeastOneQuestItem(player, LEIRYNNS_1ST_ORDER, LEIRYNNS_2ND_ORDER))
					{
						htmltext = "30291-03.html";
					}
					else if (hasQuestItems(player, LEIRYNNS_REPORT))
					{
						htmltext = "30291-04.html";
					}
					else if (hasQuestItems(player, ALEXS_ORDER))
					{
						if (hasQuestItems(player, ALEXS_LETTER))
						{
							htmltext = "30291-08.html";
						}
						else if (hasAtLeastOneQuestItem(player, OLD_ORDER, JAXS_DIARY))
						{
							htmltext = "30291-09.html";
						}
						else if (hasQuestItems(player, COMBINED_MAP))
						{
							if (getQuestItemsCount(player, GOLD_BAR) == 20)
							{
								takeItems(player, ALEXS_ORDER, 1);
								takeItems(player, COMBINED_MAP, 1);
								takeItems(player, GOLD_BAR, -1);
								giveItems(player, ALEXS_RECOMMEND, 1);
								player.getRadar().removeMarker(10133, 157155, -2383);
								qs.setCond(19, true);
								htmltext = "30291-11.html";
							}
							else
							{
								htmltext = "30291-10.html";
							}
						}
					}
					else if (hasQuestItems(player, ALEXS_RECOMMEND))
					{
						htmltext = "30291-12.html";
					}
					break;
				}
				case TYRA:
				{
					if (hasQuestItems(player, WINE_CATALOG))
					{
						htmltext = "30420-01.html";
					}
					else if (hasQuestItems(player, TYRAS_CONTRACT))
					{
						if (getQuestItemsCount(player, RED_SPORE_DUST) < 10)
						{
							htmltext = "30420-02.html";
						}
						else
						{
							takeItems(player, TYRAS_CONTRACT, 1);
							takeItems(player, RED_SPORE_DUST, -1);
							giveItems(player, MALRUKIAN_WINE, 1);
							qs.setCond(12, true);
							htmltext = "30420-03.html";
						}
					}
					else if (hasAtLeastOneQuestItem(player, JAXS_DIARY, OLD_ORDER, COMBINED_MAP, ALEXS_RECOMMEND, MALRUKIAN_WINE))
					{
						htmltext = "30420-04.html";
					}
					break;
				}
				case TREE:
				{
					if (hasQuestItems(player, COMBINED_MAP))
					{
						if (!hasAtLeastOneQuestItem(player, RUSTED_KEY, GOLD_BAR))
						{
							htmltext = "30627-01.html";
						}
						else if (hasQuestItems(player, RUSTED_KEY) && (getQuestItemsCount(player, GOLD_BAR) >= 20))
						{
							htmltext = "30627-01.html";
						}
					}
					break;
				}
				case STRONG_WOODEN_CHEST:
				{
					if (hasQuestItems(player, RUSTED_KEY))
					{
						htmltext = "30628-01.html";
					}
					break;
				}
				case MILITIAMAN_LEIRYNN:
				{
					if (hasQuestItems(player, ALEXS_WARRANT))
					{
						takeItems(player, ALEXS_WARRANT, 1);
						giveItems(player, LEIRYNNS_1ST_ORDER, 1);
						qs.setCond(3, true);
						htmltext = "30728-01.html";
					}
					else if (hasQuestItems(player, LEIRYNNS_1ST_ORDER))
					{
						if (getQuestItemsCount(player, DELU_TOTEM) < 10)
						{
							htmltext = "30728-02.html";
						}
						else
						{
							takeItems(player, LEIRYNNS_1ST_ORDER, 1);
							takeItems(player, DELU_TOTEM, -1);
							giveItems(player, LEIRYNNS_2ND_ORDER, 1);
							qs.setCond(5, true);
							htmltext = "30728-03.html";
						}
					}
					else if (hasQuestItems(player, LEIRYNNS_2ND_ORDER))
					{
						if (!hasQuestItems(player, CHIEF_KALKIS_FANG))
						{
							htmltext = "30728-04.html";
						}
						else
						{
							takeItems(player, LEIRYNNS_2ND_ORDER, 1);
							takeItems(player, CHIEF_KALKIS_FANG, 1);
							giveItems(player, LEIRYNNS_REPORT, 1);
							qs.setCond(7, true);
							htmltext = "30728-05.html";
						}
					}
					else if (hasQuestItems(player, LEIRYNNS_REPORT))
					{
						htmltext = "30728-06.html";
					}
					else if (hasAtLeastOneQuestItem(player, ALEXS_RECOMMEND, ALEXS_ORDER))
					{
						htmltext = "30728-07.html";
					}
					break;
				}
				case DRUNKARD_BORYS:
				{
					if (hasQuestItems(player, ALEXS_LETTER))
					{
						takeItems(player, ALEXS_LETTER, 1);
						giveItems(player, WINE_CATALOG, 1);
						qs.setCond(9, true);
						htmltext = "30729-01.html";
					}
					else if (hasQuestItems(player, WINE_CATALOG) && !hasQuestItems(player, MALRUKIAN_WINE))
					{
						htmltext = "30729-02.html";
					}
					else if (hasQuestItems(player, MALRUKIAN_WINE) && !hasQuestItems(player, WINE_CATALOG))
					{
						takeItems(player, MALRUKIAN_WINE, 1);
						giveItems(player, OLD_ORDER, 1);
						qs.setCond(13, true);
						htmltext = "30729-03.html";
					}
					else if (hasQuestItems(player, OLD_ORDER))
					{
						htmltext = "30729-04.html";
					}
					else if (hasAtLeastOneQuestItem(player, JAXS_DIARY, COMBINED_MAP, ALEXS_RECOMMEND))
					{
						htmltext = "30729-05.html";
					}
					break;
				}
				case BODYGUARD_JAX:
				{
					if (hasQuestItems(player, OLD_ORDER))
					{
						htmltext = "30730-01.html";
					}
					else if (hasQuestItems(player, JAXS_DIARY))
					{
						if (((getQuestItemsCount(player, SOLTS_MAP) + getQuestItemsCount(player, MAKELS_MAP)) < 2))
						{
							htmltext = "30730-02.html";
						}
						else if (((getQuestItemsCount(player, SOLTS_MAP) + getQuestItemsCount(player, MAKELS_MAP)) == 2))
						{
							takeItems(player, LAMBERTS_MAP, 1);
							takeItems(player, JAXS_DIARY, 1);
							takeItems(player, SOLTS_MAP, 1);
							takeItems(player, MAKELS_MAP, -1);
							giveItems(player, COMBINED_MAP, 1);
							qs.setCond(16, true);
							htmltext = "30730-03.html";
						}
					}
					else if (hasAtLeastOneQuestItem(player, COMBINED_MAP, ALEXS_RECOMMEND))
					{
						htmltext = "30730-04.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == MASTER_LUTHER)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}
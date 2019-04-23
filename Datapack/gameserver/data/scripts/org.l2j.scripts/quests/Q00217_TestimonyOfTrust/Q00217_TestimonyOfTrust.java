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
package quests.Q00217_TestimonyOfTrust;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Util;

/**
 * Testimony Of Trust (217)
 * @author ivantotov
 */
public final class Q00217_TestimonyOfTrust extends Quest
{
	// NPCs
	private static final int HIGH_PRIEST_BIOTIN = 30031;
	private static final int HIERARCH_ASTERIOS = 30154;
	private static final int HIGH_PRIEST_HOLLINT = 30191;
	private static final int TETRARCH_THIFIELL = 30358;
	private static final int MAGISTER_CLAYTON = 30464;
	private static final int SEER_MANAKIA = 30515;
	private static final int IRON_GATES_LOCKIRIN = 30531;
	private static final int FLAME_LORD_KAKAI = 30565;
	private static final int MAESTRO_NIKOLA = 30621;
	private static final int CARDINAL_SERESIN = 30657;
	// Items
	private static final int LETTER_TO_ELF = 2735;
	private static final int LETTER_TO_DARKELF = 2736;
	private static final int LETTER_TO_DWARF = 2737;
	private static final int LETTER_TO_ORC = 2738;
	private static final int LETTER_TO_SERESIN = 2739;
	private static final int SCROLL_OF_DARKELF_TRUST = 2740;
	private static final int SCROLL_OF_ELF_TRUST = 2741;
	private static final int SCROLL_OF_DWARF_TRUST = 2742;
	private static final int SCROLL_OF_ORC_TRUST = 2743;
	private static final int RECOMMENDATION_OF_HOLLIN = 2744;
	private static final int ORDER_OF_ASTERIOS = 2745;
	private static final int BREATH_OF_WINDS = 2746;
	private static final int SEED_OF_VERDURE = 2747;
	private static final int LETTER_OF_THIFIELL = 2748;
	private static final int BLOOD_OF_GUARDIAN_BASILISK = 2749;
	private static final int GIANT_APHID = 2750;
	private static final int STAKATOS_FLUIDS = 2751;
	private static final int BASILISK_PLASMA = 2752;
	private static final int HONEY_DEW = 2753;
	private static final int STAKATO_ICHOR = 2754;
	private static final int ORDER_OF_CLAYTON = 2755;
	private static final int PARASITE_OF_LOTA = 2756;
	private static final int LETTER_TO_MANAKIA = 2757;
	private static final int LETTER_OF_MANAKIA = 2758;
	private static final int LETTER_TO_NICHOLA = 2759;
	private static final int ORDER_OF_NICHOLA = 2760;
	private static final int HEART_OF_PORTA = 2761;
	// Reward
	private static final int MARK_OF_TRUST = 2734;
	// Monster
	private static final int DRYAD = 20013;
	private static final int DRYAD_ELDER = 20019;
	private static final int LIREIN = 20036;
	private static final int LIREIN_ELDER = 20044;
	private static final int ANT_RECRUIT = 20082;
	private static final int ANT_PATROL = 20084;
	private static final int ANT_GUARD = 20086;
	private static final int ANT_SOLDIER = 20087;
	private static final int ANT_WARRIOR_CAPTAIN = 20088;
	private static final int MARSH_STAKATO = 20157;
	private static final int PORTA = 20213;
	private static final int MARSH_STAKATO_WORKER = 20230;
	private static final int MARSH_STAKATO_SOLDIER = 20232;
	private static final int MARSH_STAKATO_DRONE = 20234;
	private static final int GUARDIAN_BASILISK = 20550;
	private static final int WINDSUS = 20553;
	// Quest Monster
	private static final int LUELL_OF_ZEPHYR_WINDS = 27120;
	private static final int ACTEA_OF_VERDANT_WILDS = 27121;
	// Misc
	private static final int MIN_LEVEL = 37;
	
	public Q00217_TestimonyOfTrust()
	{
		super(217);
		addStartNpc(HIGH_PRIEST_HOLLINT);
		addTalkId(HIGH_PRIEST_HOLLINT, HIGH_PRIEST_BIOTIN, HIERARCH_ASTERIOS, TETRARCH_THIFIELL, MAGISTER_CLAYTON, SEER_MANAKIA, IRON_GATES_LOCKIRIN, FLAME_LORD_KAKAI, MAESTRO_NIKOLA, CARDINAL_SERESIN);
		addKillId(DRYAD, DRYAD_ELDER, LIREIN, LIREIN_ELDER, ANT_RECRUIT, ANT_PATROL, ANT_GUARD, ANT_SOLDIER, ANT_WARRIOR_CAPTAIN, MARSH_STAKATO, PORTA, MARSH_STAKATO_WORKER, MARSH_STAKATO_SOLDIER, MARSH_STAKATO_DRONE, GUARDIAN_BASILISK, WINDSUS, LUELL_OF_ZEPHYR_WINDS, ACTEA_OF_VERDANT_WILDS);
		registerQuestItems(LETTER_TO_ELF, LETTER_TO_DARKELF, LETTER_TO_DWARF, LETTER_TO_ORC, LETTER_TO_SERESIN, SCROLL_OF_DARKELF_TRUST, SCROLL_OF_ELF_TRUST, SCROLL_OF_DWARF_TRUST, SCROLL_OF_ORC_TRUST, RECOMMENDATION_OF_HOLLIN, ORDER_OF_ASTERIOS, BREATH_OF_WINDS, SEED_OF_VERDURE, LETTER_OF_THIFIELL, BLOOD_OF_GUARDIAN_BASILISK, GIANT_APHID, STAKATOS_FLUIDS, BASILISK_PLASMA, HONEY_DEW, STAKATO_ICHOR, ORDER_OF_CLAYTON, PARASITE_OF_LOTA, LETTER_TO_MANAKIA, LETTER_OF_MANAKIA, LETTER_TO_NICHOLA, ORDER_OF_NICHOLA, HEART_OF_PORTA);
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
					giveItems(player, LETTER_TO_ELF, 1);
					giveItems(player, LETTER_TO_DARKELF, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				}
				break;
			}
			case "30154-02.html":
			case "30657-02.html":
			{
				htmltext = event;
				break;
			}
			case "30154-03.html":
			{
				if (hasQuestItems(player, LETTER_TO_ELF))
				{
					takeItems(player, LETTER_TO_ELF, 1);
					giveItems(player, ORDER_OF_ASTERIOS, 1);
					qs.setMemoState(2);
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "30358-02.html":
			{
				if (hasQuestItems(player, LETTER_TO_DARKELF))
				{
					takeItems(player, LETTER_TO_DARKELF, 1);
					giveItems(player, LETTER_OF_THIFIELL, 1);
					qs.setMemoState(5);
					qs.setCond(5, true);
					htmltext = event;
				}
				break;
			}
			case "30515-02.html":
			{
				if (hasQuestItems(player, LETTER_TO_MANAKIA))
				{
					takeItems(player, LETTER_TO_MANAKIA, 1);
					qs.setMemoState(11);
					qs.setCond(14, true);
					htmltext = event;
				}
				break;
			}
			case "30531-02.html":
			{
				if (hasQuestItems(player, LETTER_TO_DWARF))
				{
					takeItems(player, LETTER_TO_DWARF, 1);
					giveItems(player, LETTER_TO_NICHOLA, 1);
					qs.setMemoState(15);
					qs.setCond(18, true);
					htmltext = event;
				}
				break;
			}
			case "30565-02.html":
			{
				if (hasQuestItems(player, LETTER_TO_ORC))
				{
					takeItems(player, LETTER_TO_ORC, 1);
					giveItems(player, LETTER_TO_MANAKIA, 1);
					qs.setMemoState(10);
					qs.setCond(13, true);
					htmltext = event;
				}
				break;
			}
			case "30621-02.html":
			{
				if (hasQuestItems(player, LETTER_TO_NICHOLA))
				{
					takeItems(player, LETTER_TO_NICHOLA, 1);
					giveItems(player, ORDER_OF_NICHOLA, 1);
					qs.setMemoState(16);
					qs.setCond(19, true);
					htmltext = event;
				}
				break;
			}
			case "30657-03.html":
			{
				if (qs.isMemoState(8) && hasQuestItems(player, LETTER_TO_SERESIN))
				{
					giveItems(player, LETTER_TO_DWARF, 1);
					giveItems(player, LETTER_TO_ORC, 1);
					takeItems(player, LETTER_TO_SERESIN, 1);
					qs.setMemoState(9);
					qs.setCond(12, true);
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
				case DRYAD:
				case DRYAD_ELDER:
				{
					if (qs.isMemoState(2))
					{
						final int flag = killer.getVariables().getInt("flag", +1);
						if (getRandom(100) < (flag * 33))
						{
							addSpawn(ACTEA_OF_VERDANT_WILDS, npc, true, 200000);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_BEFORE_BATTLE);
						}
					}
					break;
				}
				case LIREIN:
				case LIREIN_ELDER:
				{
					if (qs.isMemoState(2))
					{
						final int flag = killer.getVariables().getInt("flag", +1);
						if (getRandom(100) < (flag * 33))
						{
							addSpawn(LUELL_OF_ZEPHYR_WINDS, npc, true, 200000);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_BEFORE_BATTLE);
						}
					}
					break;
				}
				case ANT_RECRUIT:
				case ANT_GUARD:
				{
					if (qs.isMemoState(6) && (getQuestItemsCount(killer, GIANT_APHID) < 5) && hasQuestItems(killer, ORDER_OF_CLAYTON) && !hasQuestItems(killer, HONEY_DEW))
					{
						if (getQuestItemsCount(killer, GIANT_APHID) >= 4)
						{
							giveItems(killer, HONEY_DEW, 1);
							takeItems(killer, GIANT_APHID, -1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (hasQuestItems(killer, BASILISK_PLASMA, STAKATO_ICHOR))
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, GIANT_APHID, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case ANT_PATROL:
				case ANT_SOLDIER:
				case ANT_WARRIOR_CAPTAIN:
				{
					if (qs.isMemoState(6) && (getQuestItemsCount(killer, GIANT_APHID) < 10) && hasQuestItems(killer, ORDER_OF_CLAYTON) && !hasQuestItems(killer, HONEY_DEW))
					{
						if (getQuestItemsCount(killer, GIANT_APHID) >= 4)
						{
							giveItems(killer, HONEY_DEW, 1);
							takeItems(killer, GIANT_APHID, -1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (hasQuestItems(killer, BASILISK_PLASMA, STAKATO_ICHOR))
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, GIANT_APHID, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MARSH_STAKATO:
				case MARSH_STAKATO_WORKER:
				{
					if (qs.isMemoState(6) && (getQuestItemsCount(killer, STAKATOS_FLUIDS) < 10) && hasQuestItems(killer, ORDER_OF_CLAYTON) && !hasQuestItems(killer, STAKATO_ICHOR))
					{
						if (getQuestItemsCount(killer, STAKATOS_FLUIDS) >= 4)
						{
							giveItems(killer, STAKATO_ICHOR, 1);
							takeItems(killer, STAKATOS_FLUIDS, -1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (hasQuestItems(killer, BASILISK_PLASMA, HONEY_DEW))
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, STAKATOS_FLUIDS, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MARSH_STAKATO_SOLDIER:
				case MARSH_STAKATO_DRONE:
				{
					if (qs.isMemoState(6) && (getQuestItemsCount(killer, STAKATOS_FLUIDS) < 5) && hasQuestItems(killer, ORDER_OF_CLAYTON) && !hasQuestItems(killer, STAKATO_ICHOR))
					{
						if (getQuestItemsCount(killer, STAKATOS_FLUIDS) >= 4)
						{
							giveItems(killer, STAKATO_ICHOR, 1);
							takeItems(killer, STAKATOS_FLUIDS, -1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (hasQuestItems(killer, BASILISK_PLASMA, HONEY_DEW))
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, STAKATOS_FLUIDS, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case PORTA:
				{
					if (qs.isMemoState(16) && !hasQuestItems(killer, HEART_OF_PORTA))
					{
						giveItems(killer, HEART_OF_PORTA, 1);
						if (hasQuestItems(killer, HEART_OF_PORTA))
						{
							qs.setCond(20, true);
						}
					}
					break;
				}
				case GUARDIAN_BASILISK:
				{
					if (qs.isMemoState(6) && (getQuestItemsCount(killer, BLOOD_OF_GUARDIAN_BASILISK) < 10) && hasQuestItems(killer, ORDER_OF_CLAYTON) && !hasQuestItems(killer, BASILISK_PLASMA))
					{
						if (getQuestItemsCount(killer, BLOOD_OF_GUARDIAN_BASILISK) >= 4)
						{
							giveItems(killer, BASILISK_PLASMA, 1);
							takeItems(killer, BLOOD_OF_GUARDIAN_BASILISK, -1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (hasQuestItems(killer, STAKATO_ICHOR, HONEY_DEW))
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, BLOOD_OF_GUARDIAN_BASILISK, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case WINDSUS:
				{
					if (qs.isMemoState(11) && (getQuestItemsCount(killer, PARASITE_OF_LOTA) < 10))
					{
						giveItems(killer, PARASITE_OF_LOTA, 2);
						if (getQuestItemsCount(killer, PARASITE_OF_LOTA) == 10)
						{
							qs.setMemoState(12);
							qs.setCond(15, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case LUELL_OF_ZEPHYR_WINDS:
				{
					if (qs.isMemoState(2) && !hasQuestItems(killer, BREATH_OF_WINDS))
					{
						if (hasQuestItems(killer, SEED_OF_VERDURE))
						{
							giveItems(killer, BREATH_OF_WINDS, 1);
							qs.setMemoState(3);
							qs.setCond(3, true);
						}
						else
						{
							giveItems(killer, BREATH_OF_WINDS, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case ACTEA_OF_VERDANT_WILDS:
				{
					if (qs.isMemoState(2) && !hasQuestItems(killer, SEED_OF_VERDURE))
					{
						if (hasQuestItems(killer, BREATH_OF_WINDS))
						{
							giveItems(killer, SEED_OF_VERDURE, 1);
							qs.setMemoState(3);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							giveItems(killer, SEED_OF_VERDURE, 1);
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
		final int memoState = qs.getMemoState();
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			if (npc.getId() == HIGH_PRIEST_HOLLINT)
			{
				if ((player.getRace() == Race.HUMAN) && (player.getLevel() >= MIN_LEVEL) && player.isInCategory(CategoryType.HUMAN_2ND_GROUP))
				{
					htmltext = "30191-03.htm";
				}
				else if ((player.getRace() == Race.HUMAN) && (player.getLevel() >= MIN_LEVEL) && player.isInCategory(CategoryType.FIRST_CLASS_GROUP))
				{
					htmltext = "30191-01a.html";
				}
				else if ((player.getRace() == Race.HUMAN) && (player.getLevel() >= MIN_LEVEL))
				{
					htmltext = "30191-01b.html";
				}
				else if ((player.getRace() == Race.HUMAN))
				{
					htmltext = "30191-01.html";
				}
				else
				{
					htmltext = "30191-02.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case HIGH_PRIEST_HOLLINT:
				{
					if (memoState == 7)
					{
						if (hasQuestItems(player, SCROLL_OF_ELF_TRUST, SCROLL_OF_DARKELF_TRUST))
						{
							giveItems(player, LETTER_TO_SERESIN, 1);
							takeItems(player, SCROLL_OF_DARKELF_TRUST, 1);
							takeItems(player, SCROLL_OF_ELF_TRUST, 1);
							qs.setMemoState(8);
							qs.setCond(10, true);
							htmltext = "30191-05.html";
						}
					}
					else if (memoState == 18)
					{
						if (hasQuestItems(player, SCROLL_OF_DWARF_TRUST, SCROLL_OF_ORC_TRUST))
						{
							takeItems(player, SCROLL_OF_DWARF_TRUST, 1);
							takeItems(player, SCROLL_OF_ORC_TRUST, 1);
							giveItems(player, RECOMMENDATION_OF_HOLLIN, 1);
							qs.setMemoState(19);
							qs.setCond(23, true);
							htmltext = "30191-06.html";
						}
					}
					else if (memoState == 19)
					{
						htmltext = "30191-07.html";
					}
					else if (memoState == 1)
					{
						htmltext = "30191-08.html";
					}
					else if (memoState == 8)
					{
						htmltext = "30191-09.html";
					}
					break;
				}
				case HIGH_PRIEST_BIOTIN:
				{
					if (memoState == 19)
					{
						if (hasQuestItems(player, RECOMMENDATION_OF_HOLLIN))
						{
							giveAdena(player, 252212, true);
							giveItems(player, MARK_OF_TRUST, 1);
							addExpAndSp(player, 1390298, 92782);
							qs.exitQuest(false, true);
							player.sendPacket(new SocialAction(player.getObjectId(), 3));
							htmltext = "30031-01.html";
						}
					}
					break;
				}
				case HIERARCH_ASTERIOS:
				{
					if (memoState == 1)
					{
						if (hasQuestItems(player, LETTER_TO_ELF))
						{
							htmltext = "30154-01.html";
						}
					}
					else if (memoState == 2)
					{
						if (hasQuestItems(player, ORDER_OF_ASTERIOS))
						{
							htmltext = "30154-04.html";
						}
					}
					else if (memoState == 3)
					{
						if (hasQuestItems(player, BREATH_OF_WINDS, SEED_OF_VERDURE))
						{
							giveItems(player, SCROLL_OF_ELF_TRUST, 1);
							takeItems(player, ORDER_OF_ASTERIOS, 1);
							takeItems(player, BREATH_OF_WINDS, 1);
							takeItems(player, SEED_OF_VERDURE, 1);
							qs.setMemoState(4);
							qs.setCond(4, true);
							htmltext = "30154-05.html";
						}
					}
					else if (memoState == 4)
					{
						htmltext = "30154-06.html";
					}
					break;
				}
				case TETRARCH_THIFIELL:
				{
					if (memoState == 4)
					{
						if (hasQuestItems(player, LETTER_TO_DARKELF))
						{
							htmltext = "30358-01.html";
						}
					}
					else if (memoState == 6)
					{
						if (hasQuestItems(player, ORDER_OF_CLAYTON) && ((getQuestItemsCount(player, STAKATO_ICHOR) + getQuestItemsCount(player, HONEY_DEW) + getQuestItemsCount(player, BASILISK_PLASMA)) == 3))
						{
							giveItems(player, SCROLL_OF_DARKELF_TRUST, 1);
							takeItems(player, BASILISK_PLASMA, -1);
							takeItems(player, HONEY_DEW, -1);
							takeItems(player, STAKATO_ICHOR, -1);
							takeItems(player, ORDER_OF_CLAYTON, 1);
							qs.setMemoState(7);
							qs.setCond(9, true);
							htmltext = "30358-03.html";
						}
					}
					else if (memoState == 7)
					{
						htmltext = "30358-04.html";
					}
					else if (memoState == 5)
					{
						htmltext = "30358-05.html";
					}
					break;
				}
				case MAGISTER_CLAYTON:
				{
					if (memoState == 5)
					{
						if (hasQuestItems(player, LETTER_OF_THIFIELL))
						{
							takeItems(player, LETTER_OF_THIFIELL, 1);
							giveItems(player, ORDER_OF_CLAYTON, 1);
							qs.setMemoState(6);
							qs.setCond(6, true);
							htmltext = "30464-01.html";
						}
					}
					else if (memoState == 6)
					{
						if (hasQuestItems(player, ORDER_OF_CLAYTON) && ((getQuestItemsCount(player, STAKATO_ICHOR) + getQuestItemsCount(player, HONEY_DEW) + getQuestItemsCount(player, BASILISK_PLASMA)) < 3))
						{
							htmltext = "30464-02.html";
						}
						else
						{
							qs.setCond(8, true);
							htmltext = "30464-03.html";
						}
					}
					break;
				}
				case SEER_MANAKIA:
				{
					if (hasQuestItems(player, LETTER_TO_MANAKIA))
					{
						htmltext = "30515-01.html";
					}
					else if (memoState == 11)
					{
						htmltext = "30515-03.html";
					}
					else if (memoState == 12)
					{
						if (getQuestItemsCount(player, PARASITE_OF_LOTA) == 10)
						{
							takeItems(player, PARASITE_OF_LOTA, -1);
							giveItems(player, LETTER_OF_MANAKIA, 1);
							qs.setMemoState(13);
							qs.setCond(16, true);
							htmltext = "30515-04.html";
						}
					}
					else if (memoState == 13)
					{
						htmltext = "30515-05.html";
					}
					break;
				}
				case IRON_GATES_LOCKIRIN:
				{
					if (memoState == 14)
					{
						if (hasQuestItems(player, LETTER_TO_DWARF))
						{
							htmltext = "30531-01.html";
						}
					}
					else if (memoState == 15)
					{
						htmltext = "30531-03.html";
					}
					else if (memoState == 17)
					{
						giveItems(player, SCROLL_OF_DWARF_TRUST, 1);
						qs.setMemoState(18);
						qs.setCond(22, true);
						htmltext = "30531-04.html";
					}
					else if (memoState == 18)
					{
						htmltext = "30531-05.html";
					}
					break;
				}
				case FLAME_LORD_KAKAI:
				{
					if (memoState == 9)
					{
						if (hasQuestItems(player, LETTER_TO_ORC))
						{
							htmltext = "30565-01.html";
						}
					}
					else if (memoState == 10)
					{
						htmltext = "30565-03.html";
					}
					else if (memoState == 13)
					{
						giveItems(player, SCROLL_OF_ORC_TRUST, 1);
						takeItems(player, LETTER_OF_MANAKIA, 1);
						qs.setMemoState(14);
						qs.setCond(17, true);
						htmltext = "30565-04.html";
					}
					else if (memoState == 14)
					{
						htmltext = "30565-05.html";
					}
					break;
				}
				case MAESTRO_NIKOLA:
				{
					if (memoState == 15)
					{
						if (hasQuestItems(player, LETTER_TO_NICHOLA))
						{
							htmltext = "30621-01.html";
						}
					}
					else if (memoState == 16)
					{
						if (!hasQuestItems(player, HEART_OF_PORTA))
						{
							htmltext = "30621-03.html";
						}
						else
						{
							takeItems(player, ORDER_OF_NICHOLA, 1);
							takeItems(player, HEART_OF_PORTA, 1);
							qs.setMemoState(17);
							qs.setCond(21, true);
							htmltext = "30621-04.html";
						}
					}
					else if (memoState == 17)
					{
						htmltext = "30621-05.html";
					}
					break;
				}
				case CARDINAL_SERESIN:
				{
					if (memoState == 8)
					{
						if (hasQuestItems(player, LETTER_TO_SERESIN))
						{
							htmltext = "30657-01.html";
						}
					}
					else if (memoState == 9)
					{
						htmltext = "30657-04.html";
					}
					else if (memoState == 18)
					{
						htmltext = "30657-05.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == HIGH_PRIEST_HOLLINT)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}
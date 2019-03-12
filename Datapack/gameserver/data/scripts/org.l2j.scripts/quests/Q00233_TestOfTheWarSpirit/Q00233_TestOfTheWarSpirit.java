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
package quests.Q00233_TestOfTheWarSpirit;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Util;

/**
 * Test Of The War Spirit (233)
 * @author ivantotov
 */
public final class Q00233_TestOfTheWarSpirit extends Quest
{
	// NPCs
	private static final int PRIESTESS_VIVYAN = 30030;
	private static final int TRADER_SARIEN = 30436;
	private static final int SEER_RACOY = 30507;
	private static final int SEER_SOMAK = 30510;
	private static final int SEER_MANAKIA = 30515;
	private static final int SHADOW_ORIM = 30630;
	private static final int ANCESTOR_MARTANKUS = 30649;
	private static final int SEER_PEKIRON = 30682;
	// Items
	private static final int VENDETTA_TOTEM = 2880;
	private static final int TAMLIN_ORC_HEAD = 2881;
	private static final int WARSPIRIT_TOTEM = 2882;
	private static final int ORIMS_CONTRACT = 2883;
	private static final int PORTAS_EYE = 2884;
	private static final int EXCUROS_SCALE = 2885;
	private static final int MORDEOS_TALON = 2886;
	private static final int BRAKIS_REMAINS1 = 2887;
	private static final int PEKIRONS_TOTEM = 2888;
	private static final int TONARS_SKULL = 2889;
	private static final int TONARS_RIB_BONE = 2890;
	private static final int TONARS_SPINE = 2891;
	private static final int TONARS_ARM_BONE = 2892;
	private static final int TONARS_THIGH_BONE = 2893;
	private static final int TONARS_REMAINS1 = 2894;
	private static final int MANAKIAS_TOTEM = 2895;
	private static final int HERMODTS_SKULL = 2896;
	private static final int HERMODTS_RIB_BONE = 2897;
	private static final int HERMODTS_SPINE = 2898;
	private static final int HERMODTS_ARM_BONE = 2899;
	private static final int HERMODTS_THIGH_BONE = 2900;
	private static final int HERMODTS_REMAINS1 = 2901;
	private static final int RACOYS_TOTEM = 2902;
	private static final int VIVIANTES_LETTER = 2903;
	private static final int INSECT_DIAGRAM_BOOK = 2904;
	private static final int KIRUNAS_SKULL = 2905;
	private static final int KIRUNAS_RIB_BONE = 2906;
	private static final int KIRUNAS_SPINE = 2907;
	private static final int KIRUNAS_ARM_BONE = 2908;
	private static final int KIRUNAS_THIGH_BONE = 2909;
	private static final int KIRUNAS_REMAINS1 = 2910;
	private static final int BRAKIS_REMAINS2 = 2911;
	private static final int TONARS_REMAINS2 = 2912;
	private static final int HERMODTS_REMAINS2 = 2913;
	private static final int KIRUNAS_REMAINS2 = 2914;
	// Reward
	private static final int MARK_OF_WARSPIRIT = 2879;
	// Monster
	private static final int NOBLE_ANT = 20089;
	private static final int NOBLE_ANT_LEADER = 20090;
	private static final int MEDUSA = 20158;
	private static final int PORTA = 20213;
	private static final int EXCURO = 20214;
	private static final int MORDERO = 20215;
	private static final int LETO_LIZARDMAN_SHAMAN = 20581;
	private static final int LETO_LIZARDMAN_OVERLORD = 20582;
	private static final int TAMLIN_ORC = 20601;
	private static final int TAMLIN_ORC_ARCHER = 20602;
	// Quest Monster
	private static final int STENOA_GORGON_QUEEN = 27108;
	// Misc
	private static final int MIN_LEVEL = 39;
	
	public Q00233_TestOfTheWarSpirit()
	{
		super(233);
		addStartNpc(SEER_SOMAK);
		addTalkId(SEER_SOMAK, PRIESTESS_VIVYAN, TRADER_SARIEN, SEER_RACOY, SEER_MANAKIA, SHADOW_ORIM, ANCESTOR_MARTANKUS, SEER_PEKIRON);
		addKillId(NOBLE_ANT, NOBLE_ANT_LEADER, MEDUSA, PORTA, EXCURO, MORDERO, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD, TAMLIN_ORC, TAMLIN_ORC_ARCHER, STENOA_GORGON_QUEEN);
		registerQuestItems(VENDETTA_TOTEM, TAMLIN_ORC_HEAD, WARSPIRIT_TOTEM, ORIMS_CONTRACT, PORTAS_EYE, EXCUROS_SCALE, MORDEOS_TALON, BRAKIS_REMAINS1, PEKIRONS_TOTEM, TONARS_SKULL, TONARS_RIB_BONE, TONARS_SPINE, TONARS_ARM_BONE, TONARS_THIGH_BONE, TONARS_REMAINS1, MANAKIAS_TOTEM, HERMODTS_SKULL, HERMODTS_RIB_BONE, HERMODTS_SPINE, HERMODTS_ARM_BONE, HERMODTS_THIGH_BONE, HERMODTS_REMAINS1, RACOYS_TOTEM, VIVIANTES_LETTER, INSECT_DIAGRAM_BOOK, KIRUNAS_SKULL, KIRUNAS_RIB_BONE, KIRUNAS_SPINE, KIRUNAS_ARM_BONE, KIRUNAS_THIGH_BONE, KIRUNAS_REMAINS1, BRAKIS_REMAINS2, TONARS_REMAINS2, HERMODTS_REMAINS2, KIRUNAS_REMAINS2);
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
				}
				break;
			}
			case "30510-05a.html":
			case "30510-05b.html":
			case "30510-05c.html":
			case "30510-05d.html":
			case "30510-05.html":
			case "30030-02.html":
			case "30030-03.html":
			case "30630-02.html":
			case "30630-03.html":
			case "30649-02.html":
			{
				htmltext = event;
				break;
			}
			case "30030-04.html":
			{
				giveItems(player, VIVIANTES_LETTER, 1);
				htmltext = event;
				break;
			}
			case "30507-02.html":
			{
				giveItems(player, RACOYS_TOTEM, 1);
				htmltext = event;
				break;
			}
			case "30515-02.html":
			{
				giveItems(player, MANAKIAS_TOTEM, 1);
				htmltext = event;
				break;
			}
			case "30630-04.html":
			{
				giveItems(player, ORIMS_CONTRACT, 1);
				htmltext = event;
				break;
			}
			case "30649-03.html":
			{
				if (hasQuestItems(player, TONARS_REMAINS2))
				{
					giveAdena(player, 161806, true);
					giveItems(player, MARK_OF_WARSPIRIT, 1);
					addExpAndSp(player, 894888, 61408);
					qs.exitQuest(false, true);
					player.sendPacket(new SocialAction(player.getObjectId(), 3));
					htmltext = event;
				}
				break;
			}
			case "30682-02.html":
			{
				giveItems(player, PEKIRONS_TOTEM, 1);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(1500, npc, killer, true))
		{
			switch (npc.getId())
			{
				case NOBLE_ANT:
				case NOBLE_ANT_LEADER:
				{
					if (hasQuestItems(killer, RACOYS_TOTEM, INSECT_DIAGRAM_BOOK))
					{
						final int i0 = getRandom(100);
						if (i0 > 65)
						{
							if (!hasQuestItems(killer, KIRUNAS_THIGH_BONE))
							{
								giveItems(killer, KIRUNAS_THIGH_BONE, 1);
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							}
							else if (!hasQuestItems(killer, KIRUNAS_ARM_BONE))
							{
								giveItems(killer, KIRUNAS_ARM_BONE, 1);
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							}
						}
						else if (i0 > 30)
						{
							if (!hasQuestItems(killer, KIRUNAS_SPINE))
							{
								giveItems(killer, KIRUNAS_SPINE, 1);
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							}
							else if (!hasQuestItems(killer, KIRUNAS_RIB_BONE))
							{
								giveItems(killer, KIRUNAS_RIB_BONE, 1);
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							}
						}
						else if (i0 > 0)
						{
							if (!hasQuestItems(killer, KIRUNAS_SKULL))
							{
								giveItems(killer, KIRUNAS_SKULL, 1);
								playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							}
						}
					}
					break;
				}
				case MEDUSA:
				{
					if (hasQuestItems(killer, MANAKIAS_TOTEM))
					{
						if (!hasQuestItems(killer, HERMODTS_RIB_BONE))
						{
							giveItems(killer, HERMODTS_RIB_BONE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else if (!hasQuestItems(killer, HERMODTS_SPINE))
						{
							giveItems(killer, HERMODTS_SPINE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else if (!hasQuestItems(killer, HERMODTS_ARM_BONE))
						{
							giveItems(killer, HERMODTS_ARM_BONE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else if (!hasQuestItems(killer, HERMODTS_THIGH_BONE))
						{
							giveItems(killer, HERMODTS_THIGH_BONE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
					}
					break;
				}
				case PORTA:
				{
					if (hasQuestItems(killer, ORIMS_CONTRACT))
					{
						giveItemRandomly(killer, npc, PORTAS_EYE, 2, 10, 1.0, true);
					}
					break;
				}
				case EXCURO:
				{
					if (hasQuestItems(killer, ORIMS_CONTRACT))
					{
						giveItemRandomly(killer, npc, EXCUROS_SCALE, 5, 10, 1.0, true);
					}
					break;
				}
				case MORDERO:
				{
					if (hasQuestItems(killer, ORIMS_CONTRACT))
					{
						giveItemRandomly(killer, npc, MORDEOS_TALON, 5, 10, 1.0, true);
					}
					break;
				}
				case LETO_LIZARDMAN_SHAMAN:
				case LETO_LIZARDMAN_OVERLORD:
				{
					if (hasQuestItems(killer, PEKIRONS_TOTEM))
					{
						if (!hasQuestItems(killer, TONARS_SKULL))
						{
							giveItems(killer, TONARS_SKULL, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else if (!hasQuestItems(killer, TONARS_RIB_BONE))
						{
							giveItems(killer, TONARS_RIB_BONE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else if (!hasQuestItems(killer, TONARS_SPINE))
						{
							giveItems(killer, TONARS_SPINE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else if (!hasQuestItems(killer, TONARS_ARM_BONE))
						{
							giveItems(killer, TONARS_ARM_BONE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else if (!hasQuestItems(killer, TONARS_THIGH_BONE))
						{
							giveItems(killer, TONARS_THIGH_BONE, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
					}
					break;
				}
				case TAMLIN_ORC:
				case TAMLIN_ORC_ARCHER:
				{
					if (hasQuestItems(killer, VENDETTA_TOTEM))
					{
						if (giveItemRandomly(killer, npc, TAMLIN_ORC_HEAD, 1, 13, 1.0, true))
						{
							qs.setCond(4, true);
						}
					}
					break;
				}
				case STENOA_GORGON_QUEEN:
				{
					if (hasQuestItems(killer, MANAKIAS_TOTEM) && !hasQuestItems(killer, HERMODTS_SKULL))
					{
						giveItems(killer, HERMODTS_SKULL, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
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
			if (npc.getId() == SEER_SOMAK)
			{
				if (player.getRace() == Race.ORC)
				{
					if (player.getClassId() == ClassId.ORC_SHAMAN)
					{
						if (player.getLevel() < MIN_LEVEL)
						{
							htmltext = "30510-03.html";
						}
						else
						{
							htmltext = "30510-04.htm";
						}
					}
					else
					{
						htmltext = "30510-02.html";
					}
				}
				else
				{
					htmltext = "30510-01.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case SEER_SOMAK:
				{
					if (!hasAtLeastOneQuestItem(player, VENDETTA_TOTEM, WARSPIRIT_TOTEM))
					{
						if (hasQuestItems(player, BRAKIS_REMAINS1, HERMODTS_REMAINS1, KIRUNAS_REMAINS1, TONARS_REMAINS1))
						{
							giveItems(player, VENDETTA_TOTEM, 1);
							takeItems(player, BRAKIS_REMAINS1, 1);
							takeItems(player, TONARS_REMAINS1, 1);
							takeItems(player, HERMODTS_REMAINS1, 1);
							takeItems(player, KIRUNAS_REMAINS1, 1);
							qs.setCond(3);
							htmltext = "30510-07.html";
						}
						else
						{
							htmltext = "30510-06.html";
						}
					}
					else if (hasQuestItems(player, VENDETTA_TOTEM))
					{
						if (getQuestItemsCount(player, TAMLIN_ORC_HEAD) < 13)
						{
							htmltext = "30510-08.html";
						}
						else
						{
							takeItems(player, VENDETTA_TOTEM, 1);
							giveItems(player, WARSPIRIT_TOTEM, 1);
							giveItems(player, BRAKIS_REMAINS2, 1);
							giveItems(player, TONARS_REMAINS2, 1);
							giveItems(player, HERMODTS_REMAINS2, 1);
							giveItems(player, KIRUNAS_REMAINS2, 1);
							qs.setCond(5);
							htmltext = "30510-09.html";
						}
					}
					else if (hasQuestItems(player, WARSPIRIT_TOTEM))
					{
						htmltext = "30510-10.html";
					}
					break;
				}
				case PRIESTESS_VIVYAN:
				{
					if (hasQuestItems(player, RACOYS_TOTEM) && !hasAtLeastOneQuestItem(player, VIVIANTES_LETTER, INSECT_DIAGRAM_BOOK))
					{
						htmltext = "30030-01.html";
					}
					else if (hasQuestItems(player, RACOYS_TOTEM, VIVIANTES_LETTER) && !hasQuestItems(player, INSECT_DIAGRAM_BOOK))
					{
						htmltext = "30030-05.html";
					}
					else if (hasQuestItems(player, RACOYS_TOTEM, INSECT_DIAGRAM_BOOK) && !hasQuestItems(player, VIVIANTES_LETTER))
					{
						htmltext = "30030-06.html";
					}
					else if (!hasQuestItems(player, RACOYS_TOTEM) && hasAtLeastOneQuestItem(player, KIRUNAS_REMAINS1, KIRUNAS_REMAINS2, VENDETTA_TOTEM))
					{
						htmltext = "30030-07.html";
					}
					break;
				}
				case TRADER_SARIEN:
				{
					if (hasQuestItems(player, RACOYS_TOTEM, VIVIANTES_LETTER) && !hasQuestItems(player, INSECT_DIAGRAM_BOOK))
					{
						takeItems(player, VIVIANTES_LETTER, 1);
						giveItems(player, INSECT_DIAGRAM_BOOK, 1);
						htmltext = "30436-01.html";
					}
					else if (hasQuestItems(player, RACOYS_TOTEM, INSECT_DIAGRAM_BOOK) && !hasQuestItems(player, VIVIANTES_LETTER))
					{
						htmltext = "30436-02.html";
					}
					else if (!hasQuestItems(player, RACOYS_TOTEM) && hasAtLeastOneQuestItem(player, KIRUNAS_REMAINS1, KIRUNAS_REMAINS2, VENDETTA_TOTEM))
					{
						htmltext = "30436-03.html";
					}
					break;
				}
				case SEER_RACOY:
				{
					if (!hasAtLeastOneQuestItem(player, RACOYS_TOTEM, KIRUNAS_REMAINS1, KIRUNAS_REMAINS2, VENDETTA_TOTEM))
					{
						htmltext = "30507-01.html";
					}
					else if (hasQuestItems(player, RACOYS_TOTEM) && !hasAtLeastOneQuestItem(player, VIVIANTES_LETTER, INSECT_DIAGRAM_BOOK))
					{
						htmltext = "30507-03.html";
					}
					else if (hasQuestItems(player, RACOYS_TOTEM, VIVIANTES_LETTER) && !hasQuestItems(player, INSECT_DIAGRAM_BOOK))
					{
						htmltext = "30507-04.html";
					}
					else if (hasQuestItems(player, RACOYS_TOTEM, INSECT_DIAGRAM_BOOK) && !hasQuestItems(player, VIVIANTES_LETTER))
					{
						if (hasQuestItems(player, KIRUNAS_SKULL, KIRUNAS_RIB_BONE, KIRUNAS_SPINE, KIRUNAS_ARM_BONE, KIRUNAS_THIGH_BONE))
						{
							takeItems(player, RACOYS_TOTEM, 1);
							takeItems(player, INSECT_DIAGRAM_BOOK, 1);
							takeItems(player, KIRUNAS_SKULL, 1);
							takeItems(player, KIRUNAS_RIB_BONE, 1);
							takeItems(player, KIRUNAS_SPINE, 1);
							takeItems(player, KIRUNAS_ARM_BONE, 1);
							takeItems(player, KIRUNAS_THIGH_BONE, 1);
							giveItems(player, KIRUNAS_REMAINS1, 1);
							if (hasQuestItems(player, BRAKIS_REMAINS1, HERMODTS_REMAINS1, TONARS_REMAINS1))
							{
								qs.setCond(2);
							}
							htmltext = "30507-06.html";
						}
						else
						{
							htmltext = "30507-05.html";
						}
					}
					else if (!hasQuestItems(player, RACOYS_TOTEM) && hasAtLeastOneQuestItem(player, KIRUNAS_REMAINS1, KIRUNAS_REMAINS2, VENDETTA_TOTEM))
					{
						htmltext = "30507-07.html";
					}
					break;
				}
				case SEER_MANAKIA:
				{
					if (!hasAtLeastOneQuestItem(player, MANAKIAS_TOTEM, HERMODTS_REMAINS2, VENDETTA_TOTEM, HERMODTS_REMAINS1))
					{
						htmltext = "30515-01.html";
					}
					else if (hasQuestItems(player, MANAKIAS_TOTEM))
					{
						if (hasQuestItems(player, HERMODTS_SKULL, HERMODTS_RIB_BONE, HERMODTS_SPINE, HERMODTS_ARM_BONE, HERMODTS_THIGH_BONE))
						{
							takeItems(player, MANAKIAS_TOTEM, 1);
							takeItems(player, HERMODTS_SKULL, 1);
							takeItems(player, HERMODTS_RIB_BONE, 1);
							takeItems(player, HERMODTS_SPINE, 1);
							takeItems(player, HERMODTS_ARM_BONE, 1);
							takeItems(player, HERMODTS_THIGH_BONE, 1);
							giveItems(player, HERMODTS_REMAINS1, 1);
							if (hasQuestItems(player, BRAKIS_REMAINS1, KIRUNAS_REMAINS1, TONARS_REMAINS1))
							{
								qs.setCond(2);
							}
							htmltext = "30515-04.html";
						}
						else
						{
							htmltext = "30515-03.html";
						}
					}
					else if (!hasQuestItems(player, MANAKIAS_TOTEM) && hasAtLeastOneQuestItem(player, HERMODTS_REMAINS1, HERMODTS_REMAINS2, VENDETTA_TOTEM))
					{
						htmltext = "30515-05.html";
					}
					break;
				}
				case SHADOW_ORIM:
				{
					if (!hasAtLeastOneQuestItem(player, ORIMS_CONTRACT, BRAKIS_REMAINS1, BRAKIS_REMAINS2, VENDETTA_TOTEM))
					{
						htmltext = "30630-01.html";
					}
					else if (hasQuestItems(player, ORIMS_CONTRACT))
					{
						if ((getQuestItemsCount(player, PORTAS_EYE) + getQuestItemsCount(player, EXCUROS_SCALE) + getQuestItemsCount(player, MORDEOS_TALON)) < 30)
						{
							htmltext = "30630-05.html";
						}
						else
						{
							takeItems(player, ORIMS_CONTRACT, 1);
							takeItems(player, PORTAS_EYE, -1);
							takeItems(player, EXCUROS_SCALE, -1);
							takeItems(player, MORDEOS_TALON, -1);
							giveItems(player, BRAKIS_REMAINS1, 1);
							if (hasQuestItems(player, HERMODTS_REMAINS1, KIRUNAS_REMAINS1, TONARS_REMAINS1))
							{
								qs.setCond(2);
							}
							htmltext = "30630-06.html";
						}
					}
					else if (!hasQuestItems(player, ORIMS_CONTRACT) && hasAtLeastOneQuestItem(player, BRAKIS_REMAINS1, BRAKIS_REMAINS2, VENDETTA_TOTEM))
					{
						htmltext = "30630-07.html";
					}
					break;
				}
				case ANCESTOR_MARTANKUS:
				{
					if (hasQuestItems(player, WARSPIRIT_TOTEM))
					{
						htmltext = "30649-01.html";
					}
					break;
				}
				case SEER_PEKIRON:
				{
					if (!hasAtLeastOneQuestItem(player, PEKIRONS_TOTEM, TONARS_REMAINS1, TONARS_REMAINS2, VENDETTA_TOTEM))
					{
						htmltext = "30682-01.html";
					}
					else if (hasQuestItems(player, PEKIRONS_TOTEM))
					{
						if (hasQuestItems(player, TONARS_SKULL, TONARS_RIB_BONE, TONARS_SPINE, TONARS_ARM_BONE, TONARS_THIGH_BONE))
						{
							takeItems(player, PEKIRONS_TOTEM, 1);
							takeItems(player, TONARS_SKULL, 1);
							takeItems(player, TONARS_RIB_BONE, 1);
							takeItems(player, TONARS_SPINE, 1);
							takeItems(player, TONARS_ARM_BONE, 1);
							takeItems(player, TONARS_THIGH_BONE, 1);
							giveItems(player, TONARS_REMAINS1, 1);
							if (hasQuestItems(player, BRAKIS_REMAINS1, HERMODTS_REMAINS1, KIRUNAS_REMAINS1))
							{
								qs.setCond(2);
							}
							htmltext = "30682-04.html";
						}
						else
						{
							htmltext = "30682-03.html";
						}
					}
					else if (!hasQuestItems(player, PEKIRONS_TOTEM) && hasAtLeastOneQuestItem(player, TONARS_REMAINS1, TONARS_REMAINS2, VENDETTA_TOTEM))
					{
						htmltext = "30682-05.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == SEER_SOMAK)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}
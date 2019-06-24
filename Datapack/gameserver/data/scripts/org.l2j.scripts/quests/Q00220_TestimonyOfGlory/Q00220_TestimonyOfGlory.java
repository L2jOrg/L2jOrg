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
package quests.Q00220_TestimonyOfGlory;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.GameUtils;

/**
 * Testimony Of Glory (220)
 * @author ivantotov
 */
public final class Q00220_TestimonyOfGlory extends Quest
{
	// NPCs
	private static final int PREFECT_KASMAN = 30501;
	private static final int PREFECT_VOKIAN = 30514;
	private static final int SEER_MANAKIA = 30515;
	private static final int FLAME_LORD_KAKAI = 30565;
	private static final int SEER_TANAPI = 30571;
	private static final int BREKA_CHIEF_VOLTAR = 30615;
	private static final int ENKU_CHIEF_KEPRA = 30616;
	private static final int TUREK_CHIEF_BURAI = 30617;
	private static final int LEUNT_CHIEF_HARAK = 30618;
	private static final int VUKU_CHIEF_DRIKO = 30619;
	private static final int GANDI_CHIEF_CHIANTA = 30642;
	// Items
	private static final int VOKIANS_ORDER = 3204;
	private static final int MANASHEN_SHARD = 3205;
	private static final int TYRANT_TALON = 3206;
	private static final int GUARDIAN_BASILISK_FANG = 3207;
	private static final int VOKIANS_ORDER2 = 3208;
	private static final int NECKLACE_OF_AUTHORITY = 3209;
	private static final int CHIANTA_1ST_ORDER = 3210;
	private static final int SCEPTER_OF_BREKA = 3211;
	private static final int SCEPTER_OF_ENKU = 3212;
	private static final int SCEPTER_OF_VUKU = 3213;
	private static final int SCEPTER_OF_TUREK = 3214;
	private static final int SCEPTER_OF_TUNATH = 3215;
	private static final int CHIANTA_2ND_ORDER = 3216;
	private static final int CHIANTA_3RD_ORDER = 3217;
	private static final int TAMLIN_ORC_SKULL = 3218;
	private static final int TIMAK_ORC_HEAD = 3219;
	private static final int SCEPTER_BOX = 3220;
	private static final int PASHIKAS_HEAD = 3221;
	private static final int VULTUS_HEAD = 3222;
	private static final int GLOVE_OF_VOLTAR = 3223;
	private static final int ENKU_OVERLORD_HEAD = 3224;
	private static final int GLOVE_OF_KEPRA = 3225;
	private static final int MAKUM_BUGBEAR_HEAD = 3226;
	private static final int GLOVE_OF_BURAI = 3227;
	private static final int MANAKIA_1ST_LETTER = 3228;
	private static final int MANAKIA_2ND_LETTER = 3229;
	private static final int KASMANS_1ST_LETTER = 3230;
	private static final int KASMANS_2ND_LETTER = 3231;
	private static final int KASMANS_3RD_LETTER = 3232;
	private static final int DRIKOS_CONTRACT = 3233;
	private static final int STAKATO_DRONE_HUSK = 3234;
	private static final int TANAPIS_ORDER = 3235;
	private static final int SCEPTER_OF_TANTOS = 3236;
	private static final int RITUAL_BOX = 3237;
	// Reward
	private static final int MARK_OF_GLORY = 3203;
	// Monster
	private static final int TYRANT = 20192;
	private static final int TYRANT_KINGPIN = 20193;
	private static final int MARSH_STAKATO_DRONE = 20234;
	private static final int GUARDIAN_BASILISK = 20550;
	private static final int MANASHEN_GARGOYLE = 20563;
	private static final int TIMAK_ORC = 20583;
	private static final int TIMAK_ORC_ARCHER = 20584;
	private static final int TIMAK_ORC_SOLDIER = 20585;
	private static final int TIMAK_ORC_WARRIOR = 20586;
	private static final int TIMAK_ORC_SHAMAN = 20587;
	private static final int TIMAK_ORC_OVERLORD = 20588;
	private static final int TAMLIN_ORC = 20601;
	private static final int TAMLIN_ORC_ARCHER = 20602;
	private static final int RAGNA_ORC_OVERLORD = 20778;
	private static final int RAGNA_ORC_SEER = 20779;
	// Quest Monster
	private static final int PASHIKA_SON_OF_VOLTAR = 27080;
	private static final int VULTUS_SON_OF_VOLTAR = 27081;
	private static final int ENKU_ORC_OVERLORD = 27082;
	private static final int MAKUM_BUGBEAR_THUG = 27083;
	private static final int REVENANT_OF_TANTOS_CHIEF = 27086;
	// Misc
	private static final int MIN_LEVEL = 37;
	
	public Q00220_TestimonyOfGlory()
	{
		super(220);
		addStartNpc(PREFECT_VOKIAN);
		addTalkId(PREFECT_VOKIAN, PREFECT_KASMAN, SEER_MANAKIA, FLAME_LORD_KAKAI, SEER_TANAPI, BREKA_CHIEF_VOLTAR, ENKU_CHIEF_KEPRA, TUREK_CHIEF_BURAI, LEUNT_CHIEF_HARAK, VUKU_CHIEF_DRIKO, GANDI_CHIEF_CHIANTA);
		addKillId(TYRANT, TYRANT_KINGPIN, MARSH_STAKATO_DRONE, GUARDIAN_BASILISK, MANASHEN_GARGOYLE, TIMAK_ORC, TIMAK_ORC_ARCHER, TIMAK_ORC_SOLDIER, TIMAK_ORC_WARRIOR, TIMAK_ORC_SHAMAN, TIMAK_ORC_OVERLORD, TAMLIN_ORC, TAMLIN_ORC_ARCHER, RAGNA_ORC_OVERLORD, RAGNA_ORC_SEER, PASHIKA_SON_OF_VOLTAR, VULTUS_SON_OF_VOLTAR, ENKU_ORC_OVERLORD, MAKUM_BUGBEAR_THUG, REVENANT_OF_TANTOS_CHIEF);
		addAttackId(RAGNA_ORC_OVERLORD, RAGNA_ORC_SEER, REVENANT_OF_TANTOS_CHIEF);
		registerQuestItems(VOKIANS_ORDER, MANASHEN_SHARD, TYRANT_TALON, GUARDIAN_BASILISK_FANG, VOKIANS_ORDER2, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER, SCEPTER_OF_BREKA, SCEPTER_OF_ENKU, SCEPTER_OF_VUKU, SCEPTER_OF_TUREK, SCEPTER_OF_TUNATH, CHIANTA_2ND_ORDER, CHIANTA_3RD_ORDER, TAMLIN_ORC_SKULL, TIMAK_ORC_HEAD, SCEPTER_BOX, PASHIKAS_HEAD, VULTUS_HEAD, GLOVE_OF_VOLTAR, ENKU_OVERLORD_HEAD, GLOVE_OF_KEPRA, MAKUM_BUGBEAR_HEAD, GLOVE_OF_BURAI, MANAKIA_1ST_LETTER, MANAKIA_2ND_LETTER, KASMANS_1ST_LETTER, KASMANS_2ND_LETTER, KASMANS_3RD_LETTER, DRIKOS_CONTRACT, STAKATO_DRONE_HUSK, TANAPIS_ORDER, SCEPTER_OF_TANTOS, RITUAL_BOX);
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
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					giveItems(player, VOKIANS_ORDER, 1);
				}
				break;
			}
			case "30514-04.htm":
			case "30514-07.html":
			case "30571-02.html":
			case "30615-03.html":
			case "30616-03.html":
			case "30642-02.html":
			case "30642-06.html":
			case "30642-08.html":
			{
				htmltext = event;
				break;
			}
			case "30501-02.html":
			{
				if (hasQuestItems(player, SCEPTER_OF_VUKU))
				{
					htmltext = event;
				}
				else if (!hasAtLeastOneQuestItem(player, SCEPTER_OF_VUKU, KASMANS_1ST_LETTER))
				{
					giveItems(player, KASMANS_1ST_LETTER, 1);
					player.getRadar().addMarker(-2150, 124443, -3724);
					htmltext = "30501-03.html";
				}
				else if (!hasQuestItems(player, SCEPTER_OF_VUKU) && hasAtLeastOneQuestItem(player, KASMANS_1ST_LETTER, DRIKOS_CONTRACT))
				{
					player.getRadar().addMarker(-2150, 124443, -3724);
					htmltext = "30501-04.html";
				}
				break;
			}
			case "30501-05.html":
			{
				if (hasQuestItems(player, SCEPTER_OF_TUREK))
				{
					htmltext = event;
				}
				else if (!hasAtLeastOneQuestItem(player, SCEPTER_OF_TUREK, KASMANS_2ND_LETTER))
				{
					giveItems(player, KASMANS_2ND_LETTER, 1);
					player.getRadar().addMarker(-94294, 110818, -3563);
					htmltext = "30501-06.html";
				}
				else if (!hasQuestItems(player, SCEPTER_OF_TUREK) && hasQuestItems(player, KASMANS_2ND_LETTER))
				{
					player.getRadar().addMarker(-94294, 110818, -3563);
					htmltext = "30501-07.html";
				}
				break;
			}
			case "30501-08.html":
			{
				if (hasQuestItems(player, SCEPTER_OF_TUNATH))
				{
					htmltext = event;
				}
				else if (!hasAtLeastOneQuestItem(player, SCEPTER_OF_TUNATH, KASMANS_3RD_LETTER))
				{
					giveItems(player, KASMANS_3RD_LETTER, 1);
					player.getRadar().addMarker(-55217, 200628, -3724);
					htmltext = "30501-09.html";
				}
				else if (!hasQuestItems(player, SCEPTER_OF_TUNATH) && hasQuestItems(player, KASMANS_3RD_LETTER))
				{
					player.getRadar().addMarker(-55217, 200628, -3724);
					htmltext = "30501-10.html";
				}
				break;
			}
			case "30515-04.html":
			{
				if (!hasQuestItems(player, SCEPTER_OF_BREKA) && hasQuestItems(player, MANAKIA_1ST_LETTER))
				{
					player.getRadar().addMarker(80100, 119991, -2264);
					htmltext = event;
				}
				else if (hasQuestItems(player, SCEPTER_OF_BREKA))
				{
					htmltext = "30515-02.html";
				}
				else if (!hasAtLeastOneQuestItem(player, SCEPTER_OF_BREKA, MANAKIA_1ST_LETTER))
				{
					giveItems(player, MANAKIA_1ST_LETTER, 1);
					player.getRadar().addMarker(80100, 119991, -2264);
					htmltext = "30515-03.html";
				}
				break;
			}
			case "30515-05.html":
			{
				if (hasQuestItems(player, SCEPTER_OF_ENKU))
				{
					htmltext = event;
				}
				else if (!hasAtLeastOneQuestItem(player, SCEPTER_OF_ENKU, MANAKIA_2ND_LETTER))
				{
					giveItems(player, MANAKIA_2ND_LETTER, 1);
					player.getRadar().addMarker(12805, 189249, -3616);
					htmltext = "30515-06.html";
				}
				else if (!hasQuestItems(player, SCEPTER_OF_ENKU) && hasQuestItems(player, MANAKIA_2ND_LETTER))
				{
					player.getRadar().addMarker(12805, 189249, -3616);
					htmltext = "30515-07.html";
				}
				break;
			}
			case "30571-03.html":
			{
				if (hasQuestItems(player, SCEPTER_BOX))
				{
					takeItems(player, SCEPTER_BOX, 1);
					giveItems(player, TANAPIS_ORDER, 1);
					qs.setCond(9, true);
					htmltext = event;
				}
				break;
			}
			case "30615-04.html":
			{
				if (hasQuestItems(player, MANAKIA_1ST_LETTER))
				{
					giveItems(player, GLOVE_OF_VOLTAR, 1);
					takeItems(player, MANAKIA_1ST_LETTER, 1);
					addAttackPlayerDesire(addSpawn(npc, PASHIKA_SON_OF_VOLTAR, npc, true, 200000), player);
					addAttackPlayerDesire(addSpawn(npc, VULTUS_SON_OF_VOLTAR, npc, true, 200000), player);
					htmltext = event;
				}
				break;
			}
			case "30616-04.html":
			{
				if (hasQuestItems(player, MANAKIA_2ND_LETTER))
				{
					giveItems(player, GLOVE_OF_KEPRA, 1);
					takeItems(player, MANAKIA_2ND_LETTER, 1);
					addAttackPlayerDesire(addSpawn(npc, ENKU_ORC_OVERLORD, npc, true, 200000), player);
					addAttackPlayerDesire(addSpawn(npc, ENKU_ORC_OVERLORD, npc, true, 200000), player);
					addAttackPlayerDesire(addSpawn(npc, ENKU_ORC_OVERLORD, npc, true, 200000), player);
					addAttackPlayerDesire(addSpawn(npc, ENKU_ORC_OVERLORD, npc, true, 200000), player);
					htmltext = event;
				}
				break;
			}
			case "30617-03.html":
			{
				if (hasQuestItems(player, KASMANS_2ND_LETTER))
				{
					giveItems(player, GLOVE_OF_BURAI, 1);
					takeItems(player, KASMANS_2ND_LETTER, 1);
					addAttackPlayerDesire(addSpawn(npc, MAKUM_BUGBEAR_THUG, npc, true, 200000), player);
					addAttackPlayerDesire(addSpawn(npc, MAKUM_BUGBEAR_THUG, npc, true, 200000), player);
					htmltext = event;
				}
				break;
			}
			case "30618-03.html":
			{
				if (hasQuestItems(player, KASMANS_3RD_LETTER))
				{
					giveItems(player, SCEPTER_OF_TUNATH, 1);
					takeItems(player, KASMANS_3RD_LETTER, 1);
					if (hasQuestItems(player, SCEPTER_OF_TUREK, SCEPTER_OF_ENKU, SCEPTER_OF_BREKA, SCEPTER_OF_VUKU))
					{
						qs.setCond(5, true);
					}
					htmltext = event;
				}
				break;
			}
			case "30619-03.html":
			{
				if (hasQuestItems(player, KASMANS_1ST_LETTER))
				{
					giveItems(player, DRIKOS_CONTRACT, 1);
					takeItems(player, KASMANS_1ST_LETTER, 1);
					htmltext = event;
				}
				break;
			}
			case "30642-03.html":
			{
				if (hasQuestItems(player, VOKIANS_ORDER2))
				{
					takeItems(player, VOKIANS_ORDER2, 1);
					giveItems(player, CHIANTA_1ST_ORDER, 1);
					qs.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "30642-07.html":
			{
				if (hasQuestItems(player, CHIANTA_1ST_ORDER, SCEPTER_OF_BREKA, SCEPTER_OF_VUKU, SCEPTER_OF_TUREK, SCEPTER_OF_TUNATH, SCEPTER_OF_ENKU))
				{
					takeItems(player, CHIANTA_1ST_ORDER, 1);
					takeItems(player, SCEPTER_OF_BREKA, 1);
					takeItems(player, SCEPTER_OF_ENKU, 1);
					takeItems(player, SCEPTER_OF_VUKU, 1);
					takeItems(player, SCEPTER_OF_TUREK, 1);
					takeItems(player, SCEPTER_OF_TUNATH, 1);
					takeItems(player, MANAKIA_1ST_LETTER, 1);
					takeItems(player, MANAKIA_2ND_LETTER, 1);
					takeItems(player, KASMANS_1ST_LETTER, 1);
					giveItems(player, CHIANTA_3RD_ORDER, 1);
					qs.setCond(6, true);
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
			switch (npc.getId())
			{
				case RAGNA_ORC_OVERLORD:
				case RAGNA_ORC_SEER:
				{
					switch (npc.getScriptValue())
					{
						case 0:
						{
							npc.getVariables().set("lastAttacker", attacker.getObjectId());
							if (!hasQuestItems(attacker, SCEPTER_OF_TANTOS))
							{
								npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.IS_IT_A_LACKEY_OF_KAKAI));
								npc.setScriptValue(1);
							}
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
				case REVENANT_OF_TANTOS_CHIEF:
				{
					switch (npc.getScriptValue())
					{
						case 0:
						{
							npc.getVariables().set("lastAttacker", attacker.getObjectId());
							if (!hasQuestItems(attacker, SCEPTER_OF_TANTOS))
							{
								npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.HOW_REGRETFUL_UNJUST_DISHONOR));
								npc.setScriptValue(1);
							}
							break;
						}
						case 1:
						{
							if (!hasQuestItems(attacker, SCEPTER_OF_TANTOS) && (npc.getCurrentHp() < (npc.getMaxHp() / 3)))
							{
								npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.INDIGNANT_AND_UNFAIR_DEATH));
								npc.setScriptValue(2);
							}
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
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, npc, killer, true))
		{
			switch (npc.getId())
			{
				case TYRANT:
				case TYRANT_KINGPIN:
				{
					if (hasQuestItems(killer, VOKIANS_ORDER) && (getQuestItemsCount(killer, TYRANT_TALON) < 10))
					{
						if (getQuestItemsCount(killer, TYRANT_TALON) == 9)
						{
							giveItems(killer, TYRANT_TALON, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, MANASHEN_SHARD) >= 10) && (getQuestItemsCount(killer, GUARDIAN_BASILISK_FANG) >= 10))
							{
								qs.setCond(2);
							}
						}
						else
						{
							giveItems(killer, TYRANT_TALON, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MARSH_STAKATO_DRONE:
				{
					if (!hasQuestItems(killer, SCEPTER_OF_VUKU) && hasQuestItems(killer, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER, DRIKOS_CONTRACT) && (getQuestItemsCount(killer, STAKATO_DRONE_HUSK) < 30))
					{
						if (getQuestItemsCount(killer, TYRANT_TALON) == 29)
						{
							giveItems(killer, STAKATO_DRONE_HUSK, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							giveItems(killer, STAKATO_DRONE_HUSK, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case GUARDIAN_BASILISK:
				{
					if (hasQuestItems(killer, VOKIANS_ORDER) && (getQuestItemsCount(killer, GUARDIAN_BASILISK_FANG) < 10))
					{
						if (getQuestItemsCount(killer, GUARDIAN_BASILISK_FANG) == 9)
						{
							giveItems(killer, GUARDIAN_BASILISK_FANG, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, MANASHEN_SHARD) >= 10) && (getQuestItemsCount(killer, TYRANT_TALON) >= 10))
							{
								qs.setCond(2);
							}
						}
						else
						{
							giveItems(killer, GUARDIAN_BASILISK_FANG, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MANASHEN_GARGOYLE:
				{
					if (hasQuestItems(killer, VOKIANS_ORDER) && (getQuestItemsCount(killer, MANASHEN_SHARD) < 10))
					{
						if (getQuestItemsCount(killer, MANASHEN_SHARD) == 9)
						{
							giveItems(killer, MANASHEN_SHARD, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if ((getQuestItemsCount(killer, TYRANT_TALON) >= 10) && (getQuestItemsCount(killer, GUARDIAN_BASILISK_FANG) >= 10))
							{
								qs.setCond(2);
							}
						}
						else
						{
							giveItems(killer, MANASHEN_SHARD, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case TIMAK_ORC:
				case TIMAK_ORC_ARCHER:
				case TIMAK_ORC_SOLDIER:
				case TIMAK_ORC_WARRIOR:
				case TIMAK_ORC_SHAMAN:
				case TIMAK_ORC_OVERLORD:
				{
					if (hasQuestItems(killer, NECKLACE_OF_AUTHORITY, CHIANTA_3RD_ORDER) && (getQuestItemsCount(killer, TIMAK_ORC_HEAD) < 20))
					{
						if (getQuestItemsCount(killer, MANASHEN_SHARD) == 19)
						{
							giveItems(killer, TIMAK_ORC_HEAD, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, TAMLIN_ORC_SKULL) >= 20)
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, TIMAK_ORC_HEAD, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case TAMLIN_ORC:
				case TAMLIN_ORC_ARCHER:
				{
					if (hasQuestItems(killer, NECKLACE_OF_AUTHORITY, CHIANTA_3RD_ORDER) && (getQuestItemsCount(killer, TAMLIN_ORC_SKULL) < 20))
					{
						if (getQuestItemsCount(killer, TAMLIN_ORC_SKULL) == 19)
						{
							giveItems(killer, TAMLIN_ORC_SKULL, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
							if (getQuestItemsCount(killer, TIMAK_ORC_HEAD) >= 20)
							{
								qs.setCond(7);
							}
						}
						else
						{
							giveItems(killer, TAMLIN_ORC_SKULL, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case RAGNA_ORC_OVERLORD:
				case RAGNA_ORC_SEER:
				{
					if (hasQuestItems(killer, TANAPIS_ORDER) && !hasQuestItems(killer, SCEPTER_OF_TANTOS))
					{
						addSpawn(REVENANT_OF_TANTOS_CHIEF, npc, true, 200000);
						npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.TOO_LATE));
					}
					break;
				}
				case PASHIKA_SON_OF_VOLTAR:
				{
					if (hasQuestItems(killer, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER, GLOVE_OF_VOLTAR) && !hasQuestItems(killer, PASHIKAS_HEAD))
					{
						if (hasQuestItems(killer, VULTUS_HEAD))
						{
							giveItems(killer, PASHIKAS_HEAD, 1);
							takeItems(killer, GLOVE_OF_VOLTAR, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							giveItems(killer, PASHIKAS_HEAD, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case VULTUS_SON_OF_VOLTAR:
				{
					if (hasQuestItems(killer, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER, GLOVE_OF_VOLTAR) && !hasQuestItems(killer, VULTUS_HEAD))
					{
						if (hasQuestItems(killer, PASHIKAS_HEAD))
						{
							giveItems(killer, VULTUS_HEAD, 1);
							takeItems(killer, GLOVE_OF_VOLTAR, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							giveItems(killer, VULTUS_HEAD, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case ENKU_ORC_OVERLORD:
				{
					if (hasQuestItems(killer, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER, GLOVE_OF_KEPRA) && (getQuestItemsCount(killer, ENKU_OVERLORD_HEAD) < 4))
					{
						if (getQuestItemsCount(killer, ENKU_OVERLORD_HEAD) == 3)
						{
							giveItems(killer, ENKU_OVERLORD_HEAD, 1);
							takeItems(killer, GLOVE_OF_KEPRA, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							giveItems(killer, ENKU_OVERLORD_HEAD, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case MAKUM_BUGBEAR_THUG:
				{
					if (hasQuestItems(killer, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER, GLOVE_OF_BURAI) && (getQuestItemsCount(killer, MAKUM_BUGBEAR_HEAD) < 2))
					{
						if (getQuestItemsCount(killer, MAKUM_BUGBEAR_HEAD) == 1)
						{
							giveItems(killer, MAKUM_BUGBEAR_HEAD, 1);
							takeItems(killer, GLOVE_OF_BURAI, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						}
						else
						{
							giveItems(killer, MAKUM_BUGBEAR_HEAD, 1);
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
					break;
				}
				case REVENANT_OF_TANTOS_CHIEF:
				{
					if (hasQuestItems(killer, TANAPIS_ORDER) && !hasQuestItems(killer, SCEPTER_OF_TANTOS))
					{
						giveItems(killer, SCEPTER_OF_TANTOS, 1);
						npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.I_LL_GET_REVENGE_SOMEDAY));
						qs.setCond(10, true);
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
			if (npc.getId() == PREFECT_VOKIAN)
			{
				if (player.getRace() == Race.ORC)
				{
					if ((player.getLevel() >= MIN_LEVEL) && player.isInCategory(CategoryType.ORC_2ND_GROUP))
					{
						htmltext = "30514-03.htm";
					}
					else if (player.getLevel() >= MIN_LEVEL)
					{
						htmltext = "30514-01a.html";
					}
					else
					{
						htmltext = "30514-02.html";
					}
				}
				else
				{
					htmltext = "30514-01.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case PREFECT_VOKIAN:
				{
					if (hasQuestItems(player, VOKIANS_ORDER))
					{
						if ((getQuestItemsCount(player, MANASHEN_SHARD) >= 10) && (getQuestItemsCount(player, TYRANT_TALON) >= 10) && (getQuestItemsCount(player, GUARDIAN_BASILISK_FANG) >= 10))
						{
							takeItems(player, VOKIANS_ORDER, 1);
							takeItems(player, MANASHEN_SHARD, -1);
							takeItems(player, TYRANT_TALON, -1);
							takeItems(player, GUARDIAN_BASILISK_FANG, -1);
							giveItems(player, VOKIANS_ORDER2, 1);
							giveItems(player, NECKLACE_OF_AUTHORITY, 1);
							qs.setCond(3, true);
							htmltext = "30514-08.html";
						}
						else
						{
							htmltext = "30514-06.html";
						}
					}
					else if (hasQuestItems(player, VOKIANS_ORDER2, NECKLACE_OF_AUTHORITY))
					{
						htmltext = "30514-09.html";
					}
					else if (!hasQuestItems(player, NECKLACE_OF_AUTHORITY) && hasAtLeastOneQuestItem(player, VOKIANS_ORDER2, SCEPTER_BOX))
					{
						htmltext = "30514-10.html";
					}
					break;
				}
				case PREFECT_KASMAN:
				{
					if (hasQuestItems(player, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER))
					{
						htmltext = "30501-01.html";
					}
					else if (hasAtLeastOneQuestItem(player, CHIANTA_2ND_ORDER, CHIANTA_3RD_ORDER, SCEPTER_BOX))
					{
						htmltext = "30501-11.html";
					}
					break;
				}
				case SEER_MANAKIA:
				{
					if (hasQuestItems(player, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER))
					{
						htmltext = "30515-01.html";
					}
					else if (hasAtLeastOneQuestItem(player, CHIANTA_2ND_ORDER, CHIANTA_3RD_ORDER, SCEPTER_BOX))
					{
						htmltext = "30515-08.html";
					}
					break;
				}
				case FLAME_LORD_KAKAI:
				{
					if (!hasQuestItems(player, RITUAL_BOX) && hasAtLeastOneQuestItem(player, SCEPTER_BOX, TANAPIS_ORDER))
					{
						htmltext = "30565-01.html";
					}
					else if (hasQuestItems(player, RITUAL_BOX))
					{
						giveAdena(player, 262720, true);
						giveItems(player, MARK_OF_GLORY, 1);
						addExpAndSp(player, 1448226, 96648);
						qs.exitQuest(false, true);
						player.sendPacket(new SocialAction(player.getObjectId(), 3));
						htmltext = "30565-02.html";
					}
					break;
				}
				case SEER_TANAPI:
				{
					if (hasQuestItems(player, SCEPTER_BOX))
					{
						htmltext = "30571-01.html";
					}
					else if (hasQuestItems(player, TANAPIS_ORDER))
					{
						if (!hasQuestItems(player, SCEPTER_OF_TANTOS))
						{
							htmltext = "30571-04.html";
						}
						else
						{
							takeItems(player, TANAPIS_ORDER, 1);
							takeItems(player, SCEPTER_OF_TANTOS, 1);
							giveItems(player, RITUAL_BOX, 1);
							qs.setCond(11, true);
							htmltext = "30571-05.html";
						}
					}
					else if (hasQuestItems(player, RITUAL_BOX))
					{
						htmltext = "30571-06.html";
					}
					break;
				}
				case BREKA_CHIEF_VOLTAR:
				{
					if (hasQuestItems(player, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER))
					{
						if (!hasAtLeastOneQuestItem(player, SCEPTER_OF_BREKA, MANAKIA_1ST_LETTER, GLOVE_OF_VOLTAR, PASHIKAS_HEAD, VULTUS_HEAD))
						{
							htmltext = "30615-01.html";
						}
						else if (hasQuestItems(player, MANAKIA_1ST_LETTER))
						{
							htmltext = "30615-02.html";
							player.getRadar().removeMarker(80100, 119991, -2264);
						}
						else if (!hasQuestItems(player, SCEPTER_OF_BREKA) && hasQuestItems(player, GLOVE_OF_VOLTAR) && ((getQuestItemsCount(player, PASHIKAS_HEAD) + getQuestItemsCount(player, VULTUS_HEAD)) < 2))
						{
							if (npc.getSummonedNpcCount() < 2)
							{
								addAttackPlayerDesire(addSpawn(npc, PASHIKA_SON_OF_VOLTAR, npc, true, 200000), player);
								addAttackPlayerDesire(addSpawn(npc, VULTUS_SON_OF_VOLTAR, npc, true, 200000), player);
							}
							htmltext = "30615-05.html";
						}
						else if (hasQuestItems(player, PASHIKAS_HEAD, VULTUS_HEAD))
						{
							giveItems(player, SCEPTER_OF_BREKA, 1);
							takeItems(player, PASHIKAS_HEAD, 1);
							takeItems(player, VULTUS_HEAD, 1);
							if (hasQuestItems(player, SCEPTER_OF_ENKU, SCEPTER_OF_VUKU, SCEPTER_OF_TUREK, SCEPTER_OF_TUNATH))
							{
								qs.setCond(5, true);
							}
							htmltext = "30615-06.html";
						}
						else if (hasQuestItems(player, SCEPTER_OF_BREKA))
						{
							htmltext = "30615-07.html";
						}
					}
					else if (hasAtLeastOneQuestItem(player, CHIANTA_2ND_ORDER, CHIANTA_3RD_ORDER, SCEPTER_BOX))
					{
						htmltext = "30615-08.html";
					}
					break;
				}
				case ENKU_CHIEF_KEPRA:
				{
					if (hasQuestItems(player, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER))
					{
						if (!hasAtLeastOneQuestItem(player, SCEPTER_OF_ENKU, MANAKIA_2ND_LETTER, GLOVE_OF_KEPRA) && ((getQuestItemsCount(player, ENKU_OVERLORD_HEAD)) < 4))
						{
							htmltext = "30616-01.html";
						}
						else if (hasQuestItems(player, MANAKIA_2ND_LETTER))
						{
							player.getRadar().removeMarker(12805, 189249, -3616);
							htmltext = "30616-02.html";
						}
						else if (hasQuestItems(player, GLOVE_OF_KEPRA) && ((getQuestItemsCount(player, ENKU_OVERLORD_HEAD)) < 4))
						{
							if (npc.getSummonedNpcCount() < 5)
							{
								addAttackPlayerDesire(addSpawn(npc, ENKU_ORC_OVERLORD, npc, true, 200000), player);
							}
							htmltext = "30616-05.html";
						}
						else if (getQuestItemsCount(player, ENKU_OVERLORD_HEAD) >= 4)
						{
							giveItems(player, SCEPTER_OF_ENKU, 1);
							takeItems(player, ENKU_OVERLORD_HEAD, -1);
							if (hasQuestItems(player, SCEPTER_OF_BREKA, SCEPTER_OF_VUKU, SCEPTER_OF_TUREK, SCEPTER_OF_TUNATH))
							{
								qs.setCond(5, true);
							}
							htmltext = "30616-06.html";
						}
						else if (hasQuestItems(player, SCEPTER_OF_ENKU))
						{
							htmltext = "30616-07.html";
						}
					}
					else if (hasAtLeastOneQuestItem(player, CHIANTA_2ND_ORDER, CHIANTA_3RD_ORDER, SCEPTER_BOX))
					{
						htmltext = "30616-08.html";
					}
					break;
				}
				case TUREK_CHIEF_BURAI:
				{
					if (hasQuestItems(player, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER))
					{
						if (!hasAtLeastOneQuestItem(player, SCEPTER_OF_TUREK, KASMANS_2ND_LETTER, GLOVE_OF_BURAI, MAKUM_BUGBEAR_HEAD))
						{
							htmltext = "30617-01.html";
						}
						else if (hasQuestItems(player, KASMANS_2ND_LETTER))
						{
							player.getRadar().removeMarker(-94294, 110818, -3563);
							htmltext = "30617-02.html";
						}
						else if (hasQuestItems(player, GLOVE_OF_BURAI))
						{
							if (npc.getSummonedNpcCount() < 3)
							{
								addAttackPlayerDesire(addSpawn(npc, MAKUM_BUGBEAR_THUG, npc, true, 200000), player);
								addAttackPlayerDesire(addSpawn(npc, MAKUM_BUGBEAR_THUG, npc, true, 200000), player);
							}
							htmltext = "30617-04.html";
						}
						else if (getQuestItemsCount(player, MAKUM_BUGBEAR_HEAD) >= 2)
						{
							giveItems(player, SCEPTER_OF_TUREK, 1);
							takeItems(player, MAKUM_BUGBEAR_HEAD, -1);
							if (hasQuestItems(player, SCEPTER_OF_ENKU, SCEPTER_OF_BREKA, SCEPTER_OF_VUKU, SCEPTER_OF_TUNATH))
							{
								qs.setCond(5, true);
							}
							htmltext = "30617-05.html";
						}
						else if (hasQuestItems(player, SCEPTER_OF_TUREK))
						{
							htmltext = "30617-06.html";
						}
					}
					else if (hasQuestItems(player, NECKLACE_OF_AUTHORITY) && hasAtLeastOneQuestItem(player, CHIANTA_2ND_ORDER, CHIANTA_3RD_ORDER, SCEPTER_BOX))
					{
						htmltext = "30617-07.html";
					}
					break;
				}
				case LEUNT_CHIEF_HARAK:
				{
					if (hasQuestItems(player, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER))
					{
						if (!hasAtLeastOneQuestItem(player, SCEPTER_OF_TUNATH, KASMANS_3RD_LETTER))
						{
							htmltext = "30618-01.html";
						}
						else if (!hasQuestItems(player, SCEPTER_OF_TUNATH) && hasQuestItems(player, KASMANS_3RD_LETTER))
						{
							player.getRadar().removeMarker(-55217, 200628, -3724);
							htmltext = "30618-02.html";
						}
						else if (hasQuestItems(player, SCEPTER_OF_TUNATH))
						{
							htmltext = "30618-04.html";
						}
					}
					else if (hasAtLeastOneQuestItem(player, CHIANTA_2ND_ORDER, CHIANTA_3RD_ORDER, SCEPTER_BOX))
					{
						htmltext = "30618-05.html";
					}
					break;
				}
				case VUKU_CHIEF_DRIKO:
				{
					if (hasQuestItems(player, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER))
					{
						if (!hasAtLeastOneQuestItem(player, SCEPTER_OF_VUKU, KASMANS_1ST_LETTER, DRIKOS_CONTRACT))
						{
							htmltext = "30619-01.html";
						}
						else if (!hasQuestItems(player, SCEPTER_OF_VUKU) && hasQuestItems(player, KASMANS_1ST_LETTER))
						{
							player.getRadar().removeMarker(-2150, 124443, -3724);
							htmltext = "30619-02.html";
						}
						else if (!hasQuestItems(player, SCEPTER_OF_VUKU) && hasQuestItems(player, DRIKOS_CONTRACT))
						{
							if (getQuestItemsCount(player, STAKATO_DRONE_HUSK) < 30)
							{
								htmltext = "30619-04.html";
							}
							else
							{
								giveItems(player, SCEPTER_OF_VUKU, 1);
								takeItems(player, DRIKOS_CONTRACT, 1);
								takeItems(player, STAKATO_DRONE_HUSK, -1);
								if (hasQuestItems(player, SCEPTER_OF_TUREK, SCEPTER_OF_ENKU, SCEPTER_OF_BREKA, SCEPTER_OF_TUNATH))
								{
									qs.setCond(5, true);
								}
								htmltext = "30619-05.html";
							}
						}
						else if (hasQuestItems(player, SCEPTER_OF_VUKU))
						{
							htmltext = "30619-06.html";
						}
					}
					else if (hasQuestItems(player, NECKLACE_OF_AUTHORITY) && hasAtLeastOneQuestItem(player, CHIANTA_2ND_ORDER, CHIANTA_3RD_ORDER, SCEPTER_BOX))
					{
						htmltext = "30619-07.html";
					}
					break;
				}
				case GANDI_CHIEF_CHIANTA:
				{
					if (hasQuestItems(player, NECKLACE_OF_AUTHORITY, VOKIANS_ORDER2))
					{
						htmltext = "30642-01.html";
					}
					else if (hasQuestItems(player, NECKLACE_OF_AUTHORITY, CHIANTA_1ST_ORDER))
					{
						if ((getQuestItemsCount(player, SCEPTER_OF_BREKA) + getQuestItemsCount(player, SCEPTER_OF_VUKU) + getQuestItemsCount(player, SCEPTER_OF_TUREK) + getQuestItemsCount(player, SCEPTER_OF_TUNATH) + getQuestItemsCount(player, SCEPTER_OF_ENKU)) < 5)
						{
							htmltext = "30642-04.html";
						}
						else if (hasQuestItems(player, SCEPTER_OF_BREKA, SCEPTER_OF_VUKU, SCEPTER_OF_TUREK, SCEPTER_OF_TUNATH, SCEPTER_OF_ENKU))
						{
							htmltext = "30642-05.html";
						}
					}
					else if (hasQuestItems(player, NECKLACE_OF_AUTHORITY, CHIANTA_2ND_ORDER))
					{
						giveItems(player, CHIANTA_3RD_ORDER, 1);
						takeItems(player, CHIANTA_2ND_ORDER, 1);
						htmltext = "30642-09.html";
					}
					else if (hasQuestItems(player, NECKLACE_OF_AUTHORITY, CHIANTA_3RD_ORDER))
					{
						if ((getQuestItemsCount(player, TAMLIN_ORC_SKULL) >= 20) && (getQuestItemsCount(player, TIMAK_ORC_HEAD) >= 20))
						{
							takeItems(player, NECKLACE_OF_AUTHORITY, 1);
							takeItems(player, CHIANTA_3RD_ORDER, 1);
							takeItems(player, TAMLIN_ORC_SKULL, -1);
							takeItems(player, TIMAK_ORC_HEAD, -1);
							giveItems(player, SCEPTER_BOX, 1);
							qs.setCond(8, true);
							htmltext = "30642-11.html";
						}
						else
						{
							htmltext = "30642-10.html";
						}
					}
					else if (hasQuestItems(player, SCEPTER_BOX))
					{
						htmltext = "30642-12.html";
					}
					else if (hasAtLeastOneQuestItem(player, TANAPIS_ORDER, RITUAL_BOX))
					{
						htmltext = "30642-13.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == PREFECT_VOKIAN)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}
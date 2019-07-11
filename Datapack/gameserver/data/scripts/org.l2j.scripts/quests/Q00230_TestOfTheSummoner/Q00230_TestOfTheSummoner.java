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
package quests.Q00230_TestOfTheSummoner;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.util.GameUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Test Of The Summoner (230)
 * @author ivantotov
 */
public final class Q00230_TestOfTheSummoner extends Quest
{
	// NPCs
	private static final int GROCER_LARA = 30063;
	private static final int HIGH_SUMMONER_GALATEA = 30634;
	private static final int SUMMONER_ALMORS = 30635;
	private static final int SUMMONER_CAMONIELL = 30636;
	private static final int SUMMONER_BELTHUS = 30637;
	private static final int SUMMONER_BASILLA = 30638;
	private static final int SUMMONER_CELESTIEL = 30639;
	private static final int SUMMONER_BRYNTHEA = 30640;
	// Items
	private static final int LETOLIZARDMAN_AMULET = 3337;
	private static final int SAC_OF_REDSPORES = 3338;
	private static final int KARULBUGBEAR_TOTEM = 3339;
	private static final int SHARDS_OF_MANASHEN = 3340;
	private static final int BREKAORC_TOTEM = 3341;
	private static final int CRIMSON_BLOODSTONE = 3342;
	private static final int TALONS_OF_TYRANT = 3343;
	private static final int WINGS_OF_DRONEANT = 3344;
	private static final int TUSK_OF_WINDSUS = 3345;
	private static final int FANGS_OF_WYRM = 3346;
	private static final int LARAS_1ST_LIST = 3347;
	private static final int LARAS_2ND_LIST = 3348;
	private static final int LARAS_3RD_LIST = 3349;
	private static final int LARAS_4TH_LIST = 3350;
	private static final int LARAS_5TH_LIST = 3351;
	private static final int GALATEAS_LETTER = 3352;
	private static final int BEGINNERS_ARCANA = 3353;
	private static final int ALMORS_ARCANA = 3354;
	private static final int CAMONIELL_ARCANA = 3355;
	private static final int BELTHUS_ARCANA = 3356;
	private static final int BASILLIA_ARCANA = 3357;
	private static final int CELESTIEL_ARCANA = 3358;
	private static final int BRYNTHEA_ARCANA = 3359;
	private static final int CRYSTAL_OF_STARTING_1ST = 3360;
	private static final int CRYSTAL_OF_INPROGRESS_1ST = 3361;
	private static final int CRYSTAL_OF_FOUL_1ST = 3362;
	private static final int CRYSTAL_OF_DEFEAT_1ST = 3363;
	private static final int CRYSTAL_OF_VICTORY_1ST = 3364;
	private static final int CRYSTAL_OF_STARTING_2ND = 3365;
	private static final int CRYSTAL_OF_INPROGRESS_2ND = 3366;
	private static final int CRYSTAL_OF_FOUL_2ND = 3367;
	private static final int CRYSTAL_OF_DEFEAT_2ND = 3368;
	private static final int CRYSTAL_OF_VICTORY_2ND = 3369;
	private static final int CRYSTAL_OF_STARTING_3RD = 3370;
	private static final int CRYSTAL_OF_INPROGRESS_3RD = 3371;
	private static final int CRYSTAL_OF_FOUL_3RD = 3372;
	private static final int CRYSTAL_OF_DEFEAT_3RD = 3373;
	private static final int CRYSTAL_OF_VICTORY_3RD = 3374;
	private static final int CRYSTAL_OF_STARTING_4TH = 3375;
	private static final int CRYSTAL_OF_INPROGRESS_4TH = 3376;
	private static final int CRYSTAL_OF_FOUL_4TH = 3377;
	private static final int CRYSTAL_OF_DEFEAT_4TH = 3378;
	private static final int CRYSTAL_OF_VICTORY_4TH = 3379;
	private static final int CRYSTAL_OF_STARTING_5TH = 3380;
	private static final int CRYSTAL_OF_INPROGRESS_5TH = 3381;
	private static final int CRYSTAL_OF_FOUL_5TH = 3382;
	private static final int CRYSTAL_OF_DEFEAT_5TH = 3383;
	private static final int CRYSTAL_OF_VICTORY_5TH = 3384;
	private static final int CRYSTAL_OF_STARTING_6TH = 3385;
	private static final int CRYSTAL_OF_INPROGRESS_6TH = 3386;
	private static final int CRYSTAL_OF_FOUL_6TH = 3387;
	private static final int CRYSTAL_OF_DEFEAT_6TH = 3388;
	private static final int CRYSTAL_OF_VICTORY_6TH = 3389;
	// Reward
	private static final int MARK_OF_SUMMONER = 3336;
	// Monster
	private static final int NOBLE_ANT = 20089;
	private static final int NOBLE_ANT_LEADER = 20090;
	private static final int WYRM = 20176;
	private static final int TYRANT = 20192;
	private static final int TYRANT_KINGPIN = 20193;
	private static final int BREKA_ORC = 20267;
	private static final int BREKA_ORC_ARCHER = 20268;
	private static final int BREKA_ORC_SHAMAN = 20269;
	private static final int BREKA_ORC_OVERLORD = 20270;
	private static final int BREKA_ORC_WARRIOR = 20271;
	private static final int FETTERED_SOUL = 20552;
	private static final int WINDSUS = 20553;
	private static final int GIANT_FUNGUS = 20555;
	private static final int MANASHEN_GARGOYLE = 20563;
	private static final int LETO_LIZARDMAN = 20577;
	private static final int LETO_LIZARDMAN_ARCHER = 20578;
	private static final int LETO_LIZARDMAN_SOLDIER = 20579;
	private static final int LETO_LIZARDMAN_WARRIOR = 20580;
	private static final int LETO_LIZARDMAN_SHAMAN = 20581;
	private static final int LETO_LIZARDMAN_OVERLORD = 20582;
	private static final int KARUL_BUGBEAR = 20600;
	// Quest Monster
	private static final int PAKO_THE_CAT = 27102;
	private static final int UNICORN_RACER = 27103;
	private static final int SHADOW_TUREN = 27104;
	private static final int MIMI_THE_CAT = 27105;
	private static final int UNICORN_PHANTASM = 27106;
	private static final int SILHOUETTE_TILFO = 27107;
	// Skill
	private static final SkillHolder REDUCTION_IN_RECOVERY_TIME = new SkillHolder(4126, 1);
	// Misc
	private static final int MIN_LEVEL = 39;
	
	private static final Map<Integer, MonsterData> MONSTERS = new HashMap<>();
	static
	{
		MONSTERS.put(PAKO_THE_CAT, new MonsterData(CRYSTAL_OF_INPROGRESS_1ST, CRYSTAL_OF_VICTORY_1ST, NpcStringId.I_M_SORRY_LORD));
		MONSTERS.put(UNICORN_RACER, new MonsterData(CRYSTAL_OF_INPROGRESS_3RD, CRYSTAL_OF_VICTORY_3RD, NpcStringId.I_LOSE));
		MONSTERS.put(SHADOW_TUREN, new MonsterData(CRYSTAL_OF_INPROGRESS_5TH, CRYSTAL_OF_VICTORY_5TH, NpcStringId.UGH_I_LOST));
		MONSTERS.put(MIMI_THE_CAT, new MonsterData(CRYSTAL_OF_INPROGRESS_2ND, CRYSTAL_OF_VICTORY_2ND, NpcStringId.LOST_SORRY_LORD));
		MONSTERS.put(UNICORN_PHANTASM, new MonsterData(CRYSTAL_OF_INPROGRESS_4TH, CRYSTAL_OF_VICTORY_4TH, NpcStringId.I_LOSE));
		MONSTERS.put(SILHOUETTE_TILFO, new MonsterData(CRYSTAL_OF_INPROGRESS_6TH, CRYSTAL_OF_VICTORY_6TH, NpcStringId.UGH_CAN_THIS_BE_HAPPENING));
	}
	
	public Q00230_TestOfTheSummoner()
	{
		super(230);
		addStartNpc(HIGH_SUMMONER_GALATEA);
		addTalkId(HIGH_SUMMONER_GALATEA, GROCER_LARA, SUMMONER_ALMORS, SUMMONER_CAMONIELL, SUMMONER_BELTHUS, SUMMONER_BASILLA, SUMMONER_CELESTIEL, SUMMONER_BRYNTHEA);
		addKillId(NOBLE_ANT, NOBLE_ANT_LEADER, WYRM, TYRANT, TYRANT_KINGPIN, BREKA_ORC, BREKA_ORC_ARCHER, BREKA_ORC_SHAMAN, BREKA_ORC_OVERLORD, BREKA_ORC_WARRIOR, FETTERED_SOUL, WINDSUS, GIANT_FUNGUS, MANASHEN_GARGOYLE, LETO_LIZARDMAN, LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD, KARUL_BUGBEAR);
		addKillId(MONSTERS.keySet());
		addAttackId(PAKO_THE_CAT, UNICORN_RACER, SHADOW_TUREN, MIMI_THE_CAT, UNICORN_PHANTASM, SILHOUETTE_TILFO);
		registerQuestItems(LETOLIZARDMAN_AMULET, SAC_OF_REDSPORES, KARULBUGBEAR_TOTEM, SHARDS_OF_MANASHEN, BREKAORC_TOTEM, CRIMSON_BLOODSTONE, TALONS_OF_TYRANT, WINGS_OF_DRONEANT, TUSK_OF_WINDSUS, FANGS_OF_WYRM, LARAS_1ST_LIST, LARAS_2ND_LIST, LARAS_3RD_LIST, LARAS_4TH_LIST, LARAS_5TH_LIST, GALATEAS_LETTER, BEGINNERS_ARCANA, ALMORS_ARCANA, CAMONIELL_ARCANA, BELTHUS_ARCANA, BASILLIA_ARCANA, CELESTIEL_ARCANA, BRYNTHEA_ARCANA, CRYSTAL_OF_STARTING_1ST, CRYSTAL_OF_INPROGRESS_1ST, CRYSTAL_OF_FOUL_1ST, CRYSTAL_OF_DEFEAT_1ST, CRYSTAL_OF_VICTORY_1ST, CRYSTAL_OF_STARTING_2ND, CRYSTAL_OF_INPROGRESS_2ND, CRYSTAL_OF_FOUL_2ND, CRYSTAL_OF_DEFEAT_2ND, CRYSTAL_OF_VICTORY_2ND, CRYSTAL_OF_STARTING_3RD, CRYSTAL_OF_INPROGRESS_3RD, CRYSTAL_OF_FOUL_3RD, CRYSTAL_OF_DEFEAT_3RD, CRYSTAL_OF_VICTORY_3RD, CRYSTAL_OF_STARTING_4TH, CRYSTAL_OF_INPROGRESS_4TH, CRYSTAL_OF_FOUL_4TH, CRYSTAL_OF_DEFEAT_4TH, CRYSTAL_OF_VICTORY_4TH, CRYSTAL_OF_STARTING_5TH, CRYSTAL_OF_INPROGRESS_5TH, CRYSTAL_OF_FOUL_5TH, CRYSTAL_OF_DEFEAT_5TH, CRYSTAL_OF_VICTORY_5TH, CRYSTAL_OF_STARTING_6TH, CRYSTAL_OF_INPROGRESS_6TH, CRYSTAL_OF_FOUL_6TH, CRYSTAL_OF_DEFEAT_6TH, CRYSTAL_OF_VICTORY_6TH);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "DESPAWN":
			{
				npc.deleteMe();
				break;
			}
			case "KILLED_ATTACKER":
			{
				final Summon summon = npc.getVariables().getObject("ATTACKER", Summon.class);
				if ((summon != null) && summon.isDead())
				{
					npc.deleteMe();
				}
				else
				{
					startQuestTimer("KILLED_ATTACKER", 5000, npc, null);
				}
				break;
			}
		}
		
		// For NPC-only timers, player is null and no further checks or actions are required.
		if (player == null)
		{
			return null;
		}
		
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
					giveItems(player, GALATEAS_LETTER, 1);
				}
				break;
			}
			case "30634-04.htm":
			case "30634-05.htm":
			case "30634-06.htm":
			case "30634-07.htm":
			case "30634-11.html":
			case "30634-11a.html":
			case "30634-11b.html":
			case "30634-11c.html":
			case "30634-11d.html":
			{
				htmltext = event;
				break;
			}
			case "30063-02.html":
			{
				switch (getRandom(5))
				{
					case 0:
					{
						giveItems(player, LARAS_1ST_LIST, 1);
						break;
					}
					case 1:
					{
						giveItems(player, LARAS_2ND_LIST, 1);
						break;
					}
					case 2:
					{
						giveItems(player, LARAS_3RD_LIST, 1);
						break;
					}
					case 3:
					{
						giveItems(player, LARAS_4TH_LIST, 1);
						break;
					}
					case 4:
					{
						giveItems(player, LARAS_5TH_LIST, 1);
						break;
					}
				}
				qs.setCond(2, true);
				takeItems(player, GALATEAS_LETTER, 1);
				htmltext = event;
				break;
			}
			case "30063-04.html":
			{
				switch (getRandom(5))
				{
					case 0:
					{
						giveItems(player, LARAS_1ST_LIST, 1);
						break;
					}
					case 1:
					{
						giveItems(player, LARAS_2ND_LIST, 1);
						break;
					}
					case 2:
					{
						giveItems(player, LARAS_3RD_LIST, 1);
						break;
					}
					case 3:
					{
						giveItems(player, LARAS_4TH_LIST, 1);
						break;
					}
					case 4:
					{
						giveItems(player, LARAS_5TH_LIST, 1);
						break;
					}
				}
				htmltext = event;
				break;
			}
			case "30635-03.html":
			{
				if (hasQuestItems(player, BEGINNERS_ARCANA))
				{
					htmltext = event;
				}
				else
				{
					htmltext = "30635-02.html";
				}
				break;
			}
			case "30635-04.html":
			{
				addSkillCastDesire(npc, player, REDUCTION_IN_RECOVERY_TIME, 1000000);
				takeItems(player, BEGINNERS_ARCANA, 1);
				giveItems(player, CRYSTAL_OF_STARTING_1ST, 1);
				takeItems(player, CRYSTAL_OF_FOUL_1ST, 1);
				takeItems(player, CRYSTAL_OF_DEFEAT_1ST, 1);
				htmltext = event;
				break;
			}
			case "30636-03.html":
			{
				if (hasQuestItems(player, BEGINNERS_ARCANA))
				{
					htmltext = event;
				}
				else
				{
					htmltext = "30636-02.html";
				}
				break;
			}
			case "30636-04.html":
			{
				addSkillCastDesire(npc, player, REDUCTION_IN_RECOVERY_TIME, 1000000);
				takeItems(player, BEGINNERS_ARCANA, 1);
				giveItems(player, CRYSTAL_OF_STARTING_3RD, 1);
				takeItems(player, CRYSTAL_OF_FOUL_3RD, 1);
				takeItems(player, CRYSTAL_OF_DEFEAT_3RD, 1);
				htmltext = event;
				break;
			}
			case "30637-03.html":
			{
				if (hasQuestItems(player, BEGINNERS_ARCANA))
				{
					htmltext = event;
				}
				else
				{
					htmltext = "30637-02.html";
				}
				break;
			}
			case "30637-04.html":
			{
				addSkillCastDesire(npc, player, REDUCTION_IN_RECOVERY_TIME, 1000000);
				takeItems(player, BEGINNERS_ARCANA, 1);
				giveItems(player, CRYSTAL_OF_STARTING_5TH, 1);
				takeItems(player, CRYSTAL_OF_FOUL_5TH, 1);
				takeItems(player, CRYSTAL_OF_DEFEAT_5TH, 1);
				htmltext = event;
				break;
			}
			case "30638-03.html":
			{
				if (hasQuestItems(player, BEGINNERS_ARCANA))
				{
					htmltext = event;
				}
				else
				{
					htmltext = "30638-02.html";
				}
				break;
			}
			case "30638-04.html":
			{
				addSkillCastDesire(npc, player, REDUCTION_IN_RECOVERY_TIME, 1000000);
				takeItems(player, BEGINNERS_ARCANA, 1);
				giveItems(player, CRYSTAL_OF_STARTING_2ND, 1);
				takeItems(player, CRYSTAL_OF_FOUL_2ND, 1);
				takeItems(player, CRYSTAL_OF_DEFEAT_2ND, 1);
				htmltext = event;
				break;
			}
			case "30639-03.html":
			{
				if (hasQuestItems(player, BEGINNERS_ARCANA))
				{
					htmltext = event;
				}
				else
				{
					htmltext = "30639-02.html";
				}
				break;
			}
			case "30639-04.html":
			{
				addSkillCastDesire(npc, player, REDUCTION_IN_RECOVERY_TIME, 1000000);
				takeItems(player, BEGINNERS_ARCANA, 1);
				giveItems(player, CRYSTAL_OF_STARTING_4TH, 1);
				takeItems(player, CRYSTAL_OF_FOUL_4TH, 1);
				takeItems(player, CRYSTAL_OF_DEFEAT_4TH, 1);
				htmltext = event;
				break;
			}
			case "30640-03.html":
			{
				if (hasQuestItems(player, BEGINNERS_ARCANA))
				{
					htmltext = event;
				}
				else
				{
					htmltext = "30640-02.html";
				}
				break;
			}
			case "30640-04.html":
			{
				addSkillCastDesire(npc, player, REDUCTION_IN_RECOVERY_TIME, 1000000);
				takeItems(player, BEGINNERS_ARCANA, 1);
				giveItems(player, CRYSTAL_OF_STARTING_6TH, 1);
				takeItems(player, CRYSTAL_OF_FOUL_6TH, 1);
				takeItems(player, CRYSTAL_OF_DEFEAT_6TH, 1);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		switch (npc.getId())
		{
			case PAKO_THE_CAT:
			{
				switch (npc.getScriptValue())
				{
					case 0:
					{
						if (isSummon)
						{
							npc.getVariables().set("ATTACKER", attacker.getAnyServitor());
							npc.setScriptValue(1);
							startQuestTimer("DESPAWN", 120000, npc, null);
							startQuestTimer("KILLED_ATTACKER", 5000, npc, null);
							
							final QuestState qs = getQuestState(attacker, false);
							if (hasQuestItems(attacker, CRYSTAL_OF_STARTING_1ST) && (qs != null) && qs.isStarted())
							{
								Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getTemplate().getDisplayId(), NpcStringId.WHHIISSHH));
								takeItems(attacker, CRYSTAL_OF_STARTING_1ST, -1);
								giveItems(attacker, CRYSTAL_OF_INPROGRESS_1ST, 1);
								addAttackPlayerDesire(npc, attacker.getAnyServitor(), 100000);
							}
						}
						break;
					}
					case 1:
					{
						if (!isSummon || (npc.getVariables().getObject("ATTACKER", Summon.class) != attacker.getAnyServitor()))
						{
							final QuestState qs = getQuestState(attacker, false);
							if (!hasQuestItems(attacker, CRYSTAL_OF_STARTING_1ST) && hasQuestItems(attacker, CRYSTAL_OF_INPROGRESS_1ST) && (qs != null) && qs.isStarted())
							{
								npc.setScriptValue(2);
								Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getTemplate().getDisplayId(), NpcStringId.RULE_VIOLATION));
								takeItems(attacker, CRYSTAL_OF_INPROGRESS_1ST, -1);
								giveItems(attacker, CRYSTAL_OF_FOUL_1ST, 1);
								takeItems(attacker, CRYSTAL_OF_STARTING_1ST, -1);
							}
							npc.deleteMe();
						}
						break;
					}
				}
				break;
			}
			case UNICORN_RACER:
			{
				switch (npc.getScriptValue())
				{
					case 0:
					{
						if (isSummon)
						{
							npc.getVariables().set("ATTACKER", attacker.getAnyServitor());
							npc.setScriptValue(1);
							startQuestTimer("DESPAWN", 120000, npc, null);
							startQuestTimer("KILLED_ATTACKER", 5000, npc, null);
							
							final QuestState qs = getQuestState(attacker, false);
							if (hasQuestItems(attacker, CRYSTAL_OF_STARTING_3RD) && (qs != null) && qs.isStarted())
							{
								Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getTemplate().getDisplayId(), NpcStringId.START_DUEL));
								takeItems(attacker, CRYSTAL_OF_STARTING_3RD, -1);
								giveItems(attacker, CRYSTAL_OF_INPROGRESS_3RD, 1);
								addAttackPlayerDesire(npc, attacker.getAnyServitor(), 100000);
							}
						}
						break;
					}
					case 1:
					{
						if (!isSummon || (npc.getVariables().getObject("ATTACKER", Summon.class) != attacker.getAnyServitor()))
						{
							final QuestState qs = getQuestState(attacker, false);
							if (!hasQuestItems(attacker, CRYSTAL_OF_STARTING_3RD) && hasQuestItems(attacker, CRYSTAL_OF_INPROGRESS_3RD) && (qs != null) && qs.isStarted())
							{
								npc.setScriptValue(2);
								Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getTemplate().getDisplayId(), NpcStringId.RULE_VIOLATION));
								takeItems(attacker, CRYSTAL_OF_INPROGRESS_3RD, -1);
								giveItems(attacker, CRYSTAL_OF_FOUL_3RD, 1);
								takeItems(attacker, CRYSTAL_OF_STARTING_3RD, -1);
							}
							npc.deleteMe();
						}
						break;
					}
				}
			}
			case SHADOW_TUREN:
			{
				switch (npc.getScriptValue())
				{
					case 0:
					{
						if (isSummon)
						{
							npc.getVariables().set("ATTACKER", attacker.getAnyServitor());
							npc.setScriptValue(1);
							startQuestTimer("DESPAWN", 120000, npc, null);
							startQuestTimer("KILLED_ATTACKER", 5000, npc, null);
							
							final QuestState qs = getQuestState(attacker, false);
							if (hasQuestItems(attacker, CRYSTAL_OF_STARTING_5TH) && (qs != null) && qs.isStarted())
							{
								Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getTemplate().getDisplayId(), NpcStringId.SO_SHALL_WE_START));
								takeItems(attacker, CRYSTAL_OF_STARTING_5TH, -1);
								giveItems(attacker, CRYSTAL_OF_INPROGRESS_5TH, 1);
								addAttackPlayerDesire(npc, attacker.getAnyServitor(), 100000);
							}
						}
						break;
					}
					case 1:
					{
						if (!isSummon || (npc.getVariables().getObject("ATTACKER", Summon.class) != attacker.getAnyServitor()))
						{
							final QuestState qs = getQuestState(attacker, false);
							if (!hasQuestItems(attacker, CRYSTAL_OF_STARTING_5TH) && hasQuestItems(attacker, CRYSTAL_OF_INPROGRESS_5TH) && (qs != null) && qs.isStarted())
							{
								npc.setScriptValue(2);
								Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getTemplate().getDisplayId(), NpcStringId.RULE_VIOLATION));
								takeItems(attacker, CRYSTAL_OF_INPROGRESS_5TH, -1);
								giveItems(attacker, CRYSTAL_OF_FOUL_5TH, 1);
								takeItems(attacker, CRYSTAL_OF_STARTING_5TH, -1);
							}
							npc.deleteMe();
						}
						break;
					}
				}
			}
			case MIMI_THE_CAT:
			{
				switch (npc.getScriptValue())
				{
					case 0:
					{
						if (isSummon)
						{
							npc.getVariables().set("ATTACKER", attacker.getAnyServitor());
							npc.setScriptValue(1);
							startQuestTimer("DESPAWN", 120000, npc, null);
							startQuestTimer("KILLED_ATTACKER", 5000, npc, null);
							
							final QuestState qs = getQuestState(attacker, false);
							if (hasQuestItems(attacker, CRYSTAL_OF_STARTING_2ND) && (qs != null) && qs.isStarted())
							{
								Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getTemplate().getDisplayId(), NpcStringId.WHISH_FIGHT));
								takeItems(attacker, CRYSTAL_OF_STARTING_2ND, -1);
								giveItems(attacker, CRYSTAL_OF_INPROGRESS_2ND, 1);
								addAttackPlayerDesire(npc, attacker.getAnyServitor(), 100000);
							}
						}
						break;
					}
					case 1:
					{
						if (!isSummon || (npc.getVariables().getObject("ATTACKER", Summon.class) != attacker.getAnyServitor()))
						{
							final QuestState qs = getQuestState(attacker, false);
							if (!hasQuestItems(attacker, CRYSTAL_OF_STARTING_2ND) && hasQuestItems(attacker, CRYSTAL_OF_INPROGRESS_2ND) && (qs != null) && qs.isStarted())
							{
								npc.setScriptValue(2);
								Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getTemplate().getDisplayId(), NpcStringId.RULE_VIOLATION));
								takeItems(attacker, CRYSTAL_OF_INPROGRESS_2ND, -1);
								giveItems(attacker, CRYSTAL_OF_FOUL_2ND, 1);
								takeItems(attacker, CRYSTAL_OF_STARTING_2ND, -1);
							}
							npc.deleteMe();
						}
						break;
					}
				}
			}
			case UNICORN_PHANTASM:
			{
				switch (npc.getScriptValue())
				{
					case 0:
					{
						if (isSummon)
						{
							npc.getVariables().set("ATTACKER", attacker.getAnyServitor());
							npc.setScriptValue(1);
							startQuestTimer("DESPAWN", 120000, npc, null);
							startQuestTimer("KILLED_ATTACKER", 5000, npc, null);
							
							final QuestState qs = getQuestState(attacker, false);
							if (hasQuestItems(attacker, CRYSTAL_OF_STARTING_4TH) && (qs != null) && qs.isStarted())
							{
								Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getTemplate().getDisplayId(), NpcStringId.START_DUEL));
								takeItems(attacker, CRYSTAL_OF_STARTING_4TH, -1);
								giveItems(attacker, CRYSTAL_OF_INPROGRESS_4TH, 1);
								addAttackPlayerDesire(npc, attacker.getAnyServitor(), 100000);
							}
						}
						break;
					}
					case 1:
					{
						if (!isSummon || (npc.getVariables().getObject("ATTACKER", Summon.class) != attacker.getAnyServitor()))
						{
							final QuestState qs = getQuestState(attacker, false);
							if (!hasQuestItems(attacker, CRYSTAL_OF_STARTING_4TH) && hasQuestItems(attacker, CRYSTAL_OF_INPROGRESS_4TH) && (qs != null) && qs.isStarted())
							{
								npc.setScriptValue(2);
								Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getTemplate().getDisplayId(), NpcStringId.RULE_VIOLATION));
								takeItems(attacker, CRYSTAL_OF_INPROGRESS_4TH, -1);
								giveItems(attacker, CRYSTAL_OF_FOUL_4TH, 1);
								takeItems(attacker, CRYSTAL_OF_STARTING_4TH, -1);
							}
							npc.deleteMe();
						}
						break;
					}
				}
			}
			case SILHOUETTE_TILFO:
			{
				switch (npc.getScriptValue())
				{
					case 0:
					{
						if (isSummon)
						{
							npc.getVariables().set("ATTACKER", attacker.getAnyServitor());
							npc.setScriptValue(1);
							startQuestTimer("DESPAWN", 120000, npc, null);
							startQuestTimer("KILLED_ATTACKER", 5000, npc, null);
							
							final QuestState qs = getQuestState(attacker, false);
							if (hasQuestItems(attacker, CRYSTAL_OF_STARTING_6TH) && (qs != null) && qs.isStarted())
							{
								Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getTemplate().getDisplayId(), NpcStringId.I_LL_WALK_ALL_OVER_YOU));
								takeItems(attacker, CRYSTAL_OF_STARTING_6TH, -1);
								giveItems(attacker, CRYSTAL_OF_INPROGRESS_6TH, 1);
								addAttackPlayerDesire(npc, attacker.getAnyServitor(), 100000);
							}
						}
						break;
					}
					case 1:
					{
						if (!isSummon || (npc.getVariables().getObject("ATTACKER", Summon.class) != attacker.getAnyServitor()))
						{
							final QuestState qs = getQuestState(attacker, false);
							if (!hasQuestItems(attacker, CRYSTAL_OF_STARTING_6TH) && hasQuestItems(attacker, CRYSTAL_OF_INPROGRESS_6TH) && (qs != null) && qs.isStarted())
							{
								npc.setScriptValue(2);
								Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getTemplate().getDisplayId(), NpcStringId.RULE_VIOLATION));
								takeItems(attacker, CRYSTAL_OF_INPROGRESS_6TH, -1);
								giveItems(attacker, CRYSTAL_OF_FOUL_6TH, 1);
								takeItems(attacker, CRYSTAL_OF_STARTING_6TH, -1);
							}
							npc.deleteMe();
						}
						break;
					}
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
				case NOBLE_ANT:
				case NOBLE_ANT_LEADER:
				{
					if (!hasQuestItems(killer, GALATEAS_LETTER) && hasQuestItems(killer, LARAS_5TH_LIST))
					{
						giveItemRandomly(killer, npc, WINGS_OF_DRONEANT, 2, 30, 1.0, true);
					}
					break;
				}
				case WYRM:
				{
					if (!hasQuestItems(killer, GALATEAS_LETTER) && hasQuestItems(killer, LARAS_5TH_LIST))
					{
						giveItemRandomly(killer, npc, FANGS_OF_WYRM, 3, 30, 1.0, true);
					}
					break;
				}
				case TYRANT:
				case TYRANT_KINGPIN:
				{
					if (!hasQuestItems(killer, GALATEAS_LETTER) && hasQuestItems(killer, LARAS_4TH_LIST))
					{
						giveItemRandomly(killer, npc, TALONS_OF_TYRANT, 3, 30, 1.0, true);
					}
					break;
				}
				case BREKA_ORC:
				case BREKA_ORC_ARCHER:
				case BREKA_ORC_WARRIOR:
				{
					if (!hasQuestItems(killer, GALATEAS_LETTER) && hasQuestItems(killer, LARAS_3RD_LIST))
					{
						giveItemRandomly(killer, npc, BREKAORC_TOTEM, 1, 30, 1.0, true);
					}
					break;
				}
				case BREKA_ORC_SHAMAN:
				case BREKA_ORC_OVERLORD:
				{
					if (!hasQuestItems(killer, GALATEAS_LETTER) && hasQuestItems(killer, LARAS_3RD_LIST))
					{
						giveItemRandomly(killer, npc, BREKAORC_TOTEM, 2, 30, 1.0, true);
					}
					break;
				}
				case FETTERED_SOUL:
				{
					if (!hasQuestItems(killer, GALATEAS_LETTER) && hasQuestItems(killer, LARAS_3RD_LIST))
					{
						giveItemRandomly(killer, npc, CRIMSON_BLOODSTONE, 6, 30, 1.0, true);
					}
					break;
				}
				case WINDSUS:
				{
					if (!hasQuestItems(killer, GALATEAS_LETTER) && hasQuestItems(killer, LARAS_4TH_LIST))
					{
						giveItemRandomly(killer, npc, TUSK_OF_WINDSUS, 3, 30, 1.0, true);
					}
					break;
				}
				case GIANT_FUNGUS:
				{
					if (!hasQuestItems(killer, GALATEAS_LETTER) && hasQuestItems(killer, LARAS_1ST_LIST))
					{
						giveItemRandomly(killer, npc, SAC_OF_REDSPORES, 2, 30, 1.0, true);
					}
					break;
				}
				case MANASHEN_GARGOYLE:
				{
					if (!hasQuestItems(killer, GALATEAS_LETTER) && hasQuestItems(killer, LARAS_2ND_LIST))
					{
						giveItemRandomly(killer, npc, SHARDS_OF_MANASHEN, 2, 30, 1.0, true);
					}
					break;
				}
				case LETO_LIZARDMAN:
				case LETO_LIZARDMAN_ARCHER:
				case LETO_LIZARDMAN_SOLDIER:
				case LETO_LIZARDMAN_WARRIOR:
				{
					if (!hasQuestItems(killer, GALATEAS_LETTER) && hasQuestItems(killer, LARAS_1ST_LIST))
					{
						giveItemRandomly(killer, npc, LETOLIZARDMAN_AMULET, 1, 30, 1.0, true);
					}
					break;
				}
				case LETO_LIZARDMAN_SHAMAN:
				case LETO_LIZARDMAN_OVERLORD:
				{
					if (!hasQuestItems(killer, GALATEAS_LETTER) && hasQuestItems(killer, LARAS_1ST_LIST))
					{
						giveItemRandomly(killer, npc, LETOLIZARDMAN_AMULET, 2, 30, 1.0, true);
					}
					break;
				}
				case KARUL_BUGBEAR:
				{
					if (!hasQuestItems(killer, GALATEAS_LETTER) && hasQuestItems(killer, LARAS_2ND_LIST))
					{
						giveItemRandomly(killer, npc, KARULBUGBEAR_TOTEM, 2, 30, 1.0, true);
					}
					break;
				}
				case SILHOUETTE_TILFO:
				case UNICORN_PHANTASM:
				case MIMI_THE_CAT:
				case SHADOW_TUREN:
				case UNICORN_RACER:
				case PAKO_THE_CAT:
				{
					final MonsterData data = MONSTERS.get(npc.getId());
					if (hasQuestItems(killer, data.getCrystalOfInprogress()))
					{
						npc.broadcastPacket(new NpcSay(npc, ChatType.NPC_GENERAL, data.getNpcStringId()));
						takeItems(killer, data.getCrystalOfInprogress(), 1);
						giveItems(killer, data.getCrystalOfVictory(), 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
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
			if (npc.getId() == HIGH_SUMMONER_GALATEA)
			{
				if ((player.getClassId() == ClassId.WIZARD) || (player.getClassId() == ClassId.ELVEN_WIZARD) || (player.getClassId() == ClassId.DARK_WIZARD))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						htmltext = "30634-03.htm";
					}
					else
					{
						htmltext = "30634-02.html";
					}
				}
				else
				{
					htmltext = "30634-01.html";
				}
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case HIGH_SUMMONER_GALATEA:
				{
					if (hasQuestItems(player, GALATEAS_LETTER))
					{
						htmltext = "30634-09.html";
					}
					else if (!hasQuestItems(player, GALATEAS_LETTER))
					{
						if (!hasQuestItems(player, ALMORS_ARCANA, BASILLIA_ARCANA, CAMONIELL_ARCANA, CELESTIEL_ARCANA, BELTHUS_ARCANA, BRYNTHEA_ARCANA) && !hasQuestItems(player, BEGINNERS_ARCANA))
						{
							htmltext = "30634-10.html";
						}
						else if (!hasQuestItems(player, ALMORS_ARCANA, BASILLIA_ARCANA, CAMONIELL_ARCANA, CELESTIEL_ARCANA, BELTHUS_ARCANA, BRYNTHEA_ARCANA) && hasQuestItems(player, BEGINNERS_ARCANA))
						{
							htmltext = "30634-11.html";
						}
						else if (hasQuestItems(player, ALMORS_ARCANA, BASILLIA_ARCANA, CAMONIELL_ARCANA, CELESTIEL_ARCANA, BELTHUS_ARCANA, BRYNTHEA_ARCANA))
						{
							giveAdena(player, 300960, true);
							giveItems(player, MARK_OF_SUMMONER, 1);
							addExpAndSp(player, 1664494, 114220);
							qs.exitQuest(false, true);
							player.sendPacket(new SocialAction(player.getObjectId(), 3));
							htmltext = "30634-12.html";
						}
					}
					break;
				}
				case GROCER_LARA:
				{
					if (hasQuestItems(player, GALATEAS_LETTER))
					{
						htmltext = "30063-01.html";
					}
					else if (!hasQuestItems(player, GALATEAS_LETTER))
					{
						if (!hasAtLeastOneQuestItem(player, LARAS_1ST_LIST, LARAS_2ND_LIST, LARAS_3RD_LIST, LARAS_4TH_LIST, LARAS_5TH_LIST))
						{
							htmltext = "30063-03.html";
						}
						else if (hasQuestItems(player, LARAS_1ST_LIST))
						{
							if ((getQuestItemsCount(player, LETOLIZARDMAN_AMULET) >= 30) && (getQuestItemsCount(player, SAC_OF_REDSPORES) >= 30))
							{
								takeItems(player, LETOLIZARDMAN_AMULET, -1);
								takeItems(player, SAC_OF_REDSPORES, -1);
								takeItems(player, LARAS_1ST_LIST, 1);
								giveItems(player, BEGINNERS_ARCANA, 2);
								qs.setCond(3, true);
								htmltext = "30063-06.html";
							}
							else
							{
								htmltext = "30063-05.html";
							}
						}
						else if (hasQuestItems(player, LARAS_2ND_LIST))
						{
							if ((getQuestItemsCount(player, KARULBUGBEAR_TOTEM) >= 30) && (getQuestItemsCount(player, SHARDS_OF_MANASHEN) >= 30))
							{
								takeItems(player, KARULBUGBEAR_TOTEM, -1);
								takeItems(player, SHARDS_OF_MANASHEN, -1);
								takeItems(player, LARAS_2ND_LIST, 1);
								giveItems(player, BEGINNERS_ARCANA, 2);
								qs.setCond(3, true);
								htmltext = "30063-08.html";
							}
							else
							{
								htmltext = "30063-07.html";
							}
						}
						else if (hasQuestItems(player, LARAS_3RD_LIST))
						{
							if ((getQuestItemsCount(player, BREKAORC_TOTEM) >= 30) && (getQuestItemsCount(player, CRIMSON_BLOODSTONE) >= 30))
							{
								takeItems(player, BREKAORC_TOTEM, -1);
								takeItems(player, CRIMSON_BLOODSTONE, -1);
								takeItems(player, LARAS_3RD_LIST, 1);
								giveItems(player, BEGINNERS_ARCANA, 2);
								qs.setCond(3, true);
								htmltext = "30063-10.html";
							}
							else
							{
								htmltext = "30063-09.html";
							}
						}
						else if (hasQuestItems(player, LARAS_4TH_LIST))
						{
							if ((getQuestItemsCount(player, TALONS_OF_TYRANT) >= 30) && (getQuestItemsCount(player, TUSK_OF_WINDSUS) >= 30))
							{
								takeItems(player, TALONS_OF_TYRANT, -1);
								takeItems(player, TUSK_OF_WINDSUS, -1);
								takeItems(player, LARAS_4TH_LIST, 1);
								giveItems(player, BEGINNERS_ARCANA, 2);
								qs.setCond(3, true);
								htmltext = "30063-12.html";
							}
							else
							{
								htmltext = "30063-11.html";
							}
						}
						else if (hasQuestItems(player, LARAS_5TH_LIST))
						{
							if ((getQuestItemsCount(player, WINGS_OF_DRONEANT) >= 30) && (getQuestItemsCount(player, FANGS_OF_WYRM) >= 30))
							{
								takeItems(player, WINGS_OF_DRONEANT, -1);
								takeItems(player, FANGS_OF_WYRM, -1);
								takeItems(player, LARAS_5TH_LIST, 1);
								giveItems(player, BEGINNERS_ARCANA, 2);
								qs.setCond(3, true);
								htmltext = "30063-14.html";
							}
							else
							{
								htmltext = "30063-13.html";
							}
						}
					}
					break;
				}
				case SUMMONER_ALMORS:
				{
					if (!hasQuestItems(player, ALMORS_ARCANA))
					{
						if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_1ST, CRYSTAL_OF_INPROGRESS_1ST, CRYSTAL_OF_FOUL_1ST, CRYSTAL_OF_DEFEAT_1ST, CRYSTAL_OF_VICTORY_1ST))
						{
							htmltext = "30635-01.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_1ST, CRYSTAL_OF_INPROGRESS_1ST, CRYSTAL_OF_FOUL_1ST, CRYSTAL_OF_VICTORY_1ST) && hasQuestItems(player, CRYSTAL_OF_DEFEAT_1ST))
						{
							htmltext = "30635-05.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_1ST, CRYSTAL_OF_INPROGRESS_1ST, CRYSTAL_OF_DEFEAT_1ST, CRYSTAL_OF_VICTORY_1ST) && hasQuestItems(player, CRYSTAL_OF_FOUL_1ST))
						{
							htmltext = "30635-06.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_1ST, CRYSTAL_OF_INPROGRESS_1ST, CRYSTAL_OF_FOUL_1ST, CRYSTAL_OF_DEFEAT_1ST) && hasQuestItems(player, CRYSTAL_OF_VICTORY_1ST))
						{
							giveItems(player, ALMORS_ARCANA, 1);
							takeItems(player, CRYSTAL_OF_VICTORY_1ST, 1);
							if (hasQuestItems(player, BASILLIA_ARCANA, CAMONIELL_ARCANA, CELESTIEL_ARCANA, BELTHUS_ARCANA, BRYNTHEA_ARCANA))
							{
								qs.setCond(4, true);
							}
							htmltext = "30635-07.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_INPROGRESS_1ST, CRYSTAL_OF_FOUL_1ST, CRYSTAL_OF_DEFEAT_1ST, CRYSTAL_OF_VICTORY_1ST) && hasQuestItems(player, CRYSTAL_OF_STARTING_1ST))
						{
							htmltext = "30635-08.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_1ST, CRYSTAL_OF_FOUL_1ST, CRYSTAL_OF_DEFEAT_1ST, CRYSTAL_OF_VICTORY_1ST) && hasQuestItems(player, CRYSTAL_OF_INPROGRESS_1ST))
						{
							htmltext = "30635-09.html";
						}
					}
					else
					{
						htmltext = "30635-10.html";
					}
					break;
				}
				case SUMMONER_CAMONIELL:
				{
					if (!hasQuestItems(player, CAMONIELL_ARCANA))
					{
						if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_3RD, CRYSTAL_OF_INPROGRESS_3RD, CRYSTAL_OF_FOUL_3RD, CRYSTAL_OF_DEFEAT_3RD, CRYSTAL_OF_VICTORY_3RD))
						{
							htmltext = "30636-01.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_3RD, CRYSTAL_OF_INPROGRESS_3RD, CRYSTAL_OF_FOUL_3RD, CRYSTAL_OF_VICTORY_3RD) && hasQuestItems(player, CRYSTAL_OF_DEFEAT_3RD))
						{
							htmltext = "30636-05.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_3RD, CRYSTAL_OF_INPROGRESS_3RD, CRYSTAL_OF_DEFEAT_3RD, CRYSTAL_OF_VICTORY_3RD) && hasQuestItems(player, CRYSTAL_OF_FOUL_3RD))
						{
							htmltext = "30636-06.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_3RD, CRYSTAL_OF_INPROGRESS_3RD, CRYSTAL_OF_FOUL_3RD, CRYSTAL_OF_DEFEAT_3RD) && hasQuestItems(player, CRYSTAL_OF_VICTORY_3RD))
						{
							giveItems(player, CAMONIELL_ARCANA, 1);
							takeItems(player, CRYSTAL_OF_VICTORY_3RD, 1);
							if (hasQuestItems(player, ALMORS_ARCANA, BASILLIA_ARCANA, CELESTIEL_ARCANA, BELTHUS_ARCANA, BRYNTHEA_ARCANA))
							{
								qs.setCond(4, true);
							}
							htmltext = "30636-07.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_INPROGRESS_3RD, CRYSTAL_OF_FOUL_3RD, CRYSTAL_OF_DEFEAT_3RD, CRYSTAL_OF_VICTORY_3RD) && hasQuestItems(player, CRYSTAL_OF_STARTING_3RD))
						{
							htmltext = "30636-08.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_3RD, CRYSTAL_OF_FOUL_3RD, CRYSTAL_OF_DEFEAT_3RD, CRYSTAL_OF_VICTORY_3RD) && hasQuestItems(player, CRYSTAL_OF_INPROGRESS_3RD))
						{
							htmltext = "30636-09.html";
						}
					}
					else
					{
						htmltext = "30636-10.html";
					}
					break;
				}
				case SUMMONER_BELTHUS:
				{
					if (!hasQuestItems(player, BELTHUS_ARCANA))
					{
						if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_5TH, CRYSTAL_OF_INPROGRESS_5TH, CRYSTAL_OF_FOUL_5TH, CRYSTAL_OF_DEFEAT_5TH, CRYSTAL_OF_VICTORY_5TH))
						{
							htmltext = "30637-01.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_5TH, CRYSTAL_OF_INPROGRESS_5TH, CRYSTAL_OF_FOUL_5TH, CRYSTAL_OF_VICTORY_5TH) && hasQuestItems(player, CRYSTAL_OF_DEFEAT_5TH))
						{
							htmltext = "30637-05.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_5TH, CRYSTAL_OF_INPROGRESS_5TH, CRYSTAL_OF_DEFEAT_5TH, CRYSTAL_OF_VICTORY_5TH) && hasQuestItems(player, CRYSTAL_OF_FOUL_5TH))
						{
							htmltext = "30637-06.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_5TH, CRYSTAL_OF_INPROGRESS_5TH, CRYSTAL_OF_FOUL_5TH, CRYSTAL_OF_DEFEAT_5TH) && hasQuestItems(player, CRYSTAL_OF_VICTORY_5TH))
						{
							giveItems(player, BELTHUS_ARCANA, 1);
							takeItems(player, CRYSTAL_OF_VICTORY_5TH, 1);
							if (hasQuestItems(player, ALMORS_ARCANA, BASILLIA_ARCANA, CAMONIELL_ARCANA, CELESTIEL_ARCANA, BRYNTHEA_ARCANA))
							{
								qs.setCond(4, true);
							}
							htmltext = "30637-07.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_INPROGRESS_5TH, CRYSTAL_OF_FOUL_5TH, CRYSTAL_OF_DEFEAT_5TH, CRYSTAL_OF_VICTORY_5TH) && hasQuestItems(player, CRYSTAL_OF_STARTING_5TH))
						{
							htmltext = "30637-08.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_5TH, CRYSTAL_OF_FOUL_5TH, CRYSTAL_OF_DEFEAT_5TH, CRYSTAL_OF_VICTORY_5TH) && hasQuestItems(player, CRYSTAL_OF_INPROGRESS_5TH))
						{
							htmltext = "30637-09.html";
						}
					}
					else
					{
						htmltext = "30637-10.html";
					}
					break;
				}
				case SUMMONER_BASILLA:
				{
					if (!hasQuestItems(player, BASILLIA_ARCANA))
					{
						if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_2ND, CRYSTAL_OF_INPROGRESS_2ND, CRYSTAL_OF_FOUL_2ND, CRYSTAL_OF_DEFEAT_2ND, CRYSTAL_OF_VICTORY_2ND))
						{
							htmltext = "30638-01.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_2ND, CRYSTAL_OF_INPROGRESS_2ND, CRYSTAL_OF_FOUL_2ND, CRYSTAL_OF_VICTORY_2ND) && hasQuestItems(player, CRYSTAL_OF_DEFEAT_2ND))
						{
							htmltext = "30638-05.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_2ND, CRYSTAL_OF_INPROGRESS_2ND, CRYSTAL_OF_DEFEAT_2ND, CRYSTAL_OF_VICTORY_2ND) && hasQuestItems(player, CRYSTAL_OF_FOUL_2ND))
						{
							htmltext = "30638-06.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_2ND, CRYSTAL_OF_INPROGRESS_2ND, CRYSTAL_OF_FOUL_2ND, CRYSTAL_OF_DEFEAT_2ND) && hasQuestItems(player, CRYSTAL_OF_VICTORY_2ND))
						{
							giveItems(player, BASILLIA_ARCANA, 1);
							takeItems(player, CRYSTAL_OF_VICTORY_2ND, 1);
							if (hasQuestItems(player, ALMORS_ARCANA, CAMONIELL_ARCANA, CELESTIEL_ARCANA, BELTHUS_ARCANA, BRYNTHEA_ARCANA))
							{
								qs.setCond(4, true);
							}
							htmltext = "30638-07.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_INPROGRESS_2ND, CRYSTAL_OF_FOUL_2ND, CRYSTAL_OF_DEFEAT_2ND, CRYSTAL_OF_VICTORY_2ND) && hasQuestItems(player, CRYSTAL_OF_STARTING_2ND))
						{
							htmltext = "30638-08.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_2ND, CRYSTAL_OF_FOUL_2ND, CRYSTAL_OF_DEFEAT_2ND, CRYSTAL_OF_VICTORY_2ND) && hasQuestItems(player, CRYSTAL_OF_INPROGRESS_2ND))
						{
							htmltext = "30638-09.html";
						}
					}
					else
					{
						htmltext = "30638-10.html";
					}
					break;
				}
				case SUMMONER_CELESTIEL:
				{
					if (!hasQuestItems(player, CELESTIEL_ARCANA))
					{
						if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_4TH, CRYSTAL_OF_INPROGRESS_4TH, CRYSTAL_OF_FOUL_4TH, CRYSTAL_OF_DEFEAT_4TH, CRYSTAL_OF_VICTORY_4TH))
						{
							htmltext = "30639-01.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_4TH, CRYSTAL_OF_INPROGRESS_4TH, CRYSTAL_OF_FOUL_4TH, CRYSTAL_OF_VICTORY_4TH) && hasQuestItems(player, CRYSTAL_OF_DEFEAT_4TH))
						{
							htmltext = "30639-05.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_4TH, CRYSTAL_OF_INPROGRESS_4TH, CRYSTAL_OF_DEFEAT_4TH, CRYSTAL_OF_VICTORY_4TH) && hasQuestItems(player, CRYSTAL_OF_FOUL_4TH))
						{
							htmltext = "30639-06.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_4TH, CRYSTAL_OF_INPROGRESS_4TH, CRYSTAL_OF_FOUL_4TH, CRYSTAL_OF_DEFEAT_4TH) && hasQuestItems(player, CRYSTAL_OF_VICTORY_4TH))
						{
							giveItems(player, CELESTIEL_ARCANA, 1);
							takeItems(player, CRYSTAL_OF_VICTORY_4TH, 1);
							if (hasQuestItems(player, ALMORS_ARCANA, BASILLIA_ARCANA, CAMONIELL_ARCANA, BELTHUS_ARCANA, BRYNTHEA_ARCANA))
							{
								qs.setCond(4, true);
							}
							htmltext = "30639-07.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_INPROGRESS_4TH, CRYSTAL_OF_FOUL_4TH, CRYSTAL_OF_DEFEAT_4TH, CRYSTAL_OF_VICTORY_4TH) && hasQuestItems(player, CRYSTAL_OF_STARTING_4TH))
						{
							htmltext = "30639-08.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_4TH, CRYSTAL_OF_FOUL_4TH, CRYSTAL_OF_DEFEAT_4TH, CRYSTAL_OF_VICTORY_4TH) && hasQuestItems(player, CRYSTAL_OF_INPROGRESS_4TH))
						{
							htmltext = "30639-09.html";
						}
					}
					else
					{
						htmltext = "30639-10.html";
					}
					break;
				}
				case SUMMONER_BRYNTHEA:
				{
					if (!hasQuestItems(player, BRYNTHEA_ARCANA))
					{
						if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_6TH, CRYSTAL_OF_INPROGRESS_6TH, CRYSTAL_OF_FOUL_6TH, CRYSTAL_OF_DEFEAT_6TH, CRYSTAL_OF_VICTORY_6TH))
						{
							htmltext = "30640-01.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_6TH, CRYSTAL_OF_INPROGRESS_6TH, CRYSTAL_OF_FOUL_6TH, CRYSTAL_OF_VICTORY_6TH) && hasQuestItems(player, CRYSTAL_OF_DEFEAT_6TH))
						{
							htmltext = "30640-05.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_6TH, CRYSTAL_OF_INPROGRESS_6TH, CRYSTAL_OF_DEFEAT_6TH, CRYSTAL_OF_VICTORY_6TH) && hasQuestItems(player, CRYSTAL_OF_FOUL_6TH))
						{
							htmltext = "30640-06.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_6TH, CRYSTAL_OF_INPROGRESS_6TH, CRYSTAL_OF_FOUL_6TH, CRYSTAL_OF_DEFEAT_6TH) && hasQuestItems(player, CRYSTAL_OF_VICTORY_6TH))
						{
							giveItems(player, BRYNTHEA_ARCANA, 1);
							takeItems(player, CRYSTAL_OF_VICTORY_6TH, 1);
							if (hasQuestItems(player, ALMORS_ARCANA, BASILLIA_ARCANA, CAMONIELL_ARCANA, CELESTIEL_ARCANA, BELTHUS_ARCANA))
							{
								qs.setCond(4, true);
							}
							htmltext = "30640-07.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_INPROGRESS_6TH, CRYSTAL_OF_FOUL_6TH, CRYSTAL_OF_DEFEAT_6TH, CRYSTAL_OF_VICTORY_6TH) && hasQuestItems(player, CRYSTAL_OF_STARTING_6TH))
						{
							htmltext = "30640-08.html";
						}
						else if (!hasAtLeastOneQuestItem(player, CRYSTAL_OF_STARTING_6TH, CRYSTAL_OF_FOUL_6TH, CRYSTAL_OF_DEFEAT_6TH, CRYSTAL_OF_VICTORY_6TH) && hasQuestItems(player, CRYSTAL_OF_INPROGRESS_6TH))
						{
							htmltext = "30640-09.html";
						}
					}
					else
					{
						htmltext = "30640-10.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted())
		{
			if (npc.getId() == HIGH_SUMMONER_GALATEA)
			{
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
	
	private static class MonsterData
	{
		private final int _crystalOfInprogress;
		private final int _crystalOfVictory;
		private final NpcStringId _npcStringId;
		
		protected MonsterData(int crystalOfInprogress, int crystalOfVictory, NpcStringId npcStringId)
		{
			_crystalOfInprogress = crystalOfInprogress;
			_crystalOfVictory = crystalOfVictory;
			_npcStringId = npcStringId;
		}
		
		protected int getCrystalOfInprogress()
		{
			return _crystalOfInprogress;
		}
		
		protected int getCrystalOfVictory()
		{
			return _crystalOfVictory;
		}
		
		protected NpcStringId getNpcStringId()
		{
			return _npcStringId;
		}
	}
}
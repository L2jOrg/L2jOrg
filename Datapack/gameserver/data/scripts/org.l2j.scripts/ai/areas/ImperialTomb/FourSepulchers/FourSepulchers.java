/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.areas.ImperialTomb.FourSepulchers;

import ai.AbstractNpcAI;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.EffectZone;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import quests.Q00620_FourGoblets.Q00620_FourGoblets;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * Four Selpuchers AI
 * @author Mobius
 */
public class FourSepulchers extends AbstractNpcAI
{
	
	// NPCs
	private static final int CONQUEROR_MANAGER = 31921;
	private static final int EMPEROR_MANAGER = 31922;
	private static final int GREAT_SAGES_MANAGER = 31923;
	private static final int JUDGE_MANAGER = 31924;
	private static final int MYSTERIOUS_CHEST = 31468;
	private static final int KEY_CHEST = 31467;
	private static final int ROOM_3_VICTIM = 18150;
	private static final int ROOM_3_CHEST_REWARDER = 18158;
	private static final int ROOM_4_CHARM_1 = 18196;
	private static final int ROOM_4_CHARM_2 = 18197;
	private static final int ROOM_4_CHARM_3 = 18198;
	private static final int ROOM_4_CHARM_4 = 18199;
	private static final int ROOM_5_STATUE_GUARD = 18232;
	private static final int ROOM_6_REWARD_CHEST = 18256;
	private static final int CONQUEROR_BOSS = 25346;
	private static final int EMPEROR_BOSS = 25342;
	private static final int GREAT_SAGES_BOSS = 25339;
	private static final int JUDGE_BOSS = 25349;
	private static final int TELEPORTER = 31452;
	// @formatter:off
	private static final int[] FIRST_TALK_NPCS =
	{
		TELEPORTER,
		31453, 31454, 31919, 31920, 31925, 31926, 31927, 31928,
		31929, 31930, 31931, 31932, 31933, 31934, 31935, 31936,
		31937, 31938, 31939, 31940, 31941, 31942, 31943, 31944
	};
	// @formatter:on
	private static final int[] CHEST_REWARD_MONSTERS =
	{
		18120, // room 1
		ROOM_3_CHEST_REWARDER,
		18177, // room 4
		18212, // room 5 - wave 2
	};
	// Items
	private static final int ENTRANCE_PASS = 91406;
	private static final int USED_PASS = 7261;
	private static final int CHAPEL_KEY = 7260;
	private static final int ANTIQUE_BROOCH = 7262;
	// Locations
	private static final Map<Integer, Location> START_HALL_SPAWNS = new HashMap<>();
	static
	{
		START_HALL_SPAWNS.put(CONQUEROR_MANAGER, new Location(181632, -85587, -7218));
		START_HALL_SPAWNS.put(EMPEROR_MANAGER, new Location(179963, -88978, -7218));
		START_HALL_SPAWNS.put(GREAT_SAGES_MANAGER, new Location(173217, -86132, -7218));
		START_HALL_SPAWNS.put(JUDGE_MANAGER, new Location(175608, -82296, -7218));
	}
	// Zones
	private static final int CONQUEROR_ZONE = 200221;
	private static final int EMPEROR_ZONE = 200222;
	private static final int GREAT_SAGES_ZONE = 200224;
	private static final int JUDGE_ZONE = 200223;
	private static final Map<Integer, Integer> MANAGER_ZONES = new HashMap<>();
	static
	{
		MANAGER_ZONES.put(CONQUEROR_MANAGER, CONQUEROR_ZONE);
		MANAGER_ZONES.put(EMPEROR_MANAGER, EMPEROR_ZONE);
		MANAGER_ZONES.put(GREAT_SAGES_MANAGER, GREAT_SAGES_ZONE);
		MANAGER_ZONES.put(JUDGE_MANAGER, JUDGE_ZONE);
	}
	// Spawns
	private static List<int[]> ROOM_SPAWN_DATA = new ArrayList<>();
	private static final Map<Integer, List<Npc>> STORED_MONSTER_SPAWNS = new HashMap<>();
	static
	{
		STORED_MONSTER_SPAWNS.put(1, new CopyOnWriteArrayList<>());
		STORED_MONSTER_SPAWNS.put(2, new CopyOnWriteArrayList<>());
		STORED_MONSTER_SPAWNS.put(3, new CopyOnWriteArrayList<>());
		STORED_MONSTER_SPAWNS.put(4, new CopyOnWriteArrayList<>());
	}
	// @formatter:off
	private static final int[][] CHEST_SPAWN_LOCATIONS =
	{
		// sepulcherId, roomNumber, npcLocX, npcLocY, npcLocZ, npcLocHeading
		{1, 1, 182074, -85579, -7216, 32768},
		{1, 2, 183868, -85577, -7216, 32768},
		{1, 3, 185681, -85573, -7216, 32768},
		{1, 4, 187498, -85566, -7216, 32768},
		{1, 5, 189306, -85571, -7216, 32768},
		{2, 1, 180375, -88968, -7216, 32768},
		{2, 2, 182151, -88962, -7216, 32768},
		{2, 3, 183960, -88964, -7216, 32768},
		{2, 4, 185792, -88966, -7216, 32768},
		{2, 5, 187625, -88953, -7216, 32768},
		{3, 1, 173218, -85703, -7216, 49152},
		{3, 2, 173206, -83929, -7216, 49152},
		{3, 3, 173208, -82085, -7216, 49152},
		{3, 4, 173191, -80290, -7216, 49152},
		{3, 5, 173198, -78465, -7216, 49152},
		{4, 1, 175601, -81905, -7216, 49152},
		{4, 2, 175619, -80094, -7216, 49152},
		{4, 3, 175608, -78268, -7216, 49152},
		{4, 4, 175588, -76472, -7216, 49152},
		{4, 5, 175594, -74655, -7216, 49152},
	};
	// Doors
	private static final int[][] DOORS =
	{
		// sepulcherId, waveNumber, doorId
		{1, 2, 25150012}, {1, 3, 25150013}, {1, 4, 25150014}, {1, 5, 25150015}, {1, 7, 25150016},
		{2, 2, 25150002}, {2, 3, 25150003}, {2, 4, 25150004}, {2, 5, 25150005}, {2, 7, 25150006},
		{3, 2, 25150032}, {3, 3, 25150033}, {3, 4, 25150034}, {3, 5, 25150035}, {3, 7, 25150036},
		{4, 2, 25150022}, {4, 3, 25150023}, {4, 4, 25150024}, {4, 5, 25150025}, {4, 7, 25150026},
	};
	// @formatter:on
	// Skill
	private static final SkillHolder PETRIFY = new SkillHolder(4616, 1);
	private static final Map<Integer, Integer> CHARM_SKILLS = new HashMap<>();
	static
	{
		CHARM_SKILLS.put(ROOM_4_CHARM_1, 4146);
		CHARM_SKILLS.put(ROOM_4_CHARM_2, 4145);
		CHARM_SKILLS.put(ROOM_4_CHARM_3, 4148);
		CHARM_SKILLS.put(ROOM_4_CHARM_4, 4624);
	}
	// Misc
	private static final Map<Integer, NpcStringId> CHARM_MSG = new HashMap<>();
	static
	{
		CHARM_MSG.put(ROOM_4_CHARM_1, NpcStringId.THE_P_ATK_REDUCTION_DEVICE_HAS_NOW_BEEN_DESTROYED);
		CHARM_MSG.put(ROOM_4_CHARM_2, NpcStringId.THE_P_ATK_REDUCTION_DEVICE_HAS_NOW_BEEN_DESTROYED); // TODO: THE_DEFENSE_REDUCTION_DEVICE_HAS_BEEN_DESTROYED
		CHARM_MSG.put(ROOM_4_CHARM_3, NpcStringId.THE_POISON_DEVICE_HAS_NOW_BEEN_DESTROYED);
		CHARM_MSG.put(ROOM_4_CHARM_4, NpcStringId.THE_POISON_DEVICE_HAS_NOW_BEEN_DESTROYED); // TODO: THE_HP_REGENERATION_REDUCTION_DEVICE_WILL_BE_ACTIVATED_IN_3_MINUTES2
	}
	private static final NpcStringId[] VICTIM_MSG =
	{
		NpcStringId.HELP_ME,
		NpcStringId.DON_T_MISS,
		NpcStringId.KEEP_PUSHING,
	};
	private static final Map<Integer, Integer> STORED_PROGRESS = new HashMap<>();
	static
	{
		STORED_PROGRESS.put(1, 1);
		STORED_PROGRESS.put(2, 1);
		STORED_PROGRESS.put(3, 1);
		STORED_PROGRESS.put(4, 1);
	}
	private static final int PARTY_MEMBER_COUNT = 0;
	private static final int ENTRY_DELAY = 3; // minutes
	private static final int TIME_ATTACK = 60; // minutes
	
	private FourSepulchers()
	{
		new DataLoader().load();
		addFirstTalkId(CONQUEROR_MANAGER, EMPEROR_MANAGER, GREAT_SAGES_MANAGER, JUDGE_MANAGER, MYSTERIOUS_CHEST, KEY_CHEST);
		addTalkId(CONQUEROR_MANAGER, EMPEROR_MANAGER, GREAT_SAGES_MANAGER, JUDGE_MANAGER, MYSTERIOUS_CHEST, KEY_CHEST);
		addFirstTalkId(FIRST_TALK_NPCS);
		addTalkId(FIRST_TALK_NPCS);
		addKillId(CHEST_REWARD_MONSTERS);
		addKillId(ROOM_3_VICTIM, ROOM_4_CHARM_1, ROOM_4_CHARM_2, ROOM_4_CHARM_3, ROOM_4_CHARM_4, ROOM_6_REWARD_CHEST, CONQUEROR_BOSS, EMPEROR_BOSS, GREAT_SAGES_BOSS, JUDGE_BOSS);
		addSpawnId(ROOM_3_VICTIM, ROOM_4_CHARM_1, ROOM_4_CHARM_2, ROOM_4_CHARM_3, ROOM_4_CHARM_4, ROOM_5_STATUE_GUARD, ROOM_6_REWARD_CHEST, CONQUEROR_BOSS, EMPEROR_BOSS, GREAT_SAGES_BOSS, JUDGE_BOSS);
	}

	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		
		switch (event)
		{
			case "Enter":
			{
				final QuestState qs = player.getQuestState(Q00620_FourGoblets.class.getSimpleName());
				if (qs == null)
				{
					return getNoQuestMsg(player);
				}
				if (qs.isStarted())
				{
					tryEnter(npc, player);
					return null;
				}
				break;
			}
			case "OpenGate":
			{
				final QuestState qs = player.getQuestState(Q00620_FourGoblets.class.getSimpleName());
				if (qs == null)
				{
					return getNoQuestMsg(player);
				}
				if (qs.isStarted() && (npc.getScriptValue() == 0))
				{
					if (hasQuestItems(player, CHAPEL_KEY))
					{
						npc.setScriptValue(1);
						takeItems(player, CHAPEL_KEY, -1);
						final int sepulcherId = getSepulcherId(player);
						final int currentWave = STORED_PROGRESS.get(sepulcherId) + 1;
						STORED_PROGRESS.put(sepulcherId, currentWave); // update progress
						for (int[] doorInfo : DOORS)
						{
							if ((doorInfo[0] == sepulcherId) && (doorInfo[1] == currentWave))
							{
								openDoor(doorInfo[2], 0);
								ThreadPool.schedule(() ->
								{
									closeDoor(doorInfo[2], 0);
								}, 15000);
								break;
							}
						}
						if (currentWave < 7)
						{
							spawnMysteriousChest(player);
						}
						else
						{
							spawnNextWave(player);
						}
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.THE_MONSTERS_HAVE_SPAWNED);
					}
					else
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
						html.setFile(player, "data/scripts/ai/areas/ImperialTomb/FourSepulchers/Gatekeeper-no.html");
						html.replace("%npcname%", npc.getName());
						player.sendPacket(html);
					}
					return null;
				}
				htmltext = getNoQuestMsg(player); // TODO: Replace with proper html?
				break;
			}
			case "SPAWN_MYSTERIOUS_CHEST":
			{
				spawnMysteriousChest(player);
				return null;
			}
			case "VICTIM_FLEE":
			{
				if ((npc != null) && !npc.isDead())
				{
					final Location destination = GeoEngine.getInstance().canMoveToTargetLoc(npc.getX(), npc.getY(), npc.getZ(), npc.getSpawn().getLocation().getX() + getRandom(-400, 400), npc.getSpawn().getLocation().getY() + getRandom(-400, 400), npc.getZ(), npc.getInstanceWorld());
					if (MathUtil.isInsideRadius3D(npc, npc.getSpawn().getLocation(), 600))
					{
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, destination);
					}
					npc.broadcastSay(ChatType.NPC_GENERAL, VICTIM_MSG[getRandom(VICTIM_MSG.length)]);
					startQuestTimer("VICTIM_FLEE", 3000, npc, null, false);
				}
				return null;
			}
			case "REMOVE_PETRIFY":
			{
				npc.stopSkillEffects(PETRIFY.getSkill());
				npc.setTargetable(true);
				npc.setIsInvul(false);
				return null;
			}
			case "WAVE_DEFEATED_CHECK":
			{
				final int sepulcherId = getSepulcherId(player);
				final int currentWave = STORED_PROGRESS.get(sepulcherId);
				Location lastLocation = null;
				for (Npc spawn : STORED_MONSTER_SPAWNS.get(sepulcherId))
				{
					lastLocation = spawn.getLocation();
					if (spawn.isDead())
					{
						STORED_MONSTER_SPAWNS.get(sepulcherId).remove(spawn);
					}
				}
				if (STORED_MONSTER_SPAWNS.get(sepulcherId).isEmpty())
				{
					if (currentWave == 2)
					{
						if (getRandomBoolean())
						{
							spawnNextWave(player);
						}
						else
						{
							spawnKeyChest(player, lastLocation);
						}
					}
					else if (currentWave == 5)
					{
						STORED_PROGRESS.put(sepulcherId, currentWave + 1);
						spawnNextWave(player);
					}
				}
				else if (sepulcherId > 0)
				{
					startQuestTimer("WAVE_DEFEATED_CHECK", 5000, null, player, false);
				}
				return null;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (npc == null)
		{
			return null;
		}
		if (npc.getId() == MYSTERIOUS_CHEST)
		{
			if (npc.getScriptValue() == 0)
			{
				npc.setScriptValue(1);
				npc.deleteMe();
				spawnNextWave(player);
			}
			return null;
		}
		if (npc.getId() == KEY_CHEST)
		{
			if (npc.getScriptValue() == 0)
			{
				npc.setScriptValue(1);
				npc.deleteMe();
				giveItems(player, CHAPEL_KEY, 1);
			}
			return null;
		}
		return npc.getId() + ".html";
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.setRandomWalking(false);
		if (npc.getId() == ROOM_3_VICTIM)
		{
			npc.disableCoreAI(true);
			npc.setRunning();
			startQuestTimer("VICTIM_FLEE", 1000, npc, null, false);
		}
		if (npc.getId() == ROOM_5_STATUE_GUARD)
		{
			npc.setTarget(npc);
			npc.doCast(PETRIFY.getSkill());
			((Attackable) npc).setCanReturnToSpawnPoint(false);
			npc.setTargetable(false);
			npc.setIsInvul(true);
			cancelQuestTimer("REMOVE_PETRIFY", npc, null);
			startQuestTimer("REMOVE_PETRIFY", 5 * 60 * 1000, npc, null, false); // 5 minutes
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case ROOM_3_VICTIM:
			{
				addSpawn(ROOM_3_CHEST_REWARDER, npc);
				break;
			}
			case ROOM_4_CHARM_1:
			case ROOM_4_CHARM_2:
			case ROOM_4_CHARM_3:
			case ROOM_4_CHARM_4:
			{
				for (Zone zone : ZoneManager.getInstance().getZones(killer))
				{
					if ((zone instanceof EffectZone) && (((EffectZone) zone).getSkillLevel(CHARM_SKILLS.get(npc.getId())) > 0))
					{
						zone.setEnabled(false);
						break;
					}
				}
				npc.broadcastSay(ChatType.NPC_GENERAL, CHARM_MSG.get(npc.getId()));
				break;
			}
			case CONQUEROR_BOSS:
			case EMPEROR_BOSS:
			case GREAT_SAGES_BOSS:
			case JUDGE_BOSS:
			{
				final int sepulcherId = getSepulcherId(killer);
				final int currentWave = STORED_PROGRESS.get(sepulcherId);
				STORED_PROGRESS.put(sepulcherId, currentWave + 1);
				
				if ((killer.getParty() != null) && (sepulcherId > 0))
				{
					for (Player mem : killer.getParty().getMembers())
					{
						if (MathUtil.isInsideRadius3D(killer, mem, 1500))
						{
							final QuestState qs = killer.getQuestState(Q00620_FourGoblets.class.getSimpleName());
							if ((qs != null) && qs.isStarted())
							{
								giveItems(mem, 7255 + sepulcherId, 1);
							}
						}
					}
				}
				
				spawnNextWave(killer);
				
				addSpawn(TELEPORTER, npc, true, 0, false);
				break;
			}
			case ROOM_6_REWARD_CHEST:
			{
				npc.dropItem(killer, 57, getRandom(300, 1300));
				break;
			}
			default:
			{
				spawnKeyChest(killer, npc.getLocation());
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private void tryEnter(Npc npc, Player player)
	{
		final int npcId = npc.getId();
		if (ZoneManager.getInstance().getZoneById(MANAGER_ZONES.get(npcId)).getPlayersInsideCount() > 0)
		{
			showHtmlFile(player, npcId + "-FULL.htm", npc, null);
			return;
		}
		if (!player.isInParty() || (player.getParty().getMemberCount() < PARTY_MEMBER_COUNT))
		{
			showHtmlFile(player, npcId + "-SP.html", npc, null);
			return;
		}
		if (!player.getParty().isLeader(player))
		{
			showHtmlFile(player, npcId + "-NL.html", npc, null);
			return;
		}
		
		for (Player mem : player.getParty().getMembers())
		{
			final QuestState qs = mem.getQuestState(Q00620_FourGoblets.class.getSimpleName());
			if ((qs == null) || (!qs.isStarted() && !qs.isCompleted()))
			{
				showHtmlFile(player, npcId + "-NS.html", npc, mem);
				return;
			}
			if (!hasQuestItems(mem, ENTRANCE_PASS))
			{
				showHtmlFile(player, npcId + "-SE.html", npc, mem);
				return;
			}
			if (player.getWeightPenalty() >= 3)
			{
				mem.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
				return;
			}
		}
		
		final GlobalVariablesManager vars = GlobalVariablesManager.getInstance();
		final long var = vars.getLong("FourSepulchers" + npcId, 0) + (TIME_ATTACK * 60 * 1000);
		if (var > System.currentTimeMillis())
		{
			showHtmlFile(player, npcId + "-NE.html", npc, null);
			return;
		}
		
		final int sepulcherId = getSepulcherId(player);
		
		// Delete any existing spawns
		ZoneManager.getInstance().getZoneById(MANAGER_ZONES.get(npcId)).forEachCreature(creature -> {
			if (GameUtils.isMonster(creature) || creature.isRaid() || (GameUtils.isNpc(creature) && ((((Npc) creature).getId() == MYSTERIOUS_CHEST) || (((Npc) creature).getId() == KEY_CHEST) || (((Npc) creature).getId() == TELEPORTER))))
			{
				creature.deleteMe();
			}
		});

		// Disable EffectZones
		for (int[] spawnInfo : CHEST_SPAWN_LOCATIONS)
		{
			if ((spawnInfo[0] == sepulcherId) && (spawnInfo[1] == 4))
			{
				for (Zone zone : ZoneManager.getInstance().getZones(spawnInfo[2], spawnInfo[3], spawnInfo[4]))
				{
					if (zone instanceof EffectZone)
					{
						zone.setEnabled(false);
					}
				}
				break;
			}
		}
		// Close all doors
		for (int[] doorInfo : DOORS)
		{
			if (doorInfo[0] == sepulcherId)
			{
				closeDoor(doorInfo[2], 0);
			}
		}
		
		// Teleport players inside
		final List<Player> members = new ArrayList<>();
		for (Player mem : player.getParty().getMembers())
		{
			if (MathUtil.isInsideRadius3D(player, mem, 700))
			{
				members.add(mem);
			}
		}
		for (Player mem : members)
		{
			mem.teleToLocation(START_HALL_SPAWNS.get(npcId), 80);
			takeItems(mem, ENTRANCE_PASS, 1);
			takeItems(mem, CHAPEL_KEY, -1);
			if (!hasQuestItems(mem, ANTIQUE_BROOCH))
			{
				giveItems(mem, USED_PASS, 1);
			}
		}
		showHtmlFile(player, npcId + "-OK.html", npc, null);
		
		// Kick all players when/if time is over
		ThreadPool.schedule(() ->
		{
			ZoneManager.getInstance().getZoneById(MANAGER_ZONES.get(npcId)).oustAllPlayers();
		}, TIME_ATTACK * 60 * 1000);
		
		// Save attack time
		vars.set("FourSepulchers" + npcId, System.currentTimeMillis());
		// Init progress
		STORED_PROGRESS.put(sepulcherId, 1); // start from 1
		// Start
		startQuestTimer("SPAWN_MYSTERIOUS_CHEST", ENTRY_DELAY * 60 * 1000, npc, player, false);
	}
	
	private void spawnNextWave(Player player)
	{
		final int sepulcherId = getSepulcherId(player);
		final int currentWave = STORED_PROGRESS.get(sepulcherId);
		for (int[] spawnInfo : ROOM_SPAWN_DATA)
		{
			if ((spawnInfo[0] == sepulcherId) && (spawnInfo[1] == currentWave))
			{
				STORED_MONSTER_SPAWNS.get(sepulcherId).add(addSpawn(spawnInfo[2], spawnInfo[3], spawnInfo[4], spawnInfo[5], spawnInfo[6], false, 0));
			}
		}
		if (currentWave == 4)
		{
			for (Zone zone : ZoneManager.getInstance().getZones(player))
			{
				if (zone instanceof EffectZone)
				{
					zone.setEnabled(true);
				}
			}
		}
		if ((currentWave == 2) || (currentWave == 5))
		{
			startQuestTimer("WAVE_DEFEATED_CHECK", 5000, null, player, false);
		}
		else
		{
			STORED_MONSTER_SPAWNS.get(sepulcherId).clear(); // no need check for these waves
		}
	}
	
	private void spawnMysteriousChest(Player player)
	{
		final int sepulcherId = getSepulcherId(player);
		final int currentWave = STORED_PROGRESS.get(sepulcherId);
		for (int[] spawnInfo : CHEST_SPAWN_LOCATIONS)
		{
			if ((spawnInfo[0] == sepulcherId) && (spawnInfo[1] == currentWave))
			{
				addSpawn(MYSTERIOUS_CHEST, spawnInfo[2], spawnInfo[3], spawnInfo[4], spawnInfo[5], false, 0);
				break;
			}
		}
	}
	
	private void spawnKeyChest(Player player, Location loc)
	{
		addSpawn(KEY_CHEST, loc != null ? loc : player);
	}
	
	private int getSepulcherId(Player player)
	{
		if (ZoneManager.getInstance().getZoneById(CONQUEROR_ZONE).isCreatureInZone(player))
		{
			return 1;
		}
		if (ZoneManager.getInstance().getZoneById(EMPEROR_ZONE).isCreatureInZone(player))
		{
			return 2;
		}
		if (ZoneManager.getInstance().getZoneById(GREAT_SAGES_ZONE).isCreatureInZone(player))
		{
			return 3;
		}
		if (ZoneManager.getInstance().getZoneById(JUDGE_ZONE).isCreatureInZone(player))
		{
			return 4;
		}
		return 0;
	}
	
	private void showHtmlFile(Player player, String file, Npc npc, Player member)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setFile(player, "data/scripts/org.l2j.scripts/ai/areas/ImperialTomb/FourSepulchers/" + file);
		if (member != null)
		{
			html.replace("%member%", member.getName());
		}
		player.sendPacket(html);
	}


	private class DataLoader extends GameXmlReader {

		@Override
		public void load()
		{
			ROOM_SPAWN_DATA.clear();
			parseDatapackFile("data/FourSepulchers.xml");
			LOGGER.info("Loaded {} spawn zones data.", ROOM_SPAWN_DATA.size());
		}

		@Override
		protected Path getSchemaFilePath() {
			return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/FourSepulchers.xsd");
		}

		@Override
		public void parseDocument(Document doc, File f)
		{
			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n.getNodeName()))
				{
					for (Node b = n.getFirstChild(); b != null; b = b.getNextSibling())
					{
						if ("spawn".equalsIgnoreCase(b.getNodeName()))
						{
							final NamedNodeMap attrs = b.getAttributes();
							final int[] info =
									{
											parseInteger(attrs, "sepulcherId"),
											parseInteger(attrs, "wave"),
											parseInteger(attrs, "npcId"),
											parseInteger(attrs, "x"),
											parseInteger(attrs, "y"),
											parseInteger(attrs, "z"),
											parseInteger(attrs, "heading")
									};
							ROOM_SPAWN_DATA.add(info);
						}
					}
				}
			}
		}
	}

	public static AbstractNpcAI provider()
	{
		return new FourSepulchers();
	}
}

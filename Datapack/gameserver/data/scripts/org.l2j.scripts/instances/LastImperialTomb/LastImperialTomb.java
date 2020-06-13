/*
 * Copyright © 2019 L2J Mobius
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
package instances.LastImperialTomb;

import instances.AbstractInstance;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.DamageInfo.DamageType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Last Imperial Tomb AI
 * @author Mobius (reworked from L2J version)
 * @author RobikBobik (Updated to Classic: The Kamael)
 * TODO: When halisha uses second transform, Halisha Breath need to stop attack.
 */
public class LastImperialTomb extends AbstractInstance
{
	private static Logger LOGGER = LoggerFactory.getLogger(LastImperialTomb.class);
	// NPCs
	private static final int GUIDE = 32011;
	private static final int CUBE = 29061;
	private static final int HALL_ALARM = 18328;
	private static final int HALL_KEEPER_SUICIDAL_SOLDIER = 18333;
	private static final int DUMMY = 29052;
	private static final int DUMMY2 = 29053;
	private static final int[] PORTRAITS =
	{
		29048,
		29049
	};
	private static final int[] DEMONS =
	{
		29050,
		29051
	};
	private static final int FRINTEZZA = 9020;
	private static final int SCARLET1 = 29046;
	private static final int SCARLET2 = 29047;
	private static final int[] ON_KILL_MONSTERS =
	{
		HALL_ALARM,
		HALL_KEEPER_SUICIDAL_SOLDIER,
		18329,
		18330,
		18331,
		18334,
		18335,
		18336,
		18337,
		18338,
		18339
	
	};
	// Items
	private static final int FIRST_SCARLET_WEAPON = 8204;
	private static final int SECOND_SCARLET_WEAPON = 7903;
	// Doors
	private static final int[] FIRST_ROOM_DOORS =
	{
		17130051,
		17130052,
		17130053,
		17130054,
		17130055,
		17130056,
		17130057,
		17130058
	};
	private static final int[] SECOND_ROOM_DOORS =
	{
		17130061,
		17130062,
		17130063,
		17130064,
		17130065,
		17130066,
		17130067,
		17130068,
		17130069,
		17130070
	};
	private static final int[] FIRST_ROUTE_DOORS =
	{
		17130042,
		17130043
	};
	private static final int[] SECOND_ROUTE_DOORS =
	{
		17130045,
		17130046
	};
	// Skills
	private static final int DEWDROP_OF_DESTRUCTION_SKILL_ID = 2276;
	private static final SkillHolder INTRO_SKILL = new SkillHolder(5004, 1);
	private static final SkillHolder FIRST_MORPH_SKILL = new SkillHolder(5017, 1);
	private static final Map<Integer, NpcStringId> SKILL_MSG = new HashMap<>();
	static
	{
		SKILL_MSG.put(1, NpcStringId.REQUIEM_OF_HATRED);
		SKILL_MSG.put(2, NpcStringId.RONDO_OF_SOLITUDE);
		SKILL_MSG.put(3, NpcStringId.FRENETIC_TOCCATA);
		SKILL_MSG.put(4, NpcStringId.FUGUE_OF_JUBILATION);
		SKILL_MSG.put(5, NpcStringId.HYPNOTIC_MAZURKA);
	}
	// Spawns
	// @formatter:off
	static final int[][] PORTRAIT_SPAWNS =
	{
		{29048, -89381, -153981, -9168, 3368, -89378, -153968, -9168, 3368},
		{29048, -86234, -152467, -9168, 37656, -86261, -152492, -9168, 37656},
		{29049, -89342, -152479, -9168, -5152, -89311, -152491, -9168, -5152},
		{29049, -86189, -153968, -9168, 29456, -86217, -153956, -9168, 29456},
	};
	// @formatter:on
	// Misc
	private static final int TEMPLATE_ID = 205;
	private static final int FRINTEZZA_WAIT_TIME = 1; // 1 minutes
	private static final int RANDOM_SONG_INTERVAL = 90; // seconds
	private static final int TIME_BETWEEN_DEMON_SPAWNS = 20; // seconds
	private static final int MAX_DEMONS = 24;
	
	public LastImperialTomb()
	{
		super(TEMPLATE_ID);
		addTalkId(GUIDE, CUBE);
		addAttackId(SCARLET1);
		addKillId(ON_KILL_MONSTERS);
		addKillId(HALL_ALARM, SCARLET2);
		addKillId(PORTRAITS);
		addKillId(DEMONS);
		addSpawnId(HALL_ALARM, DUMMY, DUMMY2);
		addSpellFinishedId(HALL_KEEPER_SUICIDAL_SOLDIER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "FRINTEZZA_INTRO_START":
			{
				final Instance world = player.getInstanceWorld();
				startQuestTimer("FRINTEZZA_INTRO_1", 17000, null, player, false);
				startQuestTimer("FRINTEZZA_INTRO_2", 20000, null, player, false);
				broadCastPacket(world, new Earthquake(-87784, -155083, -9087, 45, 27));
				break;
			}
			case "FRINTEZZA_INTRO_1":
			{
				final Instance world = player.getInstanceWorld();
				for (int doorId : FIRST_ROOM_DOORS)
				{
					world.openCloseDoor(doorId, false);
				}
				for (int doorId : FIRST_ROUTE_DOORS)
				{
					world.openCloseDoor(doorId, false);
				}
				for (int doorId : SECOND_ROOM_DOORS)
				{
					world.openCloseDoor(doorId, false);
				}
				for (int doorId : SECOND_ROUTE_DOORS)
				{
					world.openCloseDoor(doorId, false);
				}
				addSpawn(CUBE, -87904, -141296, -9168, 0, false, 0, false, world.getId());
				break;
			}
			case "FRINTEZZA_INTRO_2":
			{
				final Instance world = player.getInstanceWorld();
				
				final Npc frintezzaDummy = addSpawn(DUMMY, -87784, -155083, -9087, 16048, false, 0, false, world.getId());
				world.setParameter("frintezzaDummy", frintezzaDummy);
				
				final Npc overheadDummy = addSpawn(DUMMY, -87784, -153298, -9175, 16384, false, 0, false, world.getId());
				overheadDummy.setCollisionHeight(600);
				broadCastPacket(world, new NpcInfo(overheadDummy));
				world.setParameter("overheadDummy", overheadDummy);
				
				final Npc portraitDummy1 = addSpawn(DUMMY, -89566, -153168, -9165, 16048, false, 0, false, world.getId());
				world.setParameter("portraitDummy1", portraitDummy1);
				
				final Npc portraitDummy3 = addSpawn(DUMMY, -86004, -153168, -9165, 16048, false, 0, false, world.getId());
				world.setParameter("portraitDummy3", portraitDummy3);
				
				final Npc scarletDummy = addSpawn(DUMMY2, -87784, -153298, -9175, 16384, false, 0, false, world.getId());
				world.setParameter("scarletDummy", scarletDummy);
				
				disablePlayers(world);
				
				// broadCastPacket(world, new SpecialCamera(overheadDummy, 0, 75, -89, 0, 100, 0, 0, 1, 0, 0));
				broadCastPacket(world, new SpecialCamera(overheadDummy, 0, 75, -89, 0, 100, 0, 0, 1, 0, 0));
				broadCastPacket(world, new SpecialCamera(overheadDummy, 300, 90, -10, 6500, 7000, 0, 0, 1, 0, 0));
				
				final Npc frintezza = addSpawn(FRINTEZZA, -87780, -155086, -9080, 16384, false, 0, false, world.getId());
				frintezza.setIsImmobilized(true);
				frintezza.setIsInvul(true);
				frintezza.disableAllSkills();
				world.setParameter("frintezza", frintezza);
				
				final List<Npc> demons = new ArrayList<>();
				for (int[] element : PORTRAIT_SPAWNS)
				{
					final Monster demon = (Monster) addSpawn(element[0] + 2, element[5], element[6], element[7], element[8], false, 0, false, world.getId());
					demon.setIsImmobilized(true);
					demon.disableAllSkills();
					demons.add(demon);
				}
				world.setParameter("demons", demons);
				
				startQuestTimer("FRINTEZZA_INTRO_3", 6500, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_3":
			{
				final Instance world = player.getInstanceWorld();
				final Npc frintezzaDummy = world.getParameters().getObject("frintezzaDummy", Npc.class);
				broadCastPacket(world, new SpecialCamera(frintezzaDummy, 1800, 90, 8, 6500, 7000, 0, 0, 1, 0, 0));
				startQuestTimer("FRINTEZZA_INTRO_4", 900, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_4":
			{
				final Instance world = player.getInstanceWorld();
				final Npc frintezzaDummy = world.getParameters().getObject("frintezzaDummy", Npc.class);
				broadCastPacket(world, new SpecialCamera(frintezzaDummy, 140, 90, 10, 2500, 4500, 0, 0, 1, 0, 0));
				startQuestTimer("FRINTEZZA_INTRO_5", 4000, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_5":
			{
				final Instance world = player.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				broadCastPacket(world, new SpecialCamera(frintezza, 40, 75, -10, 0, 1000, 0, 0, 1, 0, 0));
				broadCastPacket(world, new SpecialCamera(frintezza, 40, 75, -10, 0, 12000, 0, 0, 1, 0, 0));
				startQuestTimer("FRINTEZZA_INTRO_6", 1350, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_6":
			{
				final Instance world = player.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				broadCastPacket(world, new SocialAction(frintezza.getObjectId(), 2));
				final Npc frintezzaDummy = world.getParameters().getObject("frintezzaDummy", Npc.class);
				frintezzaDummy.deleteMe();
				startQuestTimer("FRINTEZZA_INTRO_7", 8000, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_7":
			{
				final Instance world = player.getInstanceWorld();
				final List<Npc> demons = world.getParameters().getList("demons", Npc.class);
				broadCastPacket(world, new SocialAction(demons.get(1).getObjectId(), 1));
				broadCastPacket(world, new SocialAction(demons.get(2).getObjectId(), 1));
				startQuestTimer("FRINTEZZA_INTRO_8", 400, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_8":
			{
				final Instance world = player.getInstanceWorld();
				final List<Npc> demons = world.getParameters().getList("demons", Npc.class);
				final Npc portraitDummy1 = world.getParameters().getObject("portraitDummy1", Npc.class);
				final Npc portraitDummy3 = world.getParameters().getObject("portraitDummy3", Npc.class);
				broadCastPacket(world, new SocialAction(demons.get(0).getObjectId(), 1));
				broadCastPacket(world, new SocialAction(demons.get(3).getObjectId(), 1));
				sendPacketX(world, new SpecialCamera(portraitDummy1, 1000, 118, 0, 0, 1000, 0, 0, 1, 0, 0), new SpecialCamera(portraitDummy3, 1000, 62, 0, 0, 1000, 0, 0, 1, 0, 0), -87784);
				sendPacketX(world, new SpecialCamera(portraitDummy1, 1000, 118, 0, 0, 10000, 0, 0, 1, 0, 0), new SpecialCamera(portraitDummy3, 1000, 62, 0, 0, 10000, 0, 0, 1, 0, 0), -87784);
				startQuestTimer("FRINTEZZA_INTRO_9", 2000, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_9":
			{
				final Instance world = player.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				final Npc portraitDummy1 = world.getParameters().getObject("portraitDummy1", Npc.class);
				final Npc portraitDummy3 = world.getParameters().getObject("portraitDummy3", Npc.class);
				broadCastPacket(world, new SpecialCamera(frintezza, 240, 90, 0, 0, 1000, 0, 0, 1, 0, 0));
				broadCastPacket(world, new SpecialCamera(frintezza, 240, 90, 25, 5500, 10000, 0, 0, 1, 0, 0));
				broadCastPacket(world, new SocialAction(frintezza.getObjectId(), 3));
				portraitDummy1.deleteMe();
				portraitDummy3.deleteMe();
				startQuestTimer("FRINTEZZA_INTRO_10", 4500, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_10":
			{
				final Instance world = player.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				broadCastPacket(world, new SpecialCamera(frintezza, 100, 195, 35, 0, 10000, 0, 0, 1, 0, 0));
				startQuestTimer("FRINTEZZA_INTRO_11", 700, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_11":
			{
				final Instance world = player.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				broadCastPacket(world, new SpecialCamera(frintezza, 100, 195, 35, 0, 10000, 0, 0, 1, 0, 0));
				startQuestTimer("FRINTEZZA_INTRO_12", 1300, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_12":
			{
				final Instance world = player.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				broadCastPacket(world, new ExShowScreenMessage(NpcStringId.MOURNFUL_CHORALE_PRELUDE, 2, 5000));
				broadCastPacket(world, new SpecialCamera(frintezza, 120, 180, 45, 1500, 10000, 0, 0, 1, 0, 0));
				broadCastPacket(world, new MagicSkillUse(frintezza, frintezza, 5006, 1, 34000, 0));
				startQuestTimer("FRINTEZZA_INTRO_13", 1500, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_13":
			{
				final Instance world = player.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				broadCastPacket(world, new SpecialCamera(frintezza, 520, 135, 45, 8000, 10000, 0, 0, 1, 0, 0));
				startQuestTimer("FRINTEZZA_INTRO_14", 7500, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_14":
			{
				final Instance world = player.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				broadCastPacket(world, new SpecialCamera(frintezza, 1500, 110, 25, 10000, 13000, 0, 0, 1, 0, 0));
				startQuestTimer("FRINTEZZA_INTRO_15", 9500, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_15":
			{
				final Instance world = player.getInstanceWorld();
				final Npc overheadDummy = world.getParameters().getObject("overheadDummy", Npc.class);
				final Npc scarletDummy = world.getParameters().getObject("scarletDummy", Npc.class);
				broadCastPacket(world, new SpecialCamera(overheadDummy, 930, 160, -20, 0, 1000, 0, 0, 1, 0, 0));
				broadCastPacket(world, new SpecialCamera(overheadDummy, 600, 180, -25, 0, 10000, 0, 0, 1, 0, 0));
				broadCastPacket(world, new MagicSkillUse(scarletDummy, overheadDummy, 5004, 1, 5800, 0));
				startQuestTimer("FRINTEZZA_INTRO_16", 5000, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_16":
			{
				final Instance world = player.getInstanceWorld();
				final Npc scarletDummy = world.getParameters().getObject("scarletDummy", Npc.class);
				final Npc activeScarlet = addSpawn(SCARLET1, -87789, -153295, -9176, 16384, false, 0, false, world.getId());
				world.setParameter("activeScarlet", activeScarlet);
				activeScarlet.setRHandId(FIRST_SCARLET_WEAPON);
				activeScarlet.setIsInvul(true);
				activeScarlet.setIsImmobilized(true);
				activeScarlet.disableAllSkills();
				broadCastPacket(world, new SocialAction(activeScarlet.getObjectId(), 3));
				broadCastPacket(world, new SpecialCamera(scarletDummy, 800, 180, 10, 1000, 10000, 0, 0, 1, 0, 0));
				startQuestTimer("FRINTEZZA_INTRO_17", 2100, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_17":
			{
				final Instance world = player.getInstanceWorld();
				final Npc activeScarlet = world.getParameters().getObject("activeScarlet", Npc.class);
				broadCastPacket(world, new SpecialCamera(activeScarlet, 300, 60, 8, 0, 10000, 0, 0, 1, 0, 0));
				startQuestTimer("FRINTEZZA_INTRO_18", 2000, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_18":
			{
				final Instance world = player.getInstanceWorld();
				final Npc activeScarlet = world.getParameters().getObject("activeScarlet", Npc.class);
				broadCastPacket(world, new SpecialCamera(activeScarlet, 500, 90, 10, 3000, 5000, 0, 0, 1, 0, 0));
				world.setParameter("isPlayingSong", false);
				playRandomSong(world);
				startQuestTimer("FRINTEZZA_INTRO_19", 3000, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_19":
			{
				final Instance world = player.getInstanceWorld();
				final Map<Npc, Integer> portraits = new HashMap<>();
				for (int i = 0; i < PORTRAIT_SPAWNS.length; i++)
				{
					final Npc portrait = addSpawn(PORTRAIT_SPAWNS[i][0], PORTRAIT_SPAWNS[i][1], PORTRAIT_SPAWNS[i][2], PORTRAIT_SPAWNS[i][3], PORTRAIT_SPAWNS[i][4], false, 0, false, world.getId());
					portraits.put(portrait, i);
				}
				world.setParameter("portraits", portraits);
				final Npc overheadDummy = world.getParameters().getObject("overheadDummy", Npc.class);
				final Npc scarletDummy = world.getParameters().getObject("scarletDummy", Npc.class);
				overheadDummy.deleteMe();
				scarletDummy.deleteMe();
				startQuestTimer("FRINTEZZA_INTRO_20", 2000, null, player, false);
				break;
			}
			case "FRINTEZZA_INTRO_20":
			{
				final Instance world = player.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				final Npc activeScarlet = world.getParameters().getObject("activeScarlet", Npc.class);
				final List<Npc> demons = world.getParameters().getList("demons", Npc.class);
				for (Npc demon : demons)
				{
					demon.setIsImmobilized(false);
					demon.enableAllSkills();
				}
				activeScarlet.setIsInvul(false);
				activeScarlet.setIsImmobilized(false);
				activeScarlet.enableAllSkills();
				activeScarlet.setRunning();
				activeScarlet.doCast(INTRO_SKILL.getSkill());
				frintezza.enableAllSkills();
				frintezza.disableCoreAI(true);
				frintezza.setIsInvul(true);
				enablePlayers(world);
				startQuestTimer("PLAY_RANDOM_SONG", RANDOM_SONG_INTERVAL * 1000, frintezza, null, false);
				startQuestTimer("SPAWN_DEMONS", TIME_BETWEEN_DEMON_SPAWNS * 1000, null, player, false);
				break;
			}
			case "SPAWN_DEMONS":
			{
				final Instance world = player.getInstanceWorld();
				if (world != null)
				{
					final Map<Npc, Integer> portraits = world.getParameters().getMap("portraits", Npc.class, Integer.class);
					if ((portraits != null) && !portraits.isEmpty())
					{
						final List<Npc> demons = world.getParameters().getList("demons", Npc.class);
						for (int i : portraits.values())
						{
							if (demons.size() > MAX_DEMONS)
							{
								break;
							}
							final Npc demon = addSpawn(PORTRAIT_SPAWNS[i][0] + 2, PORTRAIT_SPAWNS[i][5], PORTRAIT_SPAWNS[i][6], PORTRAIT_SPAWNS[i][7], PORTRAIT_SPAWNS[i][8], false, 0, false, world.getId());
							demons.add(demon);
						}
						world.setParameter("demons", demons);
						startQuestTimer("SPAWN_DEMONS", TIME_BETWEEN_DEMON_SPAWNS * 1000, null, player, false);
					}
				}
				break;
			}
			case "PLAY_RANDOM_SONG":
			{
				if (npc != null)
				{
					final Instance world = npc.getInstanceWorld();
					playRandomSong(world);
					startQuestTimer("PLAY_RANDOM_SONG", RANDOM_SONG_INTERVAL * 1000, null, player, false);
				}
				break;
			}
			case "SCARLET_FIRST_MORPH":
			{
				final Instance world = npc.getInstanceWorld();
				npc.doCast(FIRST_MORPH_SKILL.getSkill());
				playRandomSong(world);
				break;
			}
			case "SCARLET_SECOND_MORPH":
			{
				final Instance world = npc.getInstanceWorld();
				disablePlayers(world);
				final Npc activeScarlet = world.getParameters().getObject("activeScarlet", Npc.class);
				activeScarlet.abortAttack();
				activeScarlet.abortCast();
				activeScarlet.setIsInvul(true);
				activeScarlet.setIsImmobilized(true);
				activeScarlet.disableAllSkills();
				playRandomSong(world);
				startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_1", 2000, npc, null, false);
				break;
			}
			case "SCARLET_SECOND_MORPH_CAMERA_1":
			{
				final Instance world = npc.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				broadCastPacket(world, new SocialAction(frintezza.getObjectId(), 4));
				broadCastPacket(world, new SpecialCamera(frintezza, 250, 120, 15, 0, 1000, 0, 0, 1, 0, 0));
				broadCastPacket(world, new SpecialCamera(frintezza, 250, 120, 15, 0, 10000, 0, 0, 1, 0, 0));
				
				startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_2", 7000, npc, null, false);
				break;
			}
			case "SCARLET_SECOND_MORPH_CAMERA_2":
			{
				final Instance world = npc.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				broadCastPacket(world, new MagicSkillUse(frintezza, frintezza, 5006, 1, 34000, 0));
				broadCastPacket(world, new SpecialCamera(frintezza, 500, 70, 15, 3000, 10000, 0, 0, 1, 0, 0));
				startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_3", 3000, npc, null, false);
				break;
			}
			case "SCARLET_SECOND_MORPH_CAMERA_3":
			{
				final Instance world = npc.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				broadCastPacket(world, new SpecialCamera(frintezza, 2500, 90, 12, 6000, 10000, 0, 0, 1, 0, 0));
				startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_4", 3000, npc, null, false);
				break;
			}
			case "SCARLET_SECOND_MORPH_CAMERA_4":
			{
				final Instance world = npc.getInstanceWorld();
				final Npc activeScarlet = world.getParameters().getObject("activeScarlet", Npc.class);
				final Location scarletLocation = activeScarlet.getLocation();
				int newHeading = 0;
				if (scarletLocation.getHeading() < 32768)
				{
					newHeading = Math.abs(180 - (int) (scarletLocation.getHeading() / 182.044444444));
				}
				else
				{
					newHeading = Math.abs(540 - (int) (scarletLocation.getHeading() / 182.044444444));
				}
				world.setParameter("scarletLocation", scarletLocation);
				world.setParameter("newHeading", newHeading);
				broadCastPacket(world, new SpecialCamera(activeScarlet, 250, newHeading, 12, 0, 1000, 0, 0, 1, 0, 0));
				broadCastPacket(world, new SpecialCamera(activeScarlet, 250, newHeading, 12, 0, 10000, 0, 0, 1, 0, 0));
				startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_5", 500, npc, null, false);
				break;
			}
			case "SCARLET_SECOND_MORPH_CAMERA_5":
			{
				final Instance world = npc.getInstanceWorld();
				final Npc activeScarlet = world.getParameters().getObject("activeScarlet", Npc.class);
				final int newHeading = world.getParameters().getInt("newHeading");
				activeScarlet.doDie(activeScarlet);
				broadCastPacket(world, new SpecialCamera(activeScarlet, 450, newHeading, 14, 8000, 8000, 0, 0, 1, 0, 0));
				startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_6", 6250, npc, null, false);
				startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_7", 7200, npc, null, false);
				break;
			}
			case "SCARLET_SECOND_MORPH_CAMERA_6":
			{
				final Instance world = npc.getInstanceWorld();
				final Npc activeScarlet = world.getParameters().getObject("activeScarlet", Npc.class);
				activeScarlet.deleteMe();
				break;
			}
			case "SCARLET_SECOND_MORPH_CAMERA_7":
			{
				final Instance world = npc.getInstanceWorld();
				final int newHeading = world.getParameters().getInt("newHeading");
				final Location scarletLocation = world.getParameters().getLocation("scarletLocation");
				final Npc activeScarlet = addSpawn(SCARLET2, scarletLocation, false, 0, false, world.getId());
				world.setParameter("activeScarlet", activeScarlet);
				activeScarlet.setRHandId(SECOND_SCARLET_WEAPON);
				activeScarlet.setIsInvul(true);
				activeScarlet.setIsImmobilized(true);
				activeScarlet.disableAllSkills();
				broadCastPacket(world, new SpecialCamera(activeScarlet, 450, newHeading, 12, 500, 14000, 0, 0, 1, 0, 0));
				startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_8", 8100, npc, null, false);
				break;
			}
			case "SCARLET_SECOND_MORPH_CAMERA_8":
			{
				final Instance world = npc.getInstanceWorld();
				broadCastPacket(world, new SocialAction(npc.getObjectId(), 2));
				startQuestTimer("SCARLET_SECOND_MORPH_CAMERA_9", 9000, npc, null, false);
				break;
			}
			case "SCARLET_SECOND_MORPH_CAMERA_9":
			{
				final Instance world = npc.getInstanceWorld();
				npc.setIsInvul(false);
				npc.setIsImmobilized(false);
				npc.enableAllSkills();
				enablePlayers(world);
				break;
			}
			case "FINISH_CAMERA_1":
			{
				final Instance world = npc.getInstanceWorld();
				final Npc activeScarlet = world.getParameters().getObject("activeScarlet", Npc.class);
				final int newHeading = world.getParameters().getInt("newHeading");
				broadCastPacket(world, new SpecialCamera(activeScarlet, 300, newHeading - 180, 5, 0, 7000, 0, 0, 1, 0, 0));
				broadCastPacket(world, new SpecialCamera(activeScarlet, 200, newHeading, 85, 4000, 10000, 0, 0, 1, 0, 0));
				startQuestTimer("FINISH_CAMERA_2", 7400, npc, null, false);
				startQuestTimer("FINISH_CAMERA_3", 7500, npc, null, false);
				break;
			}
			case "FINISH_CAMERA_2":
			{
				final Instance world = npc.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				assert frintezza != null;
				frintezza.doDie(player);
				break;
			}
			case "FINISH_CAMERA_3":
			{
				final Instance world = npc.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				broadCastPacket(world, new SpecialCamera(frintezza, 100, 120, 5, 0, 7000, 0, 0, 1, 0, 0));
				broadCastPacket(world, new SpecialCamera(frintezza, 100, 90, 5, 5000, 15000, 0, 0, 1, 0, 0));
				startQuestTimer("FINISH_CAMERA_4", 7000, npc, null, false);
				break;
			}
			case "FINISH_CAMERA_4":
			{
				final Instance world = npc.getInstanceWorld();
				final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
				broadCastPacket(world, new SpecialCamera(frintezza, 900, 90, 25, 7000, 10000, 0, 0, 1, 0, 0));
				startQuestTimer("FINISH_CAMERA_5", 9000, npc, null, false);
				break;
			}
			case "FINISH_CAMERA_5":
			{
				final Instance world = npc.getInstanceWorld();
				for (int doorId : FIRST_ROOM_DOORS)
				{
					world.openCloseDoor(doorId, true);
				}
				for (int doorId : FIRST_ROUTE_DOORS)
				{
					world.openCloseDoor(doorId, true);
				}
				for (int doorId : SECOND_ROOM_DOORS)
				{
					world.openCloseDoor(doorId, true);
				}
				for (int doorId : SECOND_ROUTE_DOORS)
				{
					world.openCloseDoor(doorId, true);
				}
				enablePlayers(world);
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		if (player.isGM())
		{
			enterInstance(player, npc, TEMPLATE_ID);
			player.sendMessage("SYS: You have entered as GM/Admin to Frintezza Instance");
		}
		if (npc.getId() == GUIDE)
		{
			enterInstance(player, npc, TEMPLATE_ID);
		}
		else // Teleport Cube
		{
			final Instance world = getPlayerInstance(player);
			if (world != null)
			{
				teleportPlayerOut(player, world);
			}
		}
		return null;
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.setRandomWalking(false);
		npc.setIsImmobilized(true);
		if (npc.getId() == HALL_ALARM)
		{
			npc.disableCoreAI(true);
		}
		else // dummy
		{
			npc.setIsInvul(true);
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
	{
		if (npc.getId() == SCARLET1)
		{
			if ((npc.isScriptValue(0)) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.80)))
			{
				npc.setScriptValue(1);
				startQuestTimer("SCARLET_FIRST_MORPH", 1000, npc, null, false);
			}
			if ((npc.isScriptValue(1)) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.20)))
			{
				npc.setScriptValue(2);
				startQuestTimer("SCARLET_SECOND_MORPH", 1000, npc, null, false);
			}
		}
		if (skill != null)
		{
			// When Dewdrop of Destruction is used on Portraits they suicide.
			if (Util.contains(PORTRAITS, npc.getId()) && (skill.getId() == DEWDROP_OF_DESTRUCTION_SKILL_ID))
			{
				npc.doDie(attacker);
			}
		}
		return null;
	}
	
	@Override
	public String onSpellFinished(Npc npc, Player player, Skill skill)
	{
		if (skill.isSuicideAttack())
		{
			return onKill(npc, null, false);
		}
		return super.onSpellFinished(npc, player, skill);
	}

	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Instance world = killer.getInstanceWorld();
		if ((npc.getId() == HALL_ALARM) && (world.getStatus() == 0))
		{
			world.setStatus(1);
			world.spawnGroup("room1");
			final Set<Npc> monsters = world.getAliveNpcs();
			world.setParameter("monstersCount", monsters.size() - 1);

			for (int doorId : FIRST_ROOM_DOORS)
			{
				world.openCloseDoor(doorId, true);
			}
			for (Npc monster : monsters)
			{
				monster.setRunning();
				// monster.moveToLocation(-87959, -141247, -9168, 0);
				monster.reduceCurrentHp(1, killer, null, DamageType.ATTACK); // TODO: Find better way for attack
			}
		}
		else if (npc.getId() == SCARLET2)
		{
			final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
			broadCastPacket(world, new MagicSkillCanceld(frintezza.getObjectId()));
			startQuestTimer("FINISH_CAMERA_1", 500, npc, null, false);
		}
		else if (Util.contains(DEMONS, npc.getId()))
		{
			final List<Npc> demons = world.getParameters().getList("demons", Npc.class);
			if (demons != null)
			{
				demons.remove(npc);
				world.setParameter("demons", demons);
			}
		}
		else if (Util.contains(PORTRAITS, npc.getId()))
		{
			final Map<Npc, Integer> portraits = world.getParameters().getMap("portraits", Npc.class, Integer.class);
			if (portraits != null)
			{
				portraits.remove(npc);
				world.setParameter("portraits", portraits);
			}
		}
		else
		{
			final int killCount = world.getParameters().getInt("monstersCount");
			world.setParameter("monstersCount", killCount - 1);
			if (killCount <= 0)
			{
				switch (world.getStatus())
				{
					case 1:
					{
						world.setStatus(2);
						world.spawnGroup("room2_part1");
						final Set<Npc> monsters = world.getAliveNpcs();
						world.setParameter("monstersCount", monsters.size() - 1);
						for (int doorId : FIRST_ROUTE_DOORS)
						{
							world.openCloseDoor(doorId, true);
						}
						break;
					}
					case 2:
					{
						world.setStatus(3);
						world.spawnGroup("room2_part2");
						final Set<Npc> monsters = world.getAliveNpcs();
						world.setParameter("monstersCount", monsters.size() - 1);
						for (int doorId : SECOND_ROOM_DOORS)
						{
							world.openCloseDoor(doorId, true);
						}
						
						for (Npc monster : monsters)
						{
							monster.setRunning();
							// monster.moveToLocation(-87935, -147062, -9184, 0);
							monster.reduceCurrentHp(1, killer, null, DamageType.ATTACK); // TODO: Find better way for attack
						}
						break;
					}
					case 3:
					{
						world.setStatus(4);
						for (int doorId : SECOND_ROUTE_DOORS)
						{
							world.openCloseDoor(doorId, true);
						}
						startQuestTimer("FRINTEZZA_INTRO_START", FRINTEZZA_WAIT_TIME * 60 * 1000, null, killer, false);
						break;
					}
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private void playRandomSong(Instance world)
	{
		final Npc frintezza = world.getParameters().getObject("frintezza", Npc.class);
		final boolean isPlayingSong = world.getParameters().getBoolean("isPlayingSong");
		if (isPlayingSong)
		{
			return;
		}
		world.setParameter("isPlayingSong", true);
		final int random = getRandom(1, 5);
		final SkillHolder skill = new SkillHolder(5007, random);
		final SkillHolder skillEffect = new SkillHolder(5008, random);
		broadCastPacket(world, new ExShowScreenMessage(2, -1, 2, 0, 0, 0, 0, true, 4000, false, null, SKILL_MSG.get(random), null));
		broadCastPacket(world, new MagicSkillUse(frintezza, frintezza, skill.getSkillId(), skill.getLevel(), skill.getSkill().getHitTime(), 0));
		for (Player player : world.getPlayers())
		{
			if ((player != null) && player.isOnline())
			{
				frintezza.setTarget(player);
				frintezza.doCast(skillEffect.getSkill());
			}
		}
		world.setParameter("isPlayingSong", false);
	}
	
	private void disablePlayers(Instance world)
	{
		for (Player player : world.getPlayers())
		{
			if ((player != null) && player.isOnline())
			{
				player.abortAttack();
				player.abortCast();
				player.disableAllSkills();
				player.setTarget(null);
				player.stopMove(null);
				player.setIsImmobilized(true);
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			}
		}
	}
	
	private void enablePlayers(Instance world)
	{
		for (Player player : world.getPlayers())
		{
			if ((player != null) && player.isOnline())
			{
				player.enableAllSkills();
				player.setIsImmobilized(false);
			}
		}
	}
	
	void broadCastPacket(Instance world, ServerPacket packet)
	{
		for (Player player : world.getPlayers())
		{
			if ((player != null) && player.isOnline())
			{
				player.sendPacket(packet);
			}
		}
	}
	
	private void sendPacketX(Instance world, ServerPacket packet1, ServerPacket packet2, int x)
	{
		for (Player player : world.getPlayers())
		{
			if ((player != null) && player.isOnline())
			{
				if (player.getX() < x)
				{
					player.sendPacket(packet1);
				}
				else
				{
					player.sendPacket(packet2);
				}
			}
		}
	}
	public static LastImperialTomb provider()
	{
		return new LastImperialTomb();
	}
}

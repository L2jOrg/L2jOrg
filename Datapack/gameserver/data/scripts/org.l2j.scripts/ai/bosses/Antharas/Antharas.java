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
package ai.bosses.Antharas;

import ai.AbstractNpcAI;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.NoRestartZone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.l2j.gameserver.util.GameUtils.isNpc;
import static org.l2j.gameserver.util.GameUtils.isPlayer;
import static org.l2j.gameserver.util.MathUtil.calculateDistance3D;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * Antharas AI.
 * @author Vicochips
 */
public final class Antharas extends AbstractNpcAI
{
	// NPC
	private static final int ANTHARAS = 29068; // Antharas
	private static final int BEHEMOTH = 29069; // Behemoth Dragon
	private static final int TERASQUE = 29190; // Tarask Dragon
	private static final int BOMBER = 29070; // Dragon Bomber
	// Skill
	private static final SkillHolder ANTH_JUMP = new SkillHolder(4106, 1); // Antharas Stun
	private static final SkillHolder ANTH_TAIL = new SkillHolder(4107, 1); // Antharas Stun
	private static final SkillHolder ANTH_FEAR = new SkillHolder(4108, 1); // Antharas Terror
	private static final SkillHolder ANTH_DEBUFF = new SkillHolder(4109, 1); // Curse of Antharas
	private static final SkillHolder ANTH_MOUTH = new SkillHolder(4110, 2); // Breath Attack
	private static final SkillHolder ANTH_BREATH = new SkillHolder(4111, 1); // Antharas Fossilization
	private static final SkillHolder ANTH_NORM_ATTACK = new SkillHolder(4112, 1); // Ordinary Attack
	private static final SkillHolder ANTH_NORM_ATTACK_EX = new SkillHolder(4113, 1); // Animal doing ordinary attack
	private static final SkillHolder ANTH_REGEN_1 = new SkillHolder(4125, 1); // Antharas Regeneration
	private static final SkillHolder ANTH_REGEN_2 = new SkillHolder(4239, 1); // Antharas Regeneration
	private static final SkillHolder ANTH_REGEN_3 = new SkillHolder(4240, 1); // Antharas Regeneration
	private static final SkillHolder ANTH_REGEN_4 = new SkillHolder(4241, 1); // Antharas Regeneration
	private static final SkillHolder DISPEL_BOM = new SkillHolder(5042, 1); // NPC Dispel Bomb
	private static final SkillHolder ANTH_ANTI_STRIDER = new SkillHolder(4258, 1); // Hinder Strider
	private static final SkillHolder ANTH_FEAR_SHORT = new SkillHolder(5092, 1); // Antharas Terror
	private static final SkillHolder ANTH_METEOR = new SkillHolder(5093, 1); // Antharas Meteor
	// Zone
	private static final NoRestartZone zone = ZoneManager.getInstance().getZoneById(70050, NoRestartZone.class); // Antharas Nest zone
	// Status
	private static final int ALIVE = 0;
	private static final int WAITING = 1;
	private static final int IN_FIGHT = 2;
	private static final int DEAD = 3;
	// Misc
	private GrandBoss _antharas = null;
	private static long _lastAttack = 0;
	private static int _minionCount = 0;
	private static int minionMultipler = 0;
	private static int moveChance = 0;
	private static int sandStorm = 0;
	private static Player attacker_1 = null;
	private static Player attacker_2 = null;
	private static Player attacker_3 = null;
	private static int attacker_1_hate = 0;
	private static int attacker_2_hate = 0;
	private static int attacker_3_hate = 0;
	
	private Antharas()
	{
		addSpawnId(ANTHARAS);
		addMoveFinishedId(BOMBER);
		addAggroRangeEnterId(BOMBER);
		addSpellFinishedId(ANTHARAS);
		addAttackId(ANTHARAS, BOMBER, BEHEMOTH, TERASQUE);
		addKillId(ANTHARAS, TERASQUE, BEHEMOTH);
		
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(ANTHARAS);
		final double curr_hp = info.getDouble("currentHP");
		final double curr_mp = info.getDouble("currentMP");
		final int loc_x = info.getInt("loc_x");
		final int loc_y = info.getInt("loc_y");
		final int loc_z = info.getInt("loc_z");
		final int heading = info.getInt("heading");
		final long respawnTime = info.getLong("respawn_time");
		
		switch (getStatus())
		{
			case ALIVE:
			{
				_antharas = (GrandBoss) addSpawn(ANTHARAS, 125798, 125390, -3952, 0, false, 0);
				_antharas.setCurrentHpMp(curr_hp, curr_mp);
				addBoss(_antharas);
				break;
			}
			case WAITING:
			{
				_antharas = (GrandBoss) addSpawn(ANTHARAS, 125798, 125390, -3952, 0, false, 0);
				_antharas.setCurrentHpMp(curr_hp, curr_mp);
				addBoss(_antharas);
				startQuestTimer("SPAWN_ANTHARAS", Config.ANTHARAS_WAIT_TIME * 60000, null, null);
				break;
			}
			case IN_FIGHT:
			{
				_antharas = (GrandBoss) addSpawn(ANTHARAS, loc_x, loc_y, loc_z, heading, false, 0);
				_antharas.setCurrentHpMp(curr_hp, curr_mp);
				addBoss(_antharas);
				_lastAttack = System.currentTimeMillis();
				startQuestTimer("CHECK_ATTACK", 60000, _antharas, null);
				startQuestTimer("SPAWN_MINION", 300000, _antharas, null);
				break;
			}
			case DEAD:
			{
				final long remain = respawnTime - System.currentTimeMillis();
				if (remain > 0)
				{
					startQuestTimer("CLEAR_STATUS", remain, null, null);
				}
				else
				{
					setStatus(ALIVE);
					_antharas = (GrandBoss) addSpawn(ANTHARAS, 125798, 125390, -3952, 0, false, 0);
					addBoss(_antharas);
				}
				break;
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "SPAWN_ANTHARAS":
			{
				_antharas.teleToLocation(125798, 125390, -3952, 32542);
				setStatus(IN_FIGHT);
				_lastAttack = System.currentTimeMillis();
				zone.broadcastPacket(new PlaySound("BS02_A"));
				startQuestTimer("CAMERA_1", 23, _antharas, null);
				break;
			}
			case "CAMERA_1":
			{
				zone.broadcastPacket(new SpecialCamera(npc, 700, 13, -19, 0, 10000, 20000, 0, 0, 0, 0, 0));
				startQuestTimer("CAMERA_2", 3000, npc, null);
				break;
			}
			case "CAMERA_2":
			{
				zone.broadcastPacket(new SpecialCamera(npc, 700, 13, 0, 6000, 10000, 20000, 0, 0, 0, 0, 0));
				startQuestTimer("CAMERA_3", 10000, npc, null);
				break;
			}
			case "CAMERA_3":
			{
				zone.broadcastPacket(new SpecialCamera(npc, 3700, 0, -3, 0, 10000, 10000, 0, 0, 0, 0, 0));
				zone.broadcastPacket(new SocialAction(npc.getObjectId(), 1));
				startQuestTimer("CAMERA_4", 200, npc, null);
				startQuestTimer("SOCIAL", 5200, npc, null);
				break;
			}
			case "CAMERA_4":
			{
				zone.broadcastPacket(new SpecialCamera(npc, 1100, 0, -3, 22000, 10000, 30000, 0, 0, 0, 0, 0));
				startQuestTimer("CAMERA_5", 10800, npc, null);
				break;
			}
			case "CAMERA_5":
			{
				zone.broadcastPacket(new SpecialCamera(npc, 1100, 0, -3, 300, 10000, 7000, 0, 0, 0, 0, 0));
				startQuestTimer("START_MOVE", 1900, npc, null);
				break;
			}
			case "SOCIAL":
			{
				zone.broadcastPacket(new SocialAction(npc.getObjectId(), 2));
				break;
			}
			case "START_MOVE":
			{


				World.getInstance().forAnyVisibleObjectInRange(npc, Player.class, 4000,
						hero -> zone.broadcastPacket(new ExShowScreenMessage(NpcStringId.S1_YOU_CANNOT_HOPE_TO_DEFEAT_ME_WITH_YOUR_MEAGER_STRENGTH, 2, 4000, hero.getName())),  Player::isHero);

				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(179011, 114871, -7704));
				startQuestTimer("CHECK_ATTACK", 60000, npc, null);
				startQuestTimer("SPAWN_MINION", 300000, npc, null);
				break;
			}
			case "SET_REGEN":
			{
				if (npc != null)
				{
					if (npc.getCurrentHp() < (npc.getMaxHp() * 0.25))
					{
						if (!npc.isAffectedBySkill(ANTH_REGEN_4.getSkillId()))
						{
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, ANTH_REGEN_4.getSkill(), npc);
						}
					}
					else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.5))
					{
						if (!npc.isAffectedBySkill(ANTH_REGEN_3.getSkillId()))
						{
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, ANTH_REGEN_3.getSkill(), npc);
						}
					}
					else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.75))
					{
						if (!npc.isAffectedBySkill(ANTH_REGEN_2.getSkillId()))
						{
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, ANTH_REGEN_2.getSkill(), npc);
						}
					}
					else if (!npc.isAffectedBySkill(ANTH_REGEN_1.getSkillId()))
					{
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, ANTH_REGEN_1.getSkill(), npc);
					}
					startQuestTimer("SET_REGEN", 60000, npc, null);
				}
				break;
			}
			case "CHECK_ATTACK":
			{
				if ((npc != null) && ((_lastAttack + 900000) < System.currentTimeMillis()))
				{
					setStatus(ALIVE);

					//oustCreatures();

					cancelQuestTimer("CHECK_ATTACK", npc, null);
					cancelQuestTimer("SPAWN_MINION", npc, null);
				}
				else if (npc != null)
				{
					if (attacker_1_hate > 10)
					{
						attacker_1_hate -= getRandom(10);
					}
					if (attacker_2_hate > 10)
					{
						attacker_2_hate -= getRandom(10);
					}
					if (attacker_3_hate > 10)
					{
						attacker_3_hate -= getRandom(10);
					}
					manageSkills(npc);
					startQuestTimer("CHECK_ATTACK", 60000, npc, null);
				}
				break;
			}
			case "SPAWN_MINION":
			{
				if ((minionMultipler > 1) && (_minionCount < (100 - (minionMultipler * 2))))
				{
					for (int i = 0; i < minionMultipler; i++)
					{
						addSpawn(BEHEMOTH, npc, true);
						addSpawn(TERASQUE, npc, true);
					}
					_minionCount += minionMultipler * 2;
				}
				else if (_minionCount < 98)
				{
					addSpawn(BEHEMOTH, npc, true);
					addSpawn(TERASQUE, npc, true);
					_minionCount += 2;
				}
				else if (_minionCount < 99)
				{
					addSpawn(getRandomBoolean() ? BEHEMOTH : TERASQUE, npc, true);
					_minionCount++;
				}
				
				if ((getRandom(100) > 10) && (minionMultipler < 4))
				{
					minionMultipler++;
				}
				startQuestTimer("SPAWN_MINION", 300000, npc, null);
				break;
			}
			case "CLEAR_ZONE":
			{
				zone.forEachCreature(creature -> {
					if(isNpc(creature)) {
						creature.deleteMe();
					} else if(isPlayer(creature)) {
						//creature.teleToLocation(79800 + getRandom(600), 151200 + getRandom(1100), -3534);
					}
				});
				break;
			}
			case "TID_USED_FEAR":
			{
				if ((npc != null) && (sandStorm == 0))
				{
					sandStorm = 1;
					npc.disableCoreAI(true);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(177648, 114816, -7735));
					startQuestTimer("TID_FEAR_MOVE_TIMEOVER", 2000, npc, null);
					startQuestTimer("TID_FEAR_COOLTIME", 300000, npc, null);
				}
				break;
			}
			case "TID_FEAR_COOLTIME":
			{
				sandStorm = 0;
				break;
			}
			case "TID_FEAR_MOVE_TIMEOVER":
			{
				if ((sandStorm == 1) && (npc.getX() == 177648) && (npc.getY() == 114816))
				{
					sandStorm = 2;
					moveChance = 0;
					npc.disableCoreAI(false);
				}
				else if (sandStorm == 1)
				{
					if (moveChance <= 3)
					{
						moveChance++;
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(177648, 114816, -7735));
						startQuestTimer("TID_FEAR_MOVE_TIMEOVER", 5000, npc, null);
					}
					else
					{
						//npc.teleToLocation(177648, 114816, -7735, npc.getHeading());
						startQuestTimer("TID_FEAR_MOVE_TIMEOVER", 1000, npc, null);
					}
				}
				break;
			}
			case "CLEAR_STATUS":
			{
				_antharas = (GrandBoss) addSpawn(ANTHARAS, 185708, 114298, -8221, 0, false, 0);
				addBoss(_antharas);
				Broadcast.toAllOnlinePlayers(new Earthquake(185708, 114298, -8221, 20, 10));
				setStatus(ALIVE);
				break;
			}
			case "SKIP_WAITING":
			{
				if (getStatus() == WAITING)
				{
					cancelQuestTimer("SPAWN_ANTHARAS", null, null);
					notifyEvent("SPAWN_ANTHARAS", null, null);
					player.sendMessage(getClass().getSimpleName() + ": Skipping waiting time ...");
				}
				else
				{
					player.sendMessage(getClass().getSimpleName() + ": You can't skip waiting time right now!");
				}
				break;
			}
			case "RESPAWN_ANTHARAS":
			{
				if (getStatus() == DEAD)
				{
					setRespawn(0);
					cancelQuestTimer("CLEAR_STATUS", null, null);
					notifyEvent("CLEAR_STATUS", null, null);
					player.sendMessage(getClass().getSimpleName() + ": Antharas has been respawned.");
				}
				else
				{
					player.sendMessage(getClass().getSimpleName() + ": You can't respawn antharas while antharas is alive!");
				}
				break;
			}
			case "DESPAWN_MINIONS":
			{
				if (getStatus() == IN_FIGHT)
				{
					_minionCount = 0;
					zone.forEachCreature(Creature::deleteMe, creature -> isNpc(creature) && (creature.getId() == BEHEMOTH || creature.getId() == TERASQUE));

					if (player != null) // Player dont will be null just when is this event called from GM command
					{
						player.sendMessage(getClass().getSimpleName() + ": All minions have been deleted!");
					}
				}
				else if (player != null) // Player dont will be null just when is this event called from GM command
				{
					player.sendMessage(getClass().getSimpleName() + ": You can't despawn minions right now!");
				}
				break;
			}
			case "ABORT_FIGHT":
			{
				if (getStatus() == IN_FIGHT)
				{
					setStatus(ALIVE);
					cancelQuestTimer("CHECK_ATTACK", _antharas, null);
					cancelQuestTimer("SPAWN_MINION", _antharas, null);

					//oustCreatures();
					player.sendMessage(getClass().getSimpleName() + ": Fight has been aborted!");
				}
				else
				{
					player.sendMessage(getClass().getSimpleName() + ": You can't abort fight right now!");
				}
				break;
			}
			case "MANAGE_SKILL":
			{
				manageSkills(npc);
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}

	private void oustCreatures() {
		zone.forEachCreature(creature -> {
			if(isNpc(creature)) {
				if(creature.getId() == ANTHARAS) {
					//creature.teleToLocation(185708, 114298, -8221);
				} else {
					creature.deleteMe();
				}
			} else if(isPlayer(creature)) {
				//creature.teleToLocation(79800 + getRandom(600), 151200 + getRandom(1100), -3534);
			}
		});
	}

	@Override
	public String onAggroRangeEnter(Npc npc, Player player, boolean isSummon)
	{
		npc.doCast(DISPEL_BOM.getSkill());
		npc.doDie(player);
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
	{
		_lastAttack = System.currentTimeMillis();
		
		if (npc.getId() == BOMBER)
		{
			if (isInsideRadius3D(npc,  attacker, 230))
			{
				npc.doCast(DISPEL_BOM.getSkill());
				npc.doDie(attacker);
			}
		}
		else if (npc.getId() == ANTHARAS)
		{
			if (!zone.isCreatureInZone(attacker) || (getStatus() != IN_FIGHT))
			{
				LOGGER.warn(": Player " + attacker.getName() + " attacked Antharas in invalid conditions!");
				//attacker.teleToLocation(80464, 152294, -3534);
			}

			if ((attacker.getMountType() == MountType.STRIDER) && !attacker.isAffectedBySkill(ANTH_ANTI_STRIDER.getSkillId()) && SkillCaster.checkUseConditions(npc, ANTH_ANTI_STRIDER.getSkill()))
			{
				addSkillCastDesire(npc, attacker, ANTH_ANTI_STRIDER.getSkill(), 100);
			}
			
			if (skill == null)
			{
				refreshAiParams(attacker, damage * 1000);
			}
			else if (npc.getCurrentHp() < npc.getMaxHp() * 0.25)
			{
				refreshAiParams(attacker, damage / 3 * 100);
			}
			else if (npc.getCurrentHp() < npc.getMaxHp() * 0.5)
			{
				refreshAiParams(attacker, damage * 20);
			}
			else if (npc.getCurrentHp() < npc.getMaxHp() * 0.75)
			{
				refreshAiParams(attacker, damage * 10);
			}
			else
			{
				refreshAiParams(attacker, damage / 3 * 20);
			}
			manageSkills(npc);
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (zone.isCreatureInZone(killer))
		{
			if (npc.getId() == ANTHARAS)
			{
				_antharas = null;
				notifyEvent("DESPAWN_MINIONS", null, null);
				zone.broadcastPacket(new SpecialCamera(npc, 1200, 20, -10, 0, 10000, 13000, 0, 0, 0, 0, 0));
				zone.broadcastPacket(new PlaySound("BS01_D"));
				final long respawnTime = (Config.ANTHARAS_SPAWN_INTERVAL + getRandom(-Config.ANTHARAS_SPAWN_RANDOM, Config.ANTHARAS_SPAWN_RANDOM)) * 3600000;
				setRespawn(respawnTime);
				startQuestTimer("CLEAR_STATUS", respawnTime, null, null);
				cancelQuestTimer("SET_REGEN", npc, null);
				cancelQuestTimer("CHECK_ATTACK", npc, null);
				cancelQuestTimer("SPAWN_MINION", npc, null);
				startQuestTimer("CLEAR_ZONE", 900000, null, null);
				setStatus(DEAD);
			}
			else
			{
				_minionCount--;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public void onMoveFinished(Npc npc)
	{
		npc.doCast(DISPEL_BOM.getSkill());
		npc.doDie(null);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if (npc.getId() == ANTHARAS)
		{
			((Attackable) npc).setCanReturnToSpawnPoint(false);
			npc.setRandomWalking(false);
			cancelQuestTimer("SET_REGEN", npc, null);
			startQuestTimer("SET_REGEN", 60000, npc, null);
		}
		else
		{
			for (int i = 1; i <= 6; i++)
			{
				final int x = npc.getParameters().getInt("suicide" + i + "_x");
				final int y = npc.getParameters().getInt("suicide" + i + "_y");
				final Attackable bomber = (Attackable) addSpawn(BOMBER, npc.getX(), npc.getY(), npc.getZ(), 0, true, 15000, true);
				bomber.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(x, y, npc.getZ()));
			}
			npc.deleteMe();
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onSpellFinished(Npc npc, Player player, Skill skill)
	{
		if ((skill.getId() == ANTH_FEAR.getSkillId()) || (skill.getId() == ANTH_FEAR_SHORT.getSkillId()))
		{
			startQuestTimer("TID_USED_FEAR", 7000, npc, null);
		}
		startQuestTimer("MANAGE_SKILL", 1000, npc, null);
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public boolean unload(boolean removeFromList)
	{
		if (_antharas != null)
		{
			_antharas.deleteMe();
			_antharas = null;
		}
		return super.unload(removeFromList);
	}
	
	private int getStatus()
	{
		return GrandBossManager.getInstance().getBossStatus(ANTHARAS);
	}
	
	private void addBoss(GrandBoss grandboss)
	{
		GrandBossManager.getInstance().addBoss(grandboss);
	}
	
	private void setStatus(int status)
	{
		GrandBossManager.getInstance().setBossStatus(ANTHARAS, status);
	}
	
	private void setRespawn(long respawnTime)
	{
		GrandBossManager.getInstance().getStatsSet(ANTHARAS).set("respawn_time", System.currentTimeMillis() + respawnTime);
	}
	
	private void refreshAiParams(Player attacker, int damage)
	{
		if ((attacker_1 != null) && (attacker == attacker_1))
		{
			if (attacker_1_hate < (damage + 1000))
			{
				attacker_1_hate = damage + getRandom(3000);
			}
		}
		else if ((attacker_2 != null) && (attacker == attacker_2))
		{
			if (attacker_2_hate < (damage + 1000))
			{
				attacker_2_hate = damage + getRandom(3000);
			}
		}
		else if ((attacker_3 != null) && (attacker == attacker_3))
		{
			if (attacker_3_hate < (damage + 1000))
			{
				attacker_3_hate = damage + getRandom(3000);
			}
		}
		else
		{
			final int i1 = CommonUtil.min(attacker_1_hate, attacker_2_hate, attacker_3_hate);
			if (attacker_1_hate == i1)
			{
				attacker_1_hate = damage + getRandom(3000);
				attacker_1 = attacker;
			}
			else if (attacker_2_hate == i1)
			{
				attacker_2_hate = damage + getRandom(3000);
				attacker_2 = attacker;
			}
			else if (attacker_3_hate == i1)
			{
				attacker_3_hate = damage + getRandom(3000);
				attacker_3 = attacker;
			}
		}
	}
	
	private void manageSkills(Npc npc)
	{
		if (npc.isCastingNow() || npc.isCoreAIDisabled() || !npc.isInCombat())
		{
			return;
		}
		
		int i1 = 0;
		int i2 = 0;
		Player c2 = null;
		if ((attacker_1 == null) || !isInsideRadius3D(npc, attacker_1, 9000) || attacker_1.isDead())
		{
			attacker_1_hate = 0;
		}
		
		if ((attacker_2 == null) || !isInsideRadius3D(npc, attacker_2, 9000) || attacker_2.isDead())
		{
			attacker_2_hate = 0;
		}
		
		if ((attacker_3 == null) || !isInsideRadius3D(npc, attacker_3, 9000) || attacker_3.isDead())
		{
			attacker_3_hate = 0;
		}
		
		if (attacker_1_hate > attacker_2_hate)
		{
			i1 = 2;
			i2 = attacker_1_hate;
			c2 = attacker_1;
		}
		else if (attacker_2_hate > 0)
		{
			i1 = 3;
			i2 = attacker_2_hate;
			c2 = attacker_2;
		}
		
		if (attacker_3_hate > i2)
		{
			i1 = 4;
			i2 = attacker_3_hate;
			c2 = attacker_3;
		}
		if (i2 > 0)
		{
			if (getRandom(100) < 70)
			{
				switch (i1)
				{
					case 2:
					{
						attacker_1_hate = 500;
						break;
					}
					case 3:
					{
						attacker_2_hate = 500;
						break;
					}
					case 4:
					{
						attacker_3_hate = 500;
						break;
					}
				}
			}
			
			final double distance_c2 = calculateDistance3D(npc, c2);
			final double direction_c2 = npc.calculateDirectionTo(c2);
			
			SkillHolder skillToCast;
			boolean castOnTarget = false;
			if (npc.getCurrentHp() < (npc.getMaxHp() * 0.25))
			{
				if (getRandom(100) < 30)
				{
					castOnTarget = true;
					skillToCast = ANTH_MOUTH;
				}
				else if ((getRandom(100) < 80) && (((distance_c2 < 1423) && (direction_c2 < 188) && (direction_c2 > 172)) || ((distance_c2 < 802) && (direction_c2 < 194) && (direction_c2 > 166))))
				{
					skillToCast = ANTH_TAIL;
				}
				else if ((getRandom(100) < 40) && (((distance_c2 < 850) && (direction_c2 < 210) && (direction_c2 > 150)) || ((distance_c2 < 425) && (direction_c2 < 270) && (direction_c2 > 90))))
				{
					skillToCast = ANTH_DEBUFF;
				}
				else if ((getRandom(100) < 10) && (distance_c2 < 1100))
				{
					skillToCast = ANTH_JUMP;
				}
				else if (getRandom(100) < 10)
				{
					castOnTarget = true;
					skillToCast = ANTH_METEOR;
				}
				else if (getRandom(100) < 6)
				{
					castOnTarget = true;
					skillToCast = ANTH_BREATH;
				}
				else if (getRandomBoolean())
				{
					castOnTarget = true;
					skillToCast = ANTH_NORM_ATTACK_EX;
				}
				else if (getRandom(100) < 5)
				{
					castOnTarget = true;
					skillToCast = getRandomBoolean() ? ANTH_FEAR : ANTH_FEAR_SHORT;
				}
				else
				{
					castOnTarget = true;
					skillToCast = ANTH_NORM_ATTACK;
				}
			}
			else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.5))
			{
				if ((getRandom(100) < 80) && (((distance_c2 < 1423) && (direction_c2 < 188) && (direction_c2 > 172)) || ((distance_c2 < 802) && (direction_c2 < 194) && (direction_c2 > 166))))
				{
					skillToCast = ANTH_TAIL;
				}
				else if ((getRandom(100) < 40) && (((distance_c2 < 850) && (direction_c2 < 210) && (direction_c2 > 150)) || ((distance_c2 < 425) && (direction_c2 < 270) && (direction_c2 > 90))))
				{
					skillToCast = ANTH_DEBUFF;
				}
				else if ((getRandom(100) < 10) && (distance_c2 < 1100))
				{
					skillToCast = ANTH_JUMP;
				}
				else if (getRandom(100) < 7)
				{
					castOnTarget = true;
					skillToCast = ANTH_METEOR;
				}
				else if (getRandom(100) < 6)
				{
					castOnTarget = true;
					skillToCast = ANTH_BREATH;
				}
				else if (getRandomBoolean())
				{
					castOnTarget = true;
					skillToCast = ANTH_NORM_ATTACK_EX;
				}
				else if (getRandom(100) < 5)
				{
					castOnTarget = true;
					skillToCast = getRandomBoolean() ? ANTH_FEAR : ANTH_FEAR_SHORT;
				}
				else
				{
					castOnTarget = true;
					skillToCast = ANTH_NORM_ATTACK;
				}
			}
			else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.75))
			{
				if ((getRandom(100) < 80) && (((distance_c2 < 1423) && (direction_c2 < 188) && (direction_c2 > 172)) || ((distance_c2 < 802) && (direction_c2 < 194) && (direction_c2 > 166))))
				{
					skillToCast = ANTH_TAIL;
				}
				else if ((getRandom(100) < 10) && (distance_c2 < 1100))
				{
					skillToCast = ANTH_JUMP;
				}
				else if (getRandom(100) < 5)
				{
					castOnTarget = true;
					skillToCast = ANTH_METEOR;
				}
				else if (getRandom(100) < 6)
				{
					castOnTarget = true;
					skillToCast = ANTH_BREATH;
				}
				else if (getRandomBoolean())
				{
					castOnTarget = true;
					skillToCast = ANTH_NORM_ATTACK_EX;
				}
				else if (getRandom(100) < 5)
				{
					castOnTarget = true;
					skillToCast = getRandomBoolean() ? ANTH_FEAR : ANTH_FEAR_SHORT;
				}
				else
				{
					castOnTarget = true;
					skillToCast = ANTH_NORM_ATTACK;
				}
			}
			else if ((getRandom(100) < 80) && (((distance_c2 < 1423) && (direction_c2 < 188) && (direction_c2 > 172)) || ((distance_c2 < 802) && (direction_c2 < 194) && (direction_c2 > 166))))
			{
				skillToCast = ANTH_TAIL;
			}
			else if (getRandom(100) < 3)
			{
				castOnTarget = true;
				skillToCast = ANTH_METEOR;
			}
			else if (getRandom(100) < 6)
			{
				castOnTarget = true;
				skillToCast = ANTH_BREATH;
			}
			else if (getRandomBoolean())
			{
				castOnTarget = true;
				skillToCast = ANTH_NORM_ATTACK_EX;
			}
			else if (getRandom(100) < 5)
			{
				castOnTarget = true;
				skillToCast = getRandomBoolean() ? ANTH_FEAR : ANTH_FEAR_SHORT;
			}
			else
			{
				castOnTarget = true;
				skillToCast = ANTH_NORM_ATTACK;
			}
			
			if ((skillToCast != null) && SkillCaster.checkUseConditions(npc, skillToCast.getSkill()))
			{
				if (castOnTarget)
				{
					addSkillCastDesire(npc, c2, skillToCast.getSkill(), 100);
				}
				else
				{
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skillToCast.getSkill(), npc);
				}
			}
		}
	}

	public static AbstractNpcAI provider()
	{
		return new Antharas();
	}
}
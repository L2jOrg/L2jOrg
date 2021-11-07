/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.ai.bosses;

import org.l2j.commons.util.CommonUtil;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.database.data.GrandBossData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.instancemanager.BossStatus;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.GrandBoss;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.settings.NpcSettings;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.l2j.gameserver.world.zone.type.NoRestartZone;
import org.l2j.scripts.ai.AbstractNpcAI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.gameserver.util.GameUtils.isNpc;
import static org.l2j.gameserver.util.MathUtil.calculateDistance3D;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * Antharas AI.
 * @author Vicochips
 * @author JoeAlisson
 */
public final class Antharas extends AbstractNpcAI {

	private static final Logger LOGGER = LoggerFactory.getLogger(Antharas.class);

	private static final int ANTHARAS = 29068; // Antharas
	private static final int BEHEMOTH = 29069; // Behemoth Dragon
	private static final int TERASQUE = 29190; // Tarask Dragon
	private static final int BOMBER = 29070; // Dragon Bomber

	private static final SkillHolder DISPEL_BOM = new SkillHolder(5042, 1); // NPC Dispel Bomb
	private static final SkillHolder ANTH_ANTI_STRIDER = new SkillHolder(4258, 1); // Hinder Strider

	private static final AntharasSkill ANTH_TAIL = new AntharasSkill(4107, 1, false, 80, true,
			new DistanceInfo(1423, 172, 188), new DistanceInfo(802, 166, 194)); // Antharas Stun

	private static final AntharasSkill ANTH_METEOR = new AntharasSkill(5093, 1, true, 3, false, (DistanceInfo[]) null); // Antharas Meteor
	private static final AntharasSkill ANTH_FEAR = new AntharasSkill(4108, 1, true, 5, false, (DistanceInfo) null); // Antharas Terror
	private static final AntharasSkill ANTH_FEAR_SHORT = new AntharasSkill(5092, 1, true, 5, false, (DistanceInfo) null); // Antharas Terror
	private static final AntharasSkill ANTH_BREATH = new AntharasSkill(4111, 1, true, 6, false, (DistanceInfo) null); // Antharas Fossilization
	private static final AntharasSkill ANTH_NORM_ATTACK_EX = new AntharasSkill(4113, 1, true, 50, true, (DistanceInfo) null); // Animal doing ordinary attack
	private static final AntharasSkill ANTH_NORM_ATTACK = new AntharasSkill(4112, 1, true, 100, true, (DistanceInfo) null); // Ordinary Attack
	private static final AntharasSkill ANTH_JUMP = new AntharasSkill(4106, 1, false,  10, true, new DistanceInfo(1100, Integer.MIN_VALUE	,  Integer.MAX_VALUE)); // Antharas Stun
	private static final AntharasSkill ANTH_DEBUFF = new AntharasSkill(4109, 1, false, 40, true, new DistanceInfo(850, 150, 210), new DistanceInfo(425, 90, 270)); // Curse of Antharas
	private static final AntharasSkill ANTH_MOUTH = new AntharasSkill(4110, 2, true, 30, true, (DistanceInfo) null); // Breath Attack

	private static final NoRestartZone zone = ZoneEngine.getInstance().getZoneById(70050, NoRestartZone.class); // Antharas Nest zone

	private GrandBoss antharas = null;
	private long lastAttack = 0;
	private int minionCount = 0;
	private int minionMultipler = 0;
	private Player attacker1 = null;
	private Player attacker2 = null;
	private Player attacker3 = null;
	private int attacker1Hate = 0;
	private int attacker2Hate = 0;
	private int attacker3Hate = 0;

	private final SkillDecider[] skillDeciders = {
		new SkillDecider(0.25f, 2.5, ANTH_MOUTH, ANTH_TAIL, ANTH_DEBUFF, ANTH_JUMP, ANTH_BREATH, ANTH_METEOR, ANTH_FEAR, ANTH_FEAR_SHORT, ANTH_NORM_ATTACK_EX, ANTH_NORM_ATTACK),
		new SkillDecider(0.5f, 2.0, ANTH_TAIL, ANTH_DEBUFF, ANTH_JUMP, ANTH_BREATH, ANTH_METEOR, ANTH_FEAR, ANTH_FEAR_SHORT, ANTH_NORM_ATTACK_EX, ANTH_NORM_ATTACK),
		new SkillDecider(0.75f, 1.5, ANTH_TAIL, ANTH_JUMP, ANTH_METEOR, ANTH_BREATH, ANTH_FEAR, ANTH_FEAR_SHORT, ANTH_NORM_ATTACK_EX, ANTH_NORM_ATTACK),
		new SkillDecider(1, 1, ANTH_TAIL, ANTH_METEOR, ANTH_FEAR, ANTH_FEAR_SHORT, ANTH_BREATH, ANTH_NORM_ATTACK_EX, ANTH_NORM_ATTACK)
	};

	private Antharas() {
		addSpawnId(ANTHARAS);
		addMoveFinishedId(BOMBER);
		addAggroRangeEnterId(BOMBER);
		addSpellFinishedId(ANTHARAS);
		addAttackId(ANTHARAS, BOMBER, BEHEMOTH, TERASQUE);
		addKillId(ANTHARAS, TERASQUE, BEHEMOTH);

		final var info = GrandBossManager.getInstance().getBossData(ANTHARAS);

		switch (getStatus()) {
			case ALIVE -> spawnAntharas(info, 125798, 125390, -3952, 0);
			case FIGHTING -> startFighting(info);
			case DEAD -> respawnAntharas(info);
		}
	}

	private void respawnAntharas(GrandBossData info) {
		final long remain = info.getRespawnTime() - System.currentTimeMillis();
		if (remain > 0) {
			startQuestTimer("CLEAR_STATUS", remain, null, null);
		} else {
			setStatus(BossStatus.ALIVE);
			antharas = (GrandBoss) addSpawn(ANTHARAS, 125798, 125390, -3952, 0, false, 0);
			addBoss(antharas);
		}
	}

	private void startFighting(GrandBossData info) {
		spawnAntharas(info, info.getX(), info.getY(), info.getZ(), info.getHeading());
		lastAttack = System.currentTimeMillis();
		startQuestTimer("CHECK_ATTACK", 60000, antharas, null);
		startQuestTimer("SPAWN_MINION", 300000, antharas, null);
	}

	private void spawnAntharas(GrandBossData info, int x, int y, int z, int heading) {
		antharas = (GrandBoss) addSpawn(ANTHARAS, x, y, z, heading, false, 0);
		antharas.setCurrentHpMp(info.getHp(), info.getMp());
		addBoss(antharas);
	}

	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		switch (event) {
			case "SPAWN_ANTHARAS" -> onSpawnAntharas();
			case "SOCIAL" -> zone.broadcastPacket(new SocialAction(npc.getObjectId(), 2));
			case "START_MOVE" -> onStartMove(npc);
			case "CHECK_ATTACK" -> onCheckAttack(npc);
			case "SPAWN_MINION" -> onSpawnMinion(npc);
			case "CLEAR_STATUS" -> onClearStatus();
			case "RESPAWN_ANTHARAS" -> onRespawnAntharas(player);
			case "DESPAWN_MINIONS" -> onDespawnMinions(player);
			case "MANAGE_SKILL" -> manageSkills(npc);
			default -> LOGGER.warn("Unknown event {}", event);
		}
		return super.onAdvEvent(event, npc, player);
	}

	private void onDespawnMinions(Player player) {
		if (getStatus() == BossStatus.FIGHTING) {
			minionCount = 0;
			zone.forEachCreature(Creature::deleteMe, creature -> isNpc(creature) && (creature.getId() == BEHEMOTH || creature.getId() == TERASQUE));

			if (player != null) // Player dont will be null just when is this event called from GM command
			{
				player.sendMessage(getClass().getSimpleName() + ": All minions have been deleted!");
			}
		} else if (player != null) // Player dont will be null just when is this event called from GM command
		{
			player.sendMessage(getClass().getSimpleName() + ": You can't despawn minions right now!");
		}
	}

	private void onRespawnAntharas(Player player) {
		if (getStatus() == BossStatus.DEAD) {
			setRespawn(0);
			cancelQuestTimer("CLEAR_STATUS", null, null);
			notifyEvent("CLEAR_STATUS", null, null);
			player.sendMessage(getClass().getSimpleName() + ": Antharas has been respawned.");
		} else {
			player.sendMessage(getClass().getSimpleName() + ": You can't respawn antharas while antharas is alive!");
		}
	}

	private void onClearStatus() {
		antharas = (GrandBoss) addSpawn(ANTHARAS, 185708, 114298, -8221, 0, false, 0);
		addBoss(antharas);
		Broadcast.toAllOnlinePlayers(new Earthquake(185708, 114298, -8221, 20, 10));
		setStatus(BossStatus.ALIVE);
	}

	private void onSpawnMinion(Npc npc) {
		if ((minionMultipler > 1) && (minionCount < (100 - (minionMultipler * 2)))) {
			for (int i = 0; i < minionMultipler; i++) {
				addSpawn(BEHEMOTH, npc, true);
				addSpawn(TERASQUE, npc, true);
			}
			minionCount += minionMultipler * 2;
		} else if (minionCount < 98) {
			addSpawn(BEHEMOTH, npc, true);
			addSpawn(TERASQUE, npc, true);
			minionCount += 2;
		} else if (minionCount < 99) {
			addSpawn(Rnd.nextBoolean() ? BEHEMOTH : TERASQUE, npc, true);
			minionCount++;
		}

		if ((Rnd.get(100) > 10) && (minionMultipler < 4)) {
			minionMultipler++;
		}
		startQuestTimer("SPAWN_MINION", 300000, npc, null);
	}

	private void onCheckAttack(Npc npc) {
		if ((npc != null) && ((lastAttack + 900000) < System.currentTimeMillis())) {
			setStatus(BossStatus.ALIVE);

			//oustCreatures();

			cancelQuestTimer("CHECK_ATTACK", npc, null);
			cancelQuestTimer("SPAWN_MINION", npc, null);
		} else if (npc != null) {
			if (attacker1Hate > 10) {
				attacker1Hate -= Rnd.get(10);
			}
			if (attacker2Hate > 10) {
				attacker2Hate -= Rnd.get(10);
			}
			if (attacker3Hate > 10) {
				attacker3Hate -= Rnd.get(10);
			}
			manageSkills(npc);
			startQuestTimer("CHECK_ATTACK", 60000, npc, null);
		}
	}

	private void onStartMove(Npc npc) {
		World.getInstance().forAnyVisibleObjectInRange(npc, Player.class, 4000,
				hero -> zone.broadcastPacket(new ExShowScreenMessage(NpcStringId.S1_YOU_CANNOT_HOPE_TO_DEFEAT_ME_WITH_YOUR_MEAGER_STRENGTH, 2, 4000, hero.getName())), Player::isHero);

		npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(179011, 114871, -7704));
		startQuestTimer("CHECK_ATTACK", 60000, npc, null);
		startQuestTimer("SPAWN_MINION", 300000, npc, null);
	}

	private void onSpawnAntharas() {
		antharas.teleToLocation(125798, 125390, -3952, 32542);
		setStatus(BossStatus.FIGHTING);
		lastAttack = System.currentTimeMillis();
		zone.broadcastPacket(PlaySound.sound("BS02_A"));
		startQuestTimer("CAMERA_1", 23, antharas, null);
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
		lastAttack = System.currentTimeMillis();

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
				antharas = null;
				notifyEvent("DESPAWN_MINIONS", null, null);
				zone.broadcastPacket(new SpecialCamera(npc, 1200, 20, -10, 0, 10000, 13000, 0, 0, 0, 0, 0));
				zone.broadcastPacket(PlaySound.sound("BS01_D"));
				final long respawnTime = NpcSettings.antharasSpawnInterval();
				setRespawn(respawnTime);
				startQuestTimer("CLEAR_STATUS", respawnTime, null, null);
				cancelQuestTimer("SET_REGEN", npc, null);
				cancelQuestTimer("CHECK_ATTACK", npc, null);
				cancelQuestTimer("SPAWN_MINION", npc, null);
				startQuestTimer("CLEAR_ZONE", 900000, null, null);
				setStatus(BossStatus.DEAD);
			}
			else
			{
				minionCount--;
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
		if ((skill.getId() == ANTH_FEAR.id()) || (skill.getId() == ANTH_FEAR_SHORT.id()))
		{
			startQuestTimer("TID_USED_FEAR", 7000, npc, null);
		}
		startQuestTimer("MANAGE_SKILL", 1000, npc, null);
		return super.onSpellFinished(npc, player, skill);
	}

	@Override
	public boolean unload(boolean removeFromList)
	{
		if (antharas != null)
		{
			antharas.deleteMe();
			antharas = null;
		}
		return super.unload(removeFromList);
	}

	private BossStatus getStatus() {
		return GrandBossManager.getInstance().getBossStatus(ANTHARAS);
	}

	private void addBoss(GrandBoss grandboss)
	{
		GrandBossManager.getInstance().addBoss(grandboss);
	}

	private void setStatus(BossStatus status)
	{
		GrandBossManager.getInstance().setBossStatus(ANTHARAS, status);
	}

	private void setRespawn(long respawnTime)
	{
		GrandBossManager.getInstance().getBossData(ANTHARAS).setRespawnTime(System.currentTimeMillis() + respawnTime);
	}

	private void refreshAiParams(Player attacker, int damage)
	{
		if ((attacker1 != null) && (attacker == attacker1))
		{
			if (attacker1Hate < (damage + 1000))
			{
				attacker1Hate = damage + Rnd.get(3000);
			}
		}
		else if ((attacker2 != null) && (attacker == attacker2))
		{
			if (attacker2Hate < (damage + 1000))
			{
				attacker2Hate = damage + Rnd.get(3000);
			}
		}
		else if ((attacker3 != null) && (attacker == attacker3))
		{
			if (attacker3Hate < (damage + 1000))
			{
				attacker3Hate = damage + Rnd.get(3000);
			}
		}
		else
		{
			final int i1 = CommonUtil.min(attacker1Hate, attacker2Hate, attacker3Hate);
			if (attacker1Hate == i1)
			{
				attacker1Hate = damage + Rnd.get(3000);
				attacker1 = attacker;
			}
			else if (attacker2Hate == i1)
			{
				attacker2Hate = damage + Rnd.get(3000);
				attacker2 = attacker;
			}
			else if (attacker3Hate == i1)
			{
				attacker3Hate = damage + Rnd.get(3000);
				attacker3 = attacker;
			}
		}
	}

	private void manageSkills(Npc npc) {
		if (npc.isCastingNow() || npc.isCoreAIDisabled() || !npc.isInCombat()) {
			return;
		}

		int maxHate = 0;
		Player target = null;
		if (isUnavailableTarget(npc, attacker1)) {
			attacker1Hate = 0;
		}

		if (isUnavailableTarget(npc, attacker2)) {
			attacker2Hate = 0;
		}

		if (isUnavailableTarget(npc, attacker3)) {
			attacker3Hate = 0;
		}

		if (attacker1Hate > attacker2Hate) {
			maxHate = attacker1Hate;
			target = attacker1;
			if(Rnd.chance(70)) {
				attacker1Hate = 500;
			}
		} else if (attacker2Hate > 0) {
			maxHate = attacker2Hate;
			target = attacker2;
			if(Rnd.chance(70)) {
				attacker2Hate = 500;
			}
		}

		if (attacker3Hate > maxHate) {
			maxHate = attacker3Hate;
			target = attacker3;
			if(Rnd.chance(70)) {
				attacker3Hate = 500;
			}
		}

		if (maxHate > 0) {
			useSkill(npc, target);
		}
	}

	private void useSkill(Npc npc, Player target) {
		AntharasSkill skillToCast = null;
		for (var skillDecider : skillDeciders) {
			if(skillDecider.checkCondition(npc)) {
				skillToCast = skillDecider.chooseSkill(npc, target);
			}
		}

		if(skillToCast == null) {
			return;
		}

		var skill = skillToCast.skill();
		if(SkillCaster.checkUseConditions(npc, skill)) {
			if (skillToCast.castOnTarget) {
				addSkillCastDesire(npc, target, skill, 100);
			} else {
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, npc);
			}
		}
	}

	private record DistanceInfo(int maxDistance, int minDirection, int maxDirection) { }

	private record AntharasSkill(int id, int level, boolean castOnTarget, int baseChance, boolean fixedChance, DistanceInfo... distanceInfo) {

		public boolean checkCondition(double distance, double direction, double chanceMultiplier) {
			var chance = fixedChance ? baseChance : baseChance * chanceMultiplier;
			return Rnd.chance(chance) && checkDistanceInfo(distance, direction);
		}

		private boolean checkDistanceInfo(double distance, double direction) {
			if(Util.isNullOrEmpty(distanceInfo)) {
				return true;
			}
			for (var info : distanceInfo) {
				if(checkDistanceAndDirection(distance, direction, info.maxDistance, info.maxDirection, info.minDirection)) {
					return true;
				}
			}
			return false;
		}

		private boolean checkDistanceAndDirection(double distance, double direction, int maxDistance, int maxDirection, int minDirection) {
			return distance < maxDistance && direction < maxDirection && direction > minDirection;
		}

		public Skill skill() {
			return SkillEngine.getInstance().getSkill(id, level);
		}
	}

	private record SkillDecider(float hpMultiplier, double chanceMultiplier, AntharasSkill... skills) {
		boolean checkCondition(Npc npc) {
			return npc.getCurrentHp() <= npc.getMaxHp() * hpMultiplier;
		}

		AntharasSkill chooseSkill(Npc npc, Player target) {
			final double distance = calculateDistance3D(npc, target);
			final double direction = npc.calculateDirectionTo(target);
			for (var skill : skills) {
				if (skill.checkCondition(distance, direction, chanceMultiplier)) {
					return skill;
				}
			}
			return null;
		}
	}

	private boolean isUnavailableTarget(Npc npc, Player attacker) {
		return attacker == null || !isInsideRadius3D(npc, attacker, 9000) || attacker.isDead();
	}

	public static AbstractNpcAI provider() {
		return new Antharas();
	}
}
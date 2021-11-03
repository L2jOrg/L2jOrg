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
package org.l2j.gameserver.util;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.holders.MinionHolder;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.settings.NpcSettings;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.MathUtil.calculateOffsetLocation;

/**
 * @author luisantonioa, DS, Mobius
 */
public class MinionList {
    protected final Monster master;
    private final Collection<Monster> spawnedMinions = ConcurrentHashMap.newKeySet();
    private final Collection<ScheduledFuture<?>> _respawnTasks = ConcurrentHashMap.newKeySet();

    public MinionList(Monster pMaster) {
        if (pMaster == null) {
            throw new NullPointerException("MinionList: master is null");
        }
        master = pMaster;
    }

    /**
     * Init a Minion and add it in the world as a visible object.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Get the template of the Minion to spawn</li>
     * <li>Create and Init the Minion and generate its Identifier</li>
     * <li>Set the Minion HP, MP and Heading</li>
     * <li>Set the Minion leader to this RaidBoss</li>
     * <li>Init the position of the Minion and add it in the world as a visible object</li><BR>
     * <BR>
     *
     * @param master   Monster used as master for this minion
     * @param minionId The NpcTemplate Identifier of the Minion to spawn
     */
    public static Monster spawnMinion(Monster master, int minionId) {
        // Get the template of the Minion to spawn
        final NpcTemplate minionTemplate = NpcData.getInstance().getTemplate(minionId);
        if (minionTemplate == null) {
            return null;
        }

        return initializeNpcInstance(master, new Monster(minionTemplate));
    }

    protected static Monster initializeNpcInstance(Monster master, Monster minion) {
        minion.stopAllEffects();
        minion.setIsDead(false);
        minion.setDecayed(false);

        minion.setCurrentHpMp(minion.getMaxHp(), minion.getMaxMp());
        minion.setHeading(master.getHeading());

        minion.setLeader(master);
        minion.setInstance(master.getInstanceWorld());

        var location = calculateLocationRelativeToMaster(master);
        minion.spawnMe(location.getX(), location.getY(), location.getZ());
        return minion;
    }

    /**
     * @return list of the spawned (alive) minions.
     */
    public Collection<Monster> getSpawnedMinions() {
        return spawnedMinions;
    }

    /**
     * Manage the spawn of Minions.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Get the Minion data of all Minions that must be spawn</li>
     * <li>For each Minion type, spawn the amount of Minion needed</li><BR>
     * <BR>
     */
    public final void spawnMinions(List<MinionHolder> minions) {
        if (master.isAlikeDead() || (minions == null)) {
            return;
        }

        int minionCount;
        int minionId;
        int minionsToSpawn;
        for (MinionHolder minion : minions) {
            minionCount = minion.getCount();
            minionId = minion.getId();

            minionsToSpawn = minionCount - countSpawnedMinionsById(minionId);
            if (minionsToSpawn > 0) {
                for (int i = 0; i < minionsToSpawn; i++) {
                    spawnMinion(minionId);
                }
            }
        }
    }

    /**
     * Called on the minion spawn and added them in the list of the spawned minions.
     */
    public void onMinionSpawn(Monster minion) {
        spawnedMinions.add(minion);
    }

    /**
     * Called on the master death/delete.
     *
     * @param force - When true, force delete of the spawned minions. By default minions are deleted only for raidbosses.
     */
    public void onMasterDie(boolean force) {
        for (var minion : spawnedMinions) {
            if(nonNull(minion)) {
                minion.setLeader(null);
                if(master.isRaid() || force) {
                    minion.deleteMe();
                } else {
                    minion.scheduleDespawn(NpcSettings.minionDespawnDelay());
                }
            }
        }

        spawnedMinions.clear();

        _respawnTasks.forEach(task -> {
            if(nonNull(task) && !task.isCancelled() && !task.isDone()) {
                task.cancel(true);
            }
        });
        _respawnTasks.clear();

        master.setMinionList(null);
    }

    /**
     * Called on the minion death/delete. Removed minion from the list of the spawned minions and reuse if possible.
     *
     * @param respawnTime (ms) enable respawning of this minion while master is alive. -1 - use default value: 0 (disable) for mobs and config value for raids.
     */
    public void onMinionDie(Monster minion, int respawnTime) {
        minion.setLeader(null); // prevent memory leaks
        spawnedMinions.remove(minion);

        if(respawnTime < 0 && master.isRaid()) {
            respawnTime = NpcSettings.minionRespawnTime();
        }

		if (respawnTime > 0 && !master.isAlikeDead()) {
			_respawnTasks.add(ThreadPool.schedule(new MinionRespawnTask(minion), respawnTime));
        }
    }

    /**
     * Called if master/minion was attacked. Master and all free minions receive aggro against attacker.
     */
    public void onAssist(Creature caller, Creature attacker) {
        if (attacker == null) {
            return;
        }

        if (!master.isAlikeDead() && !master.isInCombat()) {
            master.addDamageHate(attacker, 0, 1);
        }

        final boolean callerIsMaster = caller == master;
        int aggro = callerIsMaster ? 10 : 1;
        if (master.isRaid()) {
            aggro *= 10;
        }

        for (Monster minion : spawnedMinions) {
            if ((minion != null) && !minion.isDead() && (callerIsMaster || !minion.isInCombat())) {
                minion.addDamageHate(attacker, 0, aggro);
            }
        }
    }

    /**
     * Called from onTeleported() of the master Alive and able to move minions teleported to master.
     */
    public void onMasterTeleported() {
        for (Monster minion : spawnedMinions) {
            if ((minion != null) && !minion.isDead() && !minion.isMovementDisabled()) {
                var location = calculateLocationRelativeToMaster(master);
                minion.teleToLocation(location);
            }
        }
    }

    private static ILocational calculateLocationRelativeToMaster(Monster master) {
        return calculateOffsetLocation(master, 200, (int) (master.getCollisionRadius() + 30));
    }

    private void spawnMinion(int minionId) {
        if (minionId == 0) {
            return;
        }
        spawnMinion(master, minionId);
    }

    private int countSpawnedMinionsById(int minionId) {
        int count = 0;
        for (Monster minion : spawnedMinions) {
            if ((minion != null) && (minion.getId() == minionId)) {
                count++;
            }
        }
        return count;
    }

    // Statistics part

    private final class MinionRespawnTask implements Runnable {
        private final Monster _minion;

        public MinionRespawnTask(Monster minion) {
            _minion = minion;
        }

        @Override
        public void run() {
            if (!master.isAlikeDead() && master.isSpawned()) {
                // minion can be already spawned or deleted
                if (!_minion.isSpawned()) {
                    _minion.refreshID();
                    initializeNpcInstance(master, _minion);
                }

                if (!master.getAggroList().isEmpty()) {
                    _minion.getAggroList().putAll(master.getAggroList());
                    var it = master.getAggroList().keySet().iterator();
                    if(it.hasNext()) {
                        _minion.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, it.next());
                    }
                }
            }
        }
    }
}

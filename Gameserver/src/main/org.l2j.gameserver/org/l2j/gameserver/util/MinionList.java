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
package org.l2j.gameserver.util;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.holders.MinionHolder;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.nonNull;

/**
 * @author luisantonioa, DS, Mobius
 */
public class MinionList {
    protected final Monster _master;
    private final Collection<Monster> _spawnedMinions = ConcurrentHashMap.newKeySet();
    private final Collection<ScheduledFuture<?>> _respawnTasks = ConcurrentHashMap.newKeySet();

    public MinionList(Monster pMaster) {
        if (pMaster == null) {
            throw new NullPointerException("MinionList: master is null");
        }
        _master = pMaster;
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
     * @return
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

        // Set the Minion HP, MP and Heading
        minion.setCurrentHpMp(minion.getMaxHp(), minion.getMaxMp());
        minion.setHeading(master.getHeading());

        // Set the Minion leader to this RaidBoss
        minion.setLeader(master);

        // move monster to masters instance
        minion.setInstance(master.getInstanceWorld());

        // Init the position of the Minion and add it in the world as a visible object
        final int offset = 200;
        final int minRadius = (int) master.getCollisionRadius() + 30;

        int newX = Rnd.get(minRadius * 2, offset * 2); // x
        int newY = Rnd.get(newX, offset * 2); // distance
        newY = (int) Math.sqrt((newY * newY) - (newX * newX)); // y
        if (newX > (offset + minRadius)) {
            newX = (master.getX() + newX) - offset;
        } else {
            newX = (master.getX() - newX) + minRadius;
        }
        if (newY > (offset + minRadius)) {
            newY = (master.getY() + newY) - offset;
        } else {
            newY = (master.getY() - newY) + minRadius;
        }

        minion.spawnMe(newX, newY, master.getZ());

        return minion;
    }

    /**
     * @return list of the spawned (alive) minions.
     */
    public Collection<Monster> getSpawnedMinions() {
        return _spawnedMinions;
    }

    /**
     * Manage the spawn of Minions.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Get the Minion data of all Minions that must be spawn</li>
     * <li>For each Minion type, spawn the amount of Minion needed</li><BR>
     * <BR>
     *
     * @param minions
     */
    public final void spawnMinions(List<MinionHolder> minions) {
        if (_master.isAlikeDead() || (minions == null)) {
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
     *
     * @param minion
     */
    public void onMinionSpawn(Monster minion) {
        _spawnedMinions.add(minion);
    }

    /**
     * Called on the master death/delete.
     *
     * @param force - When true, force delete of the spawned minions. By default minions are deleted only for raidbosses.
     */
    public void onMasterDie(boolean force) {
        _spawnedMinions.forEach(minion -> {
            if(nonNull(minion)) {
                minion.setLeader(null);
                if(_master.isRaid() || force || Config.FORCE_DELETE_MINIONS) {
                    minion.deleteMe();
                } else {
                    minion.scheduleDespawn(Config.DESPAWN_MINION_DELAY);
                }
            }
        });
        _spawnedMinions.clear();

        _respawnTasks.forEach(task -> {
            if(nonNull(task) && !task.isCancelled() && !task.isDone()) {
                task.cancel(true);
            }
        });
        _respawnTasks.clear();

        _master.setMinionList(null);
    }

    /**
     * Called on the minion death/delete. Removed minion from the list of the spawned minions and reuse if possible.
     *
     * @param minion
     * @param respawnTime (ms) enable respawning of this minion while master is alive. -1 - use default value: 0 (disable) for mobs and config value for raids.
     */
    public void onMinionDie(Monster minion, int respawnTime) {
        minion.setLeader(null); // prevent memory leaks
        _spawnedMinions.remove(minion);

        final int time = respawnTime < 0 ? _master.isRaid() ? (int) Config.RAID_MINION_RESPAWN_TIMER : 0 : respawnTime;
		if ((time > 0) && !_master.isAlikeDead())
		{
			_respawnTasks.add(ThreadPool.schedule(new MinionRespawnTask(minion), time));
        }
    }

    /**
     * Called if master/minion was attacked. Master and all free minions receive aggro against attacker.
     *
     * @param caller
     * @param attacker
     */
    public void onAssist(Creature caller, Creature attacker) {
        if (attacker == null) {
            return;
        }

        if (!_master.isAlikeDead() && !_master.isInCombat()) {
            _master.addDamageHate(attacker, 0, 1);
        }

        final boolean callerIsMaster = caller == _master;
        int aggro = callerIsMaster ? 10 : 1;
        if (_master.isRaid()) {
            aggro *= 10;
        }

        for (Monster minion : _spawnedMinions) {
            if ((minion != null) && !minion.isDead() && (callerIsMaster || !minion.isInCombat())) {
                minion.addDamageHate(attacker, 0, aggro);
            }
        }
    }

    /**
     * Called from onTeleported() of the master Alive and able to move minions teleported to master.
     */
    public void onMasterTeleported() {
        final int offset = 200;
        final int minRadius = (int) _master.getCollisionRadius() + 30;

        for (Monster minion : _spawnedMinions) {
            if ((minion != null) && !minion.isDead() && !minion.isMovementDisabled()) {
                int newX = Rnd.get(minRadius * 2, offset * 2); // x
                int newY = Rnd.get(newX, offset * 2); // distance
                newY = (int) Math.sqrt((newY * newY) - (newX * newX)); // y
                if (newX > (offset + minRadius)) {
                    newX = (_master.getX() + newX) - offset;
                } else {
                    newX = (_master.getX() - newX) + minRadius;
                }
                if (newY > (offset + minRadius)) {
                    newY = (_master.getY() + newY) - offset;
                } else {
                    newY = (_master.getY() - newY) + minRadius;
                }

                minion.teleToLocation(new Location(newX, newY, _master.getZ()));
            }
        }
    }

    private final void spawnMinion(int minionId) {
        if (minionId == 0) {
            return;
        }
        spawnMinion(_master, minionId);
    }

    private final int countSpawnedMinionsById(int minionId) {
        int count = 0;
        for (Monster minion : _spawnedMinions) {
            if ((minion != null) && (minion.getId() == minionId)) {
                count++;
            }
        }
        return count;
    }

    // Statistics part

    public final int countSpawnedMinions() {
        return _spawnedMinions.size();
    }

    public final long lazyCountSpawnedMinionsGroups() {
        return _spawnedMinions.stream().distinct().count();
    }

    private final class MinionRespawnTask implements Runnable {
        private final Monster _minion;

        public MinionRespawnTask(Monster minion) {
            _minion = minion;
        }

        @Override
        public void run() {
            if (!_master.isAlikeDead() && _master.isSpawned()) {
                // minion can be already spawned or deleted
                if (!_minion.isSpawned()) {
                    _minion.refreshID();
                    initializeNpcInstance(_master, _minion);
                }

                if (!_master.getAggroList().isEmpty()) {
                    _minion.getAggroList().putAll(_master.getAggroList());
                    _minion.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, _minion.getAggroList().keySet().stream().findFirst().get());
                }
            }
        }
    }
}

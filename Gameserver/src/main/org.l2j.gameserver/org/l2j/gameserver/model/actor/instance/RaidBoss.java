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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.util.MathUtil;

/**
 * This class manages all RaidBoss.<br>
 * In a group mob, there are one master called RaidBoss and several slaves called Minions.
 */
public class RaidBoss extends Monster {
    private static final int RAIDBOSS_MAINTENANCE_INTERVAL = 30000; // 30 sec

    private boolean _useRaidCurse = true;

    /**
     * Constructor of RaidBoss (use Creature and Folk constructor).<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Call the Creature constructor to set the _template of the RaidBoss (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li>
     * <li>Set the name of the RaidBoss</li>
     * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
     * </ul>
     *
     * @param template to apply to the NPC
     */
    public RaidBoss(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2RaidBossInstance);
        setIsRaid(true);
        setLethalable(false);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        setRandomWalking(false);
        broadcastPacket(new PlaySound(1, getParameters().getString("RaidSpawnMusic", "Rm01_A"), 0, 0, 0, 0, 0));
    }

    @Override
    protected int getMaintenanceInterval() {
        return RAIDBOSS_MAINTENANCE_INTERVAL;
    }

    /**
     * Spawn all minions at a regular interval Also if boss is too far from home location at the time of this check, teleport it home.
     */
    @Override
    protected void startMaintenanceTask() {
        _maintenanceTask = ThreadPool.scheduleAtFixedRate(() -> checkAndReturnToSpawn(), 60000, getMaintenanceInterval() + Rnd.get(5000));
    }

    protected void checkAndReturnToSpawn() {
        if (isDead() || isMovementDisabled() || !canReturnToSpawnPoint()) {
            return;
        }

        final Spawn spawn = getSpawn();
        if (spawn == null) {
            return;
        }

        final int spawnX = spawn.getX();
        final int spawnY = spawn.getY();
        final int spawnZ = spawn.getZ();

        if (!isInCombat() && !isMovementDisabled()) {
            if (!MathUtil.isInsideRadius3D(this, spawnX, spawnY, spawnZ, Math.max(Config.MAX_DRIFT_RANGE, 200))) {
                teleToLocation(spawnX, spawnY, spawnZ);
            }
        }
    }

    @Override
    public int getVitalityPoints(int level, double exp, boolean isBoss) {
        return -super.getVitalityPoints(level, exp, isBoss);
    }

    @Override
    public boolean useVitalityRate() {
        return Config.RAIDBOSS_USE_VITALITY;
    }

    public void setUseRaidCurse(boolean val) {
        _useRaidCurse = val;
    }

    @Override
    public boolean giveRaidCurse() {
        return _useRaidCurse;
    }
}

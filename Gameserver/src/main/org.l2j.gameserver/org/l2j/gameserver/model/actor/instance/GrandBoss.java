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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

/**
 * This class manages all Grand Bosses.
 *
 * @version $Revision: 1.0.0.0 $ $Date: 2006/06/16 $
 */
public final class GrandBoss extends Monster {
    private static final int BOSS_MAINTENANCE_INTERVAL = 10000;
    private boolean _useRaidCurse = true;

    /**
     * Constructor for GrandBoss. This represent all grandbosses.
     *
     * @param template NpcTemplate of the instance
     */
    public GrandBoss(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2GrandBossInstance);
        setIsRaid(true);
        setLethalable(false);
    }

    @Override
    protected int getMaintenanceInterval() {
        return BOSS_MAINTENANCE_INTERVAL;
    }

    @Override
    public void onSpawn() {
        setRandomWalking(false);
        super.onSpawn();
    }

    @Override
    public int getVitalityPoints(int level, double exp, boolean isBoss) {
        return -super.getVitalityPoints(level, exp, isBoss);
    }

    @Override
    public boolean useVitalityRate() {
        return false;
    }

    public void setUseRaidCurse(boolean val) {
        _useRaidCurse = val;
    }

    @Override
    public boolean giveRaidCurse() {
        return _useRaidCurse;
    }
}

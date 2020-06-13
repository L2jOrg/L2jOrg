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
package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Summon;

public class SummonStats extends PlayableStats {
    public SummonStats(Summon activeChar) {
        super(activeChar);
    }

    @Override
    public Summon getCreature() {
        return (Summon) super.getCreature();
    }

    @Override
    public double getRunSpeed() {
        // In retail maximum run speed is 350 for summons and 300 for players
        return Math.min(super.getRunSpeed(), Config.MAX_RUN_SPEED + 50);
    }

    @Override
    public double getWalkSpeed() {
        return Math.min(super.getWalkSpeed(), Config.MAX_RUN_SPEED + 50);
    }
}

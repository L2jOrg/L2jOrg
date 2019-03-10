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
package org.l2j.gameserver.mobius.gameserver.model.actor.instance;

import org.l2j.gameserver.mobius.gameserver.enums.InstanceType;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2NpcTemplate;

/**
 * This class represents Friendly Mobs lying over the world.<br>
 * These friendly mobs should only attack players with karma > 0 and it is always aggro, since it just attacks players with karma.
 */
public class L2FriendlyMobInstance extends L2Attackable {
    public L2FriendlyMobInstance(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2FriendlyMobInstance);
    }

    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        if (attacker.isPlayer()) {
            return ((L2PcInstance) attacker).getReputation() < 0;
        }

        return super.isAutoAttackable(attacker);
    }

    @Override
    public boolean isAggressive() {
        return true;
    }
}

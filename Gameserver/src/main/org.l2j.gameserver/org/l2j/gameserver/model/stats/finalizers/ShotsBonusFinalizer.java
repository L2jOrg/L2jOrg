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
package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stats;

import java.util.Optional;

/**
 * @author UnAfraid
 */
public class ShotsBonusFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stats stat) {
        throwIfPresent(base);

        double baseValue = 1;
        double rubyBonus = 0;
        final Player player = creature.getActingPlayer();
        if (player != null) {
            final L2ItemInstance weapon = player.getActiveWeaponInstance();
            if ((weapon != null) && weapon.isEnchanted()) {
                baseValue += (weapon.getEnchantLevel() * 0.7) / 100;
            }
            if (player.getActiveRubyJewel() != null) {
                rubyBonus = player.getActiveRubyJewel().getBonus();
            }
        }
        return Stats.defaultValue(creature, stat, CommonUtil.constrain(baseValue, 1.0, 1.21) + rubyBonus);
    }
}

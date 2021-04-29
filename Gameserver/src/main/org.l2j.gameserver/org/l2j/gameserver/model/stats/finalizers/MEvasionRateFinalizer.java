/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.settings.CharacterSettings;

import java.util.Optional;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class MEvasionRateFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);

        double baseValue = calcWeaponPlusBaseValue(creature, stat);

        final int level = creature.getLevel();
        if (isPlayer(creature)) {
            // [Square(WIT)] * 3 + lvl;
            baseValue += (Math.sqrt(creature.getWIT()) * 3) + (level * 2);

            // Enchanted helm bonus
            baseValue += calcEnchantBodyPart(creature, BodyPart.HEAD);
        } else {
            // [Square(DEX)] * 6 + lvl;
            baseValue += (Math.sqrt(creature.getWIT()) * 3) + (level * 2);
            if (level > 69) {
                baseValue += (level - 69) + 2;
            }
        }
        return validateValue(creature, Stat.defaultValue(creature, stat, baseValue), Double.NEGATIVE_INFINITY, CharacterSettings.maxEvasion());
    }

    @Override
    public double calcEnchantBodyPartBonus(int enchantLevel) {
        return (0.2 * Math.max(enchantLevel - 3, 0)) + (0.2 * Math.max(enchantLevel - 6, 0));
    }
}

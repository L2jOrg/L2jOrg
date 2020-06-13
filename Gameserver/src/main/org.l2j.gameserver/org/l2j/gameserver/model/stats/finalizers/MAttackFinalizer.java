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
package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Optional;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class MAttackFinalizer implements IStatsFunction {

    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);

        double baseValue = calcWeaponBaseValue(creature, stat);
        if (isPlayer(creature) && nonNull(creature.getActiveWeaponInstance())) {
            baseValue += calcEnchantMAtkBonus(creature.getActiveWeaponInstance());
        }

        if (Config.CHAMPION_ENABLE && creature.isChampion()) {
            baseValue *= Config.CHAMPION_ATK;
        }
        if (creature.isRaid()) {
            baseValue *= Config.RAID_MATTACK_MULTIPLIER;
        }

        // Calculate modifiers Magic Attack
        final double intBonus = BaseStats.INT.calcBonus(creature);
        baseValue *= Math.pow(intBonus, 2) * Math.pow(creature.getLevelMod(), 2);
        return Math.min(Stat.defaultValue(creature, stat, baseValue), Config.MAX_MATK);
    }

    private double calcEnchantMAtkBonus(Item item) {
        final var enchant = item.getEnchantLevel();
        return switch (item.getCrystalType()) {
            case S -> calcEnchantMAtkBonusCrystalS(enchant);
            case A, B, C, D -> calcEnchantMAtkBonusCrystalDefault(enchant);
            default -> 0;
        };
    }

    private int calcEnchantMAtkBonusCrystalDefault(int enchant) {
        final var bonus = 3;
        return (min(enchant, 3) * bonus ) + (max(0, enchant -3) * 2 * bonus);
    }

    private int calcEnchantMAtkBonusCrystalS(int enchant) {
        final var bonus = 4;
        final var secBonus = 43;
        return ( min(enchant, 3) * bonus) + ( min(max(0, enchant -3), 13) * 4 * bonus) + ( max(0, enchant -16) * secBonus );
    }
}

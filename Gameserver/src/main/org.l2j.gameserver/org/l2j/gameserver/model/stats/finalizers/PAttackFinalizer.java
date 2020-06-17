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
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.WeaponType;
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
public class PAttackFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);

        double baseValue = calcWeaponBaseValue(creature, stat);

        if (isPlayer(creature) && nonNull(creature.getActiveWeaponInstance())) {
            baseValue += calcEnchantPAtkBonus(creature.getActiveWeaponInstance());
        }

        if (Config.CHAMPION_ENABLE && creature.isChampion()) {
            baseValue *= Config.CHAMPION_ATK;
        }
        if (creature.isRaid()) {
            baseValue *= Config.RAID_PATTACK_MULTIPLIER;
        }
        final double strBonus = creature.getSTR() > 0 ? BaseStats.STR.calcBonus(creature) : 1.;
        baseValue *= strBonus * creature.getLevelMod();
        return Math.min(Stat.defaultValue(creature, stat, baseValue), Config.MAX_PATK);
    }

    private double calcEnchantPAtkBonus(Item item) {
        final var enchant = item.getEnchantLevel();
        final var hasTwoHandBonus = item.getBodyPart() == BodyPart.TWO_HAND && item.getItemType() != WeaponType.SPEAR;
        final var isRanged =  item.getItemType().isRanged();
        return switch (item.getCrystalType()) {
            case S -> calcEnchantPAtkCrystalS(enchant, hasTwoHandBonus, isRanged);
            case A, B, C, D -> calcEnchantPAtkCrystalDefault(enchant, hasTwoHandBonus, isRanged);
            default -> 0;
        };
    }

    private int calcEnchantPAtkCrystalDefault(int enchant, boolean hasTwoHandBonus, boolean isRanged) {
        final var bonus = isRanged ? 8 : hasTwoHandBonus ? 5 : 4;
        return min(enchant, 3) * bonus + ((max(0, enchant - 3) * bonus) << 1);
    }

    private int calcEnchantPAtkCrystalS(int enchant, boolean hasTwoHandBonus, boolean isRanged) {
        final var bonus = isRanged ? 10 : hasTwoHandBonus ? 6 : 5;
        final var secBonus = isRanged ? 126 : hasTwoHandBonus ? 77 : 43;
        return ( min(enchant, 3) * bonus ) + ((min(max(0, enchant - 3), 13) * bonus) << 2) + ( max(0, enchant -16) * secBonus );
    }
}

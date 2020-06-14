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
package org.l2j.gameserver.model.stats;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.transform.TransformType;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;

import java.util.Optional;

import static java.lang.Math.max;
import static org.l2j.gameserver.util.GameUtils.*;

/**
 * @author UnAfraid
 */
@FunctionalInterface
public interface IStatsFunction {

    static double calcEnchantDefBonus(Item item) {
        final var enchant = item.getEnchantLevel();
        return enchant + (3 * max(0, enchant - 3));
    }

    default void throwIfPresent(Optional<Double> base) {
        if (base.isPresent()) {
            throw new IllegalArgumentException("base should not be set for " + getClass().getSimpleName());
        }
    }

    default double calcEnchantBodyPart(Creature creature, BodyPart... parts) {
        double value = 0;
        for (var part : parts) {
            final Item item = creature.getInventory().getItemByBodyPart(part);
            // TODO Confirm if the bonus is applied for any Grade
            if ((item != null) && (item.getEnchantLevel() >= 4)) {
                value += calcEnchantBodyPartBonus(item.getEnchantLevel());
            }
        }
        return value;
    }

    default double calcEnchantBodyPartBonus(int enchantLevel) {
        return 0;
    }

    default double calcWeaponBaseValue(Creature creature, Stat stat) {
        final double baseTemplateValue = creature.getTemplate().getBaseValue(stat, 0);
        double baseValue = creature.getTransformation().map(transform -> transform.getStats(creature, stat, baseTemplateValue)).orElse(baseTemplateValue);
        if (isPet(creature)) {
            final Pet pet = (Pet) creature;
            final Item weapon = pet.getActiveWeaponInstance();
            final double baseVal = stat == Stat.PHYSICAL_ATTACK ? pet.getPetLevelData().getPetPAtk() : stat == Stat.MAGIC_ATTACK ? pet.getPetLevelData().getPetMAtk() : baseTemplateValue;
            baseValue = baseVal + (weapon != null ? weapon.getTemplate().getStats(stat, baseVal) : 0);
        } else if (isPlayer(creature) && (!creature.isTransformed() || (creature.getTransformation().get().getType() == TransformType.COMBAT) || (creature.getTransformation().get().getType() == TransformType.MODE_CHANGE))) {
            final Item weapon = creature.getActiveWeaponInstance();
            baseValue = (weapon != null ? weapon.getTemplate().getStats(stat, baseTemplateValue) : baseTemplateValue);
        }

        return baseValue;
    }

    default double calcWeaponPlusBaseValue(Creature creature, Stat stat) {
        final double baseTemplateValue = creature.getTemplate().getBaseValue(stat, 0);
        double baseValue = creature.getTransformation().filter(transform -> !transform.isStance()).map(transform -> transform.getStats(creature, stat, baseTemplateValue)).orElse(baseTemplateValue);

        if (isPlayable(creature)) {
            final Inventory inv = creature.getInventory();
            if (inv != null) {
                baseValue = inv.calcForEachEquippedItem(item -> item.getStats(stat, 0), baseValue, Double::sum);
            }
        }

        return baseValue;
    }

    default double calcEnchantedItemBonus(Creature creature, Stat stat) {
        if (!isPlayer(creature)) {
            return 0;
        }

        return creature.getInventory().calcForEachEquippedItem(item -> calcEnchantStatBonus(creature, stat, item),0, Double::sum);
    }

    private double calcEnchantStatBonus(Creature creature, Stat stat, Item item) {
        if(!item.isEnchanted()) {
            return 0;
        }
        var bodyPart = item.getBodyPart();
        if(bodyPart.isAnyOf(BodyPart.HAIR, BodyPart.HAIR2, BodyPart.HAIR_ALL)) {
             if(stat != Stat.PHYSICAL_DEFENCE && stat != Stat.MAGICAL_DEFENCE) {
                 return 0;
             }
        } else if(item.getStats(stat, 0) <= 0) {
            return 0;
        }

        int enchant = item.getEnchantLevel();

        if (creature.getActingPlayer().isInOlympiadMode() && (Config.ALT_OLY_ENCHANT_LIMIT >= 0) && (enchant > Config.ALT_OLY_ENCHANT_LIMIT)) {
            enchant = Config.ALT_OLY_ENCHANT_LIMIT;
        }

        return switch (stat) {
            case MAGICAL_DEFENCE, PHYSICAL_DEFENCE -> calcEnchantDefBonus(item);
            default -> 0;
        };
    }

    default double validateValue(Creature creature, double value, double minValue, double maxValue) {
        if ((value > maxValue) && !creature.canOverrideCond(PcCondOverride.MAX_STATS_VALUE)) {
            return maxValue;
        }

        return max(minValue, value);
    }

    double calc(Creature creature, Optional<Double> base, Stat stat);
}

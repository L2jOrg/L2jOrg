package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stats;

import java.util.Optional;

/**
 * @author UnAfraid
 */
public class MCritRateFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stats stat) {
        throwIfPresent(base);

        double baseValue = calcWeaponPlusBaseValue(creature, stat);
        if (creature.isPlayer()) {
            // Enchanted legs bonus
            baseValue += calcEnchantBodyPart(creature, ItemTemplate.SLOT_LEGS);
        }

        final double witBonus = creature.getWIT() > 0 ? BaseStats.WIT.calcBonus(creature) : 1.;
        return validateValue(creature, Stats.defaultValue(creature, stat, baseValue * witBonus * 10), 0, Config.MAX_MCRIT_RATE);
    }

    @Override
    public double calcEnchantBodyPartBonus(int enchantLevel, boolean isBlessed) {
        if (isBlessed) {
            return (0.5 * Math.max(enchantLevel - 3, 0)) + (0.5 * Math.max(enchantLevel - 6, 0));
        }

        return (0.34 * Math.max(enchantLevel - 3, 0)) + (0.34 * Math.max(enchantLevel - 6, 0));
    }
}

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Optional;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class PCriticalRateFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);

        double baseValue = calcWeaponBaseValue(creature, stat);
        if (isPlayer(creature)) {
            // Enchanted legs bonus
            baseValue += calcEnchantBodyPart(creature, BodyPart.LEGS);
        }
        final double dexBonus = creature.getDEX() > 0 ? BaseStats.DEX.calcBonus(creature) : 1.;
        return validateValue(creature, Stat.defaultValue(creature, stat, baseValue * dexBonus * 10), 0, Config.MAX_PCRIT_RATE);
    }

    @Override
    public double calcEnchantBodyPartBonus(int enchantLevel, boolean isBlessed) {
        if (isBlessed) {
            return (0.5 * Math.max(enchantLevel - 3, 0)) + (0.5 * Math.max(enchantLevel - 6, 0));
        }

        return (0.34 * Math.max(enchantLevel - 3, 0)) + (0.34 * Math.max(enchantLevel - 6, 0));
    }
}

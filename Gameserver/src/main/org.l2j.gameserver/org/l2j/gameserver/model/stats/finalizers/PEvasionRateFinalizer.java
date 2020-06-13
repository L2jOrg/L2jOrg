package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Optional;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class PEvasionRateFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);

        double baseValue = calcWeaponPlusBaseValue(creature, stat);

        final int level = creature.getLevel();
        if (isPlayer(creature)) {
            // [Square(DEX)] * 5 + lvl;
            baseValue += (Math.sqrt(creature.getDEX()) * 5) + level;
            if (level > 69) {
                baseValue += level - 69;
            }
            if (level > 77) {
                baseValue += 1;
            }
            if (level > 80) {
                baseValue += 2;
            }
            if (level > 87) {
                baseValue += 2;
            }
            if (level > 92) {
                baseValue += 1;
            }
            if (level > 97) {
                baseValue += 1;
            }

            // Enchanted helm bonus
            baseValue += calcEnchantBodyPart(creature, BodyPart.HEAD);
        } else {
            // [Square(DEX)] * 5 + lvl;
            baseValue += (Math.sqrt(creature.getDEX()) * 5) + level;
            if (level > 69) {
                baseValue += (level - 69) + 2;
            }
        }

        return validateValue(creature, Stat.defaultValue(creature, stat, baseValue), Double.NEGATIVE_INFINITY, Config.MAX_EVASION);
    }

    @Override
    public double calcEnchantBodyPartBonus(int enchantLevel) {
        return (0.2 * Math.max(enchantLevel - 3, 0)) + (0.2 * Math.max(enchantLevel - 6, 0));
    }
}

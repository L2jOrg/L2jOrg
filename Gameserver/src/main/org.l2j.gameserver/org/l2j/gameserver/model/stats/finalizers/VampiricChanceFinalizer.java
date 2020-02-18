package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Optional;

/**
 * @author Sdw
 */
public class VampiricChanceFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);

        final double amount = creature.getStats().getValue(Stat.ABSORB_DAMAGE_PERCENT, 0) * 100;
        final double vampiricSum = creature.getStats().getVampiricSum();

        return amount > 0 ? Stat.defaultValue(creature, stat, Math.min(1.0, vampiricSum / amount / 100)) : 0;
    }
}

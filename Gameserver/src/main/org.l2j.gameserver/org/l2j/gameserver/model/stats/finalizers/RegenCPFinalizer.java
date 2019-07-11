package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stats;

import java.util.Optional;

/**
 * @author UnAfraid
 */
public class RegenCPFinalizer implements IStatsFunction {
    @Override
    public double calc(L2Character creature, Optional<Double> base, Stats stat) {
        throwIfPresent(base);
        if (!creature.isPlayer()) {
            return 0;
        }

        final Player player = creature.getActingPlayer();
        double baseValue = player.getTemplate().getBaseCpRegen(creature.getLevel()) * creature.getLevelMod() * BaseStats.CON.calcBonus(creature) * Config.CP_REGEN_MULTIPLIER;
        if (player.isSitting()) {
            baseValue *= 1.5; // Sitting
        } else if (!player.isMoving()) {
            baseValue *= 1.1; // Staying
        } else if (player.isRunning()) {
            baseValue *= 0.7; // Running
        }
        return Stats.defaultValue(player, stat, baseValue);
    }
}

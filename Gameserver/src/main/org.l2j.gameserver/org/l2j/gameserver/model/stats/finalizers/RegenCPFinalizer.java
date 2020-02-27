package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Optional;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class RegenCPFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);
        if (!isPlayer(creature)) {
            return 0;
        }

        final Player player = creature.getActingPlayer();
        double baseValue = player.getTemplate().getBaseCpRegen(creature.getLevel()) * creature.getLevelMod() * BaseStats.CON.calcBonus(creature);
        if (player.isSitting()) {
            baseValue *= 1.5; // Sitting
        } else if (!player.isMoving()) {
            baseValue *= 1.1; // Staying
        } else if (player.isRunning()) {
            baseValue *= 0.7; // Running
        }
        return Stat.defaultValue(player, stat, baseValue);
    }
}

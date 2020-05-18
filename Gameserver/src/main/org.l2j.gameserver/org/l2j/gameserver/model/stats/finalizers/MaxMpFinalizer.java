package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Optional;

import static org.l2j.gameserver.util.GameUtils.isPet;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class MaxMpFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);

        double baseValue = calcWeaponPlusBaseValue(creature, stat);
        if (isPet(creature)) {
            final Pet pet = (Pet) creature;
            baseValue += pet.getPetLevelData().getPetMaxMP();
        } else if (isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            if (player != null) {
                baseValue += player.getTemplate().getBaseMpMax(player.getLevel());
            }
        }
        final double menBonus = creature.getMEN() > 0 ? BaseStats.MEN.calcBonus(creature) : 1.;
        baseValue *= menBonus;
        return Stat.defaultValue(creature, stat, baseValue);
    }
}

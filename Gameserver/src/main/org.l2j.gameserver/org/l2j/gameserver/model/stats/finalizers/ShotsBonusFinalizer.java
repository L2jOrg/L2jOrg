package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Optional;

/**
 * @author UnAfraid
 */
public class ShotsBonusFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);

        double baseValue = 1;
        final Player player = creature.getActingPlayer();
        if (player != null) {
            final Item weapon = player.getActiveWeaponInstance();
            if ((weapon != null) && weapon.isEnchanted()) {
                baseValue += (weapon.getEnchantLevel() * 0.7) / 100;
            }
        }
        return Stat.defaultValue(creature, stat, CommonUtil.constrain(baseValue, 1.0, 1.21));
    }
}

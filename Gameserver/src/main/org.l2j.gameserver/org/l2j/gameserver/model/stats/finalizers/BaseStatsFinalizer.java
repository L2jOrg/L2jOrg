package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.data.xml.impl.ArmorSetsData;
import org.l2j.gameserver.model.ArmorSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class BaseStatsFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);

        // Apply template value
        double baseValue = creature.getTemplate().getBaseValue(stat, 0);

        // Should not apply armor set and henna bonus to summons.
        if (isPlayer(creature))
        {
            final Player player = creature.getActingPlayer();
            final Set<ArmorSet> appliedSets = new HashSet<>(2);

            var baseStat = BaseStats.valueOf(stat);

            // Armor sets calculation
            for (Item item : player.getInventory().getPaperdollItems()) {
                for (ArmorSet set : ArmorSetsData.getInstance().getSets(item.getId())) {
                    if ((set.getPiecesCount(player, Item::getId) >= set.getMinimumPieces()) && appliedSets.add(set)) {
                        baseValue += set.getStatsBonus(baseStat);
                    }
                }
            }

            // Henna calculation
            baseValue += player.getHennaValue(baseStat);
            baseValue += player.getStatsData().getValue(baseStat);
        }

        return validateValue(creature, Stat.defaultValue(creature, stat, baseValue), 1, BaseStats.MAX_STAT_VALUE - 1);
    }
}

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.data.xml.impl.EnchantItemHPBonusData;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stats;

import java.util.Optional;

import static org.l2j.gameserver.util.GameUtils.isPet;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class MaxHpFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stats stat) {
        throwIfPresent(base);

        double baseValue = creature.getTemplate().getBaseValue(stat, 0);
        if (isPet(creature)) {
            final Pet pet = (Pet) creature;
            baseValue = pet.getPetLevelData().getPetMaxHP();
        } else if (isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            if (player != null) {
                baseValue = player.getTemplate().getBaseHpMax(player.getLevel());

                // Apply enchanted item's bonus HP
                for (Item item : player.getInventory().getPaperdollItems(Item::isEnchanted)) {
                    if (item.isArmor())
                    {
                        final long bodyPart = item.getItem().getBodyPart();
                        if ((bodyPart != ItemTemplate.SLOT_NECK) && (bodyPart != ItemTemplate.SLOT_LR_EAR) && (bodyPart != ItemTemplate.SLOT_LR_FINGER))
                        {
                            baseValue += EnchantItemHPBonusData.getInstance().getHPBonus(item);
                        }
                    }
                }
            }
        }
        final double conBonus = creature.getCON() > 0 ? BaseStats.CON.calcBonus(creature) : 1.;
        baseValue *= conBonus;
        return Stats.defaultValue(creature, stat, baseValue);
    }
}

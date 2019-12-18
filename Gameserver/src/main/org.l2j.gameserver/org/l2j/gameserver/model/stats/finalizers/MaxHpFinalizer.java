package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.data.xml.impl.EnchantItemHPBonusData;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Optional;

import static org.l2j.gameserver.util.GameUtils.isPet;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class MaxHpFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
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
                        var bodyPart = item.getBodyPart();
                        if (!bodyPart.isAnyOf(BodyPart.NECK, BodyPart.EAR, BodyPart.FINGER))
                        {
                            baseValue += EnchantItemHPBonusData.getInstance().getHPBonus(item);
                        }
                    }
                }
            }
        }
        final double conBonus = creature.getCON() > 0 ? BaseStats.CON.calcBonus(creature) : 1.;
        baseValue *= conBonus;
        return Stat.defaultValue(creature, stat, baseValue);
    }
}

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stats;

import java.util.Optional;

/**
 * @author UnAfraid
 */
public class MDefenseFinalizer implements IStatsFunction {
    private static final int[] SLOTS =
            {
                    Inventory.PAPERDOLL_LFINGER,
                    Inventory.PAPERDOLL_RFINGER,
                    Inventory.PAPERDOLL_LEAR,
                    Inventory.PAPERDOLL_REAR,
                    Inventory.PAPERDOLL_NECK
            };

    @Override
    public double calc(Creature creature, Optional<Double> base, Stats stat) {
        throwIfPresent(base);
        double baseValue = creature.getTemplate().getBaseValue(stat, 0);
        if (creature.isPet()) {
            final Pet pet = (Pet) creature;
            baseValue = pet.getPetLevelData().getPetMDef();
        }
        baseValue += calcEnchantedItemBonus(creature, stat);

        final Inventory inv = creature.getInventory();
        if (inv != null) {
            for (L2ItemInstance item : inv.getPaperdollItems(L2ItemInstance::isEquipped)) {
                baseValue += item.getItem().getStats(stat, 0);
            }
        }

        if (creature.isPlayer()) {
            final Player player = creature.getActingPlayer();
            for (int slot : SLOTS) {
                if (!player.getInventory().isPaperdollSlotEmpty(slot)) {
                    final int defaultStatValue = player.getTemplate().getBaseDefBySlot(slot);
                    baseValue -= creature.getTransformation().map(transform -> transform.getBaseDefBySlot(player, slot)).orElse(defaultStatValue);
                }
            }
        } else if (creature.isPet() && (creature.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_NECK) != 0)) {
            baseValue -= 13;
        }
        if (creature.isRaid()) {
            baseValue *= Config.RAID_MDEFENCE_MULTIPLIER;
        }

        final double bonus = creature.getMEN() > 0 ? BaseStats.MEN.calcBonus(creature) : 1.;
        baseValue *= bonus * creature.getLevelMod();
        return defaultValue(creature, stat, baseValue);
    }

    private double defaultValue(Creature creature, Stats stat, double baseValue) {
        final double mul = Math.max(creature.getStat().getMul(stat), 0.5);
        final double add = creature.getStat().getAdd(stat);
        return (baseValue * mul) + add + creature.getStat().getMoveTypeValue(stat, creature.getMoveType());
    }
}

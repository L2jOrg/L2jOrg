package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Optional;

import static org.l2j.gameserver.util.GameUtils.isPet;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class MDefenseFinalizer implements IStatsFunction {

    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);
        double baseValue = creature.getTemplate().getBaseValue(stat, 0);
        if (isPet(creature)) {
            final Pet pet = (Pet) creature;
            baseValue = pet.getPetLevelData().getPetMDef();
        }
        baseValue += calcEnchantedItemBonus(creature, stat);

        final Inventory inv = creature.getInventory();
        if (inv != null) {
            for (Item item : inv.getPaperdollItems(Item::isEquipped)) {
                baseValue += item.getTemplate().getStats(stat, 0);
            }
        }

        if (isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            for (var slot : InventorySlot.accessories()) {
                if (!player.getInventory().isPaperdollSlotEmpty(slot)) {
                    final int defaultStatValue = player.getTemplate().getBaseDefBySlot(slot);
                    baseValue -= creature.getTransformation().map(transform -> transform.getBaseDefBySlot(player, slot)).orElse(defaultStatValue);
                }
            }
        } else if (isPet(creature) && (creature.getInventory().getPaperdollObjectId(InventorySlot.NECK) != 0)) {
            baseValue -= 13;
        }
        if (creature.isRaid()) {
            baseValue *= Config.RAID_MDEFENCE_MULTIPLIER;
        }

        final double bonus = creature.getMEN() > 0 ? BaseStats.MEN.calcBonus(creature) : 1.;
        baseValue *= bonus * creature.getLevelMod();
        return defaultValue(creature, stat, baseValue);
    }

    private double defaultValue(Creature creature, Stat stat, double baseValue) {
        final double mul = Math.max(creature.getStats().getMul(stat), 0.5);
        final double add = creature.getStats().getAdd(stat);
        return (baseValue * mul) + add + creature.getStats().getMoveTypeValue(stat, creature.getMoveType());
    }
}

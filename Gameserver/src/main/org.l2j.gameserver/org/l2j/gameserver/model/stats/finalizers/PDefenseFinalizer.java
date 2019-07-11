/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stats;

import java.util.Optional;

/**
 * @author UnAfraid
 */
public class PDefenseFinalizer implements IStatsFunction {
    private static final int[] SLOTS =
            {
                    Inventory.PAPERDOLL_CHEST,
                    Inventory.PAPERDOLL_LEGS,
                    Inventory.PAPERDOLL_HEAD,
                    Inventory.PAPERDOLL_FEET,
                    Inventory.PAPERDOLL_GLOVES,
                    Inventory.PAPERDOLL_UNDER,
                    Inventory.PAPERDOLL_CLOAK,
                    Inventory.PAPERDOLL_HAIR
            };

    @Override
    public double calc(Creature creature, Optional<Double> base, Stats stat) {
        throwIfPresent(base);
        double baseValue = creature.getTemplate().getBaseValue(stat, 0);
        if (creature.isPet()) {
            final Pet pet = (Pet) creature;
            baseValue = pet.getPetLevelData().getPetPDef();
        }
        baseValue += calcEnchantedItemBonus(creature, stat);

        final Inventory inv = creature.getInventory();
        if (inv != null) {
            for (L2ItemInstance item : inv.getPaperdollItems()) {
                baseValue += item.getItem().getStats(stat, 0);
            }

            if (creature.isPlayer()) {
                final Player player = creature.getActingPlayer();
                for (int slot : SLOTS) {
                    if (!inv.isPaperdollSlotEmpty(slot) || //
                            ((slot == Inventory.PAPERDOLL_LEGS) && !inv.isPaperdollSlotEmpty(Inventory.PAPERDOLL_CHEST) && (inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST).getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR))) {
                        final int defaultStatValue = player.getTemplate().getBaseDefBySlot(slot);
                        baseValue -= creature.getTransformation().map(transform -> transform.getBaseDefBySlot(player, slot)).orElse(defaultStatValue);
                    }
                }
            }
        }
        if (creature.isRaid()) {
            baseValue *= Config.RAID_PDEFENCE_MULTIPLIER;
        }
        if (creature.getLevel() > 0) {
            baseValue *= creature.getLevelMod();
        }

        return defaultValue(creature, stat, baseValue);
    }

    private double defaultValue(Creature creature, Stats stat, double baseValue) {
        final double mul = Math.max(creature.getStat().getMul(stat), 0.5);
        final double add = creature.getStat().getAdd(stat);
        return (baseValue * mul) + add + creature.getStat().getMoveTypeValue(stat, creature.getMoveType());
    }
}

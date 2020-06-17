/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;

import java.util.function.ToDoubleFunction;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author JoeAlisson
 */
public abstract class AbstractDefenseFinalizer implements IStatsFunction {


    protected double calcEnchantDefBonus(Item item) {
        return item.getCrystalType() == CrystalType.S ? calcEnchantPdefCrystalS(item.getEnchantLevel()) : calcEnchantPdefCrystalDefault(item.getEnchantLevel());
    }

    private double calcEnchantPdefCrystalDefault(int enchant) {
        return min(enchant, 3) + max(0, enchant -3) * 3;
    }

    private double calcEnchantPdefCrystalS(int enchant) {
        return (min(enchant, 3) << 1) + (min(max(0, enchant - 3), 10) << 3) + max(0, enchant -10) * 26;
    }

    protected ToDoubleFunction<Player> baseDefBySlot(InventorySlot slot) {
        return player -> player.getTransformation().map(t -> t.getBaseDefBySlot(player, slot)).orElseGet(() -> player.getTemplate().getBaseDefBySlot(slot));
    }

    protected double defaultValue(Creature creature, Stat stat, double baseValue) {
        final double mul = max(creature.getStats().getMul(stat), 0.5);
        final double add = creature.getStats().getAdd(stat);
        return (baseValue * mul) + add + creature.getStats().getMoveTypeValue(stat, creature.getMoveType());
    }
}

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

import org.l2j.gameserver.engine.item.EnchantItemEngine;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;
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
            if (nonNull(player)) {
                baseValue = player.getTemplate().getBaseHpMax(player.getLevel());
                var inventory = player.getInventory();

                baseValue += InventorySlot.armorset().stream()
                        .map(inventory::getPaperdollItem).filter(Objects::nonNull)
                        .mapToInt(EnchantItemEngine.getInstance()::getArmorHpBonus)
                        .reduce(0, Integer::sum);
            }
        }
        final double conBonus = creature.getCON() > 0 ? BaseStats.CON.calcBonus(creature) : 1.;
        baseValue *= conBonus;
        return Stat.defaultValue(creature, stat, baseValue);
    }
}

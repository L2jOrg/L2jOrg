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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.falseIfNullOrElse;
import static org.l2j.gameserver.enums.InventorySlot.CHEST;
import static org.l2j.gameserver.enums.InventorySlot.LEGS;
import static org.l2j.gameserver.util.GameUtils.calcIfIsPlayer;
import static org.l2j.gameserver.util.GameUtils.isPet;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class PDefenseFinalizer extends AbstractDefenseFinalizer {

    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);
        double baseValue = creature.getTemplate().getBaseValue(stat, 0);

        if (isPet(creature)) {
            final Pet pet = (Pet) creature;
            baseValue = pet.getPetLevelData().getPetPDef();
        }

        final Inventory inv = creature.getInventory();

        if (nonNull(inv)) {
            for (var slot : InventorySlot.armors()) {
                var item = inv.getPaperdollItem(slot);
                if(nonNull(item)) {
                    baseValue += item.getStats(stat, 0);
                    baseValue -= calcIfIsPlayer(creature, baseDefBySlot(slot));
                    baseValue += calcIfIsPlayer(creature, player -> calcEnchantDefBonus(item));
                } else if(slot == LEGS && falseIfNullOrElse(inv.getPaperdollItem(CHEST), chest -> chest.getBodyPart() == BodyPart.FULL_ARMOR)) {
                    baseValue -= calcIfIsPlayer(creature, baseDefBySlot(slot));
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
}

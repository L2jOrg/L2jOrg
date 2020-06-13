/*
 * Copyright © 2019 L2J Mobius
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

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Optional;

import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * @author UnAfraid
 */
public class AttributeFinalizer implements IStatsFunction {
    private final AttributeType _type;
    private final boolean _isWeapon;

    public AttributeFinalizer(AttributeType type, boolean isWeapon) {
        _type = type;
        _isWeapon = isWeapon;
    }

    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);

        double baseValue = creature.getTemplate().getBaseValue(stat, 0);
        if (isPlayable(creature)) {
            if (_isWeapon) {
                final Item weapon = creature.getActiveWeaponInstance();
                if (weapon != null) {
                    final AttributeHolder weaponInstanceHolder = weapon.getAttribute(_type);
                    if (weaponInstanceHolder != null) {
                        baseValue += weaponInstanceHolder.getValue();
                    }

                    final AttributeHolder weaponHolder = weapon.getTemplate().getAttribute(_type);
                    if (weaponHolder != null) {
                        baseValue += weaponHolder.getValue();
                    }
                }
            } else {
                final Inventory inventory = creature.getInventory();
                if (inventory != null) {
                    for (Item item : inventory.getPaperdollItems(Item::isArmor)) {
                        final AttributeHolder weaponInstanceHolder = item.getAttribute(_type);
                        if (weaponInstanceHolder != null) {
                            baseValue += weaponInstanceHolder.getValue();
                        }

                        final AttributeHolder weaponHolder = item.getTemplate().getAttribute(_type);
                        if (weaponHolder != null) {
                            baseValue += weaponHolder.getValue();
                        }
                    }
                }
            }
        }
        return Stat.defaultValue(creature, stat, baseValue);
    }
}

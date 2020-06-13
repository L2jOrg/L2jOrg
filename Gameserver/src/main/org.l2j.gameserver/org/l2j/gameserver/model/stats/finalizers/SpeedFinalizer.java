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
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.model.PetLevelData;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.world.zone.type.SwampZone;

import java.util.Optional;

import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class SpeedFinalizer implements IStatsFunction {
    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);

        double baseValue = getBaseSpeed(creature, stat);
        if (isPlayer(creature)) {
            // Enchanted feet bonus
            baseValue += calcEnchantBodyPart(creature, BodyPart.FEET);
        }

        final byte speedStat = (byte) creature.getStats().getAdd(Stat.STAT_BONUS_SPEED, -1);
        if ((speedStat >= 0) && (speedStat < BaseStats.values().length)) {
            final BaseStats baseStat = BaseStats.values()[speedStat];
            final double bonusDex = Math.max(0, baseStat.calcValue(creature) - 55);
            baseValue += bonusDex;
        }

        return validateValue(creature, Stat.defaultValue(creature, stat, baseValue), 1, Config.MAX_RUN_SPEED);
    }

    @Override
    public double calcEnchantBodyPartBonus(int enchantLevel) {
        return (0.6 * Math.max(enchantLevel - 3, 0)) + (0.6 * Math.max(enchantLevel - 6, 0));
    }

    private double getBaseSpeed(Creature creature, Stat stat) {
        double baseValue = calcWeaponPlusBaseValue(creature, stat);
        if (isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            if (player.isMounted()) {
                final PetLevelData data = PetDataTable.getInstance().getPetLevelData(player.getMountNpcId(), player.getMountLevel());
                if (data != null) {
                    baseValue = data.getSpeedOnRide(stat);
                    // if level diff with mount >= 10, it decreases move speed by 50%
                    if ((player.getMountLevel() - creature.getLevel()) >= 10) {
                        baseValue /= 2;
                    }

                    // if mount is hungry, it decreases move speed by 50%
                    if (player.isHungry()) {
                        baseValue /= 2;
                    }
                }
            }
        }
        if (isPlayable(creature) && creature.isInsideZone(ZoneType.SWAMP)) {
            final SwampZone zone = ZoneManager.getInstance().getZone(creature, SwampZone.class);
            if (zone != null) {
                baseValue *= zone.getMoveBonus();
            }
        }
        return baseValue;
    }
}

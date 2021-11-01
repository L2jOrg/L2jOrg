/*
 * Copyright © 2019-2021 L2JOrg
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
import org.l2j.gameserver.data.database.data.ResidenceFunctionData;
import org.l2j.gameserver.engine.clan.clanhall.ClanHallEngine;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.settings.ChampionSettings;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.world.zone.type.CastleZone;
import org.l2j.gameserver.world.zone.type.ClanHallZone;
import org.l2j.gameserver.world.zone.type.MotherTreeZone;

import java.util.Optional;

import static org.l2j.gameserver.util.GameUtils.isPet;
import static org.l2j.gameserver.util.GameUtils.isPlayer;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * @author UnAfraid
 */
public class RegenHPFinalizer implements IStatsFunction {

    private static double calcSiegeRegenModifier(Player player) {
        if (player == null || player.getClan() == null) {
            return 0;
        }

        final Siege siege = SiegeManager.getInstance().getSiege(player);
        if (siege == null || !siege.isInProgress()) {
            return 0;
        }

        final var siegeClan = siege.getAttackerClan(player.getClanId());
        if (siegeClan == null) {
            return 0;
        }
        var flagsIt = siegeClan.getFlags().iterator();
        if(!flagsIt.hasNext() || !isInsideRadius3D(player, flagsIt.next(), 200)) {
            return 0;
        }
        return 1.5; // If all is true, then modifier will be 50% more
    }

    @Override
    public double calc(Creature creature, Optional<Double> base, Stat stat) {
        throwIfPresent(base);

        double baseValue = isPlayer(creature) ? creature.getActingPlayer().getTemplate().getBaseHpRegen(creature.getLevel()) : creature.getTemplate().getBaseHpReg();
        if(creature.isRaid()) {
            baseValue *=  Config.RAID_HP_REGEN_MULTIPLIER;
        }

        if (creature.isChampion()) {
            baseValue *= ChampionSettings.hpRegenMultiplier();
        }

        if (isPlayer(creature)) {
            final Player player = creature.getActingPlayer();

            final double siegeModifier = calcSiegeRegenModifier(player);
            if (siegeModifier > 0) {
                baseValue *= siegeModifier;
            }

            if (player.isInsideZone(ZoneType.CLAN_HALL) && (player.getClan() != null) && (player.getClan().getHideoutId() > 0)) {
                final ClanHallZone zone = ZoneEngine.getInstance().findFirstZone(player, ClanHallZone.class);
                final int posChIndex = zone == null ? -1 : zone.getResidenceId();
                final int clanHallIndex = player.getClan().getHideoutId();
                if ((clanHallIndex > 0) && (clanHallIndex == posChIndex)) {
                    final AbstractResidence residense = ClanHallEngine.getInstance().getClanHallById(player.getClan().getHideoutId());
                    if (residense != null) {
                        final ResidenceFunctionData func = residense.getFunction(ResidenceFunctionType.HP_REGEN);
                        if (func != null) {
                            baseValue *= func.getValue();
                        }
                    }
                }
            }

            if (player.isInsideZone(ZoneType.CASTLE) && (player.getClan() != null) && (player.getClan().getCastleId() > 0)) {
                final CastleZone zone = ZoneEngine.getInstance().findFirstZone(player, CastleZone.class);
                final int posCastleIndex = zone == null ? -1 : zone.getResidenceId();
                final int castleIndex = player.getClan().getCastleId();
                if ((castleIndex > 0) && (castleIndex == posCastleIndex)) {
                    final Castle castle = CastleManager.getInstance().getCastleById(player.getClan().getCastleId());
                    if (castle != null) {
                        var func = castle.getCastleFunction(Castle.FUNC_RESTORE_HP);
                        if (func != null) {
                            baseValue *= (func.getLevel() / 100f);
                        }
                    }
                }
            }

            // Mother Tree effect is calculated at last
            if (player.isInsideZone(ZoneType.MOTHER_TREE)) {
                final MotherTreeZone zone = ZoneEngine.getInstance().findFirstZone(player, MotherTreeZone.class);
                final int hpBonus = zone == null ? 0 : zone.getHpRegenBonus();
                baseValue += hpBonus;
            }

            // Calculate Movement bonus
            if (player.isSitting()) {
                baseValue *= 1.5; // Sitting
            } else if (!player.isMoving()) {
                baseValue *= 1.1; // Staying
            } else if (player.isRunning()) {
                baseValue *= 0.7; // Running
            }

            // Add CON bonus
            baseValue *= creature.getLevelMod() * BaseStats.CON.calcBonus(creature);
        } else if (isPet(creature)) {
            baseValue = ((Pet) creature).getPetLevelData().getPetRegenHP() * Config.PET_HP_REGEN_MULTIPLIER;
        }

        return Stat.defaultValue(creature, stat, baseValue);
    }
}

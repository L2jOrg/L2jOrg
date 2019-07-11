package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.ClanHallData;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.FortManager;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.instancemanager.ZoneManager;
import org.l2j.gameserver.model.L2SiegeClan;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Castle.CastleFunction;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.model.entity.Fort.FortFunction;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.model.residences.ResidenceFunction;
import org.l2j.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.model.zone.type.L2CastleZone;
import org.l2j.gameserver.model.zone.type.L2ClanHallZone;
import org.l2j.gameserver.model.zone.type.L2FortZone;
import org.l2j.gameserver.model.zone.type.L2MotherTreeZone;
import org.l2j.gameserver.util.GameUtils;

import java.util.Optional;

/**
 * @author UnAfraid
 */
public class RegenHPFinalizer implements IStatsFunction {
    private static double calcSiegeRegenModifier(Player activeChar) {
        if ((activeChar == null) || (activeChar.getClan() == null)) {
            return 0;
        }

        final Siege siege = SiegeManager.getInstance().getSiege(activeChar.getX(), activeChar.getY(), activeChar.getZ());
        if ((siege == null) || !siege.isInProgress()) {
            return 0;
        }

        final L2SiegeClan siegeClan = siege.getAttackerClan(activeChar.getClan().getId());
        if ((siegeClan == null) || siegeClan.getFlag().isEmpty() || !GameUtils.checkIfInRange(200, activeChar, siegeClan.getFlag().stream().findAny().get(), true)) {
            return 0;
        }

        return 1.5; // If all is true, then modifier will be 50% more
    }

    @Override
    public double calc(L2Character creature, Optional<Double> base, Stats stat) {
        throwIfPresent(base);

        double baseValue = creature.isPlayer() ? creature.getActingPlayer().getTemplate().getBaseHpRegen(creature.getLevel()) : creature.getTemplate().getBaseHpReg();
        baseValue *= creature.isRaid() ? Config.RAID_HP_REGEN_MULTIPLIER : Config.HP_REGEN_MULTIPLIER;

        if (Config.CHAMPION_ENABLE && creature.isChampion()) {
            baseValue *= Config.CHAMPION_HP_REGEN;
        }

        if (creature.isPlayer()) {
            final Player player = creature.getActingPlayer();

            final double siegeModifier = calcSiegeRegenModifier(player);
            if (siegeModifier > 0) {
                baseValue *= siegeModifier;
            }

            if (player.isInsideZone(ZoneId.CLAN_HALL) && (player.getClan() != null) && (player.getClan().getHideoutId() > 0)) {
                final L2ClanHallZone zone = ZoneManager.getInstance().getZone(player, L2ClanHallZone.class);
                final int posChIndex = zone == null ? -1 : zone.getResidenceId();
                final int clanHallIndex = player.getClan().getHideoutId();
                if ((clanHallIndex > 0) && (clanHallIndex == posChIndex)) {
                    final AbstractResidence residense = ClanHallData.getInstance().getClanHallById(player.getClan().getHideoutId());
                    if (residense != null) {
                        final ResidenceFunction func = residense.getFunction(ResidenceFunctionType.HP_REGEN);
                        if (func != null) {
                            baseValue *= func.getValue();
                        }
                    }
                }
            }

            if (player.isInsideZone(ZoneId.CASTLE) && (player.getClan() != null) && (player.getClan().getCastleId() > 0)) {
                final L2CastleZone zone = ZoneManager.getInstance().getZone(player, L2CastleZone.class);
                final int posCastleIndex = zone == null ? -1 : zone.getResidenceId();
                final int castleIndex = player.getClan().getCastleId();
                if ((castleIndex > 0) && (castleIndex == posCastleIndex)) {
                    final Castle castle = CastleManager.getInstance().getCastleById(player.getClan().getCastleId());
                    if (castle != null) {
                        final CastleFunction func = castle.getCastleFunction(Castle.FUNC_RESTORE_HP);
                        if (func != null) {
                            baseValue *= (func.getLvl() / 100);
                        }
                    }
                }
            }

            if (player.isInsideZone(ZoneId.FORT) && (player.getClan() != null) && (player.getClan().getFortId() > 0)) {
                final L2FortZone zone = ZoneManager.getInstance().getZone(player, L2FortZone.class);
                final int posFortIndex = zone == null ? -1 : zone.getResidenceId();
                final int fortIndex = player.getClan().getFortId();
                if ((fortIndex > 0) && (fortIndex == posFortIndex)) {
                    final Fort fort = FortManager.getInstance().getFortById(player.getClan().getCastleId());
                    if (fort != null) {
                        final FortFunction func = fort.getFortFunction(Fort.FUNC_RESTORE_HP);
                        if (func != null) {
                            baseValue *= (func.getLvl() / 100);
                        }
                    }
                }
            }

            // Mother Tree effect is calculated at last
            if (player.isInsideZone(ZoneId.MOTHER_TREE)) {
                final L2MotherTreeZone zone = ZoneManager.getInstance().getZone(player, L2MotherTreeZone.class);
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
        } else if (creature.isPet()) {
            baseValue = ((Pet) creature).getPetLevelData().getPetRegenHP() * Config.PET_HP_REGEN_MULTIPLIER;
        }

        return Stats.defaultValue(creature, stat, baseValue);
    }
}

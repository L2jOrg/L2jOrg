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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.quest.Event;
import org.l2j.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.gameserver.world.MapRegionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequestRestartPoint extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestRestartPoint.class);
    protected int _requestedPointType;

    @Override
    public void readImpl() {
        _requestedPointType = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();

        if (player == null) {
            return;
        }

        if (!player.canRevive()) {
            return;
        }

        if (player.isFakeDeath()) {
            player.stopFakeDeath(true);
            return;
        } else if (!player.isDead()) {
            LOGGER.warn("Living player [" + player.getName() + "] called RestartPointPacket! Ban this player!");
            return;
        }

        // Custom event resurrection management.
        if (player.isOnCustomEvent()) {
            for (AbstractEventListener listener : player.getListeners(EventType.ON_CREATURE_DEATH)) {
                if (listener.getOwner() instanceof Event) {
                    ((Event) listener.getOwner()).notifyEvent("ResurrectPlayer", null, player);
                    return;
                }
            }
        }

        final Castle castle = CastleManager.getInstance().getCastle(player);
        if ((castle != null) && castle.getSiege().isInProgress()) {
            if ((player.getClan() != null) && castle.getSiege().checkIsAttacker(player.getClan())) {
                // Schedule respawn delay for attacker
                ThreadPool.schedule(new DeathTask(player), castle.getSiege().getAttackerRespawnDelay());
                if (castle.getSiege().getAttackerRespawnDelay() > 0) {
                    player.sendMessage("You will be re-spawned in " + (castle.getSiege().getAttackerRespawnDelay() / 1000) + " seconds");
                }
                return;
            }
        }

        portPlayer(player);
    }

    protected final void portPlayer(Player player) {
        Location loc = null;
        Instance instance = null;

        // force jail
        if (player.isJailed()) {
            _requestedPointType = 27;
        }

        switch (_requestedPointType) {
            case 1: // to clanhall
            {
                if ((player.getClan() == null) || (player.getClan().getHideoutId() == 0)) {
                    LOGGER.warn("Player [" + player.getName() + "] called RestartPointPacket - To Clanhall and he doesn't have Clanhall!");
                    return;
                }
                loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.CLANHALL);
                final ClanHall residense = ClanHallManager.getInstance().getClanHallByClan(player.getClan());

                if ((residense != null) && (residense.hasFunction(ResidenceFunctionType.EXP_RESTORE))) {
                    player.restoreExp(residense.getFunction(ResidenceFunctionType.EXP_RESTORE).getValue());
                }
                break;
            }
            case 2: // to castle
            {
                final Clan clan = player.getClan();
                Castle castle = CastleManager.getInstance().getCastle(player);
                if ((castle != null) && castle.getSiege().isInProgress()) {
                    // Siege in progress
                    if (castle.getSiege().checkIsDefender(clan)) {
                        loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.CASTLE);
                    } else if (castle.getSiege().checkIsAttacker(clan)) {
                        loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN);
                    } else {
                        LOGGER.warn("Player [" + player.getName() + "] called RestartPointPacket - To Castle and he doesn't have Castle!");
                        return;
                    }
                } else {
                    if ((clan == null) || (clan.getCastleId() == 0)) {
                        return;
                    }
                    loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.CASTLE);
                }

                if (clan != null) {
                    castle = CastleManager.getInstance().getCastleByOwner(clan);
                    if (castle != null) {
                        var castleFunction = castle.getCastleFunction(Castle.FUNC_RESTORE_EXP);
                        if (castleFunction != null) {
                            player.restoreExp(castleFunction.getLevel());
                        }
                    }
                }
                break;
            }
            case 3: // to fortress
            {
                break;
            }
            case 4: // to siege HQ
            {
                SiegeClanData siegeClan = null;
                final Castle castle = CastleManager.getInstance().getCastle(player);

                if ((castle != null) && castle.getSiege().isInProgress()) {
                    siegeClan = castle.getSiege().getAttackerClan(player.getClan());
                }

                if (((siegeClan == null) || siegeClan.getFlags().isEmpty())) {
                    LOGGER.warn("Player [" + player.getName() + "] called RestartPointPacket - To Siege HQ and he doesn't have Siege HQ!");
                    return;
                }
                loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.SIEGEFLAG);
                break;
            }
            case 5: // Fixed or Player is a festival participant
            {
                if (!player.isGM() && !player.getInventory().haveItemForSelfResurrection()) {
                    LOGGER.warn("Player [" + player.getName() + "] called RestartPointPacket - Fixed and he isn't festival participant!");
                    return;
                }
                if (player.isGM() || player.destroyItemByItemId("Feather", 10649, 1, player, false) || player.destroyItemByItemId("Feather", 13300, 1, player, false) || player.destroyItemByItemId("Feather", 13128, 1, player, false)) {
                    player.doRevive(100.00);
                } else {
                    instance = player.getInstanceWorld();
                    loc = new Location(player);
                }
                break;
            }
            case 6: // TODO: Agathion resurrection
            case 7: // TODO: Adventurer's Song
            {
                break;
            }
            case 27: // to jail
            {
                if (!player.isJailed()) {
                    return;
                }
                loc = new Location(-114356, -249645, -2984);
                break;
            }
            default: {
                loc = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN);
                break;
            }
        }

        // Teleport and revive
        if (loc != null) {
            player.setIsPendingRevive(true);
            player.teleToLocation(loc, true, instance);
        }
    }

    class DeathTask implements Runnable {
        final Player activeChar;

        DeathTask(Player _activeChar) {
            activeChar = _activeChar;
        }

        @Override
        public void run() {
            portPlayer(activeChar);
        }
    }

}

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
package org.l2j.gameserver.util;

import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.world.WorldTimeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Flood protector implementation.
 *
 * @author fordfrog
 */
public final class FloodProtectorAction {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FloodProtectorAction.class);
    /**
     * Client for this instance of flood protector.
     */
    private final GameClient _client;
    /**
     * Configuration of this instance of flood protector.
     */
    private final FloodProtectorConfig _config;
    /**
     * Request counter.
     */
    private final AtomicInteger _count = new AtomicInteger(0);
    /**
     * Next game tick when new request is allowed.
     */
    private volatile int _nextGameTick = WorldTimeController.getInstance().getGameTicks();
    /**
     * Flag determining whether exceeding request has been logged.
     */
    private boolean _logged;
    /**
     * Flag determining whether punishment application is in progress so that we do not apply punisment multiple times (flooding).
     */
    private volatile boolean _punishmentInProgress;

    /**
     * Creates new instance of FloodProtectorAction.
     *
     * @param client the game client for which flood protection is being created
     * @param config flood protector configuration
     */
    public FloodProtectorAction(GameClient client, FloodProtectorConfig config) {
        super();
        _client = client;
        _config = config;
    }

    /**
     * Checks whether the request is flood protected or not.
     *
     * @param command command issued or short command description
     * @return true if action is allowed, otherwise false
     */
    public boolean tryPerformAction(String command) {
        final int curTick = WorldTimeController.getInstance().getGameTicks();

        if ((_client.getPlayer() != null) && _client.getPlayer().canOverrideCond(PcCondOverride.FLOOD_CONDITIONS)) {
            return true;
        }

        if ((curTick < _nextGameTick) || _punishmentInProgress) {
            if (_config.LOG_FLOODING && !_logged) {
                log(" called command ", command, " ~", String.valueOf((_config.FLOOD_PROTECTION_INTERVAL - (_nextGameTick - curTick)) * WorldTimeController.MILLIS_IN_TICK), " ms after previous command");
                _logged = true;
            }

            _count.incrementAndGet();

            if (!_punishmentInProgress && (_config.PUNISHMENT_LIMIT > 0) && (_count.get() >= _config.PUNISHMENT_LIMIT) && (_config.PUNISHMENT_TYPE != null)) {
                _punishmentInProgress = true;

                if ("kick".equals(_config.PUNISHMENT_TYPE)) {
                    kickPlayer();
                } else if ("ban".equals(_config.PUNISHMENT_TYPE)) {
                    banAccount();
                } else if ("jail".equals(_config.PUNISHMENT_TYPE)) {
                    jailChar();
                }

                _punishmentInProgress = false;
            }
            return false;
        }

        if ((_count.get() > 0) && _config.LOG_FLOODING) {
            log(" issued ", String.valueOf(_count), " extra requests within ~", String.valueOf(_config.FLOOD_PROTECTION_INTERVAL * WorldTimeController.MILLIS_IN_TICK), " ms");
        }

        _nextGameTick = curTick + _config.FLOOD_PROTECTION_INTERVAL;
        _logged = false;
        _count.set(0);
        return true;
    }

    /**
     * Kick player from game (close network connection).
     */
    private void kickPlayer() {
        Disconnection.of(_client).defaultSequence(false);
        log("kicked for flooding");
    }

    /**
     * Bans char account and logs out the char.
     */
    private void banAccount() {
        PunishmentManager.getInstance().startPunishment(new PunishmentTask(_client.getAccountName(), PunishmentAffect.ACCOUNT, PunishmentType.BAN, System.currentTimeMillis() + _config.PUNISHMENT_TIME, "", getClass().getSimpleName()));
        log(" banned for flooding ", _config.PUNISHMENT_TIME <= 0 ? "forever" : "for " + (_config.PUNISHMENT_TIME / 60000) + " mins");
    }

    /**
     * Jails char.
     */
    private void jailChar() {
        if (_client.getPlayer() == null) {
            return;
        }

        final int charId = _client.getPlayer().getObjectId();
        if (charId > 0) {
            PunishmentManager.getInstance().startPunishment(new PunishmentTask(charId, PunishmentAffect.CHARACTER, PunishmentType.JAIL, System.currentTimeMillis() + _config.PUNISHMENT_TIME, "", getClass().getSimpleName()));
        }

        log(" jailed for flooding ", _config.PUNISHMENT_TIME <= 0 ? "forever" : "for " + (_config.PUNISHMENT_TIME / 60000) + " mins");
    }

    private void log(String... lines) {
        final StringBuilder output = new StringBuilder(100);
        output.append(_config.FLOOD_PROTECTOR_TYPE);
        output.append(": ");
        String address = null;
        try {
            if (!_client.isDetached()) {
                address = _client.getHostAddress();
            }
        } catch (Exception e) {
        }

        final ConnectionState state = _client.getConnectionState();
        switch (state) {
            case IN_GAME: {
                if (_client.getPlayer() != null) {
                    output.append(_client.getPlayer().getName());
                    output.append("(");
                    output.append(_client.getPlayer().getObjectId());
                    output.append(") ");
                }
                break;
            }
            case AUTHENTICATED: {
                if (_client.getAccountName() != null) {
                    output.append(_client.getAccountName());
                    output.append(" ");
                }
                break;
            }
            case CONNECTED: {
                if (address != null) {
                    output.append(address);
                }
                break;
            }
            default: {
                throw new IllegalStateException("Missing state on switch");
            }
        }

        Arrays.stream(lines).forEach(output::append);

        LOGGER.warn(output.toString());
    }
}
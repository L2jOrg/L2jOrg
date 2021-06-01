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
package org.l2j.gameserver.instancemanager;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.world.World;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;

public final class AntiFeedManager {
    public static final int GAME_ID = 0;
    public static final int OLYMPIAD_ID = 1;
    public static final int TVT_ID = 2;
    public static final int L2EVENT_ID = 3;

    private final IntMap<Long> _lastDeathTimes = new CHashIntMap<>();
    private final IntMap<IntMap<AtomicInteger>> _eventIPs = new CHashIntMap<>();

    private AntiFeedManager() {
    }

    /**
     * Set time of the last player's death to current
     *
     * @param objectId Player's objectId
     */
    public void setLastDeathTime(int objectId) {
        _lastDeathTimes.put(objectId, System.currentTimeMillis());
    }

    /**
     * Check if current kill should be counted as non-feeded.
     *
     * @param attacker Attacker character
     * @param target   Target character
     * @return True if kill is non-feeded.
     */
    public boolean check(Creature attacker, Creature target) {
        if (!Config.ANTIFEED_ENABLE) {
            return true;
        }

        if (target == null) {
            return false;
        }

        final Player targetPlayer = target.getActingPlayer();
        if (targetPlayer == null) {
            return false;
        }

        if ((Config.ANTIFEED_INTERVAL > 0) && _lastDeathTimes.containsKey(targetPlayer.getObjectId())) {
            if ((System.currentTimeMillis() - _lastDeathTimes.get(targetPlayer.getObjectId())) < Config.ANTIFEED_INTERVAL) {
                return false;
            }
        }

        if (Config.ANTIFEED_DUALBOX && (attacker != null)) {
            final Player attackerPlayer = attacker.getActingPlayer();
            if (attackerPlayer == null) {
                return false;
            }

            final GameClient targetClient = targetPlayer.getClient();
            final GameClient attackerClient = attackerPlayer.getClient();
            if ((targetClient == null) || (attackerClient == null)) {
                // unable to check ip address
                return !Config.ANTIFEED_DISCONNECTED_AS_DUALBOX;
            }

            return !targetClient.getHostAddress().equals(attackerClient.getHostAddress());
        }

        return true;
    }

    /**
     * Register new event for dualbox check. Should be called only once.
     */
    public void registerEvent(int eventId) {
        _eventIPs.putIfAbsent(eventId, new CHashIntMap<>());
    }

    /**
     * @param eventId
     * @param player
     * @param max
     * @return If number of all simultaneous connections from player's IP address lower than max then increment connection count and return true.<br>
     * False if number of all simultaneous connections from player's IP address higher than max.
     */
    public boolean tryAddPlayer(int eventId, Player player, int max) {
        return tryAddClient(eventId, player.getClient(), max);
    }

    /**
     * @param eventId
     * @param client
     * @param max
     * @return If number of all simultaneous connections from player's IP address lower than max then increment connection count and return true.<br>
     * False if number of all simultaneous connections from player's IP address higher than max.
     */
    public boolean tryAddClient(int eventId, GameClient client, int max) {
        if (client == null) {
            return false; // unable to determine IP address
        }

        final var event = _eventIPs.get(eventId);
        if (event == null) {
            return false; // no such event registered
        }

        final int addrHash = client.getHostAddress().hashCode();
        final AtomicInteger connectionCount = event.computeIfAbsent(addrHash, k -> new AtomicInteger());

        if (!Config.DUALBOX_COUNT_OFFLINE_TRADERS) {
            final String address = client.getHostAddress();
            for (Player player : World.getInstance().getPlayers()) {
                if (player.getClient() == null && player.getIPAddress().equals(address)) {
                    connectionCount.decrementAndGet();
                }
            }
        }

        if ((connectionCount.get() + 1) <= (max + Config.DUALBOX_CHECK_WHITELIST.getOrDefault(addrHash, 0))) {
            connectionCount.incrementAndGet();
            return true;
        }
        return false;
    }

    private boolean removeClient(int eventId, GameClient client) {
        if (isNull(client)) {
            return false;
        }

        final var event = _eventIPs.get(eventId);
        if (isNull(event)) {
            return false; // no such event registered
        }
        return event.computeIfPresent(client.getHostAddress().hashCode(), this::decrementIpEvents) != null;
    }

    private AtomicInteger decrementIpEvents(int key, AtomicInteger value) {
        if (isNull(value) || value.decrementAndGet() == 0) {
            return null;
        }
        return value;
    }

    public void onDisconnect(GameClient client) {
        if (isNull(client) || Util.isNullOrEmpty(client.getHostAddress())) {
            return;
        }
        
        _eventIPs.forEach((k, v) -> removeClient(k, client));
    }

    /**
     * Clear all entries for this eventId.
     *
     * @param eventId
     */
    public void clear(int eventId) {
        final var event = _eventIPs.get(eventId);
        if (event != null) {
            event.clear();
        }
    }

    /**
     * @return maximum number of allowed connections (whitelist + max)
     */
    public int getLimit(GameClient client, int max) {
        if (client == null) {
            return max;
        }

        final Integer addrHash = client.getHostAddress().hashCode();
        int limit = max;
        if (Config.DUALBOX_CHECK_WHITELIST.containsKey(addrHash)) {
            limit += Config.DUALBOX_CHECK_WHITELIST.get(addrHash);
        }
        return limit;
    }

    public static AntiFeedManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final AntiFeedManager INSTANCE = new AntiFeedManager();
    }
}
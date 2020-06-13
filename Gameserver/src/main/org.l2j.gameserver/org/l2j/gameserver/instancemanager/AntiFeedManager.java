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
package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class AntiFeedManager {
    public static final int GAME_ID = 0;
    public static final int OLYMPIAD_ID = 1;
    public static final int TVT_ID = 2;
    public static final int L2EVENT_ID = 3;

    private final Map<Integer, Long> _lastDeathTimes = new ConcurrentHashMap<>();
    private final Map<Integer, Map<Integer, AtomicInteger>> _eventIPs = new ConcurrentHashMap<>();

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

        // Players in offline mode should't be valid targets.
        if (targetPlayer.getClient().isDetached()) {
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
            if ((targetClient == null) || (attackerClient == null) || targetClient.isDetached() || attackerClient.isDetached()) {
                // unable to check ip address
                return !Config.ANTIFEED_DISCONNECTED_AS_DUALBOX;
            }

            return !targetClient.getHostAddress().equals(attackerClient.getHostAddress());
        }

        return true;
    }

    /**
     * Clears all timestamps
     */
    public void clear() {
        _lastDeathTimes.clear();
    }

    /**
     * Register new event for dualbox check. Should be called only once.
     *
     * @param eventId
     */
    public void registerEvent(int eventId) {
        _eventIPs.putIfAbsent(eventId, new ConcurrentHashMap<>());
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

        final Map<Integer, AtomicInteger> event = _eventIPs.get(eventId);
        if (event == null) {
            return false; // no such event registered
        }

        final Integer addrHash = client.getHostAddress().hashCode();
        final AtomicInteger connectionCount = event.computeIfAbsent(addrHash, k -> new AtomicInteger());

        if (!Config.DUALBOX_COUNT_OFFLINE_TRADERS) {
            final String address = client.getHostAddress();
            for (Player player : World.getInstance().getPlayers()) {
                if (((player.getClient() == null) || player.getClient().isDetached()) && player.getIPAddress().equals(address)) {
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

    /**
     * Decreasing number of active connection from player's IP address
     *
     * @param eventId
     * @param player
     * @return true if success and false if any problem detected.
     */
    public boolean removePlayer(int eventId, Player player) {
        return removeClient(eventId, player.getClient());
    }

    /**
     * Decreasing number of active connection from player's IP address
     *
     * @param eventId
     * @param client
     * @return true if success and false if any problem detected.
     */
    public boolean removeClient(int eventId, GameClient client) {
        if (client == null) {
            return false; // unable to determine IP address
        }

        final Map<Integer, AtomicInteger> event = _eventIPs.get(eventId);
        if (event == null) {
            return false; // no such event registered
        }

        final Integer addrHash = client.getHostAddress().hashCode();

        return event.computeIfPresent(addrHash, (k, v) ->
        {
            if ((v == null) || (v.decrementAndGet() == 0)) {
                return null;
            }
            return v;
        }) != null;
    }

    /**
     * Remove player connection IP address from all registered events lists.
     *
     * @param client
     */
    public void onDisconnect(GameClient client) {
        if ((client == null) || (client.getHostAddress() == null) || (client.getPlayer() == null)) {
            return;
        }

        _eventIPs.forEach((k, v) ->
        {
            removeClient(k, client);
        });
    }

    /**
     * Clear all entries for this eventId.
     *
     * @param eventId
     */
    public void clear(int eventId) {
        final Map<Integer, AtomicInteger> event = _eventIPs.get(eventId);
        if (event != null) {
            event.clear();
        }
    }

    /**
     * @param player
     * @param max
     * @return maximum number of allowed connections (whitelist + max)
     */
    public int getLimit(Player player, int max) {
        return getLimit(player.getClient(), max);
    }

    /**
     * @param client
     * @param max
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
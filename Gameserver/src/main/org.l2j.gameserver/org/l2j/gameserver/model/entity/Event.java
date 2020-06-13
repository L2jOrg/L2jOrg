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
package org.l2j.gameserver.model.entity;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.PlayerEventHolder;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Nik
 * @since 2011/05/17 21:51:39
 */
public class Event {
    public static final Map<Integer, String> _teamNames = new ConcurrentHashMap<>();
    public static final Set<Player> _registeredPlayers = ConcurrentHashMap.newKeySet();
    public static final Map<Integer, Set<Player>> _teams = new ConcurrentHashMap<>();
    protected static final Logger LOGGER = LoggerFactory.getLogger(Event.class);
    private static final Map<Player, PlayerEventHolder> _connectionLossData = new ConcurrentHashMap<>();
    public static EventState eventState = EventState.OFF;
    public static String _eventName = "";
    public static String _eventCreator = "";
    public static String _eventInfo = "";
    public static int _teamsNumber = 0;
    public static int _npcId = 0;

    /**
     * @param player
     * @return The team ID where the player is in, or -1 if player is null or team not found.
     */
    public static int getPlayerTeamId(Player player) {
        if (player == null) {
            return -1;
        }

        for (Entry<Integer, Set<Player>> team : _teams.entrySet()) {
            if (team.getValue().contains(player)) {
                return team.getKey();
            }
        }

        return -1;
    }

    public static List<Player> getTopNKillers(int n) {
        final Map<Player, Integer> tmp = new HashMap<>();
        for (Set<Player> teamList : _teams.values()) {
            for (Player player : teamList) {
                if (player.getEventStatus() == null) {
                    continue;
                }
                tmp.put(player, player.getEventStatus().getKills().size());
            }
        }

        sortByValue(tmp);

        // If the map size is less than "n", n will be as much as the map size
        if (tmp.size() <= n) {
            return new ArrayList<>(tmp.keySet());
        }

        final List<Player> toReturn = new ArrayList<>(tmp.keySet());
        return toReturn.subList(1, n);
    }

    public static void showEventHtml(Player player, String objectid) {
        // TODO: work on this
        if (eventState == EventState.STANDBY) {
            try {
                final String htmContent;
                final NpcHtmlMessage html = new NpcHtmlMessage(Integer.parseInt(objectid));

                if (_registeredPlayers.contains(player)) {
                    htmContent = HtmCache.getInstance().getHtm(player, "data/html/mods/EventEngine/Participating.htm");
                } else {
                    htmContent = HtmCache.getInstance().getHtm(player, "data/html/mods/EventEngine/Participation.htm");
                }

                if (htmContent != null) {
                    html.setHtml(htmContent);
                }

                html.replace("%objectId%", objectid); // Yeah, we need this.
                html.replace("%eventName%", _eventName);
                html.replace("%eventCreator%", _eventCreator);
                html.replace("%eventInfo%", _eventInfo);
                player.sendPacket(html);
            } catch (Exception e) {
                LOGGER.warn("Exception on showEventHtml(): " + e.getMessage(), e);
            }
        }
    }

    /**
     * Spawns an event participation NPC near the player. The npc id used to spawning is Event._npcId
     *
     * @param target
     */
    public static void spawnEventNpc(Player target) {
        try {
            final Spawn spawn = new Spawn(_npcId);
            spawn.setXYZ(target.getX() + 50, target.getY() + 50, target.getZ());
            spawn.setAmount(1);
            spawn.setHeading(target.getHeading());
            spawn.stopRespawn();
            SpawnTable.getInstance().addNewSpawn(spawn, false);

            spawn.init();
            spawn.getLastSpawn().setCurrentHp(999999999);
            spawn.getLastSpawn().setTitle(_eventName);
            spawn.getLastSpawn().getVariables().set("eventmob", true);
            spawn.getLastSpawn().setIsInvul(true);
            // spawn.getLastSpawn().decayMe();
            // spawn.getLastSpawn().spawnMe(spawn.getLastSpawn().getX(), spawn.getLastSpawn().getY(), spawn.getLastSpawn().getZ());

            spawn.getLastSpawn().broadcastPacket(new MagicSkillUse(spawn.getLastSpawn(), spawn.getLastSpawn(), 1034, 1, 1, 1));

            // _npcs.add(spawn.getLastSpawn());
        } catch (Exception e) {
            LOGGER.warn("Exception on spawn(): " + e.getMessage(), e);
        }
    }

    /**
     * Zoey76: TODO: Rewrite this in a way that doesn't iterate over all spawns.
     */
    public static void unspawnEventNpcs() {
        SpawnTable.getInstance().forEachSpawn(spawn ->
        {
            final Npc npc = spawn.getLastSpawn();
            if ((npc != null) && npc.getVariables().getBoolean("eventmob", false)) {
                npc.deleteMe();
                spawn.stopRespawn();
                SpawnTable.getInstance().deleteSpawn(spawn, false);
            }
            return true;
        });
    }

    /**
     * @param player
     * @return False: If player is null, his event status is null or the event state is off. True: if the player is inside the _registeredPlayers list while the event state is STANDBY. If the event state is ON, it will check if the player is inside in one of the teams.
     */
    public static boolean isParticipant(Player player) {
        if ((player == null) || (player.getEventStatus() == null)) {
            return false;
        }

        switch (eventState) {
            case OFF: {
                return false;
            }
            case STANDBY: {
                return _registeredPlayers.contains(player);
            }
            case ON: {
                for (Set<Player> teamList : _teams.values()) {
                    if (teamList.contains(player)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Adds the player to the list of participants. If the event state is NOT STANDBY, the player wont be registered.
     *
     * @param player
     */
    public static void registerPlayer(Player player) {
        if (eventState != EventState.STANDBY) {
            player.sendMessage("The registration period for this event is over.");
            return;
        }

        if ((Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP == 0) || AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.L2EVENT_ID, player, Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP)) {
            _registeredPlayers.add(player);
        } else {
            player.sendMessage("You have reached the maximum allowed participants per IP.");
        }
    }

    /**
     * Removes the player from the participating players and the teams and restores his init stats before he registered at the event (loc, pvp, pk, title etc)
     *
     * @param player
     */
    public static void removeAndResetPlayer(Player player) {
        try {
            if (isParticipant(player)) {
                if (player.isDead()) {
                    player.restoreExp(100.0);
                    player.doRevive();
                    player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
                    player.setCurrentCp(player.getMaxCp());
                }

                player.decayMe();
                player.spawnMe(player.getX(), player.getY(), player.getZ());
                player.broadcastUserInfo();

                player.stopTransformation(true);
            }

            if (player.getEventStatus() != null) {
                player.getEventStatus().restorePlayerStats();
            }

            player.setEventStatus(null);

            _registeredPlayers.remove(player);
            final int teamId = getPlayerTeamId(player);
            if (_teams.containsKey(teamId)) {
                _teams.get(teamId).remove(player);
            }
        } catch (Exception e) {
            LOGGER.warn("Error at unregisterAndResetPlayer in the event:" + e.getMessage(), e);
        }
    }

    /**
     * The player's event status will be saved at _connectionLossData
     *
     * @param player
     */
    public static void savePlayerEventStatus(Player player) {
        _connectionLossData.put(player, player.getEventStatus());
    }

    /**
     * If _connectionLossData contains the player, it will restore the player's event status. Also it will remove the player from the _connectionLossData.
     *
     * @param player
     */
    public static void restorePlayerEventStatus(Player player) {
        if (_connectionLossData.containsKey(player)) {
            player.setEventStatus(_connectionLossData.get(player));
            _connectionLossData.remove(player);
        }
    }

    /**
     * If the event is ON or STANDBY, it will not start. Sets the event state to STANDBY and spawns registration NPCs
     *
     * @return a string with information if the event participation has been successfully started or not.
     */
    public static String startEventParticipation() {
        try {
            switch (eventState) {
                case ON: {
                    return "Cannot start event, it is already on.";
                }
                case STANDBY: {
                    return "Cannot start event, it is on standby mode.";
                }
                case OFF: // Event is off, so no problem turning it on.
                {
                    eventState = EventState.STANDBY;
                    break;
                }
            }

            // Register the event at AntiFeedManager and clean it for just in case if the event is already registered.
            AntiFeedManager.getInstance().registerEvent(AntiFeedManager.L2EVENT_ID);
            AntiFeedManager.getInstance().clear(AntiFeedManager.L2EVENT_ID);

            // Just in case
            unspawnEventNpcs();
            _registeredPlayers.clear();
            // _npcs.clear();

            if (NpcData.getInstance().getTemplate(_npcId) == null) {
                return "Cannot start event, invalid npc id.";
            }

            try (FileReader fr = new FileReader(Config.DATAPACK_ROOT + "/data/events/" + _eventName);
                 BufferedReader br = new BufferedReader(fr)) {
                _eventCreator = br.readLine();
                _eventInfo = br.readLine();
            }

            final Set<Player> temp = new HashSet<>();
            for (Player player : World.getInstance().getPlayers()) {
                if (!player.isOnline()) {
                    continue;
                }

                if (!temp.contains(player)) {
                    spawnEventNpc(player);
                    temp.add(player);
                }

                World.getInstance().forEachVisibleObjectInRange(player, Player.class, 1000, temp::add);
            }
        } catch (Exception e) {
            LOGGER.warn("Event: " + e.getMessage());
            return "Cannot start event participation, an error has occured.";
        }

        return "The event participation has been successfully started.";
    }

    /**
     * If the event is ON or OFF, it will not start. Sets the event state to ON, creates the teams, adds the registered players ordered by level at the teams and adds a new event status to the players.
     *
     * @return a string with information if the event has been successfully started or not.
     */
    public static String startEvent() {
        try {
            switch (eventState) {
                case ON: {
                    return "Cannot start event, it is already on.";
                }
                case STANDBY: {
                    eventState = EventState.ON;
                    break;
                }
                case OFF: // Event is off, so no problem turning it on.
                {
                    return "Cannot start event, it is off. Participation start is required.";
                }
            }

            // Clean the things we will use, just in case.
            unspawnEventNpcs();
            _teams.clear();
            _connectionLossData.clear();

            // Insert empty lists at _teams.
            for (int i = 0; i < _teamsNumber; i++) {
                _teams.put(i + 1, ConcurrentHashMap.newKeySet());
            }

            int i = 0;
            while (!_registeredPlayers.isEmpty()) {
                // Get the player with the biggest level
                int max = 0;
                Player biggestLvlPlayer = null;
                for (Player player : _registeredPlayers) {
                    if (player == null) {
                        continue;
                    }

                    if (max < player.getLevel()) {
                        max = player.getLevel();
                        biggestLvlPlayer = player;
                    }
                }

                if (biggestLvlPlayer == null) {
                    continue;
                }

                _registeredPlayers.remove(biggestLvlPlayer);
                _teams.get(i + 1).add(biggestLvlPlayer);
                biggestLvlPlayer.setEventStatus();
                i = (i + 1) % _teamsNumber;
            }
        } catch (Exception e) {
            LOGGER.warn("Event: " + e.getMessage());
            return "Cannot start event, an error has occured.";
        }

        return "The event has been successfully started.";
    }

    /**
     * If the event state is OFF, it will not finish. Sets the event state to OFF, unregisters and resets the players, unspawns and clers the event NPCs, clears the teams, registered players, connection loss data, sets the teams number to 0, sets the event name to empty.
     *
     * @return a string with information if the event has been successfully stopped or not.
     */
    public static String finishEvent() {
        switch (eventState) {
            case OFF: {
                return "Cannot finish event, it is already off.";
            }
            case STANDBY: {
                for (Player player : _registeredPlayers) {
                    removeAndResetPlayer(player);
                }

                unspawnEventNpcs();
                // _npcs.clear();
                _registeredPlayers.clear();
                _teams.clear();
                _connectionLossData.clear();
                _teamsNumber = 0;
                _eventName = "";
                eventState = EventState.OFF;
                return "The event has been stopped at STANDBY mode, all players unregistered and all event npcs unspawned.";
            }
            case ON: {
                for (Set<Player> teamList : _teams.values()) {
                    for (Player player : teamList) {
                        removeAndResetPlayer(player);
                    }
                }

                eventState = EventState.OFF;
                AntiFeedManager.getInstance().clear(AntiFeedManager.TVT_ID);
                unspawnEventNpcs(); // Just in case
                // _npcs.clear();
                _registeredPlayers.clear();
                _teams.clear();
                _connectionLossData.clear();
                _teamsNumber = 0;
                _eventName = "";
                _npcId = 0;
                _eventCreator = "";
                _eventInfo = "";
                return "The event has been stopped, all players unregistered and all event npcs unspawned.";
            }
        }

        return "The event has been successfully finished.";
    }

    private static Map<Player, Integer> sortByValue(Map<Player, Integer> unsortMap) {
        final List<Entry<Player, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        list.sort(Comparator.comparing(Entry::getValue));

        final Map<Player, Integer> sortedMap = new LinkedHashMap<>();
        for (Entry<Player, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public enum EventState {
        OFF, // Not running
        STANDBY, // Waiting for participants to register
        ON // Registration is over and the event has started.
    }
}

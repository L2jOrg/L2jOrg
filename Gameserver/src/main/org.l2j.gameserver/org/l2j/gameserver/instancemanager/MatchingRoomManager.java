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
package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.enums.PartyMatchingRoomLevelType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.matching.MatchingRoom;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Sdw
 */
public class MatchingRoomManager {
    private static final Map<MatchingRoomType, Map<Integer, MatchingRoom>> _rooms = new ConcurrentHashMap<>(2);
    private final AtomicInteger _id = new AtomicInteger(0);
    private volatile Set<Player> _waitingList;

    private MatchingRoomManager() {

    }

    public void addToWaitingList(Player player) {
        if (_waitingList == null) {
            synchronized (this) {
                if (_waitingList == null) {
                    _waitingList = ConcurrentHashMap.newKeySet(1);
                }
            }
        }
        _waitingList.add(player);
    }

    public void removeFromWaitingList(Player player) {
        getPlayerInWaitingList().remove(player);
    }

    public Set<Player> getPlayerInWaitingList() {
        return _waitingList == null ? Collections.emptySet() : _waitingList;
    }

    public List<Player> getPlayerInWaitingList(int minLevel, int maxLevel, List<ClassId> classIds, String query) {
        if (_waitingList == null) {
            return Collections.emptyList();
        }
        return _waitingList.stream() //
                .filter(p -> (p != null) //
                        && (p.getLevel() >= minLevel) //
                        && (p.getLevel() <= maxLevel)) //
                .filter(p -> (classIds == null) //
                        || classIds.contains(p.getClassId())) //
                .filter(p -> (query == null) //
                        || query.isEmpty() //
                        || p.getName().toLowerCase().contains(query)) //
                .collect(Collectors.toList());
    }

    public int addMatchingRoom(MatchingRoom room) {
        final int roomId = _id.incrementAndGet();
        _rooms.computeIfAbsent(room.getRoomType(), k -> new ConcurrentHashMap<>()).put(roomId, room);
        return roomId;
    }

    public void removeMatchingRoom(MatchingRoom room) {
        _rooms.getOrDefault(room.getRoomType(), Collections.emptyMap()).remove(room.getId());
    }

    public Map<Integer, MatchingRoom> getPartyMathchingRooms() {
        return _rooms.get(MatchingRoomType.PARTY);
    }

    public List<MatchingRoom> getPartyMathchingRooms(int location, PartyMatchingRoomLevelType type, int requestorLevel) {
        //@formatter:off
        return _rooms.getOrDefault(MatchingRoomType.PARTY, Collections.emptyMap()).values().stream()
                .filter(room -> (location < 0) || (room.getLocation() == location))
                .filter(room -> (type == PartyMatchingRoomLevelType.ALL) || ((room.getMinLvl() >= requestorLevel) && (room.getMaxLvl() <= requestorLevel)))
                .collect(Collectors.toList());
        //@formatter:on
    }

    public Map<Integer, MatchingRoom> getCCMathchingRooms() {
        return _rooms.get(MatchingRoomType.COMMAND_CHANNEL);
    }

    public List<MatchingRoom> getCCMathchingRooms(int location, int level) {
        //@formatter:off
        return _rooms.getOrDefault(MatchingRoomType.COMMAND_CHANNEL, Collections.emptyMap()).values().stream()
                .filter(r -> r.getLocation() == location)
                .filter(r -> (r.getMinLvl() <= level) && (r.getMaxLvl() >= level))
                .collect(Collectors.toList());
        //@formatter:on
    }

    public MatchingRoom getCCMatchingRoom(int roomId) {
        return _rooms.getOrDefault(MatchingRoomType.COMMAND_CHANNEL, Collections.emptyMap()).get(roomId);
    }

    public MatchingRoom getPartyMathchingRoom(int location, int level) {
        //@formatter:off
        return _rooms.getOrDefault(MatchingRoomType.PARTY, Collections.emptyMap()).values().stream()
                .filter(r -> r.getLocation() == location)
                .filter(r -> (r.getMinLvl() <= level) && (r.getMaxLvl() >= level))
                .findFirst()
                .orElse(null);
        //@formatter:on
    }

    public MatchingRoom getPartyMathchingRoom(int roomId) {
        return _rooms.getOrDefault(MatchingRoomType.PARTY, Collections.emptyMap()).get(roomId);
    }

    public static MatchingRoomManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final MatchingRoomManager INSTANCE = new MatchingRoomManager();
    }
}

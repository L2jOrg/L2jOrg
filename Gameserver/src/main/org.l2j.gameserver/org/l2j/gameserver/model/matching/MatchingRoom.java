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
package org.l2j.gameserver.model.matching;

import org.l2j.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.world.MapRegionManager;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sdw
 */
public abstract class MatchingRoom implements IIdentifiable {
    private final int _id;
    private String _title;
    private int _loot;
    private int _minLvl;
    private int _maxLvl;
    private int _maxCount;
    private volatile Set<Player> _members;
    private Player _leader;

    public MatchingRoom(String title, int loot, int minlvl, int maxlvl, int maxmem, Player leader) {
        _id = MatchingRoomManager.getInstance().addMatchingRoom(this);
        _title = title;
        _loot = loot;
        _minLvl = minlvl;
        _maxLvl = maxlvl;
        _maxCount = maxmem;
        _leader = leader;

        addMember(_leader);
        onRoomCreation(leader);
    }

    public Set<Player> getMembers() {
        if (_members == null) {
            synchronized (this) {
                if (_members == null) {
                    _members = ConcurrentHashMap.newKeySet(1);
                }
            }
        }

        return _members;
    }

    public void addMember(Player player) {
        if ((player.getLevel() < _minLvl) || (player.getLevel() > _maxLvl) || ((_members != null) && (_members.size() >= _maxCount))) {
            notifyInvalidCondition(player);
            return;
        }

        getMembers().add(player);
        MatchingRoomManager.getInstance().removeFromWaitingList(player);
        notifyNewMember(player);
        player.setMatchingRoom(this);
        player.broadcastUserInfo(UserInfoType.CLAN);
    }

    public void deleteMember(Player player, boolean kicked) {
        boolean leaderChanged = false;

        if (player == _leader) {
            if (getMembers().isEmpty()) {
                MatchingRoomManager.getInstance().removeMatchingRoom(this);
            } else {
                final Iterator<Player> iter = getMembers().iterator();
                if (iter.hasNext()) {
                    _leader = iter.next();
                    iter.remove();
                    leaderChanged = true;
                }
            }
        } else {
            getMembers().remove(player);
        }

        player.setMatchingRoom(null);
        player.broadcastUserInfo(UserInfoType.CLAN);
        MatchingRoomManager.getInstance().addToWaitingList(player);

        notifyRemovedMember(player, kicked, leaderChanged);
    }

    @Override
    public int getId() {
        return _id;
    }

    public int getLootType() {
        return _loot;
    }

    public void setLootType(int loot) {
        _loot = loot;
    }

    public int getMinLvl() {
        return _minLvl;
    }

    public void setMinLvl(int minLvl) {
        _minLvl = minLvl;
    }

    public int getMaxLvl() {
        return _maxLvl;
    }

    public void setMaxLvl(int maxLvl) {
        _maxLvl = maxLvl;
    }

    public int getLocation() {
        return MapRegionManager.getInstance().getBBs(_leader.getLocation());
    }

    public int getMembersCount() {
        return _members == null ? 0 : _members.size();
    }

    public int getMaxMembers() {
        return _maxCount;
    }

    public void setMaxMembers(int maxCount) {
        _maxCount = maxCount;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public Player getLeader() {
        return _leader;
    }

    public boolean isLeader(Player player) {
        return player == _leader;
    }

    protected abstract void onRoomCreation(Player player);

    protected abstract void notifyInvalidCondition(Player player);

    protected abstract void notifyNewMember(Player player);

    protected abstract void notifyRemovedMember(Player player, boolean kicked, boolean leaderChanged);

    public abstract void disbandRoom();

    public abstract MatchingRoomType getRoomType();

    public abstract MatchingMemberType getMemberType(Player player);
}

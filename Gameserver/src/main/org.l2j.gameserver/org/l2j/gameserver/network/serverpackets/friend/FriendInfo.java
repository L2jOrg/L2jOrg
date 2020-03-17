package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.data.database.data.PlayerData;

import java.time.LocalDate;

class FriendInfo {

    int objectId;
    String name;
    int level;
    int classId;
    boolean online;
    int clanId;
    LocalDate createDate;
    long lastAccess;

    FriendInfo(int friendId, PlayerData friendData) {
        this(friendId, friendData.getName(), false, friendData.getLevel(), friendData.getClassId());
        clanId = friendData.getClanId();
        createDate = friendData.getCreateDate();
        lastAccess = friendData.getLastAccess();
    }

    FriendInfo(int objectId, String name, boolean online, int level, int classId) {
        this.objectId = objectId;
        this.name = name;
        this.online = online;
        this.level = level;
        this.classId = classId;
    }
}

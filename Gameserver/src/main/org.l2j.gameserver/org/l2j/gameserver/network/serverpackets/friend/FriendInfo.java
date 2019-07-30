package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.data.database.data.CharacterData;

class FriendInfo {
    int objId;
    String name;
    int level;
    int classId;
    boolean online;

    FriendInfo(int friendId, CharacterData friendData) {
        this(friendId, friendData.getName(), false, friendData.getLevel(), friendData.getClassId());
    }

    FriendInfo(int objId, String name, boolean online, int level, int classId) {
        this.objId = objId;
        this.name = name;
        this.online = online;
        this.level = level;
        this.classId = classId;
    }
}

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
package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Sdw
 */
public class ExFriendDetailInfo extends ServerPacket {
    private final int _objectId;
    private Player friend;
    private FriendInfo info;
    private final String _name;

    public ExFriendDetailInfo(Player player, String name) {
        _objectId = player.getObjectId();
        _name = name;

        var friendId = PlayerNameTable.getInstance().getIdByName(name);

        friend = World.getInstance().findPlayer(friendId);
        if(isNull(friend)) {
            info = new FriendInfo(friendId, getDAO(PlayerDAO.class).findFriendData(friendId));
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_FRIEND_DETAIL_INFO);

        writeInt(_objectId);

        if (isNull(friend)) {
            WriteFriendInfo();
        } else {
            writePlayerInfo();
        }
    }

    private void writePlayerInfo() {
        writeString(friend.getName());
        writeInt(friend.isOnline());
        writeInt(friend.isOnline() ? friend.getObjectId() : 0);
        writeShort(friend.getLevel());
        writeShort(friend.getClassId().getId());
        writeInt(friend.getClanId());
        writeInt(friend.getClanCrestId());
        writeString(friend.getClan() != null ? friend.getClan().getName() : "");
        writeInt(friend.getAllyId());
        writeInt(friend.getAllyCrestId());
        writeString(friend.getClan() != null ? friend.getClan().getAllyName() : "");

        var createDate = friend.getCreateDate();
        writeByte(createDate.getMonthValue());
        writeByte(createDate.getDayOfMonth());

        writeInt(-1);
        writeString(""); //TODO  memo
    }

    private void WriteFriendInfo() {
        writeString(_name);
        writeInt(info.online);
        writeInt(info.online ? info.objectId : 0);
        writeShort(info.level);
        writeShort(info.classId);
        writeInt(info.clanId);

        Clan clan;
        if(info.clanId > 0 && nonNull(clan = ClanTable.getInstance().getClan(info.clanId))) {
            writeInt(clan.getCrestId());
            writeString(clan.getName());
            writeInt(clan.getAllyId());
            writeInt(clan.getAllyCrestId());
            writeString(clan.getAllyName());
        } else {
            writeInt(0);
            writeString("");
            writeInt(0);
            writeInt(0);
            writeString("");
        }

        if(nonNull(info.createDate)) {
            writeByte(info.createDate.getMonthValue());
            writeByte(info.createDate.getDayOfMonth());
        } else  {
            writeByte(0);
            writeByte(0);
        }
        writeInt((int) ((System.currentTimeMillis() - info.lastAccess) / 1000));
        writeString(""); //TODO  memo
    }
}

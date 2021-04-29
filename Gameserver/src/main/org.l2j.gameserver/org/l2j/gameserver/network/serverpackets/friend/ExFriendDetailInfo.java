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
package org.l2j.gameserver.network.serverpackets.friend;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.engine.clan.ClanEngine;
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
    private final Player friend;
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
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_FRIEND_DETAIL_INFO, buffer );

        buffer.writeInt(_objectId);

        if (isNull(friend)) {
            WriteFriendInfo(buffer);
        } else {
            writePlayerInfo(buffer);
        }
    }

    private void writePlayerInfo(WritableBuffer buffer) {
        buffer.writeString(friend.getName());
        buffer.writeInt(friend.isOnline());
        buffer.writeInt(friend.isOnline() ? friend.getObjectId() : 0);
        buffer.writeShort(friend.getLevel());
        buffer.writeShort(friend.getClassId().getId());
        buffer.writeInt(friend.getClanId());
        buffer.writeInt(friend.getClanCrestId());
        buffer.writeString(friend.getClan() != null ? friend.getClan().getName() : "");
        buffer.writeInt(friend.getAllyId());
        buffer.writeInt(friend.getAllyCrestId());
        buffer.writeString(friend.getClan() != null ? friend.getClan().getAllyName() : "");

        var createDate = friend.getCreateDate();
        buffer.writeByte(createDate.getMonthValue());
        buffer.writeByte(createDate.getDayOfMonth());

        buffer.writeInt(-1);
        buffer.writeString(""); //TODO  memo
    }

    private void WriteFriendInfo(WritableBuffer buffer) {
        buffer.writeString(_name);
        buffer.writeInt(info.online);
        buffer.writeInt(info.online ? info.objectId : 0);
        buffer.writeShort(info.level);
        buffer.writeShort(info.classId);
        buffer.writeInt(info.clanId);

        Clan clan;
        if(info.clanId > 0 && nonNull(clan = ClanEngine.getInstance().getClan(info.clanId))) {
            buffer.writeInt(clan.getCrestId());
            buffer.writeString(clan.getName());
            buffer.writeInt(clan.getAllyId());
            buffer.writeInt(clan.getAllyCrestId());
            buffer.writeString(clan.getAllyName());
        } else {
            buffer.writeInt(0);
            buffer.writeString("");
            buffer.writeInt(0);
            buffer.writeInt(0);
            buffer.writeString("");
        }

        if(nonNull(info.createDate)) {
            buffer.writeByte(info.createDate.getMonthValue());
            buffer.writeByte(info.createDate.getDayOfMonth());
        } else  {
            buffer.writeByte(0);
            buffer.writeByte(0);
        }
        buffer.writeInt((int) ((System.currentTimeMillis() - info.lastAccess) / 1000));
        buffer.writeString(""); //TODO  memo
    }
}

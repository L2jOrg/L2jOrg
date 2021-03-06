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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.enums.SubclassInfoType;
import org.l2j.gameserver.enums.SubclassType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sdw
 */
public class ExSubjobInfo extends ServerPacket {
    private final int _currClassId;
    private final int _currRace;
    private final int _type;
    private final List<SubInfo> _subs;

    public ExSubjobInfo(Player player, SubclassInfoType type) {
        _currClassId = player.getClassId().getId();
        _currRace = player.getRace().ordinal();
        _type = type.ordinal();

        _subs = new ArrayList<>();
        _subs.add(0, new SubInfo(player));
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SUBJOB_INFO, buffer );

        buffer.writeByte(_type);
        buffer.writeInt(_currClassId);
        buffer.writeInt(_currRace);
        buffer.writeInt(_subs.size());
        for (SubInfo sub : _subs) {
            buffer.writeInt(sub.getIndex());
            buffer.writeInt(sub.getClassId());
            buffer.writeInt(sub.getLevel());
            buffer.writeByte(sub.getType());
        }
    }


    private final class SubInfo {
        private final int _index;
        private final int _classId;
        private final int _level;
        private final int _type;

        public SubInfo(Player player) {
            _index = 0;
            _classId = player.getBaseClass();
            _level = player.getStats().getBaseLevel();
            _type = SubclassType.BASECLASS.ordinal();
        }

        public int getIndex() {
            return _index;
        }

        public int getClassId() {
            return _classId;
        }

        public int getLevel() {
            return _level;
        }

        public int getType() {
            return _type;
        }
    }
}
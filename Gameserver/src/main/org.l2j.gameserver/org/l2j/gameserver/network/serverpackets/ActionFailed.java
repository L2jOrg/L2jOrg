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
import org.l2j.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.EnumMap;
import java.util.Map;

public final class ActionFailed extends ServerPacket {

    public static final ActionFailed STATIC_PACKET = new ActionFailed();
    private static final Map<SkillCastingType, ActionFailed> STATIC_PACKET_BY_CASTING_TYPE = new EnumMap<>(SkillCastingType.class);

    static {
        for (var castingType : SkillCastingType.values()) {
            STATIC_PACKET_BY_CASTING_TYPE.put(castingType, new ActionFailed(castingType.getClientBarId()));
        }
    }

    private final int castingType;

    private ActionFailed() {
        this(0);
    }

    private ActionFailed(int castingType) {
        this.castingType = castingType;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.ACTION_FAIL, buffer );
        buffer.writeInt(castingType); // MagicSkillUse castingType
    }

    public static ActionFailed of(SkillCastingType castingType) {
        return STATIC_PACKET_BY_CASTING_TYPE.getOrDefault(castingType, STATIC_PACKET);
    }

}

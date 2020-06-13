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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.EnumMap;
import java.util.Map;

import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * @author JoeAlisson
 */
public final class StatusUpdate extends ServerPacket {
    private final int objectId;
    private final boolean isPlayable;
    private final Map<StatusUpdateType, Integer> updates = new EnumMap<>(StatusUpdateType.class);
    private int casterObjectId = 0;
    private boolean isVisible = false;

    public StatusUpdate(WorldObject object) {
        objectId = object.getObjectId();
        isPlayable = isPlayable(object);
    }

    public StatusUpdate addUpdate(StatusUpdateType type, int level) {
        updates.put(type, level);

        if (isPlayable) {
            isVisible = switch (type) {
                case CUR_HP, CUR_MP, CUR_CP -> true;
                default -> false;
            };
        }
        return this;
    }

    public void addCaster(WorldObject object) {
        casterObjectId = object.getObjectId();
    }

    public boolean hasUpdates() {
        return !updates.isEmpty();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.STATUS_UPDATE);

        writeInt(objectId); // casterId
        writeInt(isVisible ? casterObjectId : 0x00);
        writeByte(isVisible);
        writeByte(updates.size());
        for (var entry : updates.entrySet()) {
            writeByte(entry.getKey().getClientId());
            writeInt(entry.getValue());
        }
    }

    public static StatusUpdate of(WorldObject object, StatusUpdateType type, int value) {
        return new StatusUpdate(object).addUpdate(type, value);
    }

}

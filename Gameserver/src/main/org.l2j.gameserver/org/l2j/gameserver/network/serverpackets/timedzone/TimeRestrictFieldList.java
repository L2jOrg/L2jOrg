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
package org.l2j.gameserver.network.serverpackets.timedzone;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.timedzone.TimeRestrictZoneEngine;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.l2j.gameserver.world.zone.type.TimeRestrictZone;

import java.util.Collection;

/**
 * @author JoeAlisson
 */
public class TimeRestrictFieldList extends ServerPacket {

    private final Collection<TimeRestrictZone> fieldList;

    public TimeRestrictFieldList() {
        fieldList = ZoneEngine.getInstance().getAllZones(TimeRestrictZone.class);
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)  {
        writeId(ServerExPacketId.EX_TIME_RESTRICT_FIELD_LIST, buffer);
        var player = client.getPlayer();
        buffer.writeInt(fieldList.size());

        for(var field : fieldList) {
            var items = field.requiredItems();

            buffer.writeInt(items.size());
            for (var item : items) {
                buffer.writeInt(item.getId());
                buffer.writeLong(item.getCount());
            }

            buffer.writeInt(field.getResetCycle());
            buffer.writeInt(field.getId());
            buffer.writeInt(field.getMinLevel());
            buffer.writeInt(field.getMaxLevel());
            buffer.writeInt(field.getTime());

            var zoneInfo = TimeRestrictZoneEngine.getInstance().getTimeRestrictZoneInfo(player, field);
            zoneInfo.updateRemainingTime();

            buffer.writeInt(zoneInfo.remainingTime());
            buffer.writeInt(field.getMaxTime());
            buffer.writeInt(field.getRechargeTime() - zoneInfo.getRechargedTime());
            buffer.writeInt(field.getRechargeTime());
            buffer.writeByte(field.isEnabled());
            buffer.writeByte(field.isUserBound());
            buffer.writeByte(zoneInfo.remainingTime() > 0);
            buffer.writeByte(field.pcCafePoints() > 0);
            buffer.writeByte(player.getPcCafePoints() >= field.pcCafePoints());
            buffer.writeByte(field.worldInZone());
        }
    }
}

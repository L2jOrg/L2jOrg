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
package org.l2j.gameserver.network.serverpackets.timedhunter;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JoeAlisson
 */
public class TimeRestrictFieldList extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)  {
        writeId(ServerExPacketId.EX_TIME_RESTRICT_FIELD_LIST, buffer );

        List<TimeRestrictedFieldInfo> infos = new ArrayList<>();

        addField(infos);

        buffer.writeInt(infos.size());

        for(var info : infos) {
            buffer.writeInt(info.requiredItems.size());

            for (var item : info.requiredItems) {
                buffer.writeInt(item.itemId);
                buffer.writeLong(item.count);
            }

            buffer.writeInt(info.resetCycle.ordinal()); // 0 weekly, 1 Daily
            buffer.writeInt(info.fieldId);
            buffer.writeInt(info.minLevel);
            buffer.writeInt(info.maxLevel);
            buffer.writeInt(info.remainTimeBase);
            buffer.writeInt(info.remainTime);
            buffer.writeInt(info.remainTimeMax);
            buffer.writeInt(info.remainRefillTime);
            buffer.writeInt(info.refillTimeMax);
            buffer.writeByte(info.fieldActivated);
            buffer.writeByte(info.userBound);
            buffer.writeByte(info.canReEnter);
            buffer.writeByte(info.isVipOnly); // pc cafe only ?
            buffer.writeByte(info.isVipUser); // pc cafe user ?
            buffer.writeByte(info.worldInZone);
        }
    }

    private void addField(List<TimeRestrictedFieldInfo> infos) {
        var field = new TimeRestrictedFieldInfo();
        field.resetCycle = ResetCycle.DAILY;
        field.fieldId = 2;
        field.minLevel = 78;
        field.maxLevel = 999;
        field.remainTimeBase = 3600;
        field.remainTime = 3600;
        field.remainTimeMax =  21600;
        field.remainRefillTime = 18000;
        field.refillTimeMax = 18000;
        field.fieldActivated = true;

        var item = new FieldRequiredItem();
        item.itemId = 57;
        item.count = 10000;

       var item2 = new FieldRequiredItem();
       item2.itemId = 100;
       item.count = 2000;

        field.requiredItems = List.of(item, item2);
        infos.add(field);
    }

    static class TimeRestrictedFieldInfo {
        public byte userBound;
        public byte canReEnter;
        public byte isVipOnly;
        public byte isVipUser;
        public byte worldInZone;
        List<FieldRequiredItem> requiredItems;
        ResetCycle resetCycle;
        int fieldId;
        int minLevel;
        int maxLevel;
        int remainTimeBase;
        int remainTime;
        int remainTimeMax;
        int remainRefillTime;
        int refillTimeMax;
        boolean fieldActivated;
    }

    enum ResetCycle {
        WEEKLY,
        DAILY
    }

    static class FieldRequiredItem {
        int itemId;
        long count;
    }
}

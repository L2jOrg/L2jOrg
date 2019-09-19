package org.l2j.gameserver.network.serverpackets.timedhunter;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.ArrayList;
import java.util.List;

public class TimedHuntingZoneList extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_TIME_RESTRICT_FIELD_LIST);

        List<TimeRestrictedFieldInfo> infos = new ArrayList<>();

        addField(infos);

        writeInt(infos.size());

        for(var info : infos) {
            writeInt(info.requiredItems.size());

            for (var item : info.requiredItems) {
                writeInt(item.itemId);
                writeLong(item.count);
            }

            writeInt(info.resetCycle);
            writeInt(info.fieldId);
            writeInt(info.minLevel);
            writeInt(info.maxLevel);
            writeInt(info.remainTimeBase);
            writeInt(info.remainTime);
            writeInt(info.remainTimeMax);
            writeInt(info.remainRefillTime);
            writeInt(info.refillTimeMax);
            writeByte(info.fieldActivated);
        }
    }

    private void addField(List<TimeRestrictedFieldInfo> infos) {
        var field = new TimeRestrictedFieldInfo();
        field.resetCycle = 1;
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

        field.requiredItems = List.of(item);
        infos.add(field);
    }

    static class TimeRestrictedFieldInfo {
        List<FieldRequiredItem> requiredItems;
        int resetCycle;
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

    static class FieldRequiredItem {
        int itemId;
        long count;
    }


}

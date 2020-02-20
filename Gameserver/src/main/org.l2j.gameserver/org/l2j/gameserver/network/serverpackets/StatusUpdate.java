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

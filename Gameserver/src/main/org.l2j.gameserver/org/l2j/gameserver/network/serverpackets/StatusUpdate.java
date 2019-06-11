package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class StatusUpdate extends IClientOutgoingPacket {
    private final int _objectId;
    private final boolean _isPlayable;
    private final Map<StatusUpdateType, Integer> _updates = new LinkedHashMap<>();
    private int _casterObjectId = 0;
    private boolean _isVisible = false;

    /**
     * Create {@link StatusUpdate} packet for given {@link L2Object}.
     *
     * @param object
     */
    public StatusUpdate(L2Object object) {
        _objectId = object.getObjectId();
        _isPlayable = object.isPlayable();
    }

    public void addUpdate(StatusUpdateType type, int level) {
        _updates.put(type, level);

        if (_isPlayable) {
            switch (type) {
                case CUR_HP:
                case CUR_MP:
                case CUR_CP: {
                    _isVisible = true;
                }
            }
        }
    }

    public void addCaster(L2Object object) {
        _casterObjectId = object.getObjectId();
    }

    public boolean hasUpdates() {
        return !_updates.isEmpty();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.STATUS_UPDATE);

        writeInt(_objectId); // casterId
        writeInt(_isVisible ? _casterObjectId : 0x00);
        writeByte((byte) (_isVisible ? 0x01 : 0x00));
        writeByte((byte) _updates.size());
        for (Entry<StatusUpdateType, Integer> entry : _updates.entrySet()) {
            writeByte((byte) entry.getKey().getClientId());
            writeInt(entry.getValue());
        }
    }

}

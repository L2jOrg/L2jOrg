package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class StatusUpdate implements IClientOutgoingPacket
{
    private final int _objectId;
    private int _casterObjectId = 0;
    private final boolean _isPlayable;
    private boolean _isVisible = false;
    private final Map<StatusUpdateType, Integer> _updates = new LinkedHashMap<>();

    /**
     * Create {@link StatusUpdate} packet for given {@link L2Object}.
     * @param object
     */
    public StatusUpdate(L2Object object)
    {
        _objectId = object.getObjectId();
        _isPlayable = object.isPlayable();
    }

    public void addUpdate(StatusUpdateType type, int level)
    {
        _updates.put(type, level);

        if (_isPlayable)
        {
            switch (type)
            {
                case CUR_HP:
                case CUR_MP:
                case CUR_CP:
                {
                    _isVisible = true;
                }
            }
        }
    }

    public void addCaster(L2Object object)
    {
        _casterObjectId = object.getObjectId();
    }

    public boolean hasUpdates()
    {
        return !_updates.isEmpty();
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.STATUS_UPDATE.writeId(packet);

        packet.writeD(_objectId); // casterId
        packet.writeD(_isVisible ? _casterObjectId : 0x00);
        packet.writeC(_isVisible ? 0x01 : 0x00);
        packet.writeC(_updates.size());
        for (Entry<StatusUpdateType, Integer> entry : _updates.entrySet())
        {
            packet.writeC(entry.getKey().getClientId());
            packet.writeD(entry.getValue());
        }
        return true;
    }
}

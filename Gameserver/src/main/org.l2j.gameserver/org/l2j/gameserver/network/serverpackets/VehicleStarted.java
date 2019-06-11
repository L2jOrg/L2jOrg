package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Kerberos
 */
public class VehicleStarted extends IClientOutgoingPacket {
    private final int _objectId;
    private final int _state;

    /**
     * @param boat
     * @param state
     */
    public VehicleStarted(L2Character boat, int state) {
        _objectId = boat.getObjectId();
        _state = state;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.VEHICLE_START);

        writeInt(_objectId);
        writeInt(_state);
    }

}

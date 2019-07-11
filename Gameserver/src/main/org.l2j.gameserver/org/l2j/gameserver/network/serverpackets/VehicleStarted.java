package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Kerberos
 */
public class VehicleStarted extends ServerPacket {
    private final int _objectId;
    private final int _state;

    /**
     * @param boat
     * @param state
     */
    public VehicleStarted(Creature boat, int state) {
        _objectId = boat.getObjectId();
        _state = state;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.VEHICLE_START);

        writeInt(_objectId);
        writeInt(_state);
    }

}

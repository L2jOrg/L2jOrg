package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class CameraMode extends IClientOutgoingPacket {
    private final int _mode;

    /**
     * Forces client camera mode change
     *
     * @param mode 0 - third person cam 1 - first person cam
     */
    public CameraMode(int mode) {
        _mode = mode;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.CAMERA_MODE.writeId(packet);

        packet.putInt(_mode);
    }
}

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class CameraMode extends ServerPacket {
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.CAMERA_MODE);

        writeInt(_mode);
    }

}

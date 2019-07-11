package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CAMERA_MODE);

        writeInt(_mode);
    }

}

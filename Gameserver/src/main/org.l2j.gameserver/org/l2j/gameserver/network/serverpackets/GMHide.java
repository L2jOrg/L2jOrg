package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Kerberos
 */
@Deprecated
public class GMHide extends ServerPacket {
    private final int _mode;

    /**
     * @param mode (0 = display windows, 1 = hide windows)
     */
    public GMHide(int mode) {
        _mode = mode;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.GM_HIDE);

        writeInt(_mode);
    }

}

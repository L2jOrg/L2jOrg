package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author devScarlet, mrTJO
 */
public class ShowXMasSeal extends ServerPacket {
    private final int _item;

    public ShowXMasSeal(int item) {
        _item = item;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.SHOW_XMAS_SEAL);

        writeInt(_item);
    }

}

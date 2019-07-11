package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SHOW_XMAS_SEAL);

        writeInt(_item);
    }

}

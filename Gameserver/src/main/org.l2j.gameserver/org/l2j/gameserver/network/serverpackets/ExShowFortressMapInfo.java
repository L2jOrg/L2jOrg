package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * TODO: Rewrite!!!!!!
 *
 * @author KenM
 */
public class ExShowFortressMapInfo extends ServerPacket {

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_FORTRESS_MAP_INFO);

        writeInt(0); // fortress id
        writeInt(0); // fortress siege in progress ?
        writeInt(0); // barracks count
        /* foreach barrack
         * writeInt(0);
         */
    }
}

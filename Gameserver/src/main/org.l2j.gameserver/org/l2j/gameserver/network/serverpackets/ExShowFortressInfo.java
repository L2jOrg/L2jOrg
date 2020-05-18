package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author KenM
 */
public class ExShowFortressInfo extends ServerPacket {
    public static final ExShowFortressInfo STATIC_PACKET = new ExShowFortressInfo();

    private ExShowFortressInfo() {

    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_FORTRESS_INFO);

        writeInt(0); // fortress amount

        /* foreach forts
        writeInt(fort.getId());
        writeString(clan != null ? clan.getName() : ""); // owning clan
        writeInt(0x00); // fort siege in progress ?
        // Time of possession
        writeInt(fort.getOwnedTime());*/
    }

}

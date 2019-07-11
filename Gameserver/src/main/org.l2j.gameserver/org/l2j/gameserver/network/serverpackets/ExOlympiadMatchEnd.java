package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author GodKratos
 */
public class ExOlympiadMatchEnd extends ServerPacket {
    public static final ExOlympiadMatchEnd STATIC_PACKET = new ExOlympiadMatchEnd();

    private ExOlympiadMatchEnd() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_OLYMPIAD_MATCH_END);
    }

}
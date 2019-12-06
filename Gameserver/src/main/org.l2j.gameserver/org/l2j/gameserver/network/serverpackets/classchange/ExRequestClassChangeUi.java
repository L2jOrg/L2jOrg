package org.l2j.gameserver.network.serverpackets.classchange;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
@StaticPacket
public class ExRequestClassChangeUi extends ServerPacket {

    public static final ExRequestClassChangeUi STATIC_PACKET = new ExRequestClassChangeUi();

    private ExRequestClassChangeUi() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_REQUEST_CLASS_CHANGE);
    }
}

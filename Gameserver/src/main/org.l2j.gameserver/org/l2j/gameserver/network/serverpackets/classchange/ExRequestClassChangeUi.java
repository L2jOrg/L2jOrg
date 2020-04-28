package org.l2j.gameserver.network.serverpackets.classchange;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
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
        writeId(ServerExPacketId.EX_CLASS_CHANGE_SET_ALARM);
    }
}

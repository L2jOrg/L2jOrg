package org.l2j.gameserver.network.serverpackets.attributechange;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
@StaticPacket
public class ExChangeAttributeFail extends ServerPacket {
    public static final ServerPacket STATIC = new ExChangeAttributeFail();

    private ExChangeAttributeFail() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_CHANGE_ATTRIBUTE_FAIL);
    }

}
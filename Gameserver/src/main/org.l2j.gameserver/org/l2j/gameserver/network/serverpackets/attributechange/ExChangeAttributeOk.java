package org.l2j.gameserver.network.serverpackets.attributechange;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
@StaticPacket
public class ExChangeAttributeOk extends ServerPacket {
    public static final ServerPacket STATIC = new ExChangeAttributeOk();

    private ExChangeAttributeOk() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CHANGE_ATTRIBUTE_OK);
    }

}
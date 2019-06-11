package org.l2j.gameserver.network.serverpackets.attributechange;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
@StaticPacket
public class ExChangeAttributeFail extends IClientOutgoingPacket {
    public static final IClientOutgoingPacket STATIC = new ExChangeAttributeFail();

    private ExChangeAttributeFail() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_CHANGE_ATTRIBUTE_FAIL);
    }

}
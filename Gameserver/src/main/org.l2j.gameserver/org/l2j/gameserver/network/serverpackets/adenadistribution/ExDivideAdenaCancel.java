package org.l2j.gameserver.network.serverpackets.adenadistribution;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
@StaticPacket
public class ExDivideAdenaCancel extends ServerPacket {
    public static final ExDivideAdenaCancel STATIC_PACKET = new ExDivideAdenaCancel();

    private ExDivideAdenaCancel() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_DIVIDE_ADENA_CANCEL);

        writeByte((byte) 0x00); // TODO: Find me
    }

}

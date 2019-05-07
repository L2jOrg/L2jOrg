package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Gnacik
 **/
@StaticPacket
public class ExBirthdayPopup extends IClientOutgoingPacket {
    public static final ExBirthdayPopup STATIC_PACKET = new ExBirthdayPopup();

    private ExBirthdayPopup() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_NOTIFY_BIRTH_DAY.writeId(packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return 5;
    }
}

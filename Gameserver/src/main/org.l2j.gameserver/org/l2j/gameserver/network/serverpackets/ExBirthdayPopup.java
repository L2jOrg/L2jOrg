package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Gnacik
 **/
@StaticPacket
public class ExBirthdayPopup extends ServerPacket {
    public static final ExBirthdayPopup STATIC_PACKET = new ExBirthdayPopup();

    private ExBirthdayPopup() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_NOTIFY_BIRTH_DAY);
    }

}

package org.l2j.gameserver.network.serverpackets.ceremonyofchaos;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExCuriousHouseObserveMode extends IClientOutgoingPacket {
    public static final ExCuriousHouseObserveMode STATIC_ENABLED = new ExCuriousHouseObserveMode(0);
    public static final ExCuriousHouseObserveMode STATIC_DISABLED = new ExCuriousHouseObserveMode(1);

    private final int _spectating;

    private ExCuriousHouseObserveMode(int spectating) {
        _spectating = spectating;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_CURIOUS_HOUSE_OBSERVE_MODE);
        writeByte((byte) _spectating);
    }

}

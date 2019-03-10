package org.l2j.gameserver.mobius.gameserver.network.serverpackets.sayune;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExNotifyFlyMoveStart extends IClientOutgoingPacket {
    public static final ExNotifyFlyMoveStart STATIC_PACKET = new ExNotifyFlyMoveStart();

    private ExNotifyFlyMoveStart() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_NOTIFY_FLY_MOVE_START.writeId(packet);
    }
}
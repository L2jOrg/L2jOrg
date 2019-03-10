package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.mobius.gameserver.model.entity.Castle;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author l3x
 */
@StaticPacket
public final class ExSendManorList extends IClientOutgoingPacket {
    public static final ExSendManorList STATIC_PACKET = new ExSendManorList();

    private ExSendManorList() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SEND_MANOR_LIST.writeId(packet);

        final Collection<Castle> castles = CastleManager.getInstance().getCastles();
        packet.putInt(castles.size());
        for (Castle castle : castles) {
            packet.putInt(castle.getResidenceId());
        }
    }
}

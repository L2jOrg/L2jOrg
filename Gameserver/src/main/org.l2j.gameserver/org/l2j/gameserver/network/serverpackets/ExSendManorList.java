package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

/**
 * @author l3x
 */
@StaticPacket
public final class ExSendManorList extends ServerPacket {
    public static final ExSendManorList STATIC_PACKET = new ExSendManorList();

    private ExSendManorList() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SEND_MANOR_LIST);

        final Collection<Castle> castles = CastleManager.getInstance().getCastles();
        writeInt(castles.size());
        for (Castle castle : castles) {
            writeInt(castle.getId());
        }
    }

}

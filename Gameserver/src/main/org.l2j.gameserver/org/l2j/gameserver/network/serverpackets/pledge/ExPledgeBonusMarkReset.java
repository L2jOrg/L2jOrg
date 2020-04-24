package org.l2j.gameserver.network.serverpackets.pledge;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExPledgeBonusMarkReset extends ServerPacket {
    public static ExPledgeBonusMarkReset STATIC_PACKET = new ExPledgeBonusMarkReset();

    private ExPledgeBonusMarkReset() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PLEDGE_ACTIVITY_MARK_RESET);
    }

}

package org.l2j.gameserver.network.serverpackets.pledge;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
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
        writeId(ServerPacketId.EX_PLEDGE_BONUS_MARK_RESET);
    }

}

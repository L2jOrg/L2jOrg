package org.l2j.gameserver.mobius.gameserver.network.serverpackets.pledgebonus;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExPledgeBonusMarkReset extends IClientOutgoingPacket {
    public static ExPledgeBonusMarkReset STATIC_PACKET = new ExPledgeBonusMarkReset();

    private ExPledgeBonusMarkReset() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PLEDGE_BONUS_MARK_RESET.writeId(packet);
    }
}

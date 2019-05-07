package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author GodKratos
 */
public class ExBrPremiumState extends IClientOutgoingPacket {

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BR_PREMIUM_STATE.writeId(packet);
        var activeChar = client.getActiveChar();
        packet.putInt(activeChar.getObjectId());
        packet.put((byte) (activeChar.getVipTier() > 0 ? 0x01 : 0x00));
    }

    @Override
    protected int size(L2GameClient client) {
        return 10;
    }
}

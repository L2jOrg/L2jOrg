package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author GodKratos
 */
public class ExBrPremiumState extends ServerPacket {

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_BR_PREMIUM_STATE);
        var activeChar = client.getActiveChar();
        writeInt(activeChar.getObjectId());
        writeByte((byte) (activeChar.getVipTier() > 0 ? 0x01 : 0x00));
    }

}

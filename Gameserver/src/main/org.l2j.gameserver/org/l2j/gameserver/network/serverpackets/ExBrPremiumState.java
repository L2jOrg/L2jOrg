package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author GodKratos
 */
public class ExBrPremiumState extends ServerPacket {

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BR_NOTIFY_PREMIUM_STATE);
        var activeChar = client.getPlayer();
        writeInt(activeChar.getObjectId());
        writeByte((byte) (activeChar.getVipTier() > 0 ? 0x01 : 0x00));
    }

}

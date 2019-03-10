package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExResponseResetList extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;

    public ExResponseResetList(L2PcInstance activeChar) {
        _activeChar = activeChar;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_RESPONSE_RESET_LIST.writeId(packet);

        packet.putLong(_activeChar.getAdena());
        packet.putLong(_activeChar.getBeautyTickets());

        packet.putInt(_activeChar.getAppearance().getHairStyle());
        packet.putInt(_activeChar.getAppearance().getHairColor());
        packet.putInt(_activeChar.getAppearance().getFace());
    }
}

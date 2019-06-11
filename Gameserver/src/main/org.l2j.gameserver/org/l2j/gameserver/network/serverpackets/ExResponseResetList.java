package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_RESPONSE_RESET_LIST);

        writeLong(_activeChar.getAdena());
        writeLong(_activeChar.getBeautyTickets());

        writeInt(_activeChar.getAppearance().getHairStyle());
        writeInt(_activeChar.getAppearance().getHairColor());
        writeInt(_activeChar.getAppearance().getFace());
    }

}

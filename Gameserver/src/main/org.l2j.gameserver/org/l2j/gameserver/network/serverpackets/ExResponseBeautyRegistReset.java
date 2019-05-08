package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExResponseBeautyRegistReset extends IClientOutgoingPacket {
    public static final int FAILURE = 0;
    public static final int SUCCESS = 1;
    public static final int CHANGE = 0;
    public static final int RESTORE = 1;
    private final L2PcInstance _activeChar;
    private final int _type;
    private final int _result;

    public ExResponseBeautyRegistReset(L2PcInstance activeChar, int type, int result) {
        _activeChar = activeChar;
        _type = type;
        _result = result;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_RESPONSE_BEAUTY_REGIST_RESET.writeId(packet);

        packet.putLong(_activeChar.getAdena());
        packet.putLong(_activeChar.getBeautyTickets());
        packet.putInt(_type);
        packet.putInt(_result);
        packet.putInt(_activeChar.getVisualHair());
        packet.putInt(_activeChar.getVisualFace());
        packet.putInt(_activeChar.getVisualHairColor());
    }

    @Override
    protected int size(L2GameClient client) {
        return 41;
    }
}

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExShowBeautyMenu extends IClientOutgoingPacket {
    // TODO: Enum
    public static final int MODIFY_APPEARANCE = 0;
    public static final int RESTORE_APPEARANCE = 1;
    private final L2PcInstance _activeChar;
    private final int _type;

    public ExShowBeautyMenu(L2PcInstance activeChar, int type) {
        _activeChar = activeChar;
        _type = type;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_SHOW_BEAUTY_MENU);

        writeInt(_type);
        writeInt(_activeChar.getVisualHair());
        writeInt(_activeChar.getVisualHairColor());
        writeInt(_activeChar.getVisualFace());
    }

}
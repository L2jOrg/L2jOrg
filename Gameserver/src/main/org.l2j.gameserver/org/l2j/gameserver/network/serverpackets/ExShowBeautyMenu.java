package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author Sdw
 */
public class ExShowBeautyMenu extends ServerPacket {
    // TODO: Enum
    public static final int MODIFY_APPEARANCE = 0;
    public static final int RESTORE_APPEARANCE = 1;
    private final Player _activeChar;
    private final int _type;

    public ExShowBeautyMenu(Player activeChar, int type) {
        _activeChar = activeChar;
        _type = type;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_BEAUTY_MENU);

        writeInt(_type);
        writeInt(_activeChar.getVisualHair());
        writeInt(_activeChar.getVisualHairColor());
        writeInt(_activeChar.getVisualFace());
    }

}
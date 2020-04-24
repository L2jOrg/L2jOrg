package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author Sdw
 */
public class ExResponseResetList extends ServerPacket {
    private final Player _activeChar;

    public ExResponseResetList(Player activeChar) {
        _activeChar = activeChar;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_RESPONSE_RESET_LIST);

        writeLong(_activeChar.getAdena());
        writeLong(_activeChar.getBeautyTickets());

        writeInt(_activeChar.getAppearance().getHairStyle());
        writeInt(_activeChar.getAppearance().getHairColor());
        writeInt(_activeChar.getAppearance().getFace());
    }

}

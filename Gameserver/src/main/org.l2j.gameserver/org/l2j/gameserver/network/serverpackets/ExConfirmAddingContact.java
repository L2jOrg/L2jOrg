package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author mrTJO & UnAfraid
 */
public class ExConfirmAddingContact extends ServerPacket {
    private final String _charName;
    private final boolean _added;

    public ExConfirmAddingContact(String charName, boolean added) {
        _charName = charName;
        _added = added;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_AGIT_AUCTION_CMD);

        writeString(_charName);
        writeInt(_added ? 0x01 : 0x00);
    }

}

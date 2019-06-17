package org.l2j.gameserver.network.serverpackets.compound;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExEnchantSucess extends ServerPacket {
    private final int _itemId;

    public ExEnchantSucess(int itemId) {
        _itemId = itemId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_ENCHANT_SUCESS);

        writeInt(_itemId);
    }

}

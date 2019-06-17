package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class RecipeShopMsg extends ServerPacket {
    private final L2PcInstance _activeChar;

    public RecipeShopMsg(L2PcInstance player) {
        _activeChar = player;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.RECIPE_SHOP_MSG);

        writeInt(_activeChar.getObjectId());
        writeString(_activeChar.getStoreName());
    }

}

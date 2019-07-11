package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class RecipeShopMsg extends ServerPacket {
    private final Player _activeChar;

    public RecipeShopMsg(Player player) {
        _activeChar = player;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.RECIPE_SHOP_MSG);

        writeInt(_activeChar.getObjectId());
        writeString(_activeChar.getStoreName());
    }

}

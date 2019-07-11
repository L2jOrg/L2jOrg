package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class RecipeShopItemInfo extends ServerPacket {
    private final Player _player;
    private final int _recipeId;

    public RecipeShopItemInfo(Player player, int recipeId) {
        _player = player;
        _recipeId = recipeId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.RECIPE_SHOP_ITEM_INFO);

        writeInt(_player.getObjectId());
        writeInt(_recipeId);
        writeInt((int) _player.getCurrentMp());
        writeInt(_player.getMaxMp());
        writeInt(0xffffffff);
        writeLong(0x00);
        writeByte((byte) 0x00); // Trigger offering window if 1
        writeLong(0x00);
    }

}

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class RecipeShopItemInfo extends IClientOutgoingPacket {
    private final L2PcInstance _player;
    private final int _recipeId;

    public RecipeShopItemInfo(L2PcInstance player, int recipeId) {
        _player = player;
        _recipeId = recipeId;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.RECIPE_SHOP_ITEM_INFO);

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

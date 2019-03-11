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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.RECIPE_SHOP_ITEM_INFO.writeId(packet);

        packet.putInt(_player.getObjectId());
        packet.putInt(_recipeId);
        packet.putInt((int) _player.getCurrentMp());
        packet.putInt(_player.getMaxMp());
        packet.putInt(0xffffffff);
        packet.putLong(0x00);
        packet.put((byte) 0x00); // Trigger offering window if 1
        packet.putLong(0x00);
    }
}

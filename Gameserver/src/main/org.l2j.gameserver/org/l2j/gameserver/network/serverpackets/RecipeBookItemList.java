package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2RecipeList;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class RecipeBookItemList extends IClientOutgoingPacket {
    private final boolean _isDwarvenCraft;
    private final int _maxMp;
    private L2RecipeList[] _recipes;

    public RecipeBookItemList(boolean isDwarvenCraft, int maxMp) {
        _isDwarvenCraft = isDwarvenCraft;
        _maxMp = maxMp;
    }

    public void addRecipes(L2RecipeList[] recipeBook) {
        _recipes = recipeBook;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.RECIPE_BOOK_ITEM_LIST.writeId(packet);

        packet.putInt(_isDwarvenCraft ? 0x00 : 0x01); // 0 = Dwarven - 1 = Common
        packet.putInt(_maxMp);

        if (_recipes == null) {
            packet.putInt(0);
        } else {
            packet.putInt(_recipes.length); // number of items in recipe book
            for (int i = 0; i < _recipes.length; i++) {
                packet.putInt(_recipes[i].getId());
                packet.putInt(i + 1);
            }
        }
    }
}

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2RecipeList;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

import static java.util.Objects.nonNull;

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
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.RECIPE_BOOK_ITEM_LIST);

        writeInt(_isDwarvenCraft ? 0x00 : 0x01); // 0 = Dwarven - 1 = Common
        writeInt(_maxMp);

        if (_recipes == null) {
            writeInt(0);
        } else {
            writeInt(_recipes.length); // number of items in recipe book
            for (int i = 0; i < _recipes.length; i++) {
                writeInt(_recipes[i].getId());
                writeInt(i + 1);
            }
        }
    }

}

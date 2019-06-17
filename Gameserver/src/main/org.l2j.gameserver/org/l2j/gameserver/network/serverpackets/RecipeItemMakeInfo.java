package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.model.L2RecipeList;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeItemMakeInfo extends ServerPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecipeItemMakeInfo.class);

    private final int _id;
    private final L2PcInstance _activeChar;
    private final boolean _success;

    public RecipeItemMakeInfo(int id, L2PcInstance player, boolean success) {
        _id = id;
        _activeChar = player;
        _success = success;
    }

    public RecipeItemMakeInfo(int id, L2PcInstance player) {
        _id = id;
        _activeChar = player;
        _success = true;
    }

    @Override
    public void writeImpl(L2GameClient client) throws InvalidDataPacketException {
        final L2RecipeList recipe = RecipeData.getInstance().getRecipeList(_id);
        if (recipe != null) {
            writeId(ServerPacketId.RECIPE_ITEM_MAKE_INFO);
            writeInt(_id);
            writeInt(recipe.isDwarvenRecipe() ? 0 : 1); // 0 = Dwarven - 1 = Common
            writeInt((int) _activeChar.getCurrentMp());
            writeInt(_activeChar.getMaxMp());
            writeInt(_success ? 1 : 0); // item creation success/failed
            writeByte((byte) 0x00);
            writeLong(0x00);
        } else {
            LOGGER.info("Character: " + _activeChar + ": Requested unexisting recipe with id = " + _id);
            throw new InvalidDataPacketException();
        }
    }

}

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.model.L2RecipeList;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class RecipeItemMakeInfo extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) throws InvalidDataPacketException {
        final L2RecipeList recipe = RecipeData.getInstance().getRecipeList(_id);
        if (recipe != null) {
            OutgoingPackets.RECIPE_ITEM_MAKE_INFO.writeId(packet);
            packet.putInt(_id);
            packet.putInt(recipe.isDwarvenRecipe() ? 0 : 1); // 0 = Dwarven - 1 = Common
            packet.putInt((int) _activeChar.getCurrentMp());
            packet.putInt(_activeChar.getMaxMp());
            packet.putInt(_success ? 1 : 0); // item creation success/failed
            packet.put((byte) 0x00);
            packet.putLong(0x00);
        } else {
            LOGGER.info("Character: " + _activeChar + ": Requested unexisting recipe with id = " + _id);
            throw new InvalidDataPacketException();
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 35;
    }
}

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2ManufactureItem;
import org.l2j.gameserver.model.L2RecipeList;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Iterator;

import static java.util.Objects.nonNull;

public class RecipeShopManageList extends IClientOutgoingPacket {
    private final L2PcInstance _seller;
    private final boolean _isDwarven;
    private L2RecipeList[] _recipes;

    public RecipeShopManageList(L2PcInstance seller, boolean isDwarven) {
        _seller = seller;
        _isDwarven = isDwarven;

        if (_isDwarven && _seller.hasDwarvenCraft()) {
            _recipes = _seller.getDwarvenRecipeBook();
        } else {
            _recipes = _seller.getCommonRecipeBook();
        }

        if (_seller.hasManufactureShop()) {
            final Iterator<L2ManufactureItem> it = _seller.getManufactureItems().values().iterator();
            L2ManufactureItem item;
            while (it.hasNext()) {
                item = it.next();
                if ((item.isDwarven() != _isDwarven) || !seller.hasRecipeList(item.getRecipeId())) {
                    it.remove();
                }
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.RECIPE_SHOP_MANAGE_LIST.writeId(packet);

        packet.putInt(_seller.getObjectId());
        packet.putInt((int) _seller.getAdena());
        packet.putInt(_isDwarven ? 0x00 : 0x01);

        if (_recipes == null) {
            packet.putInt(0);
        } else {
            packet.putInt(_recipes.length); // number of items in recipe book

            for (int i = 0; i < _recipes.length; i++) {
                final L2RecipeList temp = _recipes[i];
                packet.putInt(temp.getId());
                packet.putInt(i + 1);
            }
        }

        if (!_seller.hasManufactureShop()) {
            packet.putInt(0x00);
        } else {
            packet.putInt(_seller.getManufactureItems().size());
            for (L2ManufactureItem item : _seller.getManufactureItems().values()) {
                packet.putInt(item.getRecipeId());
                packet.putInt(0x00);
                packet.putLong(item.getCost());
            }
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 20 + (nonNull(_recipes) ? _recipes.length * 8 : 4) + (_seller.hasManufactureShop() ? _seller.getManufactureItems().size() * 16 : 4);
    }
}

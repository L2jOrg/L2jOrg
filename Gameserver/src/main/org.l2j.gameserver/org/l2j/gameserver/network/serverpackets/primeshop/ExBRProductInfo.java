package org.l2j.gameserver.network.serverpackets.primeshop;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Gnacik
 */
public class ExBRProductInfo extends ServerPacket {
    private final PrimeShopProduct item;
    private final int points;
    private final long adenas;
    private final long coins;

    public ExBRProductInfo(PrimeShopProduct item, Player player) {
        this.item = item;
        points = player.getL2Coins();
        adenas = player.getAdena();
        coins = player.getInventory().getInventoryItemCount(23805, -1);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_BR_PRODUCT_INFO);

        writeInt(item.getId());
        writeInt(item.getPrice());
        writeInt(item.getItems().size());
        for (PrimeShopItem item : item.getItems()) {
            writeInt(item.getId());
            writeInt((int) item.getCount());
            writeInt(item.getWeight());
            writeInt(item.isTradable());
        }
        writeLong(adenas);
        writeLong(points);
        writeLong(coins);
    }

}

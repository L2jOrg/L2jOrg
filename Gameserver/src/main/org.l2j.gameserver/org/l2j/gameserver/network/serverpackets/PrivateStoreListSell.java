package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.SellBuffsManager;
import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class PrivateStoreListSell extends AbstractItemPacket {
    private final L2PcInstance _player;
    private final L2PcInstance _seller;

    public PrivateStoreListSell(L2PcInstance player, L2PcInstance seller) {
        _player = player;
        _seller = seller;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        if (_seller.isSellingBuffs()) {
            SellBuffsManager.getInstance().sendBuffMenu(_player, _seller, 0);
        } else {
            writeId(ServerPacketId.PRIVATE_STORE_LIST);

            writeInt(_seller.getObjectId());
            writeInt(_seller.getSellList().isPackaged() ? 1 : 0);
            writeLong(_player.getAdena());
            writeInt(0x00);
            writeInt(_seller.getSellList().getItems().length);
            for (TradeItem item : _seller.getSellList().getItems()) {
                writeItem(item);
                writeLong(item.getPrice());
                writeLong(item.getItem().getReferencePrice() * 2);
            }
        }
    }

}

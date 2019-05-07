package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.SellBuffsManager;
import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class PrivateStoreListSell extends AbstractItemPacket {
    private final L2PcInstance _player;
    private final L2PcInstance _seller;

    public PrivateStoreListSell(L2PcInstance player, L2PcInstance seller) {
        _player = player;
        _seller = seller;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        if (_seller.isSellingBuffs()) {
            SellBuffsManager.getInstance().sendBuffMenu(_player, _seller, 0);
        } else {
            OutgoingPackets.PRIVATE_STORE_LIST.writeId(packet);

            packet.putInt(_seller.getObjectId());
            packet.putInt(_seller.getSellList().isPackaged() ? 1 : 0);
            packet.putLong(_player.getAdena());
            packet.putInt(0x00);
            packet.putInt(_seller.getSellList().getItems().length);
            for (TradeItem item : _seller.getSellList().getItems()) {
                writeItem(packet, item);
                packet.putLong(item.getPrice());
                packet.putLong(item.getItem().getReferencePrice() * 2);
            }
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 29 * _seller.getSellList().getItems().length * 120;
    }
}

package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author ShanSoft
 */
public class ExBuySellList extends AbstractItemPacket {
    private final boolean _done;
    private final int _inventorySlots;
    private Collection<L2ItemInstance> _sellList;
    private Collection<L2ItemInstance> _refundList = null;
    private double _castleTaxRate = 1;

    public ExBuySellList(L2PcInstance player, boolean done) {
        final L2Summon pet = player.getPet();
        _sellList = player.getInventory().getItems(item -> !item.isEquipped() && item.isSellable() && ((pet == null) || (item.getObjectId() != pet.getControlObjectId())));
        _inventorySlots = player.getInventory().getItems((item) -> !item.isQuestItem()).size();
        if (player.hasRefund()) {
            _refundList = player.getRefund().getItems();
        }
        _done = done;
    }

    public ExBuySellList(L2PcInstance player, boolean done, double castleTaxRate) {
        this(player, done);
        _castleTaxRate = 1 - castleTaxRate;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_BUY_SELL_LIST.writeId(packet);

        packet.putInt(0x01); // Type SELL
        packet.putInt(_inventorySlots);

        if ((_sellList != null)) {
            packet.putShort((short) _sellList.size());
            for (L2ItemInstance item : _sellList) {
                writeItem(packet, item);
                packet.putLong((long) ((item.getItem().getReferencePrice() / 2) * _castleTaxRate));
            }
        } else {
            packet.putShort((short) 0x00);
        }

        if ((_refundList != null) && !_refundList.isEmpty()) {
            packet.putShort((short) _refundList.size());
            int i = 0;
            for (L2ItemInstance item : _refundList) {
                writeItem(packet, item);
                packet.putInt(i++);
                packet.putLong((item.getItem().getReferencePrice() / 2) * item.getCount());
            }
        } else {
            packet.putShort((short) 0x00);
        }

        packet.put((byte)( _done ? 0x01 : 0x00));
    }
}

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

/**
 * @author ShanSoft
 */
public class ExBuySellList extends AbstractItemPacket {
    private final boolean _done;
    private final int _inventorySlots;
    private Collection<Item> _sellList;
    private Collection<Item> _refundList = null;
    private double _castleTaxRate = 1;

    public ExBuySellList(Player player, boolean done) {
        final Summon pet = player.getPet();
        _sellList = player.getInventory().getItems(item -> !item.isEquipped() && item.isSellable() && ((pet == null) || (item.getObjectId() != pet.getControlObjectId())));
        _inventorySlots = player.getInventory().getItems((item) -> !item.isQuestItem()).size();
        if (player.hasRefund()) {
            _refundList = player.getRefund().getItems();
        }
        _done = done;
    }

    public ExBuySellList(Player player, boolean done, double castleTaxRate) {
        this(player, done);
        _castleTaxRate = 1 - castleTaxRate;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_BUY_SELL_LIST);

        writeInt(0x01); // Type SELL
        writeInt(_inventorySlots);

        if ((_sellList != null)) {
            writeShort((short) _sellList.size());
            for (Item item : _sellList) {
                writeItem(item);
                writeLong((long) ((item.getTemplate().getReferencePrice() / 2) * _castleTaxRate));
            }
        } else {
            writeShort((short) 0x00);
        }

        if ((_refundList != null) && !_refundList.isEmpty()) {
            writeShort((short) _refundList.size());
            int i = 0;
            for (Item item : _refundList) {
                writeItem(item);
                writeInt(i++);
                writeLong((item.getTemplate().getReferencePrice() / 2) * item.getCount());
            }
        } else {
            writeShort((short) 0x00);
        }
        writeByte((byte)( _done ? 0x01 : 0x00));
    }

}

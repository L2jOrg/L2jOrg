package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class WareHouseWithdrawalList extends AbstractItemPacket {
    public static final int PRIVATE = 1;
    public static final int CLAN = 2;
    public static final int CASTLE = 3; // not sure
    public static final int FREIGHT = 1;
    private final int _sendType;
    private final int _invSize;
    private final List<Integer> _itemsStackable = new ArrayList<>();
    private L2PcInstance _activeChar;
    private long _playerAdena;
    private Collection<L2ItemInstance> _items;
    /**
     * <ul>
     * <li>0x01-Private Warehouse</li>
     * <li>0x02-Clan Warehouse</li>
     * <li>0x03-Castle Warehouse</li>
     * <li>0x04-Warehouse</li>
     * </ul>
     */
    private int _whType;

    public WareHouseWithdrawalList(int sendType, L2PcInstance player, int type) {
        _sendType = sendType;
        _activeChar = player;
        _whType = type;

        _playerAdena = _activeChar.getAdena();
        _invSize = player.getInventory().getSize();
        if (_activeChar.getActiveWarehouse() == null) {
            LOGGER.warn("error while sending withdraw request to: " + _activeChar.getName());
            return;
        }

        _items = _activeChar.getActiveWarehouse().getItems();

        for (L2ItemInstance item : _items) {
            if (item.isStackable()) {
                _itemsStackable.add(item.getDisplayId());
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.WAREHOUSE_WITHDRAW_LIST.writeId(packet);
        packet.put((byte) _sendType);
        if (_sendType == 2) {
            packet.putShort((short) 0x00);
            packet.putInt(_invSize);
            packet.putInt(_items.size());
            for (L2ItemInstance item : _items) {
                writeItem(packet, item);
                packet.putInt(item.getObjectId());
                packet.putInt(0x00);
                packet.putInt(0x00);
            }
        } else {
            packet.putShort((short) _whType);
            packet.putLong(_playerAdena);
            packet.putInt(_invSize);
            packet.putInt(_items.size());
        }
    }
}

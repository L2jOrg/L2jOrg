package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

public final class TradeStart extends AbstractItemPacket {
    private final int _sendType;
    private final L2PcInstance _activeChar;
    private final L2PcInstance _partner;
    private final Collection<L2ItemInstance> _itemList;
    private int _mask = 0;

    public TradeStart(int sendType, L2PcInstance player) {
        _sendType = sendType;
        _activeChar = player;
        _partner = player.getActiveTradeList().getPartner();
        _itemList = _activeChar.getInventory().getAvailableItems(true, (_activeChar.canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && Config.GM_TRADE_RESTRICTED_ITEMS), false);

        if (_partner != null) {
            if (player.getFriendList().contains(_partner.getObjectId())) {
                _mask |= 0x01;
            }
            if ((player.getClanId() > 0) && (_partner.getClanId() == _partner.getClanId())) {
                _mask |= 0x02;
            }
            if ((MentorManager.getInstance().getMentee(player.getObjectId(), _partner.getObjectId()) != null) || (MentorManager.getInstance().getMentee(_partner.getObjectId(), player.getObjectId()) != null)) {
                _mask |= 0x04;
            }
            if ((player.getAllyId() > 0) && (player.getAllyId() == _partner.getAllyId())) {
                _mask |= 0x08;
            }

            // Does not shows level
            if (_partner.isGM()) {
                _mask |= 0x10;
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) throws InvalidDataPacketException {
        if ((_activeChar.getActiveTradeList() == null) || (_partner == null)) {
            throw new InvalidDataPacketException();
        }

        OutgoingPackets.TRADE_START.writeId(packet);
        packet.put((byte) _sendType);
        if (_sendType == 2) {
            packet.putInt(_itemList.size());
            packet.putInt(_itemList.size());
            for (L2ItemInstance item : _itemList) {
                writeItem(packet, item);
            }
        } else {
            packet.putInt(_partner.getObjectId());
            packet.put((byte) _mask); // some kind of mask
            if ((_mask & 0x10) == 0) {
                packet.put((byte) _partner.getLevel());
            }
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 14 + (_sendType == 2 ? _itemList.size() * 100 : 0);
    }
}

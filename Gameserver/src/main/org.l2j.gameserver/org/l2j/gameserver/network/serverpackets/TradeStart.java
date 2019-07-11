package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

public final class TradeStart extends AbstractItemPacket {
    private final int _sendType;
    private final Player _activeChar;
    private final Player _partner;
    private final Collection<L2ItemInstance> _itemList;
    private int _mask = 0;

    public TradeStart(int sendType, Player player) {
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
    public void writeImpl(L2GameClient client) throws InvalidDataPacketException {
        if ((_activeChar.getActiveTradeList() == null) || (_partner == null)) {
            throw new InvalidDataPacketException();
        }

        writeId(ServerPacketId.TRADE_START);
        writeByte((byte) _sendType);
        if (_sendType == 2) {
            writeInt(_itemList.size());
            writeInt(_itemList.size());
            for (L2ItemInstance item : _itemList) {
                writeItem(item);
            }
        } else {
            writeInt(_partner.getObjectId());
            writeByte((byte) _mask); // some kind of mask
            if ((_mask & 0x10) == 0) {
                writeByte((byte) _partner.getLevel());
            }
        }
    }

}

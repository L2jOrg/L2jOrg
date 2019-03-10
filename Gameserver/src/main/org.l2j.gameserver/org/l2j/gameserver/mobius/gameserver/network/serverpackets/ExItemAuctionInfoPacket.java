package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.itemauction.ItemAuction;
import org.l2j.gameserver.mobius.gameserver.model.itemauction.ItemAuctionBid;
import org.l2j.gameserver.mobius.gameserver.model.itemauction.ItemAuctionState;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Forsaiken
 */
public final class ExItemAuctionInfoPacket extends AbstractItemPacket {
    private final boolean _refresh;
    private final int _timeRemaining;
    private final ItemAuction _currentAuction;
    private final ItemAuction _nextAuction;

    public ExItemAuctionInfoPacket(boolean refresh, ItemAuction currentAuction, ItemAuction nextAuction) {
        if (currentAuction == null) {
            throw new NullPointerException();
        }

        if (currentAuction.getAuctionState() != ItemAuctionState.STARTED) {
            _timeRemaining = 0;
        } else {
            _timeRemaining = (int) (currentAuction.getFinishingTimeRemaining() / 1000); // in seconds
        }

        _refresh = refresh;
        _currentAuction = currentAuction;
        _nextAuction = nextAuction;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ITEM_AUCTION_INFO.writeId(packet);

        packet.put((byte) (_refresh ? 0x00 : 0x01));
        packet.putInt(_currentAuction.getInstanceId());

        final ItemAuctionBid highestBid = _currentAuction.getHighestBid();
        packet.putLong(highestBid != null ? highestBid.getLastBid() : _currentAuction.getAuctionInitBid());

        packet.putInt(_timeRemaining);
        writeItem(packet, _currentAuction.getItemInfo());

        if (_nextAuction != null) {
            packet.putLong(_nextAuction.getAuctionInitBid());
            packet.putInt((int) (_nextAuction.getStartingTime() / 1000)); // unix time in seconds
            writeItem(packet, _nextAuction.getItemInfo());
        }
    }
}

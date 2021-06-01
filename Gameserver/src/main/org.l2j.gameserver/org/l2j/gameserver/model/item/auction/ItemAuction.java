/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.item.auction;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.data.database.data.ItemAuctionBid;
import org.l2j.gameserver.data.database.data.ItemAuctionData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Forsaiken
 */
public final class ItemAuction {
    private static final long ENDING_TIME_EXTEND_5 = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
    private static final long ENDING_TIME_EXTEND_3 = TimeUnit.MILLISECONDS.convert(3, TimeUnit.MINUTES);

    private final ItemAuctionData data;
    private final AuctionItem _auctionItem;
    private final List<ItemAuctionBid> auctionBids;
    private final ItemInfo _itemInfo;
    private volatile ItemAuctionExtendState _scheduledAuctionEndingExtendState;
    private volatile ItemAuctionExtendState _auctionEndingExtendState;
    private ItemAuctionBid _highestBid;
    private int _lastBidPlayerObjId;

    public ItemAuction(ItemAuctionData data, AuctionItem auctionItem, List<ItemAuctionBid> auctionBids) {
        this.data = data;
        this.auctionBids = auctionBids;
        this._auctionItem = auctionItem;
        _scheduledAuctionEndingExtendState = ItemAuctionExtendState.INITIAL;
        _auctionEndingExtendState = ItemAuctionExtendState.INITIAL;

        final Item item = _auctionItem.createNewItemInstance();
        _itemInfo = new ItemInfo(item);
        World.getInstance().removeObject(item);

        for (ItemAuctionBid bid : this.auctionBids) {
            if ((_highestBid == null) || (_highestBid.getLastBid() < bid.getLastBid())) {
                _highestBid = bid;
            }
        }
    }

    public ItemAuction(int auctionId, int instanceId, long startingTime, long endingTime, AuctionItem auctionItem) {
        this(ItemAuctionData.of(auctionId, instanceId, auctionItem.getAuctionItemId(), startingTime, endingTime, ItemAuctionState.CREATED), auctionItem, new ArrayList<>());
    }

    public final ItemAuctionState getAuctionState() {
        return data.getAuctionState();
    }

    public final boolean setAuctionState(ItemAuctionState wanted) {
        data.setAuctionState(wanted);
        storeMe();
        return true;
    }

    public final int getAuctionId() {
        return data.getAuction();
    }

    public final int getInstanceId() {
        return data.getInstance();
    }

    public final ItemInfo getItemInfo() {
        return _itemInfo;
    }

    public final Item createNewItemInstance() {
        return _auctionItem.createNewItemInstance();
    }

    public final long getAuctionInitBid() {
        return _auctionItem.getAuctionInitBid();
    }

    public final ItemAuctionBid getHighestBid() {
        return _highestBid;
    }

    public final ItemAuctionExtendState getAuctionEndingExtendState() {
        return _auctionEndingExtendState;
    }

    public final ItemAuctionExtendState getScheduledAuctionEndingExtendState() {
        return _scheduledAuctionEndingExtendState;
    }

    public final void setScheduledAuctionEndingExtendState(ItemAuctionExtendState state) {
        _scheduledAuctionEndingExtendState = state;
    }

    public final long getStartingTime() {
        return data.getStartingTime();
    }

    public final long getEndingTime() {
        return data.getEndingTime();
    }

    public final long getFinishingTimeRemaining() {
        return Math.max(data.getEndingTime() - System.currentTimeMillis(), 0);
    }

    public final void storeMe() {
        getDAO(ItemDAO.class).save(data);
    }

    public final int getAndSetLastBidPlayerObjectId(int playerObjId) {
        final int lastBid = _lastBidPlayerObjId;
        _lastBidPlayerObjId = playerObjId;
        return lastBid;
    }

    private void updatePlayerBid(ItemAuctionBid bid, boolean delete) {
        // TODO nBd maybe move such stuff to you db updater :D
        updatePlayerBidInternal(bid, delete);
    }

    final void updatePlayerBidInternal(ItemAuctionBid bid, boolean delete) {
        if(delete) {
            getDAO(ItemDAO.class).deleteItemAuctionBid(data.getAuction(), bid.getPlayerObjId());
        } else {
            getDAO(ItemDAO.class).save(bid);
        }
    }

    public final void registerBid(Player player, long newBid) {
        if (player == null) {
            throw new NullPointerException();
        }

        if (newBid < _auctionItem.getAuctionInitBid()) {
            player.sendPacket(SystemMessageId.YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID);
            return;
        }

        if (newBid > 100000000000L) {
            player.sendPacket(SystemMessageId.THE_HIGHEST_BID_IS_OVER_999_9_BILLION_THEREFORE_YOU_CANNOT_PLACE_A_BID);
            return;
        }

        if (getAuctionState() != ItemAuctionState.STARTED) {
            return;
        }

        final int playerId = player.getObjectId();

        synchronized (auctionBids) {
            if ((_highestBid != null) && (newBid < _highestBid.getLastBid())) {
                player.sendPacket(SystemMessageId.YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID);
                return;
            }

            ItemAuctionBid bid = getBidFor(playerId);
            if (bid == null) {
                if (!reduceItemCount(player, newBid)) {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
                    return;
                }

                bid = new ItemAuctionBid(data.getAuction(), playerId, newBid);
                auctionBids.add(bid);
            } else {
                if (!bid.isCanceled()) {
                    if (newBid < bid.getLastBid()) // just another check
                    {
                        player.sendPacket(SystemMessageId.YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID);
                        return;
                    }

                    if (!reduceItemCount(player, newBid - bid.getLastBid())) {
                        player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
                        return;
                    }
                } else if (!reduceItemCount(player, newBid)) {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
                    return;
                }

                bid.setLastBid(newBid);
            }

            onPlayerBid(player, bid);
            updatePlayerBid(bid, false);

            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_SUBMITTED_A_BID_FOR_THE_AUCTION_OF_S1);
            sm.addLong(newBid);
            player.sendPacket(sm);
        }
    }

    private void onPlayerBid(Player player, ItemAuctionBid bid) {
        if (_highestBid == null) {
            _highestBid = bid;
        } else if (_highestBid.getLastBid() < bid.getLastBid()) {
            final Player old = _highestBid.getPlayer();
            if (old != null) {
                old.sendPacket(SystemMessageId.YOU_WERE_OUTBID_THE_NEW_HIGHEST_BID_IS_S1_ADENA);
            }

            _highestBid = bid;
        }

        if ((data.getEndingTime() - System.currentTimeMillis()) <= (1000 * 60 * 10)) // 10 minutes
        {
            switch (_auctionEndingExtendState) {
                case INITIAL: {
                    _auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_5_MIN;
                    data.updateEndingTime(ENDING_TIME_EXTEND_5);
                    broadcastToAllBidders(SystemMessage.getSystemMessage(SystemMessageId.BIDDER_EXISTS_THE_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_5_MINUTES));
                    break;
                }
                case EXTEND_BY_5_MIN: {
                    if (getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId()) {
                        _auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_3_MIN;
                       data.updateEndingTime(ENDING_TIME_EXTEND_3);
                        broadcastToAllBidders(SystemMessage.getSystemMessage(SystemMessageId.BIDDER_EXISTS_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_3_MINUTES));
                    }
                    break;
                }
                case EXTEND_BY_3_MIN: {
                    if (Config.ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID > 0) {
                        if (getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId()) {
                            _auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A;
                            data.updateEndingTime(Config.ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID);
                        }
                    }
                    break;
                }
                case EXTEND_BY_CONFIG_PHASE_A: {
                    if (getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId()) {
                        if (_scheduledAuctionEndingExtendState == ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B) {
                            _auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B;
                            data.updateEndingTime(Config.ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID);
                        }
                    }
                    break;
                }
                case EXTEND_BY_CONFIG_PHASE_B: {
                    if (getAndSetLastBidPlayerObjectId(player.getObjectId()) != player.getObjectId()) {
                        if (_scheduledAuctionEndingExtendState == ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A) {
                            data.updateEndingTime(Config.ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID);
                            _auctionEndingExtendState = ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A;
                        }
                    }
                }
            }
        }
    }

    public final void broadcastToAllBidders(ServerPacket packet) {
        ThreadPool.execute(() -> broadcastToAllBiddersInternal(packet));
    }

    public final void broadcastToAllBiddersInternal(ServerPacket packet) {
        for (int i = auctionBids.size(); i-- > 0; ) {
            final ItemAuctionBid bid = auctionBids.get(i);
            if (bid != null) {
                final Player player = bid.getPlayer();
                if (player != null) {
                    player.sendPacket(packet);
                }
            }
        }
    }

    public final boolean cancelBid(Player player) {
        if (player == null) {
            throw new NullPointerException();
        }

        switch (getAuctionState()) {
            case CREATED: {
                return false;
            }
            case FINISHED: {
                if (data.getStartingTime() < (System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(Config.ALT_ITEM_AUCTION_EXPIRED_AFTER, TimeUnit.DAYS))) {
                    return false;
                }
                break;
            }
        }

        final int playerObjId = player.getObjectId();

        synchronized (auctionBids) {
            if (_highestBid == null) {
                return false;
            }

            final int bidIndex = getBidIndexFor(playerObjId);
            if (bidIndex == -1) {
                return false;
            }

            final ItemAuctionBid bid = auctionBids.get(bidIndex);
            if (bid.getPlayerObjId() == _highestBid.getPlayerObjId()) {
                // can't return winning bid
                if (getAuctionState() == ItemAuctionState.FINISHED) {
                    return false;
                }

                player.sendPacket(SystemMessageId.YOU_CURRENTLY_HAVE_THE_HIGHEST_BID_BUT_THE_RESERVE_HAS_NOT_BEEN_MET);
                return true;
            }

            if (bid.isCanceled()) {
                return false;
            }

            increaseItemCount(player, bid.getLastBid());
            bid.cancelBid();

            // delete bid from database if auction already finished
            updatePlayerBid(bid, getAuctionState() == ItemAuctionState.FINISHED);

            player.sendPacket(SystemMessageId.YOU_HAVE_CANCELED_YOUR_BID);
        }
        return true;
    }

    public final void clearCanceledBids() {
        if (getAuctionState() != ItemAuctionState.FINISHED) {
            throw new IllegalStateException("Attempt to clear canceled bids for non-finished auction");
        }

        synchronized (auctionBids) {
            for (ItemAuctionBid bid : auctionBids) {
                if ((bid == null) || !bid.isCanceled()) {
                    continue;
                }
                updatePlayerBid(bid, true);
            }
        }
    }

    private boolean reduceItemCount(Player player, long count) {
        if (!player.reduceAdena("ItemAuction", count, player, true)) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID);
            return false;
        }
        return true;
    }

    private void increaseItemCount(Player player, long count) {
        player.addAdena("ItemAuction", count, player, true);
    }

    public final ItemAuctionBid getBidFor(int playerObjId) {
        final int index = getBidIndexFor(playerObjId);
        return index != -1 ? auctionBids.get(index) : null;
    }

    private int getBidIndexFor(int playerObjId) {
        for (int i = auctionBids.size(); i-- > 0; ) {
            final ItemAuctionBid bid = auctionBids.get(i);
            if ((bid != null) && (bid.getPlayerObjId() == playerObjId)) {
                return i;
            }
        }
        return -1;
    }
}

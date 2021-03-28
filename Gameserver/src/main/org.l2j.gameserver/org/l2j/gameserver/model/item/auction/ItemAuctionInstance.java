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

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.data.database.data.ItemAuctionBid;
import org.l2j.gameserver.data.database.data.ItemAuctionData;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author JoeAlisson
 */
public final class ItemAuctionInstance {
    public static final String AUCTION_HAS_FINISHED_MSG = "Auction {} has finished. Highest bid by {} for instance {}";
    protected static final Logger LOGGER = LoggerFactory.getLogger(ItemAuctionInstance.class);
    private static final long START_TIME_SPACE = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
    private static final long FINISH_TIME_SPACE = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);

    private final int instanceId;
    private final AtomicInteger _auctionIds;
    private final IntMap<ItemAuction> auctions;
    private final ArrayList<AuctionItem> _items;
    private final AuctionDateGenerator _dateGenerator;

    private ItemAuction _currentAuction;
    private ItemAuction _nextAuction;
    private ScheduledFuture<?> _stateTask;

    public ItemAuctionInstance(int instanceId, AtomicInteger auctionIds, Node node) throws Exception {
        this.instanceId = instanceId;
        _auctionIds = auctionIds;
        auctions = new HashIntMap<>();
        _items = new ArrayList<>();

        final NamedNodeMap nanode = node.getAttributes();
        final StatsSet generatorConfig = new StatsSet();
        for (int i = nanode.getLength(); i-- > 0; ) {
            final Node n = nanode.item(i);
            if (n != null) {
                generatorConfig.set(n.getNodeName(), n.getNodeValue());
            }
        }

        _dateGenerator = new AuctionDateGenerator(generatorConfig);

        for (Node na = node.getFirstChild(); na != null; na = na.getNextSibling()) {
            try {
                if ("item".equalsIgnoreCase(na.getNodeName())) {
                    final NamedNodeMap naa = na.getAttributes();
                    final int auctionItemId = Integer.parseInt(naa.getNamedItem("auctionItemId").getNodeValue());
                    final int auctionLenght = Integer.parseInt(naa.getNamedItem("auctionLenght").getNodeValue());
                    final long auctionInitBid = Integer.parseInt(naa.getNamedItem("auctionInitBid").getNodeValue());

                    final int itemId = Integer.parseInt(naa.getNamedItem("itemId").getNodeValue());
                    final int itemCount = Integer.parseInt(naa.getNamedItem("itemCount").getNodeValue());

                    if (auctionLenght < 1) {
                        throw new IllegalArgumentException("auctionLenght < 1 for instanceId: " + this.instanceId + ", itemId " + itemId);
                    }

                    final StatsSet itemExtra = new StatsSet();
                    final AuctionItem item = new AuctionItem(auctionItemId, auctionLenght, auctionInitBid, itemId, itemCount, itemExtra);

                    if (!item.checkItemExists()) {
                        throw new IllegalArgumentException("Item with id " + itemId + " not found");
                    }

                    for (AuctionItem tmp : _items) {
                        if (tmp.getAuctionItemId() == auctionItemId) {
                            throw new IllegalArgumentException("Dublicated auction item id " + auctionItemId);
                        }
                    }

                    _items.add(item);

                    for (Node nb = na.getFirstChild(); nb != null; nb = nb.getNextSibling()) {
                        if ("extra".equalsIgnoreCase(nb.getNodeName())) {
                            final NamedNodeMap nab = nb.getAttributes();
                            for (int i = nab.getLength(); i-- > 0; ) {
                                final Node n = nab.item(i);
                                if (n != null) {
                                    itemExtra.set(n.getNodeName(), n.getNodeValue());
                                }
                            }
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                LOGGER.warn(getClass().getSimpleName() + ": Failed loading auction item", e);
            }
        }

        if (_items.isEmpty()) {
            throw new IllegalArgumentException("No items defined");
        }

        getDAO(ItemDAO.class).findItemAuctionsByInstance(this.instanceId).forEach(this::addToAuctions);

        LOGGER.info("Loaded {} item(s) and registered {} auction(s) for instance {}", _items.size(), auctions.size(), this.instanceId);
        checkAndSetCurrentAndNextAuction();
    }

    private void addToAuctions(ItemAuctionData data) {
        if (data.getAuctionState() == ItemAuctionState.FINISHED && data.getStartingTime() < (System.currentTimeMillis() - Duration.ofDays(Config.ALT_ITEM_AUCTION_EXPIRED_AFTER).toMillis())) {
            LOGGER.info("Clearing expired auction: {}", data.getAuction());
            getDAO(ItemDAO.class).deleteItemAuction(data.getAuction());
            getDAO(ItemDAO.class).deleteItemAuctionBid(data.getAuction());
            return;
        }

       var auctionItem = getAuctionItem(data.getAuctionItem());
        if (isNull(auctionItem)) {
            LOGGER.warn("AuctionItem: {} not found for auction: {}", data.getAuctionItem(), data.getAuction());
            return;
        }

        var auctionBids = getDAO(ItemDAO.class).findAuctionBids(data.getAuction());
        auctions.put(data.getAuction(), new ItemAuction(data, auctionItem, auctionBids));
    }


    public final ItemAuction getCurrentAuction() {
        return _currentAuction;
    }

    public final ItemAuction getNextAuction() {
        return _nextAuction;
    }

    public final void shutdown() {
        final ScheduledFuture<?> stateTask = _stateTask;
        if (stateTask != null) {
            stateTask.cancel(false);
        }
    }

    private AuctionItem getAuctionItem(int auctionItemId) {
        for (int i = _items.size(); i-- > 0; ) {
            final AuctionItem item = _items.get(i);
            if (item.getAuctionItemId() == auctionItemId) {
                return item;
            }
        }
        return null;
    }

    final void checkAndSetCurrentAndNextAuction() {
        final ItemAuction[] itemAuctions = this.auctions.values().toArray(new ItemAuction[this.auctions.size()]);

        ItemAuction currentAuction = null;
        ItemAuction nextAuction = null;

        switch (itemAuctions.length) {
            case 0: {
                nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
                break;
            }
            case 1: {
                switch (itemAuctions[0].getAuctionState()) {
                    case CREATED: {
                        if (itemAuctions[0].getStartingTime() < (System.currentTimeMillis() + START_TIME_SPACE)) {
                            currentAuction = itemAuctions[0];
                            nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
                        } else {
                            nextAuction = itemAuctions[0];
                        }
                        break;
                    }
                    case STARTED: {
                        currentAuction = itemAuctions[0];
                        nextAuction = createAuction(Math.max(currentAuction.getEndingTime() + FINISH_TIME_SPACE, System.currentTimeMillis() + START_TIME_SPACE));
                        break;
                    }
                    case FINISHED: {
                        currentAuction = itemAuctions[0];
                        nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException();
                    }
                }
                break;
            }

            default: {
                Arrays.sort(itemAuctions, Comparator.comparingLong(ItemAuction::getStartingTime).reversed());
                // just to make sure we won't skip any auction because of little different times
                final long currentTime = System.currentTimeMillis();
                for (ItemAuction auction : itemAuctions) {
                    if (auction.getAuctionState() == ItemAuctionState.STARTED) {
                        currentAuction = auction;
                        break;
                    } else if (auction.getStartingTime() <= currentTime) {
                        currentAuction = auction;
                        break; // only first
                    }
                }
                for (ItemAuction auction : itemAuctions) {
                    if ((auction.getStartingTime() > currentTime) && (currentAuction != auction)) {
                        nextAuction = auction;
                        break;
                    }
                }
                if (nextAuction == null) {
                    nextAuction = createAuction(System.currentTimeMillis() + START_TIME_SPACE);
                }
                break;
            }
        }

        this.auctions.put(nextAuction.getAuctionId(), nextAuction);

        _currentAuction = currentAuction;
        _nextAuction = nextAuction;

        if ((currentAuction != null) && (currentAuction.getAuctionState() != ItemAuctionState.FINISHED)) {
            if (currentAuction.getAuctionState() == ItemAuctionState.STARTED) {
                setStateTask(ThreadPool.schedule(new ScheduleAuctionTask(currentAuction), Math.max(currentAuction.getEndingTime() - System.currentTimeMillis(), 0)));
            } else {
                setStateTask(ThreadPool.schedule(new ScheduleAuctionTask(currentAuction), Math.max(currentAuction.getStartingTime() - System.currentTimeMillis(), 0)));
            }
            LOGGER.info("Schedule current auction {}  for instance {}", currentAuction.getAuctionId(), instanceId);
        } else {
            var nextTask =  Duration.ofMillis(Math.max(nextAuction.getStartingTime() - System.currentTimeMillis(), 0));
            setStateTask(ThreadPool.schedule(new ScheduleAuctionTask(nextAuction), nextTask));
            LOGGER.info("Schedule next auction {} in {} for instance {}", nextAuction.getAuctionId(), nextTask, instanceId);
        }
    }

    public final ItemAuction[] getAuctionsByBidder(int bidderObjId) {
        final var itemAuctions = getAuctions();
        final ArrayList<ItemAuction> stack = new ArrayList<>(itemAuctions.size());
        for (ItemAuction auction : getAuctions()) {
            if (auction.getAuctionState() != ItemAuctionState.CREATED) {
                final ItemAuctionBid bid = auction.getBidFor(bidderObjId);
                if (bid != null) {
                    stack.add(auction);
                }
            }
        }
        return stack.toArray(new ItemAuction[stack.size()]);
    }

    public final Collection<ItemAuction> getAuctions() {
        final Collection<ItemAuction> itemAuctions;

        synchronized (this.auctions) {
            itemAuctions = this.auctions.values();
        }

        return itemAuctions;
    }

    final void onAuctionFinished(ItemAuction auction) {
        auction.broadcastToAllBiddersInternal(SystemMessage.getSystemMessage(SystemMessageId.S1_S_AUCTION_HAS_ENDED).addInt(auction.getAuctionId()));

        final ItemAuctionBid bid = auction.getHighestBid();
        if (bid != null) {
            final Item item = auction.createNewItemInstance();
            final Player player = bid.getPlayer();
            if (player != null) {
                player.getWarehouse().addItem("ItemAuction", item, null, null);
                player.sendPacket(SystemMessageId.YOU_HAVE_BID_THE_HIGHEST_PRICE_AND_HAVE_WON_THE_ITEM_THE_ITEM_CAN_BE_FOUND_IN_YOUR_PERSONAL_WAREHOUSE);

                LOGGER.info(AUCTION_HAS_FINISHED_MSG, auction.getAuctionId(), player, instanceId);
            } else {
                item.changeOwner(bid.getPlayerObjId());
                item.changeItemLocation(ItemLocation.WAREHOUSE);
                item.updateDatabase();
                World.getInstance().removeObject(item);

                LOGGER.info(AUCTION_HAS_FINISHED_MSG, auction.getAuctionId(), PlayerNameTable.getInstance().getNameById(bid.getPlayerObjId()), instanceId);
            }

            // Clean all canceled bids
            auction.clearCanceledBids();
        } else {
            LOGGER.info("Auction {} has finished. There have not been any bid for instance {}", auction.getAuctionId(), instanceId);
        }
    }

    final void setStateTask(ScheduledFuture<?> future) {
        final ScheduledFuture<?> stateTask = _stateTask;
        if (stateTask != null) {
            stateTask.cancel(false);
        }

        _stateTask = future;
    }

    private ItemAuction createAuction(long after) {
        final AuctionItem auctionItem = _items.get(Rnd.get(_items.size()));
        final long startingTime = _dateGenerator.nextDate(after);
        final long endingTime = startingTime + TimeUnit.MILLISECONDS.convert(auctionItem.getAuctionLength(), TimeUnit.MINUTES);
        final ItemAuction auction = new ItemAuction(_auctionIds.getAndIncrement(), instanceId, startingTime, endingTime, auctionItem);
        auction.storeMe();
        return auction;
    }


    private final class ScheduleAuctionTask implements Runnable {
        private final ItemAuction _auction;

        public ScheduleAuctionTask(ItemAuction auction) {
            _auction = auction;
        }

        @Override
        public final void run() {
            try {
                runImpl();
            } catch (Exception e) {
                LOGGER.error(getClass().getSimpleName() + ": Failed scheduling auction " + _auction.getAuctionId(), e);
            }
        }

        private void runImpl() throws Exception {
            final ItemAuctionState state = _auction.getAuctionState();
            switch (state) {
                case CREATED: {
                    if (!_auction.setAuctionState(ItemAuctionState.STARTED)) {
                        throw new IllegalStateException("Could not set auction state: " + ItemAuctionState.STARTED + ", expected: " + state);
                    }
                    LOGGER.info(getClass().getSimpleName() + ": Auction " + _auction.getAuctionId() + " has started for instance " + _auction.getInstanceId());
                    checkAndSetCurrentAndNextAuction();
                    break;
                }
                case STARTED: {
                    switch (_auction.getAuctionEndingExtendState()) {
                        case EXTEND_BY_5_MIN: {
                            if (_auction.getScheduledAuctionEndingExtendState() == ItemAuctionExtendState.INITIAL) {
                                _auction.setScheduledAuctionEndingExtendState(ItemAuctionExtendState.EXTEND_BY_5_MIN);
                                setStateTask(ThreadPool.schedule(this, Math.max(_auction.getEndingTime() - System.currentTimeMillis(), 0)));
                                return;
                            }
                            break;
                        }
                        case EXTEND_BY_3_MIN: {
                            if (_auction.getScheduledAuctionEndingExtendState() != ItemAuctionExtendState.EXTEND_BY_3_MIN) {
                                _auction.setScheduledAuctionEndingExtendState(ItemAuctionExtendState.EXTEND_BY_3_MIN);
                                setStateTask(ThreadPool.schedule(this, Math.max(_auction.getEndingTime() - System.currentTimeMillis(), 0)));
                                return;
                            }
                            break;
                        }
                        case EXTEND_BY_CONFIG_PHASE_A: {
                            if (_auction.getScheduledAuctionEndingExtendState() != ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B) {
                                _auction.setScheduledAuctionEndingExtendState(ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_B);
                                setStateTask(ThreadPool.schedule(this, Math.max(_auction.getEndingTime() - System.currentTimeMillis(), 0)));
                                return;
                            }
                            break;
                        }
                        case EXTEND_BY_CONFIG_PHASE_B: {
                            if (_auction.getScheduledAuctionEndingExtendState() != ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A) {
                                _auction.setScheduledAuctionEndingExtendState(ItemAuctionExtendState.EXTEND_BY_CONFIG_PHASE_A);
                                setStateTask(ThreadPool.schedule(this, Math.max(_auction.getEndingTime() - System.currentTimeMillis(), 0)));
                                return;
                            }
                        }
                    }

                    if (!_auction.setAuctionState(ItemAuctionState.FINISHED)) {
                        throw new IllegalStateException("Could not set auction state: " + ItemAuctionState.FINISHED + ", expected: " + state);
                    }

                    onAuctionFinished(_auction);
                    checkAndSetCurrentAndNextAuction();
                    break;
                }

                default: {
                    throw new IllegalStateException("Invalid state: " + state);
                }
            }
        }
    }
}

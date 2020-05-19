package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.clanhallauction.ClanHallAuction;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.ScheduleTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Sdw
 */
public class ClanHallAuctionManager extends AbstractEventManager<AbstractEvent<?>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClanHallAuctionManager.class);

    private static final Map<Integer, ClanHallAuction> AUCTIONS = new HashMap<>();

    private ClanHallAuctionManager() {
    }

    @ScheduleTarget
    private void onEventStart() {
        LOGGER.info(getClass().getSimpleName() + ": Clan Hall Auction has started!");
        AUCTIONS.clear();

        //@formatter:off
        ClanHallManager.getInstance().getFreeAuctionableHall()
                .forEach(c -> AUCTIONS.put(c.getId(), new ClanHallAuction(c.getId())));
        //@formatter:on
    }

    @ScheduleTarget
    private void onEventEnd() {
        AUCTIONS.values().forEach(ClanHallAuction::finalizeAuctions);
        AUCTIONS.clear();
        LOGGER.info(getClass().getSimpleName() + ": Clan Hall Auction has ended!");
    }

    @Override
    public void onInitialized() {
    }

    public ClanHallAuction getClanHallAuctionById(int clanHallId) {
        return AUCTIONS.get(clanHallId);
    }

    public ClanHallAuction getClanHallAuctionByClan(Clan clan) {
        //@formatter:off
        return AUCTIONS.values().stream()
                .filter(a -> a.getBids().containsKey(clan.getId()))
                .findFirst()
                .orElse(null);
        //@formatter:on
    }

    public boolean checkForClanBid(int clanHallId, Clan clan) {
        //@formatter:off
        return AUCTIONS.entrySet().stream()
                .filter(a -> a.getKey() != clanHallId)
                .anyMatch(a -> a.getValue().getBids().containsKey(clan.getId()));
        //@formatter:on
    }

    public static ClanHallAuctionManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ClanHallAuctionManager INSTANCE = new ClanHallAuctionManager();
    }
}

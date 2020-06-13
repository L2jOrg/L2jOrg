/*
 * Copyright © 2019-2020 L2JOrg
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

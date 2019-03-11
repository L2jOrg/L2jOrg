/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.Config;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.util.IGameXmlReader;
import org.l2j.gameserver.model.itemauction.ItemAuctionInstance;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Forsaiken
 */
public final class ItemAuctionManager implements IGameXmlReader {
    private static final Logger LOGGER = Logger.getLogger(ItemAuctionManager.class.getName());

    private final Map<Integer, ItemAuctionInstance> _managerInstances = new HashMap<>();
    private final AtomicInteger _auctionIds = new AtomicInteger(1);

    protected ItemAuctionManager() {
        if (!Config.ALT_ITEM_AUCTION_ENABLED) {
            LOGGER.info("Disabled by config.");
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement statement = con.createStatement();
             ResultSet rset = statement.executeQuery("SELECT auctionId FROM item_auction ORDER BY auctionId DESC LIMIT 0, 1")) {
            if (rset.next()) {
                _auctionIds.set(rset.getInt(1) + 1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed loading auctions.", e);
        }

        load();
    }

    public static void deleteAuction(int auctionId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement statement = con.prepareStatement("DELETE FROM item_auction WHERE auctionId=?")) {
                statement.setInt(1, auctionId);
                statement.execute();
            }

            try (PreparedStatement statement = con.prepareStatement("DELETE FROM item_auction_bid WHERE auctionId=?")) {
                statement.setInt(1, auctionId);
                statement.execute();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "L2ItemAuctionManagerInstance: Failed deleting auction: " + auctionId, e);
        }
    }

    /**
     * Gets the single instance of {@code ItemAuctionManager}.
     *
     * @return single instance of {@code ItemAuctionManager}
     */
    public static ItemAuctionManager getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void load() {
        _managerInstances.clear();
        parseDatapackFile("data/ItemAuctions.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _managerInstances.size() + " instance(s).");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        try {
            for (Node na = doc.getFirstChild(); na != null; na = na.getNextSibling()) {
                if ("list".equalsIgnoreCase(na.getNodeName())) {
                    for (Node nb = na.getFirstChild(); nb != null; nb = nb.getNextSibling()) {
                        if ("instance".equalsIgnoreCase(nb.getNodeName())) {
                            final NamedNodeMap nab = nb.getAttributes();
                            final int instanceId = Integer.parseInt(nab.getNamedItem("id").getNodeValue());

                            if (_managerInstances.containsKey(instanceId)) {
                                throw new Exception("Dublicated instanceId " + instanceId);
                            }

                            final ItemAuctionInstance instance = new ItemAuctionInstance(instanceId, _auctionIds, nb);
                            _managerInstances.put(instanceId, instance);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Failed loading auctions from xml.", e);
        }
    }

    public final void shutdown() {
        for (ItemAuctionInstance instance : _managerInstances.values()) {
            instance.shutdown();
        }
    }

    public final ItemAuctionInstance getManagerInstance(int instanceId) {
        return _managerInstances.get(instanceId);
    }

    public final int getNextAuctionId() {
        return _auctionIds.getAndIncrement();
    }

    private static class SingletonHolder {
        protected static final ItemAuctionManager _instance = new ItemAuctionManager();
    }
}
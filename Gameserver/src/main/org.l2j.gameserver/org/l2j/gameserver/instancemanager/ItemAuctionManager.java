/*
 * Copyright © 2019 L2J Mobius
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

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.item.auction.ItemAuctionInstance;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * @author Forsaiken
 */
public final class ItemAuctionManager extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemAuctionManager.class);

    private final Map<Integer, ItemAuctionInstance> _managerInstances = new HashMap<>();
    private final AtomicInteger _auctionIds = new AtomicInteger(1);

    private ItemAuctionManager() {
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
            LOGGER.error("Failed loading auctions.", e);
        }

        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/ItemAuctions.xsd");
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
            LOGGER.error("ItemAuctionManager: Failed deleting auction: " + auctionId, e);
        }
    }

    @Override
    public void load() {
        _managerInstances.clear();
        parseDatapackFile("data/ItemAuctions.xml");
        LOGGER.info("Loaded {} instance(s).", _managerInstances.size());
        releaseResources();
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
            LOGGER.error(getClass().getSimpleName() + ": Failed loading auctions from xml.", e);
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

    public static ItemAuctionManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ItemAuctionManager INSTANCE = new ItemAuctionManager();
    }
}
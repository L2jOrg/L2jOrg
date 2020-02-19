package org.l2j.gameserver.data.xml.impl;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Loads buy lists for NPCs.
 *
 * @author NosBit
 */
public final class BuyListData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuyListData.class.getName());
    private final Map<Integer, ProductList> _buyLists = new HashMap<>();

    private BuyListData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/buylist.xsd");
    }

    @Override
    public synchronized void load() {
        _buyLists.clear();
        parseDatapackDirectory("data/buylists", false);
        if (getSettings(GeneralSettings.class).loadCustomBuyList()) {
            parseDatapackDirectory("data/buylists/custom", false);
        }

        LOGGER.info("Loaded {} BuyLists.", _buyLists.size());

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM `buylists`")) {
            while (rs.next()) {
                final int buyListId = rs.getInt("buylist_id");
                final int itemId = rs.getInt("item_id");
                final long count = rs.getLong("count");
                final long nextRestockTime = rs.getLong("next_restock_time");
                final ProductList buyList = getBuyList(buyListId);
                if (buyList == null) {
                    LOGGER.warn("BuyList found in database but not loaded from xml! BuyListId: " + buyListId);
                    continue;
                }
                final Product product = buyList.getProductByItemId(itemId);
                if (product == null) {
                    LOGGER.warn("ItemId found in database but not loaded from xml! BuyListId: " + buyListId + " ItemId: " + itemId);
                    continue;
                }
                if (count < product.getMaxCount()) {
                    product.setCount(count);
                    product.restartRestockTask(nextRestockTime);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to load buyList data from database.", e);
        }
    }

    @Override
    public void parseDocument(Document doc, File f) {
        try {
            final int buyListId = Integer.parseInt(f.getName().replaceAll(".xml", ""));
            forEach(doc, "list", (list) ->
            {
                final int defaultBaseTax = parseInteger(list.getAttributes(), "baseTax", 0);
                final ProductList buyList = new ProductList(buyListId);
                forEach(list, (node) ->
                {
                    switch (node.getNodeName()) {
                        case "item": {
                            final NamedNodeMap attrs = node.getAttributes();

                            final int itemId = parseInteger(attrs, "id");
                            final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
                            if (item != null) {
                                final long price = parseLong(attrs, "price", -1L);
                                final long restockDelay = parseLong(attrs, "restock_delay", -1L);
                                final long count = parseLong(attrs, "count", -1L);
                                final int baseTax = parseInteger(attrs, "baseTax", defaultBaseTax);

                                buyList.addProduct(new Product(buyListId, item, price, restockDelay, count, baseTax));
                            } else {
                                LOGGER.warn("Item not found. BuyList:" + buyListId + " ItemID:" + itemId + " File:" + f);
                            }
                            break;
                        }
                        case "npcs": {
                            forEach(node, "npc", (npcNode) -> buyList.addAllowedNpc(Integer.parseInt(npcNode.getTextContent())));
                            break;
                        }
                    }
                });
                _buyLists.put(buyListId, buyList);
            });
        } catch (Exception e) {
            LOGGER.warn("Failed to load buyList data from xml File:" + f.getName(), e);
        }
    }

    public ProductList getBuyList(int listId) {
        return _buyLists.get(listId);
    }

    public static BuyListData getInstance() {
        return Singleton._instance;
    }

    private static class Singleton {
        private static final BuyListData _instance = new BuyListData();
    }
}

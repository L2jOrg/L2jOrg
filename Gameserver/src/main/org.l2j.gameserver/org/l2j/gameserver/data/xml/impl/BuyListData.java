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
package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.BuyListDAO;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import java.io.File;
import java.nio.file.Path;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * Loads buy lists for NPCs.
 *
 * @author NosBit
 * @author JoeAlisson
 */
public final class BuyListData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuyListData.class);

    private final IntMap<ProductList> buyLists = new HashIntMap<>();

    private BuyListData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/buylist.xsd");
    }

    @Override
    public synchronized void load() {
        buyLists.clear();
        parseDatapackDirectory("data/buylists", false);
        if (getSettings(GeneralSettings.class).loadCustomBuyList()) {
            parseDatapackDirectory("data/buylists/custom", false);
        }
        releaseResources();

        LOGGER.info("Loaded {} BuyLists.", buyLists.size());

        getDAO(BuyListDAO.class).findAll().forEach(info -> {
            final var list = getBuyList(info.getId());
            if(isNull(list)) {
                LOGGER.warn("BuyList found in database but not loaded from xml! BuyListId: {}", info.getId());
                return;
            }

            final Product product = list.getProductByItemId(info.getItemId());
            if (isNull(product)) {
                LOGGER.warn("ItemId found in database but not loaded from xml! BuyListId: {} item id {}", info.getId(), info.getItemId());
                return;
            }

            product.updateInfo(info);
        });
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
                buyLists.put(buyListId, buyList);
            });
        } catch (Exception e) {
            LOGGER.warn("Failed to load buyList data from xml File:" + f.getName(), e);
        }
    }

    public ProductList getBuyList(int listId) {
        return buyLists.get(listId);
    }

    public static void init() {
        getInstance().load();
    }

    public static BuyListData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final BuyListData INSTANCE = new BuyListData();
    }
}

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
package org.l2j.gameserver.engine.item.shop;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.LinkedHashIntMap;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.database.dao.LCoinShopDAO;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.item.shop.l2store.RestrictionPeriod;
import org.l2j.gameserver.engine.item.shop.lcoin.LCoinShopProduct;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.settings.FeatureSettings;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author JoeAlisson
 */
public class LCoinShop extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(LCoinShop.class);

    private final IntMap<LCoinShopProduct> productInfos = new LinkedHashIntMap<>();
    private final IntMap<Map<String, Integer>> shopHistory = new HashIntMap<>();

    private LCoinShop() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/shop/l-coin.xsd");
    }

    @Override
    public void load() {
        if (!FeatureSettings.isLCoinStoreEnabled()){
            return;
        }
        parseDatapackFile("data/shop/l-coin.xml");
        releaseResources();
        reloadShopHistory();
    }

    public void reloadShopHistory() {
        getDAO(LCoinShopDAO.class).deleteExpired();
        synchronized (shopHistory) {
            shopHistory.clear();
            getDAO(LCoinShopDAO.class).loadAllGrouped(this::addHistory);
        }
    }

    private void addHistory(ResultSet result) {
        try {
            while (result.next()) {
                int id = result.getInt("product_id");
                String account = result.getString("account");
                int count = result.getInt("sum");
                shopHistory.computeIfAbsent(id, i -> new HashMap<>()).put(account, count);
            }
        } catch (SQLException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "list",  list -> forEach(list, "product", this::parseProduct));
    }

    private void parseProduct(Node productNode) {
        var attributes = productNode.getAttributes();
        var id = parseInt(attributes, "id");
        var restrictionAmount = parseInt(attributes, "restriction-amount");
        var restrictionPeriod = parseEnum(attributes, RestrictionPeriod.class, "restriction-period");
        var minLevel = parseInt(attributes, "min-level", 1);
        var serverItemAmount = parseInt(attributes, "server-item-amount", -1);
        var expiration = parseString(attributes,"expiration-date");
        LocalDateTime expirationDate = null;

        if(nonNull(expiration)) {
            expirationDate = Util.parseLocalDateTime(expiration);
        }

        List<ItemHolder> ingredients = new ArrayList<>();
        ItemHolder production = null;
        final NodeList list = productNode.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {

            final Node targetNode = list.item(i);

            var holder = parseItemHolder(targetNode);
            if (isNull(holder)) {
                return;
            }

            if(isNull(ItemEngine.getInstance().getTemplate(holder.getId()))) {
                LOGGER.error("Item template does not exists for itemId: {} in product id {}", holder.getId(), id);
                return;
            }

            if ("ingredient".equalsIgnoreCase(targetNode.getNodeName())) {
                ingredients.add(holder);
            } else {
                production = holder;
            }
        }

        if (ingredients.isEmpty() || production == null) {
            LOGGER.error("Incorrect configuration product id {}", id);
            return;
        }

        if (productInfos.put(id, new LCoinShopProduct(id, restrictionAmount, restrictionPeriod, minLevel, ingredients, production, serverItemAmount, expirationDate)) != null) {
            LOGGER.warn("Duplicate product id {}", id);
        }
    }

    public LCoinShopProduct getProductInfo(int id) {
        return productInfos.get(id);
    }

    public IntMap<LCoinShopProduct> getProductInfos() {
        return productInfos;
    }

    public void addHistory(Player player, LCoinShopProduct product, int amount) {
        shopHistory.computeIfAbsent(product.id(), id -> new HashMap<>()).merge(player.getAccountName(), amount, Integer::sum);
    }

    public int boughtCount(Player player, LCoinShopProduct product) {
        return shopHistory.getOrDefault(product.id(), Collections.emptyMap()).getOrDefault(player.getAccountName(), 0);
    }

    public static void init() {
        getInstance().load();
    }

    public static LCoinShop getInstance() {
        return LCoinShop.Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final LCoinShop INSTANCE = new LCoinShop();
    }
}

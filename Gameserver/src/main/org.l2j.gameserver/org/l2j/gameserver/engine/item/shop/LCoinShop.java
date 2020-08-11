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
package org.l2j.gameserver.engine.item.shop;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.LinkedHashIntMap;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.item.shop.lcoin.LCoinShopProduct;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

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
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/shop/l-coin.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/shop/l-coin.xml");
        releaseResources();
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "list",  list -> forEach(list, "product", this::parseProduct));
    }

    private void parseProduct(Node productNode) {
        var attributes = productNode.getAttributes();
        var id = parseInt(attributes, "id");
        var limitPerDay = parseInt(attributes, "limitPerDay", 0);
        var minLevel = parseInt(attributes, "minLevel", 1);
        var remainServerItemAmount = parseInt(attributes, "remainServerItemAmount", -1);
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

        if (productInfos.put(id, new LCoinShopProduct(id, limitPerDay, minLevel, ingredients, production, remainServerItemAmount, expirationDate)) != null) {
            LOGGER.warn("Duplicate product id {}", id);
        }
    }

    public LCoinShopProduct getProductInfo(int id) {
        return productInfos.get(id);
    }

    public IntMap<LCoinShopProduct> getProductInfos() {
        return productInfos;
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

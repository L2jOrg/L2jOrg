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
import org.l2j.gameserver.data.xml.model.LCoinShopProductInfo;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.data.xml.model.LCoinShopProductInfo.Category;

public class LCoinShopData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(LCoinShopData.class);
    private IntMap<LCoinShopProductInfo> productInfos = new HashIntMap<>();

    public LCoinShopData() {
        load();
    }

    public LCoinShopProductInfo getProductInfo(int id) {
        return productInfos.get(id);
    }

    public IntMap<LCoinShopProductInfo> getProductInfos() {
        return productInfos;
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/LCoinShop.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/LCoinShop.xml");
        releaseResources();
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "list",  list -> forEach(list, "product", this::parseProduct));
    }

    private void parseProduct(Node productNode) {
        var attributes = productNode.getAttributes();
        var id = parseInteger(attributes, "id");
        var category = parseEnum(attributes, Category.class, "category", Category.Equip);
        var limitPerDay = parseInteger(attributes, "limitPerDay", 0);
        var minLevel = parseInteger(attributes, "minLevel", 1);
        var isEvent = parseBoolean(attributes, "isEvent", false);
        var remainServerItemAmount = parseInteger(attributes, "remainServerItemAmount", -1);
        List<ItemHolder> ingredients = new ArrayList<>();
        ItemHolder production = null;
        final NodeList list = productNode.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            final Node targetNode = list.item(i);
            var holder = parseItemInfo(targetNode);
            if (holder == null) {
                return;
            }

            if ("ingredient".equalsIgnoreCase(targetNode.getNodeName())) {
                ingredients.add(holder);
            }
            else {
                production = holder;
            }
        }

        if (ingredients.isEmpty() || production == null) {
            LOGGER.error("Incorrect configuration product id {}", id);
            return;
        }

        if (productInfos.put(id, new LCoinShopProductInfo(id, category, limitPerDay, minLevel, isEvent, ingredients, production, remainServerItemAmount)) != null) {
            LOGGER.warn("Duplicate product id {}", id);
        }
    }

    private ItemHolder parseItemInfo(Node itemInfoNode) {
        var attributes = itemInfoNode.getAttributes();
        var itemId = parseInteger(attributes, "id");
        var count = parseInteger(attributes, "count");

        final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
        if (isNull(item)) {
            LOGGER.error("Item template does not exists for itemId: {} in product id {}", itemId, itemInfoNode.getAttributes().getNamedItem("id"));
            return null;
        }

        return new ItemHolder(itemId, count);
    }

    public static LCoinShopData getInstance() {
        return LCoinShopData.Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final LCoinShopData INSTANCE = new LCoinShopData();
    }
}

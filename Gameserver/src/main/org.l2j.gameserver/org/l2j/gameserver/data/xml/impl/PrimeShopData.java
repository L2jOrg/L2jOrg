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
import org.l2j.gameserver.data.database.dao.PrimeShopDAO;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRProductInfo;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Gnacik, UnAfraid
 */
public class PrimeShopData extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrimeShopData.class);
    private static final int VIP_GIFT_BASE_ID = 100000;

    private final IntMap<PrimeShopProduct> primeItems = new HashIntMap<>(140);
    private final IntMap<PrimeShopProduct> vipGifts = new HashIntMap<>(10);

    private PrimeShopData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/primeShop.xsd");
    }

    @Override
    public void load() {
        primeItems.clear();
        parseDatapackFile("data/primeShop.xml");
        LOGGER.info("Loaded {} items", primeItems.size());
        releaseResources();
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "list", list  -> forEach(list, "product", this::parseProduct));
    }

    private void parseProduct(Node productNode) {
        final List<PrimeShopItem> items = new ArrayList<>();
        for (Node b = productNode.getFirstChild(); b != null; b = b.getNextSibling()) {
            if ("item".equalsIgnoreCase(b.getNodeName())) {
                var attrs = b.getAttributes();

                final int itemId = parseInteger(attrs, "id");
                final int count = parseInteger(attrs, "count");

                final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
                if (isNull(item)) {
                    LOGGER.error("Item template does not exists for itemId: {} in product id {}", itemId, productNode.getAttributes().getNamedItem("id"));
                    return;
                }
                items.add(new PrimeShopItem(itemId, count, item.getWeight(), item.isTradeable() ? 1 : 0));
            }
        }
        var attrs = productNode.getAttributes();
        var product = new PrimeShopProduct(parseInteger(attrs, "id"), items);
        product.setCategory(parseByte(attrs, "category"));
        product.setPaymentType(parseByte(attrs, "paymentType"));
        product.setPrice(parseInteger(attrs, "price"));
		product.setPanelType(parseByte(attrs, "panelType"));
		product.setRecommended(parseByte(attrs, "recommended"));
		product.setStart(parseInteger(attrs, "startSale"));
		product.setEnd(parseInteger(attrs, "endSale"));
		product.setDaysOfWeek(parseByte(attrs, "dayOfWeek"));
		product.setStartHour(parseByte(attrs, "startHour"));
		product.setStartMinute(parseByte(attrs, "startMinute"));
		product.setStopHour(parseByte(attrs, "stopHour"));
		product.setStopMinute(parseByte(attrs, "stopMinute"));
		product.setStock(parseByte(attrs, "stock"));
		product.setMaxStock(parseByte(attrs, "maxStock"));
		product.setSalePercent(parseByte(attrs, "salePercent"));
		product.setMinLevel(parseByte(attrs, "minLevel"));
        product.setMaxLevel(parseByte(attrs, "maxLevel"));
        product.setMinBirthday(parseByte(attrs, "minBirthday"));
        product.setMaxBirthday(parseByte(attrs, "maxBirthday"));
        product.setRestrictionDay(parseByte(attrs, "restrictionDay"));
        product.setAvailableCount(parseByte(attrs, "availableCount"));
        product.setVipTier(parseByte(attrs, "vipTier"));
        product.setSilverCoin(parseInteger(attrs, "silverCoin"));
        product.setVipGift(parseBoolean(attrs, "isVipGift"));

        if(product.isVipGift()) {
            vipGifts.put(product.getId(), product);
        } else {
            primeItems.put(product.getId(), product);
        }
    }

    public void showProductInfo(Player player, int brId) {
        final PrimeShopProduct item = primeItems.get(brId);

        if ((player == null) || (item == null)) {
            return;
        }

        player.sendPacket(new ExBRProductInfo(item, player));
    }

    public PrimeShopProduct getItem(int productId) {
        if(primeItems.containsKey(productId)) {
            return primeItems.get(productId);
        }
        return vipGifts.get(productId);
    }

    public PrimeShopProduct getVipGiftOfTier(byte tier) {
        return vipGifts.get(VIP_GIFT_BASE_ID + tier);
    }

    public IntMap<PrimeShopProduct> getPrimeItems() {
        return primeItems;
    }

    public static PrimeShopData getInstance() {
        return Singleton.INSTANCE;
    }

    public boolean canReceiveVipGift(Player player) {
        return player.getVipTier() > 0 && !getDAO(PrimeShopDAO.class).hasBougthAnyItemInRangeToday(player.getObjectId(), VIP_GIFT_BASE_ID+1, VIP_GIFT_BASE_ID+10);
    }

    private static class Singleton {
        private static final PrimeShopData INSTANCE = new PrimeShopData();
    }
}

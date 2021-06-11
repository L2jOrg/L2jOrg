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
import org.l2j.gameserver.data.database.dao.L2StoreDAO;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreItem;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreProduct;
import org.l2j.gameserver.engine.item.shop.l2store.RestrictionPeriod;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.item.ItemTemplate;
import org.l2j.gameserver.network.serverpackets.store.ExBRProductInfo;
import org.l2j.gameserver.settings.FeatureSettings;
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
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * @author Gnacik, UnAfraid
 * @author JoeAlisson
 */
public class L2Store extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(L2Store.class);
    private static final int VIP_GIFT_BASE_ID = 100000;

    private final IntMap<L2StoreProduct> primeItems = new HashIntMap<>(140);
    private final IntMap<L2StoreProduct> vipGifts = new HashIntMap<>(10);

    private L2Store() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/shop/l2-store.xsd");
    }

    @Override
    public void load() {
        if (!FeatureSettings.isL2StoreEnabled()){
            return;
        }
        primeItems.clear();
        parseDatapackFile("data/shop/l2-store.xml");
        LOGGER.info("Loaded {} items", primeItems.size());
        releaseResources();
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "list", list  -> forEach(list, "product", this::parseProduct));
    }

    private void parseProduct(Node productNode) {
        final List<L2StoreItem> items = parseItems(productNode);
        if (isNullOrEmpty(items)) {
            return;
        }
        L2StoreProduct product = createProduct(productNode, items);

        if(product.isVipGift()) {
            vipGifts.put(product.getId(), product);
        } else {
            primeItems.put(product.getId(), product);
        }
    }

    private List<L2StoreItem> parseItems(Node productNode) {
        final List<L2StoreItem> items = new ArrayList<>();
        for (Node b = productNode.getFirstChild(); b != null; b = b.getNextSibling()) {
            if ("item".equalsIgnoreCase(b.getNodeName())) {
                var attrs = b.getAttributes();

                final int itemId = parseInt(attrs, "id");
                final int count = parseInt(attrs, "count");

                final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
                if (isNull(item)) {
                    LOGGER.error("Item template does not exists for itemId: {} in product id {}", itemId, productNode.getAttributes().getNamedItem("id"));
                    continue;
                }
                items.add(new L2StoreItem(itemId, count, item.getWeight(), item.isTradeable()));
            }
        }
        return items;
    }

    private L2StoreProduct createProduct(Node productNode, List<L2StoreItem> items) {
        var attrs = productNode.getAttributes();
        var product = new L2StoreProduct(parseInt(attrs, "id"), items);
        product.setCategory(parseByte(attrs, "category"));
        product.setPaymentType(parseByte(attrs, "payment-type"));
        product.setPrice(parseInt(attrs, "price"));
        product.setPanelType(parseByte(attrs, "panel-type"));
        product.setRecommended(parseByte(attrs, "recommended"));
        product.setStart(parseInt(attrs, "start-sale"));
        product.setEnd(parseInt(attrs, "end-sale"));
        product.setDaysOfWeek(parseByte(attrs, "days-of-week"));
        product.setStartHour(parseByte(attrs, "star-hour"));
        product.setStartMinute(parseByte(attrs, "start-minute"));
        product.setStopHour(parseByte(attrs, "stop-hour"));
        product.setStopMinute(parseByte(attrs, "stop-minute"));
        product.setStock(parseByte(attrs, "stock"));
        product.setMaxStock(parseByte(attrs, "max-stock"));
        product.setSalePercent(parseByte(attrs, "sale-percent"));
        product.setMinLevel(parseByte(attrs, "min-level"));
        product.setMaxLevel(parseByte(attrs, "max-level"));
        product.setMinBirthday(parseByte(attrs, "min-birthday"));
        product.setMaxBirthday(parseByte(attrs, "max-birthday"));
        product.setRestrictionAmount(parseByte(attrs, "restriction-amount"));
        product.setRestrictionPeriod(parseEnum(attrs, RestrictionPeriod.class, "restriction-period"));
        product.setAvailableCount(parseByte(attrs, "available-count"));
        product.setVipTier(parseByte(attrs, "vip-tier"));
        product.setSilverCoin(parseInt(attrs, "silver-coin"));
        product.setVipGift(parseBoolean(attrs, "is-vip-gift"));
        return product;
    }

    public void showProductInfo(Player player, int brId) {
        final L2StoreProduct item = primeItems.get(brId);

        if ((player == null) || (item == null)) {
            return;
        }

        player.sendPacket(new ExBRProductInfo(item, player));
    }

    public L2StoreProduct getItem(int productId) {
        if(primeItems.containsKey(productId)) {
            return primeItems.get(productId);
        }
        return vipGifts.get(productId);
    }

    public L2StoreProduct getVipGiftOfTier(byte tier) {
        return vipGifts.get(VIP_GIFT_BASE_ID + tier);
    }

    public IntMap<L2StoreProduct> getPrimeItems() {
        return primeItems;
    }

    public boolean canReceiveVipGift(Player player) {
        return player.getVipTier() > 0 && !getDAO(L2StoreDAO.class).hasBoughtAnyProductInRangeToday(player.getAccountName(), VIP_GIFT_BASE_ID+1, VIP_GIFT_BASE_ID+10);
    }

    public static void init() {
        getInstance().load();
    }

    public static L2Store getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final L2Store INSTANCE = new L2Store();
    }
}

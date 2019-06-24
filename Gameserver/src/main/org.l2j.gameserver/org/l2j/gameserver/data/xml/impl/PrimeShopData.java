package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.l2j.gameserver.data.database.dao.PrimeShopDAO;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.L2Item;
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

    private final IntObjectMap<PrimeShopProduct> primeItems = new HashIntObjectMap<>(140);
    private final IntObjectMap<PrimeShopProduct> vipGifts = new HashIntObjectMap<>(7);

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

                final L2Item item = ItemTable.getInstance().getTemplate(itemId);
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

    public void showProductInfo(L2PcInstance player, int brId) {
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

    public IntObjectMap<PrimeShopProduct> getPrimeItems() {
        return primeItems;
    }

    public static PrimeShopData getInstance() {
        return Singleton.INSTANCE;
    }

    public boolean canReceiveVipGift(L2PcInstance player) {
        return player.getVipTier() > 0 && !getDAO(PrimeShopDAO.class).hasBougthAnyItemInRangeToday(player.getObjectId(), VIP_GIFT_BASE_ID+1, VIP_GIFT_BASE_ID+7);
    }

    private static class Singleton {
        private static final PrimeShopData INSTANCE = new PrimeShopData();
    }
}

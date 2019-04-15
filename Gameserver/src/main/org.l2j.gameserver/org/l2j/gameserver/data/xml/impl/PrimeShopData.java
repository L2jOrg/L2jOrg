package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRProductInfo;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Gnacik, UnAfraid
 */
public class PrimeShopData extends IGameXmlReader{
    private static final Logger LOGGER = LoggerFactory.getLogger(PrimeShopData.class);

    private final Map<Integer, PrimeShopProduct> primeItems = new LinkedHashMap<>();

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
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", list  -> forEach(list, "product", this::parseProduct));
    }

    private void parseProduct(Node productNode) {
        final List<PrimeShopItem> items = new ArrayList<>();
        for (Node b = productNode.getFirstChild(); b != null; b = b.getNextSibling()) {
            if ("item".equalsIgnoreCase(b.getNodeName())) {
                var attrs = b.getAttributes();

                final int itemId = parseInteger(attrs, "itemId");
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
        product.setPaymentType(parseByte(attrs, "paimentType"));
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

	/*	<xs:attribute type="xs:byte" name="minLevel"/>
		<xs:attribute type="xs:byte" name="maxLevel"/>
		<xs:attribute type="xs:byte" name="minBirthday"/>
		<xs:attribute type="xs:byte" name="maxBirthday"/>
		<xs:attribute type="xs:byte" name="restrictionDay"/>
		<xs:attribute type="xs:byte" name="availableCount"/>
		<xs:attribute type="xs:byte" name="vipTier" default="0"/>
		<xs:attribute type="xs:int" name="silverCoin" default="0"/>

                _category = set.getInt("cat", 0);
        _paymentType = set.getInt("paymentType", 0);
        _price = set.getInt("price");
        _panelType = set.getInt("panelType", 0);
        _recommended = set.getInt("recommended", 0);
        _start = set.getInt("startSale", 0);
        _end = set.getInt("endSale", 0);
        _daysOfWeek = set.getInt("daysOfWeek", 127);
        _startHour = set.getInt("startHour", 0);
        _startMinute = set.getInt("startMinute", 0);
        _stopHour = set.getInt("stopHour", 0);
        _stopMinute = set.getInt("stopMinute", 0);
        _stock = set.getInt("stock", 0);
        _maxStock = set.getInt("maxStock", -1);
        _salePercent = set.getInt("salePercent", 0);
        _minLevel = set.getInt("minLevel", 0);
        _maxLevel = set.getInt("maxLevel", 0);
        _minBirthday = set.getInt("minBirthday", 0);
        _maxBirthday = set.getInt("maxBirthday", 0);
        _restrictionDay = set.getInt("restrictionDay", 0);
        _availableCount = set.getInt("availableCount", 0);
        _items = items;
*/

    }

    public void showProductInfo(L2PcInstance player, int brId) {
        final PrimeShopProduct item = primeItems.get(brId);

        if ((player == null) || (item == null)) {
            return;
        }

        player.sendPacket(new ExBRProductInfo(item, player));
    }

    public PrimeShopProduct getItem(int brId) {
        return primeItems.get(brId);
    }

    public Map<Integer, PrimeShopProduct> getPrimeItems() {
        return primeItems;
    }

    public static PrimeShopData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PrimeShopData INSTANCE = new PrimeShopData();
    }
}

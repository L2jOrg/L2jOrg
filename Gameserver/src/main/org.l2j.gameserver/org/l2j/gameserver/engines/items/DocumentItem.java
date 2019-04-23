package org.l2j.gameserver.engines.items;

import org.l2j.gameserver.engines.DocumentBase;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.L2ExtractableProduct;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.model.stats.functions.FuncTemplate;
import org.l2j.gameserver.settings.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * @author mkizub, JIV
 */
public final class DocumentItem extends DocumentBase {
    private final List<L2Item> _itemsInFile = new LinkedList<>();
    Logger LOGGER = LoggerFactory.getLogger(DocumentItem.class.getName());
    private Item _currentItem = null;

    /**
     * @param file
     */
    public DocumentItem(File file) {
        super(file);
    }

    @Override
    protected StatsSet getStatsSet() {
        return _currentItem.set;
    }

    @Override
    protected String getTableValue(String name) {
        return _tables.get(name)[_currentItem.currentLevel];
    }

    @Override
    protected String getTableValue(String name, int idx) {
        return _tables.get(name)[idx - 1];
    }

    @Override
    protected void parseDocument(Document doc) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("item".equalsIgnoreCase(d.getNodeName())) {
                        try {
                            _currentItem = new Item();
                            parseItem(d);
                            _itemsInFile.add(_currentItem.item);
                            resetTable();
                        } catch (Exception e) {
                            LOGGER.warn("Cannot create item " + _currentItem.id, e);
                        }
                    }
                }
            }
        }
    }

    protected void parseItem(Node n) throws InvocationTargetException {
        final int itemId = Integer.parseInt(n.getAttributes().getNamedItem("id").getNodeValue());
        final String className = n.getAttributes().getNamedItem("type").getNodeValue();
        final String itemName = n.getAttributes().getNamedItem("name").getNodeValue();
        final String additionalName = n.getAttributes().getNamedItem("additionalName") != null ? n.getAttributes().getNamedItem("additionalName").getNodeValue() : null;
        _currentItem.id = itemId;
        _currentItem.name = itemName;
        _currentItem.type = className;
        _currentItem.set = new StatsSet();
        _currentItem.set.set("item_id", itemId);
        _currentItem.set.set("name", itemName);
        _currentItem.set.set("additionalName", additionalName);

        final Node first = n.getFirstChild();
        for (n = first; n != null; n = n.getNextSibling()) {
            if ("table".equalsIgnoreCase(n.getNodeName())) {
                if (_currentItem.item != null) {
                    throw new IllegalStateException("Item created but table node found! Item " + itemId);
                }
                parseTable(n);
            } else if ("set".equalsIgnoreCase(n.getNodeName())) {
                if (_currentItem.item != null) {
                    throw new IllegalStateException("Item created but set node found! Item " + itemId);
                }
                parseBeanSet(n, _currentItem.set, 1);
            } else if ("stats".equalsIgnoreCase(n.getNodeName())) {
                makeItem();
                for (Node b = n.getFirstChild(); b != null; b = b.getNextSibling()) {
                    if ("stat".equalsIgnoreCase(b.getNodeName())) {
                        final Stats type = Stats.valueOfXml(b.getAttributes().getNamedItem("type").getNodeValue());
                        final double value = Double.valueOf(b.getTextContent());
                        _currentItem.item.addFunctionTemplate(new FuncTemplate(null, null, "add", 0x00, type, value));
                    }
                }
            } else if ("skills".equalsIgnoreCase(n.getNodeName())) {
                makeItem();
                for (Node b = n.getFirstChild(); b != null; b = b.getNextSibling()) {
                    if ("skill".equalsIgnoreCase(b.getNodeName())) {
                        final int id = parseInteger(b.getAttributes(), "id");
                        final int level = parseInteger(b.getAttributes(), "level");
                        final ItemSkillType type = parseEnum(b.getAttributes(), ItemSkillType.class, "type", ItemSkillType.NORMAL);
                        final int chance = parseInteger(b.getAttributes(), "type_chance", 100);
                        final int value = parseInteger(b.getAttributes(), "type_value", 0);
                        _currentItem.item.addSkill(new ItemSkillHolder(id, level, type, chance, value));
                    }
                }
            } else if ("capsuled_items".equalsIgnoreCase(n.getNodeName())) {
                makeItem();
                for (Node b = n.getFirstChild(); b != null; b = b.getNextSibling()) {
                    if ("item".equals(b.getNodeName())) {
                        final int id = parseInteger(b.getAttributes(), "id");
                        final int min = parseInteger(b.getAttributes(), "min");
                        final int max = parseInteger(b.getAttributes(), "max");
                        final double chance = parseDouble(b.getAttributes(), "chance");
                        final int minEnchant = parseInteger(b.getAttributes(), "minEnchant", 0);
                        final int maxEnchant = parseInteger(b.getAttributes(), "maxEnchant", 0);
                        _currentItem.item.addCapsuledItem(new L2ExtractableProduct(id, min, max, chance, minEnchant, maxEnchant));
                    }
                }
            } else if ("cond".equalsIgnoreCase(n.getNodeName())) {
                makeItem();
                final Condition condition = parseCondition(n.getFirstChild(), _currentItem.item);
                final Node msg = n.getAttributes().getNamedItem("msg");
                final Node msgId = n.getAttributes().getNamedItem("msgId");
                if ((condition != null) && (msg != null)) {
                    condition.setMessage(msg.getNodeValue());
                } else if ((condition != null) && (msgId != null)) {
                    condition.setMessageId(Integer.decode(getValue(msgId.getNodeValue(), null)));
                    final Node addName = n.getAttributes().getNamedItem("addName");
                    if ((addName != null) && (Integer.decode(getValue(msgId.getNodeValue(), null)) > 0)) {
                        condition.addName();
                    }
                }
                _currentItem.item.attachCondition(condition);
            }
        }
        // bah! in this point item doesn't have to be still created
        makeItem();
    }

    private void makeItem() throws InvocationTargetException {
        // If item exists just reload the data.
        if (_currentItem.item != null) {
            _currentItem.item.set(_currentItem.set);
            return;
        }

        try {
            final Constructor<?> itemClass = Class.forName("org.l2j.gameserver.model.items.L2" + _currentItem.type).getConstructor(StatsSet.class);
            _currentItem.item = (L2Item) itemClass.newInstance(_currentItem.set);
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }

    public List<L2Item> getItemList() {
        return _itemsInFile;
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/items.xsd");
    }

    @Override
    public void load() {
    }

    @Override
    public void parseDocument(Document doc, File f) {
    }
}

package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.items.enchant.EnchantScroll;
import org.l2j.gameserver.model.items.enchant.EnchantSupportItem;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads item enchant data.
 *
 * @author UnAfraid
 */
public class EnchantItemData implements IGameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantItemData.class);

    private final Map<Integer, EnchantScroll> _scrolls = new HashMap<>();
    private final Map<Integer, EnchantSupportItem> _supports = new HashMap<>();

    private EnchantItemData() {
        load();
    }

    @Override
    public synchronized void load() {
        _scrolls.clear();
        _supports.clear();
        parseDatapackFile("data/EnchantItemData.xml");
        LOGGER.info("Loaded {} Enchant Scrolls.", _scrolls.size());
        LOGGER.info("Loaded {} Support Items.", _supports.size() );
    }

    @Override
    public void parseDocument(Document doc, File f) {
        StatsSet set;
        Node att;
        NamedNodeMap attrs;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("enchant".equalsIgnoreCase(d.getNodeName())) {
                        attrs = d.getAttributes();
                        set = new StatsSet();
                        for (int i = 0; i < attrs.getLength(); i++) {
                            att = attrs.item(i);
                            set.set(att.getNodeName(), att.getNodeValue());
                        }

                        try {
                            final EnchantScroll item = new EnchantScroll(set);
                            for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                                if ("item".equalsIgnoreCase(cd.getNodeName())) {
                                    item.addItem(parseInteger(cd.getAttributes(), "id"));
                                }
                            }
                            _scrolls.put(item.getId(), item);
                        } catch (NullPointerException e) {
                            LOGGER.warn("Unexistent enchant scroll: {} defined in enchant data!", set.getString("id") );
                        } catch (IllegalAccessError e) {
                            LOGGER.warn("Wrong enchant scroll item type: {}  defined in enchant data!", set.getString("id"));
                        }
                    } else if ("support".equalsIgnoreCase(d.getNodeName())) {
                        attrs = d.getAttributes();
                        set = new StatsSet();
                        for (int i = 0; i < attrs.getLength(); i++) {
                            att = attrs.item(i);
                            set.set(att.getNodeName(), att.getNodeValue());
                        }

                        try {
                            final EnchantSupportItem item = new EnchantSupportItem(set);
                            _supports.put(item.getId(), item);
                        } catch (NullPointerException e) {
                            LOGGER.warn(": Unexistent enchant support item: " + set.getString("id") + " defined in enchant data!");
                        } catch (IllegalAccessError e) {
                            LOGGER.warn(": Wrong enchant support item type: " + set.getString("id") + " defined in enchant data!");
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the enchant scroll.
     *
     * @param scroll the scroll
     * @return enchant template for scroll
     */
    public final EnchantScroll getEnchantScroll(L2ItemInstance scroll) {
        return _scrolls.get(scroll.getId());
    }

    /**
     * Gets the support item.
     *
     * @param item the item
     * @return enchant template for support item
     */
    public final EnchantSupportItem getSupportItem(L2ItemInstance item) {
        return _supports.get(item.getId());
    }

    public static EnchantItemData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final EnchantItemData INSTANCE = new EnchantItemData();
    }
}

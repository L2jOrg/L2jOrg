package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Loads the category data with Class or NPC IDs.
 *
 * @author NosBit, xban1x
 */
public final class CategoryData extends IGameXmlReader{
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryData.class);

    private final Map<CategoryType, Set<Integer>> _categories = new HashMap<>();

    private CategoryData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/CategoryData.xsd");
    }

    @Override
    public void load() {
        _categories.clear();
        parseDatapackFile("data/CategoryData.xml");
        LOGGER.info("Loaded {} Categories", _categories.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node node = doc.getFirstChild(); node != null; node = node.getNextSibling()) {
            if ("list".equalsIgnoreCase(node.getNodeName())) {
                for (Node list_node = node.getFirstChild(); list_node != null; list_node = list_node.getNextSibling()) {
                    if ("category".equalsIgnoreCase(list_node.getNodeName())) {
                        final NamedNodeMap attrs = list_node.getAttributes();
                        final CategoryType categoryType = CategoryType.findByName(attrs.getNamedItem("name").getNodeValue());
                        if (categoryType == null) {
                            LOGGER.warn("Can't find category by name: {}", attrs.getNamedItem("name").getNodeValue());
                            continue;
                        }

                        final Set<Integer> ids = new HashSet<>();
                        for (Node category_node = list_node.getFirstChild(); category_node != null; category_node = category_node.getNextSibling()) {
                            if ("id".equalsIgnoreCase(category_node.getNodeName())) {
                                ids.add(Integer.parseInt(category_node.getTextContent()));
                            }
                        }
                        _categories.put(categoryType, ids);
                    }
                }
            }
        }
    }

    /**
     * Checks if ID is in category.
     *
     * @param type The category type
     * @param id   The id to be checked
     * @return {@code true} if id is in category, {@code false} if id is not in category or category was not found
     */
    public boolean isInCategory(CategoryType type, int id) {
        final Set<Integer> category = getCategoryByType(type);
        if (category == null) {
            LOGGER.warn(getClass().getSimpleName() + ": Can't find category type: " + type);
            return false;
        }
        return category.contains(id);
    }

    /**
     * Gets the category by category type.
     *
     * @param type The category type
     * @return A {@code Set} containing all the IDs in category if category is found, {@code null} if category was not found
     */
    public Set<Integer> getCategoryByType(CategoryType type) {
        return _categories.get(type);
    }

    public static CategoryData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final CategoryData INSTANCE = new CategoryData();
    }
}

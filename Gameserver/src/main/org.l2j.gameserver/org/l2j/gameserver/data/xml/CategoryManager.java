package org.l2j.gameserver.data.xml;

import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Loads the category data with Class or NPC IDs.
 *
 * @author NosBit, xban1x
 * @author JoeAlisson
 */
public final class CategoryManager extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryManager.class);

    private final Map<CategoryType, IntSet> categories = new HashMap<>();

    private CategoryManager() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/CategoryData.xsd");
    }

    @Override
    public void load() {
        categories.clear();
        parseDatapackFile("data/CategoryData.xml");
        LOGGER.info("Loaded {} Categories", categories.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", list -> forEach(list, "category", category -> {
            var attrs = category.getAttributes();
            var categoryType = parseEnum(attrs, CategoryType.class, "name", null);

            if (isNull(categoryType)) {
                LOGGER.warn("Can't find category by name: {}", attrs.getNamedItem("name").getNodeValue());
                return;
            }

            IntSet set = new HashIntSet();
            forEach(category, "id", idNode -> set.add(Integer.parseInt(idNode.getTextContent())));
            categories.put(categoryType, set);
        }));
    }

    /**
     * Checks if ID is in category.
     *
     * @param type The category type
     * @param id   The id to be checked
     * @return {@code true} if id is in category, {@code false} if id is not in category or category was not found
     */
    public boolean isInCategory(CategoryType type, int id) {
        final IntSet category = getCategoryByType(type);
        if (isNull(category)) {
            LOGGER.warn("Can't find category type: {}", type);
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
    public IntSet getCategoryByType(CategoryType type) {
        return categories.get(type);
    }

    public static void init() {
        getInstance().load();
    }

    public static CategoryManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final CategoryManager INSTANCE = new CategoryManager();
    }
}

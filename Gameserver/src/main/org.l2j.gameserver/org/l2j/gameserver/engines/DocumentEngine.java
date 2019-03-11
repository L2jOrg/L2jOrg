package org.l2j.gameserver.engines;

import org.l2j.commons.util.filter.XMLFilter;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engines.items.DocumentItem;
import org.l2j.gameserver.model.items.L2Item;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author mkizub
 */
public class DocumentEngine {
    private static final Logger LOGGER = Logger.getLogger(DocumentEngine.class.getName());

    private final List<File> _itemFiles = new LinkedList<>();

    protected DocumentEngine() {
        hashFiles("data/stats/items", _itemFiles);
        if (Config.CUSTOM_ITEMS_LOAD) {
            hashFiles("data/stats/items/custom", _itemFiles);
        }
    }

    public static DocumentEngine getInstance() {
        return SingletonHolder._instance;
    }

    private void hashFiles(String dirname, List<File> hash) {
        final File dir = new File(Config.DATAPACK_ROOT, dirname);
        if (!dir.exists()) {
            LOGGER.warning("Dir " + dir.getAbsolutePath() + " not exists");
            return;
        }
        final File[] files = dir.listFiles(new XMLFilter());
        for (File f : files) {
            hash.add(f);
        }
    }

    /**
     * Return created items
     *
     * @return List of {@link L2Item}
     */
    public List<L2Item> loadItems() {
        final List<L2Item> list = new LinkedList<>();
        for (File f : _itemFiles) {
            final DocumentItem document = new DocumentItem(f);
            document.parse();
            list.addAll(document.getItemList());
        }
        return list;
    }

    private static class SingletonHolder {
        protected static final DocumentEngine _instance = new DocumentEngine();
    }
}

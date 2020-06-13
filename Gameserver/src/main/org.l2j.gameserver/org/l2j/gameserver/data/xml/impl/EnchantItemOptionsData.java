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
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.options.EnchantOptions;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.computeIfNonNull;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class EnchantItemOptionsData extends GameXmlReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantItemOptionsData.class);

    private final IntMap<IntMap<EnchantOptions>> data = new HashIntMap<>();

    private EnchantItemOptionsData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/EnchantItemOptions.xsd");
    }

    @Override
    public synchronized void load() {
        data.clear();
        parseDatapackFile("data/EnchantItemOptions.xml");
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", list -> forEach(list, "item", itemNode -> forEach(itemNode, "options", optionsNode -> {
                var attr = optionsNode.getAttributes();
                var option = new EnchantOptions(parseInt(attr, "level"));
                for (byte i = 0; i < 3 ; i++) {
                    option.setOption(i, parseInt(attr, "option" + (i+1)));
                }
                data.computeIfAbsent(parseInt(itemNode.getAttributes(), "id"), id -> new HashIntMap<>()).put(option.getLevel(), option);
            })
        ));
        LOGGER.info("Loaded {} Option Items.", data.size());
    }

    /**
     * @param itemId
     * @param enchantLevel
     * @return enchant effects information.
     */
    public EnchantOptions getOptions(int itemId, int enchantLevel) {
        if (!data.containsKey(itemId) || !data.get(itemId).containsKey(enchantLevel)) {
            return null;
        }
        return data.get(itemId).get(enchantLevel);
    }

    /**
     * @param item
     * @return enchant effects information.
     */
    public EnchantOptions getOptions(Item item) {
        return computeIfNonNull(item, i -> getOptions(i.getId(), i.getEnchantLevel()));
    }

    public static void init() {
        getInstance().load();
    }

    public static EnchantItemOptionsData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final EnchantItemOptionsData INSTANCE = new EnchantItemOptionsData();
    }
}

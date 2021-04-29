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
package org.l2j.gameserver.engine.item;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.computeIfNonNull;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class ItemEnsoulEngine extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemEnsoulEngine.class);

    private final Map<CrystalType, EnsoulFee> ensoulFees = new EnumMap<>(CrystalType.class);
    private final IntMap<EnsoulOption> ensoulOptions = new HashIntMap<>();
    private final IntMap<EnsoulStone> ensoulStones = new HashIntMap<>();

    private ItemEnsoulEngine() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return ServerSettings.dataPackDirectory().resolve("data/xsd/ensoul-stones.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/stats/ensoul-stones.xml");
        LOGGER.info("Loaded {} fees", ensoulFees.size());
        LOGGER.info("Loaded {} options", ensoulOptions.size());
        LOGGER.info("Loaded {} stones", ensoulStones.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, XmlReader::isNode, ensoulNode -> {
            switch (ensoulNode.getNodeName()) {
                case "fee" -> parseFees(ensoulNode);
                case "stone" -> parseStones(ensoulNode);
            }
        }));
    }

    private void parseFees(Node feeNode) {
        ItemHolder common = null;
        ItemHolder special = null;
        ItemHolder replaceCommon = null;
        ItemHolder replaceSpecial = null;
        List<ItemHolder> remove = new ArrayList<>();
        
        for(var feeTypeNode = feeNode.getFirstChild(); nonNull(feeTypeNode); feeTypeNode = feeTypeNode.getNextSibling()) {
            switch (feeTypeNode.getNodeName()) {
                case "common" -> common = parseItemHolder(feeTypeNode);
                case "special" -> special = parseItemHolder(feeTypeNode);
                case "replace-common" -> replaceCommon = parseItemHolder(feeTypeNode);
                case "replace-special" -> replaceSpecial = parseItemHolder(feeTypeNode);
                case "remove" -> remove.add(parseItemHolder(feeTypeNode));
            }
        }

        final CrystalType type = parseEnum(feeNode.getAttributes(), CrystalType.class, "crystalType");
        final EnsoulFee fee = new EnsoulFee(common, special, replaceCommon, replaceSpecial, remove);
        ensoulFees.put(type, fee);
    }

    private void parseStones(Node stoneNode) {
        List<EnsoulOption> options = new ArrayList<>(2);
        for(var optionNode = stoneNode.getFirstChild(); nonNull(optionNode); optionNode = optionNode.getNextSibling()) {
            options.add(parseOption(optionNode));
        }

        final var attrs = stoneNode.getAttributes();
        final var id = parseInt(attrs, "id");
        final var type = parseEnum(attrs, EnsoulType.class, "type");
        final EnsoulStone stone = new EnsoulStone(id,type, options) ;
        ensoulStones.put(id, stone);
    }

    private EnsoulOption parseOption(Node ensoulNode) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        final var id = parseInt(attrs, "id");
        final var name = parseString(attrs, "name");
        final var desc = parseString(attrs, "desc");
        final var skill = new SkillHolder(parseInt(attrs, "skill-id"), parseInt(attrs, "skill-level"));
        var option =  new EnsoulOption(id, name, desc, skill);
        ensoulOptions.put(id, option);
        return option;
    }

    public ItemHolder getEnsoulFee(CrystalType crystal, EnsoulType type) {
        return computeIfNonNull(ensoulFees.get(crystal), fees -> type == EnsoulType.COMMON ? fees.common() : fees.special());
    }

    public ItemHolder getReplaceEnsoulFee(CrystalType crystal, EnsoulType type) {
        return computeIfNonNull(ensoulFees.get(crystal), fees -> type == EnsoulType.COMMON ? fees.replaceCommon() : fees.replaceSpecial());
    }

    public Collection<ItemHolder> getRemovalFee(CrystalType type) {
        final EnsoulFee fee = ensoulFees.get(type);
        return nonNull(fee) ? fee.remove() : Collections.emptyList();
    }

    public EnsoulOption getOption(int id) {
        return ensoulOptions.get(id);
    }

    public EnsoulStone getStone(int id) {
        return ensoulStones.get(id);
    }

    public int getStone(EnsoulType type, int optionId) {
        for (EnsoulStone stone : ensoulStones.values()) {
            if (stone.type() == type) {
                for (var option : stone.options()) {
                    if (option.id() == optionId) {
                        return stone.id();
                    }
                }
            }
        }
        return 0;
    }

    public static void init() {
        getInstance().load();
    }

    public static ItemEnsoulEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ItemEnsoulEngine INSTANCE = new ItemEnsoulEngine();
    }
}

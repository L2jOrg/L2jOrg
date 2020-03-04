package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.model.ensoul.EnsoulFee;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.ensoul.EnsoulStone;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.computeIfNonNull;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class EnsoulData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnsoulData.class);

    private final Map<CrystalType, EnsoulFee> ensoulFees = new EnumMap<>(CrystalType.class);
    private final IntMap<EnsoulOption> ensoulOptions = new HashIntMap<>();
    private final IntMap<EnsoulStone> ensoulStones = new HashIntMap<>();

    private EnsoulData() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/ensoulStones.xsd");
    }

    @Override
    public void load() {
        parseDatapackFile("data/stats/ensoulStones.xml");
        LOGGER.info("Loaded {} fees", ensoulFees.size());
        LOGGER.info("Loaded {} options", ensoulOptions.size());
        LOGGER.info("Loaded {} stones", ensoulStones.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, XmlReader::isNode, ensoulNode -> {
            switch (ensoulNode.getNodeName()) {
                case "fee" -> parseFees(ensoulNode);
                case "option" -> parseOptions(ensoulNode);
                case "stone" -> parseStones(ensoulNode);
            }
        }));
    }

    private void parseFees(Node ensoulNode) {
        final CrystalType type = parseEnum(ensoulNode.getAttributes(), CrystalType.class, "crystalType");
        final EnsoulFee fee = new EnsoulFee(type);
        forEach(ensoulNode, XmlReader::isNode, feeNode -> {
            switch (feeNode.getNodeName()) {
                case "first" -> parseFee(feeNode, fee, 0);
                case "secondary" -> parseFee(feeNode, fee, 1);
                case "third" -> parseFee(feeNode, fee, 2);
                case "reNormal" -> parseReFee(feeNode, fee, 0);
                case "reSecondary" -> parseReFee(feeNode, fee, 1);
                case "reThird" -> parseReFee(feeNode, fee, 2);
                case "remove" -> parseRemove(feeNode, fee);
            }
        });
    }

    private void parseFee(Node ensoulNode, EnsoulFee fee, int index) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        fee.setEnsoul(index, new ItemHolder(parseInt(attrs, "itemId"), parseInteger(attrs, "count")));
        ensoulFees.put(fee.getCrystalType(), fee);
    }

    private void parseReFee(Node ensoulNode, EnsoulFee fee, int index) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        fee.setResoul(index, new ItemHolder(parseInt(attrs, "itemId"),  parseInt(attrs, "count")));
    }

    private void parseRemove(Node ensoulNode, EnsoulFee fee) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        fee.addRemovalFee(new ItemHolder(parseInt(attrs, "itemId"),  parseInt(attrs, "count")));
    }

    private void parseOptions(Node ensoulNode) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        final int id = parseInt(attrs, "id");
        final String name = parseString(attrs, "name");
        final String desc = parseString(attrs, "desc");
        final int skillId = parseInt(attrs, "skillId");
        final int skillLevel = parseInt(attrs, "skillLevel");
        final EnsoulOption option = new EnsoulOption(id, name, desc, skillId, skillLevel);
        ensoulOptions.put(option.getId(), option);
    }

    private void parseStones(Node ensoulNode) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        final EnsoulStone stone = new EnsoulStone(parseInt(attrs, "id"), parseInt(attrs, "slotType"));
        forEach(ensoulNode, "option", optionNode -> stone.addOption(parseInteger(optionNode.getAttributes(), "id")));
        ensoulStones.put(stone.getId(), stone);
    }

    public ItemHolder getEnsoulFee(CrystalType type, int index) {
        return computeIfNonNull(ensoulFees.get(type), e -> e.getEnsoul(index));
    }

    public ItemHolder getResoulFee(CrystalType type, int index) {
        return computeIfNonNull(ensoulFees.get(type), e -> e.getResoul(index));
    }

    public Collection<ItemHolder> getRemovalFee(CrystalType type) {
        final EnsoulFee fee = ensoulFees.get(type);
        return fee != null ? fee.getRemovalFee() : Collections.emptyList();
    }

    public EnsoulOption getOption(int id) {
        return ensoulOptions.get(id);
    }

    public EnsoulStone getStone(int id) {
        return ensoulStones.get(id);
    }

    public int getStone(int type, int optionId) {
        for (EnsoulStone stone : ensoulStones.values()) {
            if (stone.getSlotType() == type) {
                for (int id : stone.getOptions()) {
                    if (id == optionId) {
                        return stone.getId();
                    }
                }
            }
        }
        return 0;
    }

    public static void init() {
        getInstance().load();
    }

    public static EnsoulData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final EnsoulData INSTANCE = new EnsoulData();
    }
}

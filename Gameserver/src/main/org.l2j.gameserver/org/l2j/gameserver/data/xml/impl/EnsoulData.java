package org.l2j.gameserver.data.xml.impl;

import org.l2j.commons.util.IXmlReader;
import org.l2j.gameserver.model.ensoul.EnsoulFee;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.ensoul.EnsoulStone;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;

/**
 * @author UnAfraid
 */
public class EnsoulData implements IGameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnsoulData.class.getName());
    private final Map<CrystalType, EnsoulFee> _ensoulFees = new EnumMap<>(CrystalType.class);
    private final Map<Integer, EnsoulOption> _ensoulOptions = new HashMap<>();
    private final Map<Integer, EnsoulStone> _ensoulStones = new HashMap<>();

    private EnsoulData() {
        load();
    }

    @Override
    public void load() {
        parseDatapackDirectory("data/stats/ensoul", true);
        LOGGER.info("Loaded {} fees", _ensoulFees.size());
        LOGGER.info("Loaded {} options", _ensoulOptions.size());
        LOGGER.info("Loaded {} stones", _ensoulStones.size());
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, IXmlReader::isNode, ensoulNode ->
        {
            switch (ensoulNode.getNodeName()) {
                case "fee": {
                    parseFees(ensoulNode);
                    break;
                }
                case "option": {
                    parseOptions(ensoulNode);
                    break;
                }
                case "stone": {
                    parseStones(ensoulNode);
                    break;
                }
            }
        }));
    }

    private void parseFees(Node ensoulNode) {
        final CrystalType type = parseEnum(ensoulNode.getAttributes(), CrystalType.class, "crystalType");
        final EnsoulFee fee = new EnsoulFee(type);
        forEach(ensoulNode, IXmlReader::isNode, feeNode ->
        {
            switch (feeNode.getNodeName()) {
                case "first": {
                    parseFee(feeNode, fee, 0);
                    break;
                }
                case "secondary": {
                    parseFee(feeNode, fee, 1);
                    break;
                }
                case "third": {
                    parseFee(feeNode, fee, 2);
                    break;
                }
                case "reNormal": {
                    parseReFee(feeNode, fee, 0);
                    break;
                }
                case "reSecondary": {
                    parseReFee(feeNode, fee, 1);
                    break;
                }
                case "reThird": {
                    parseReFee(feeNode, fee, 2);
                    break;
                }
                case "remove": {
                    parseRemove(feeNode, fee);
                    break;
                }
            }
        });
    }

    private void parseFee(Node ensoulNode, EnsoulFee fee, int index) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        final int id = parseInteger(attrs, "itemId");
        final int count = parseInteger(attrs, "count");
        fee.setEnsoul(index, new ItemHolder(id, count));
        _ensoulFees.put(fee.getCrystalType(), fee);
    }

    private void parseReFee(Node ensoulNode, EnsoulFee fee, int index) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        final int id = parseInteger(attrs, "itemId");
        final int count = parseInteger(attrs, "count");
        fee.setResoul(index, new ItemHolder(id, count));
    }

    private void parseRemove(Node ensoulNode, EnsoulFee fee) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        final int id = parseInteger(attrs, "itemId");
        final int count = parseInteger(attrs, "count");
        fee.addRemovalFee(new ItemHolder(id, count));
    }

    private void parseOptions(Node ensoulNode) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        final int id = parseInteger(attrs, "id");
        final String name = parseString(attrs, "name");
        final String desc = parseString(attrs, "desc");
        final int skillId = parseInteger(attrs, "skillId");
        final int skillLevel = parseInteger(attrs, "skillLevel");
        final EnsoulOption option = new EnsoulOption(id, name, desc, skillId, skillLevel);
        _ensoulOptions.put(option.getId(), option);
    }

    private void parseStones(Node ensoulNode) {
        final NamedNodeMap attrs = ensoulNode.getAttributes();
        final int id = parseInteger(attrs, "id");
        final int slotType = parseInteger(attrs, "slotType");
        final EnsoulStone stone = new EnsoulStone(id, slotType);
        forEach(ensoulNode, "option", optionNode -> stone.addOption(parseInteger(optionNode.getAttributes(), "id")));
        _ensoulStones.put(stone.getId(), stone);
    }

    public ItemHolder getEnsoulFee(CrystalType type, int index) {
        final EnsoulFee fee = _ensoulFees.get(type);
        return fee != null ? fee.getEnsoul(index) : null;
    }

    public ItemHolder getResoulFee(CrystalType type, int index) {
        final EnsoulFee fee = _ensoulFees.get(type);
        return fee != null ? fee.getResoul(index) : null;
    }

    public Collection<ItemHolder> getRemovalFee(CrystalType type) {
        final EnsoulFee fee = _ensoulFees.get(type);
        return fee != null ? fee.getRemovalFee() : Collections.emptyList();
    }

    public EnsoulOption getOption(int id) {
        return _ensoulOptions.get(id);
    }

    public EnsoulStone getStone(int id) {
        return _ensoulStones.get(id);
    }

    public int getStone(int type, int optionId) {
        for (EnsoulStone stone : _ensoulStones.values()) {
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

    public static EnsoulData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final EnsoulData INSTANCE = new EnsoulData();
    }
}

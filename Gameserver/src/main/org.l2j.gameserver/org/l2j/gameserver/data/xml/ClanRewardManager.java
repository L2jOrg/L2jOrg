package org.l2j.gameserver.data.xml;

import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author UnAfraid
 */
public class ClanRewardManager extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClanRewardManager.class);

    private final Map<ClanRewardType, List<ClanRewardBonus>> clanRewards = new ConcurrentHashMap<>();

    private ClanRewardManager() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return Path.of("config/xsd/ClanReward.xsd");
    }

    @Override
    public void load() {
        parseFile(new File("config/ClanReward.xml"));
        for (ClanRewardType type : ClanRewardType.values()) {
            LOGGER.info("Loaded {} rewards for {}", (clanRewards.containsKey(type) ? clanRewards.get(type).size() : 0), type);
        }
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc.getFirstChild(), XmlReader::isNode, listNode -> {
            switch (listNode.getNodeName()) {
                case "membersOnline" -> parseMembersOnline(listNode);
                case "huntingBonus" -> parseHuntingBonus(listNode);
            }
        });
    }

    private void parseMembersOnline(Node node) {
        forEach(node, XmlReader::isNode, memberNode -> {
            if ("players".equalsIgnoreCase(memberNode.getNodeName())) {

                final NamedNodeMap attrs = memberNode.getAttributes();
                final int requiredAmount = parseInteger(attrs, "size");
                final int level = parseInteger(attrs, "level");
                final ClanRewardBonus bonus = new ClanRewardBonus(ClanRewardType.MEMBERS_ONLINE, level, requiredAmount);

                forEach(memberNode, XmlReader::isNode, skillNode -> {
                    if ("skill".equalsIgnoreCase(skillNode.getNodeName())) {
                        final NamedNodeMap skillAttr = skillNode.getAttributes();
                        final int skillId = parseInteger(skillAttr, "id");
                        final int skillLevel = parseInteger(skillAttr, "level");
                        bonus.setSkillReward(new SkillHolder(skillId, skillLevel));
                    }
                });

                clanRewards.computeIfAbsent(bonus.getType(), key -> new ArrayList<>()).add(bonus);
            }
        });
    }

    private void parseHuntingBonus(Node node) {
        forEach(node, XmlReader::isNode, memberNode -> {

            if ("hunting".equalsIgnoreCase(memberNode.getNodeName())) {
                final NamedNodeMap attrs = memberNode.getAttributes();
                final int requiredAmount = parseInteger(attrs, "points");
                final int level = parseInteger(attrs, "level");
                final ClanRewardBonus bonus = new ClanRewardBonus(ClanRewardType.HUNTING_MONSTERS, level, requiredAmount);

                forEach(memberNode, XmlReader::isNode, itemsNode -> {
                    if ("item".equalsIgnoreCase(itemsNode.getNodeName())) {
                        final NamedNodeMap itemsAttr = itemsNode.getAttributes();
                        final int id = parseInteger(itemsAttr, "id");
                        final int count = parseInteger(itemsAttr, "count");
                        bonus.setItemReward(new ItemHolder(id, count));
                    }
                });

                clanRewards.computeIfAbsent(bonus.getType(), key -> new ArrayList<>()).add(bonus);
            }
        });
    }

    public List<ClanRewardBonus> getClanRewardBonuses(ClanRewardType type) {
        return clanRewards.get(type);
    }

    public ClanRewardBonus getHighestReward(ClanRewardType type) {
        ClanRewardBonus selectedBonus = null;
        for (ClanRewardBonus currentBonus : clanRewards.get(type)) {
            if ((selectedBonus == null) || (selectedBonus.getLevel() < currentBonus.getLevel())) {
                selectedBonus = currentBonus;
            }
        }
        return selectedBonus;
    }

    public static void init() {
        getInstance().load();
    }

    public static ClanRewardManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ClanRewardManager INSTANCE = new ClanRewardManager();
    }
}
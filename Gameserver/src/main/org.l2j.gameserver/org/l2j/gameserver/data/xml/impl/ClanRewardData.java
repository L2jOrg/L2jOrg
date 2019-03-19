package org.l2j.gameserver.data.xml.impl;

import org.l2j.commons.util.IXmlReader;
import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author UnAfraid
 */
public class ClanRewardData implements IGameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClanRewardData.class);

    private final Map<ClanRewardType, List<ClanRewardBonus>> _clanRewards = new ConcurrentHashMap<>();

    private ClanRewardData() {
        load();
    }

    @Override
    public void load() {
        parseFile(new File("config/ClanReward.xml"));
        for (ClanRewardType type : ClanRewardType.values()) {
            LOGGER.info("Loaded: {} rewards for {}", (_clanRewards.containsKey(type) ? _clanRewards.get(type).size() : 0), type);
        }
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc.getFirstChild(), IXmlReader::isNode, listNode ->
        {
            switch (listNode.getNodeName()) {
                case "membersOnline": {
                    parseMembersOnline(listNode);
                    break;
                }
                case "huntingBonus": {
                    parseHuntingBonus(listNode);
                    break;
                }
            }
        });
    }

    private void parseMembersOnline(Node node) {
        forEach(node, IXmlReader::isNode, memberNode ->
        {
            if ("players".equalsIgnoreCase(memberNode.getNodeName())) {
                final NamedNodeMap attrs = memberNode.getAttributes();
                final int requiredAmount = parseInteger(attrs, "size");
                final int level = parseInteger(attrs, "level");
                final ClanRewardBonus bonus = new ClanRewardBonus(ClanRewardType.MEMBERS_ONLINE, level, requiredAmount);
                forEach(memberNode, IXmlReader::isNode, skillNode ->
                {
                    if ("skill".equalsIgnoreCase(skillNode.getNodeName())) {
                        final NamedNodeMap skillAttr = skillNode.getAttributes();
                        final int skillId = parseInteger(skillAttr, "id");
                        final int skillLevel = parseInteger(skillAttr, "level");
                        bonus.setSkillReward(new SkillHolder(skillId, skillLevel));
                    }
                });
                _clanRewards.computeIfAbsent(bonus.getType(), key -> new ArrayList<>()).add(bonus);
            }
        });
    }

    private void parseHuntingBonus(Node node) {
        forEach(node, IXmlReader::isNode, memberNode ->
        {
            if ("hunting".equalsIgnoreCase(memberNode.getNodeName())) {
                final NamedNodeMap attrs = memberNode.getAttributes();
                final int requiredAmount = parseInteger(attrs, "points");
                final int level = parseInteger(attrs, "level");
                final ClanRewardBonus bonus = new ClanRewardBonus(ClanRewardType.HUNTING_MONSTERS, level, requiredAmount);
                forEach(memberNode, IXmlReader::isNode, itemsNode ->
                {
                    if ("item".equalsIgnoreCase(itemsNode.getNodeName())) {
                        final NamedNodeMap itemsAttr = itemsNode.getAttributes();
                        final int id = parseInteger(itemsAttr, "id");
                        final int count = parseInteger(itemsAttr, "count");
                        bonus.setItemReward(new ItemHolder(id, count));
                    }
                });
                _clanRewards.computeIfAbsent(bonus.getType(), key -> new ArrayList<>()).add(bonus);
            }
        });
    }

    public List<ClanRewardBonus> getClanRewardBonuses(ClanRewardType type) {
        return _clanRewards.get(type);
    }

    public ClanRewardBonus getHighestReward(ClanRewardType type) {
        ClanRewardBonus selectedBonus = null;
        for (ClanRewardBonus currentBonus : _clanRewards.get(type)) {
            if ((selectedBonus == null) || (selectedBonus.getLevel() < currentBonus.getLevel())) {
                selectedBonus = currentBonus;
            }
        }
        return selectedBonus;
    }

    public Collection<List<ClanRewardBonus>> getClanRewardBonuses() {
        return _clanRewards.values();
    }

    public static ClanRewardData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ClanRewardData INSTANCE = new ClanRewardData();
    }
}

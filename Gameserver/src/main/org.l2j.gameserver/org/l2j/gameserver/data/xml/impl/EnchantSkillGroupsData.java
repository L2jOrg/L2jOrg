package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.EnchantSkillHolder;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * This class holds the Enchant Groups information.
 *
 * @author Micr0
 */
public class EnchantSkillGroupsData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantSkillGroupsData.class);
    public static int MAX_ENCHANT_LEVEL;
    private final Map<Integer, EnchantSkillHolder> _enchantSkillHolders = new LinkedHashMap<>();
    private final Map<SkillHolder, Set<Integer>> _enchantSkillTrees = new HashMap<>();

    private EnchantSkillGroupsData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/EnchantSkillGroups.xsd");
    }

    @Override
    public void load() {
        _enchantSkillHolders.clear();
        parseDatapackFile("data/EnchantSkillGroups.xml");
        MAX_ENCHANT_LEVEL = _enchantSkillHolders.size();
        LOGGER.info("Loaded {}  enchant routes, max enchant set to {}", _enchantSkillHolders.size(), MAX_ENCHANT_LEVEL);
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "enchant", enchantNode ->
        {
            final EnchantSkillHolder enchantSkillHolder = new EnchantSkillHolder(new StatsSet(parseAttributes(enchantNode)));

            forEach(enchantNode, "sps", spsNode -> forEach(spsNode, "sp", spNode ->
            {
                enchantSkillHolder.addSp(parseEnum(spNode.getAttributes(), SkillEnchantType.class, "type"), parseInteger(spNode.getAttributes(), "amount"));
            }));

            forEach(enchantNode, "chances", chancesNode -> forEach(chancesNode, "chance", chanceNode ->
            {
                enchantSkillHolder.addChance(parseEnum(chanceNode.getAttributes(), SkillEnchantType.class, "type"), parseInteger(chanceNode.getAttributes(), "value"));
            }));

            forEach(enchantNode, "items", itemsNode -> forEach(itemsNode, "item", itemNode ->
            {
                enchantSkillHolder.addRequiredItem(parseEnum(itemNode.getAttributes(), SkillEnchantType.class, "type"), new ItemHolder(new StatsSet(parseAttributes(itemNode))));
            }));

            _enchantSkillHolders.put(parseInteger(enchantNode.getAttributes(), "level"), enchantSkillHolder);
        }));
    }

    public void addRouteForSkill(int skillId, int level, int route) {
        addRouteForSkill(new SkillHolder(skillId, level), route);
    }

    public void addRouteForSkill(SkillHolder holder, int route) {
        _enchantSkillTrees.computeIfAbsent(holder, k -> new HashSet<>()).add(route);
    }

    public Set<Integer> getRouteForSkill(int skillId, int level) {
        return getRouteForSkill(skillId, level, 0);
    }

    public Set<Integer> getRouteForSkill(int skillId, int level, int subLevel) {
        return getRouteForSkill(new SkillHolder(skillId, level, subLevel));
    }

    public Set<Integer> getRouteForSkill(SkillHolder holder) {
        return _enchantSkillTrees.getOrDefault(holder, Collections.emptySet());
    }

    public boolean isEnchantable(Skill skill) {
        return isEnchantable(new SkillHolder(skill.getId(), skill.getLevel()));
    }

    public boolean isEnchantable(SkillHolder holder) {
        return _enchantSkillTrees.containsKey(holder);
    }

    public EnchantSkillHolder getEnchantSkillHolder(int level) {
        return _enchantSkillHolders.getOrDefault(level, null);
    }

    public static EnchantSkillGroupsData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final EnchantSkillGroupsData INSTANCE = new EnchantSkillGroupsData();
    }
}
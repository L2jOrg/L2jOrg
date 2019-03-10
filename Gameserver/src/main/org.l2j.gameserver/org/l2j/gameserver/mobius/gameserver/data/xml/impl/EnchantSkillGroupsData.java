package org.l2j.gameserver.mobius.gameserver.data.xml.impl;

import org.l2j.gameserver.mobius.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.holders.EnchantSkillHolder;
import org.l2j.gameserver.mobius.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.mobius.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.util.IGameXmlReader;
import org.w3c.dom.Document;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class holds the Enchant Groups information.
 *
 * @author Micr0
 */
public class EnchantSkillGroupsData implements IGameXmlReader {
    private static final Logger LOGGER = Logger.getLogger(EnchantSkillGroupsData.class.getName());
    public static int MAX_ENCHANT_LEVEL;
    private final Map<Integer, EnchantSkillHolder> _enchantSkillHolders = new LinkedHashMap<>();
    private final Map<SkillHolder, Set<Integer>> _enchantSkillTrees = new HashMap<>();

    /**
     * Instantiates a new enchant groups table.
     */
    protected EnchantSkillGroupsData() {
        load();
    }

    public static EnchantSkillGroupsData getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void load() {
        _enchantSkillHolders.clear();
        parseDatapackFile("data/EnchantSkillGroups.xml");
        MAX_ENCHANT_LEVEL = _enchantSkillHolders.size();
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _enchantSkillHolders.size() + " enchant routes, max enchant set to " + MAX_ENCHANT_LEVEL + ".");
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

    private static class SingletonHolder {
        protected static final EnchantSkillGroupsData _instance = new EnchantSkillGroupsData();
    }
}
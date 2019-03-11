package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.model.StatsSet;

import java.util.*;

/**
 * @author Sdw
 */
public class EnchantSkillHolder {
    private final int _level;
    private final int _enchantFailLevel;
    private final Map<SkillEnchantType, Long> _sp = new EnumMap<>(SkillEnchantType.class);
    private final Map<SkillEnchantType, Integer> _chance = new EnumMap<>(SkillEnchantType.class);
    private final Map<SkillEnchantType, Set<ItemHolder>> _requiredItems = new EnumMap<>(SkillEnchantType.class);

    public EnchantSkillHolder(StatsSet set) {
        _level = set.getInt("level");
        _enchantFailLevel = set.getInt("enchantFailLevel");
    }

    public int getLevel() {
        return _level;
    }

    public int getEnchantFailLevel() {
        return _enchantFailLevel;
    }

    public void addSp(SkillEnchantType type, long sp) {
        _sp.put(type, sp);
    }

    public long getSp(SkillEnchantType type) {
        return _sp.getOrDefault(type, 0L);
    }

    public void addChance(SkillEnchantType type, int chance) {
        _chance.put(type, chance);
    }

    public int getChance(SkillEnchantType type) {
        return _chance.getOrDefault(type, 100);
    }

    public void addRequiredItem(SkillEnchantType type, ItemHolder item) {
        _requiredItems.computeIfAbsent(type, k -> new HashSet<>()).add(item);
    }

    public Set<ItemHolder> getRequiredItems(SkillEnchantType type) {
        return _requiredItems.getOrDefault(type, Collections.emptySet());
    }
}

package org.l2j.gameserver.engine.item.enchant;

import java.util.Collection;

/**
 * @author JoeAlisson
 */
public record EnchantFailReward(int id, Collection<RangedEnchantFailAmount> values) {

    public long amount(int enchantLevel) {
        return values.stream().filter(v -> v.isValid(enchantLevel)).findFirst().map(v -> v.amount(enchantLevel)).orElse(0);
    }

    public void add(RangedEnchantFailAmount rangedEnchantFailAmount) {
        values.add(rangedEnchantFailAmount);
    }
}

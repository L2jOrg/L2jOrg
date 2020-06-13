package org.l2j.gameserver.engine.item.enchant;

import org.l2j.gameserver.model.item.instance.Item;

import java.util.List;

/**
 * @author JoeAlisson
 */
public record ScrollGroup(List<EnchantChance> enchantChances) {

    public double enchantChanceForItem(Item item) {
        return enchantChances.stream()
                .filter(enchantChance -> enchantChance.isValid(item))
                .map(EnchantChance::group)
                .flatMap(g -> g.chances().stream())
                .filter(rangedChance -> rangedChance.isValid(item.getEnchantLevel()))
                .mapToDouble(RangedChance::chance).findFirst().orElse(0);
    }
}

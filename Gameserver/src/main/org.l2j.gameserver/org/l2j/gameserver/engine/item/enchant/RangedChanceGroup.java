package org.l2j.gameserver.engine.item.enchant;

import org.l2j.gameserver.model.item.instance.Item;

import java.util.Collection;

import static org.l2j.commons.util.Util.isBetween;

/**
 * @author JoeAlisson
 *
 */
public record RangedChanceGroup(int min, int max, Collection<RangedChance> chances) {

    public boolean isValid(Item item) {
        return isBetween(item.getEnchantLevel(), min, max);
    }
}

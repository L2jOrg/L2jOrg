package org.l2j.gameserver.engine.item.enchant;

import org.l2j.gameserver.model.item.instance.Item;

import static org.l2j.commons.util.Util.isBetween;

/**
 * @author JoeAlisson
 */
public record RangedChance(int from, int until, float chance) {

    public boolean isValid(Item item) {
        return isBetween(item.getEnchantLevel(), from, until);
    }
}

package org.l2j.gameserver.engine.item.enchant;

import org.l2j.commons.util.Util;

/**
 * @author JoeAlisson
 */
public record RangedEnchantFailAmount(int from, int until, int value, int bonusPerEnchant) {

    public boolean isValid(int value) {
        return Util.isBetween(value, from, until);
    }

    public int amount(int enchantLevel) {
        return value + (enchantLevel - from) * bonusPerEnchant;
    }
}

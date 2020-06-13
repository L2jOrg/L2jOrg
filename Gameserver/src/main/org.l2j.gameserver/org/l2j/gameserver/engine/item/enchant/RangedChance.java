package org.l2j.gameserver.engine.item.enchant;

import static org.l2j.commons.util.Util.isBetween;

/**
 * @author JoeAlisson
 */
public record RangedChance(int from, int until, float chance) {

    public boolean isValid(int value) {
        return isBetween(value, from, until);
    }
}

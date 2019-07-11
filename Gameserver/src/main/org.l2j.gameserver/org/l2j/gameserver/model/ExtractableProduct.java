package org.l2j.gameserver.model;

/**
 * @author JIV
 */
public class ExtractableProduct {
    private final int _id;
    private final int _min;
    private final int _max;
    private final int _chance;
    private final int _minEnchant;
    private final int _maxEnchant;

    /**
     * Create Extractable product
     *
     * @param id         create item id
     * @param min        item count max
     * @param max        item count min
     * @param chance     chance for creating
     * @param minEnchant item min enchant
     * @param maxEnchant item max enchant
     */
    public ExtractableProduct(int id, int min, int max, double chance, int minEnchant, int maxEnchant) {
        _id = id;
        _min = min;
        _max = max;
        _chance = (int) (chance * 1000);
        _minEnchant = minEnchant;
        _maxEnchant = maxEnchant;
    }

    public int getId() {
        return _id;
    }

    public int getMin() {
        return _min;
    }

    public int getMax() {
        return _max;
    }

    public int getChance() {
        return _chance;
    }

    public int getMinEnchant() {
        return _minEnchant;
    }

    public int getMaxEnchant() {
        return _maxEnchant;
    }
}

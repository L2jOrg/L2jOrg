package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.StatType;

/**
 * This class describes a RecipeList statUse and altStatChange component.
 */
public class RecipeStat {
    /**
     * The Identifier of the statType
     */
    private final StatType _type;

    /**
     * The value of the statType
     */
    private final int _value;

    /**
     * Constructor of RecipeStat.
     *
     * @param type
     * @param value
     */
    public RecipeStat(String type, int value) {
        _type = Enum.valueOf(StatType.class, type);
        _value = value;
    }

    /**
     * @return the the type of the RecipeStat.
     */
    public StatType getType() {
        return _type;
    }

    /**
     * @return the value of the RecipeStat.
     */
    public int getValue() {
        return _value;
    }
}

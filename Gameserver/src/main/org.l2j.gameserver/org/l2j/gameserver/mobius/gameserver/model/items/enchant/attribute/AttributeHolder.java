package org.l2j.gameserver.mobius.gameserver.model.items.enchant.attribute;

import org.l2j.gameserver.mobius.gameserver.enums.AttributeType;

/**
 * @author UnAfraid
 */
public class AttributeHolder
{
    private final AttributeType _type;
    private int _value;

    public AttributeHolder(AttributeType type, int value)
    {
        _type = type;
        _value = value;
    }

    public AttributeType getType()
    {
        return _type;
    }

    public int getValue()
    {
        return _value;
    }

    public void setValue(int value)
    {
        _value = value;
    }

    public void incValue(int with)
    {
        _value += with;
    }

    @Override
    public String toString()
    {
        return _type.name() + " +" + _value;
    }
}

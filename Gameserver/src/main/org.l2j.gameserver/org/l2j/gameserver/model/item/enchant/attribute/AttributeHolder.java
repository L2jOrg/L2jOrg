/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.item.enchant.attribute;

import org.l2j.gameserver.enums.AttributeType;

/**
 * @author UnAfraid
 */
public class AttributeHolder {
    private final AttributeType _type;
    private int _value;

    public AttributeHolder(AttributeType type, int value) {
        _type = type;
        _value = value;
    }

    public AttributeType getType() {
        return _type;
    }

    public int getValue() {
        return _value;
    }

    public void setValue(int value) {
        _value = value;
    }

    public void incValue(int with) {
        _value += with;
    }

    @Override
    public String toString() {
        return _type.name() + " +" + _value;
    }
}

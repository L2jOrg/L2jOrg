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
package org.l2j.gameserver.util;

/**
 * @param <E> The enum type
 * @author HorridoJoho
 */
public final class EnumIntBitmask<E extends Enum<E>> implements Cloneable {
    private final Class<E> _enumClass;
    private int _bitmask;

    public EnumIntBitmask(Class<E> enumClass, boolean all) {
        _enumClass = enumClass;

        final E[] values = _enumClass.getEnumConstants();
        if (values.length > 32) {
            throw new IllegalArgumentException("Enum too big for an integer bitmask.");
        }

        if (all) {
            setAll();
        } else {
            clear();
        }
    }

    public EnumIntBitmask(Class<E> enumClass, int bitmask) {
        _enumClass = enumClass;
        _bitmask = bitmask;
    }

    public static <E extends Enum<E>> int getAllBitmask(Class<E> enumClass) {
        int allBitmask = 0;
        final E[] values = enumClass.getEnumConstants();
        if (values.length > 32) {
            throw new IllegalArgumentException("Enum too big for an integer bitmask.");
        }
        for (E value : values) {
            allBitmask |= 1 << value.ordinal();
        }
        return allBitmask;
    }

    public void setAll() {
        set(_enumClass.getEnumConstants());
    }

    public void clear() {
        _bitmask = 0;
    }

    @SafeVarargs
    public final void set(E... many) {
        clear();
        for (E one : many) {
            _bitmask |= 1 << one.ordinal();
        }
    }

    @SafeVarargs
    public final void set(E first, E... more) {
        clear();
        add(first, more);
    }

    @SafeVarargs
    public final void add(E first, E... more) {
        _bitmask |= 1 << first.ordinal();
        if (more != null) {
            for (E one : more) {
                _bitmask |= 1 << one.ordinal();
            }
        }
    }

    @SafeVarargs
    public final void remove(E first, E... more) {
        _bitmask &= ~(1 << first.ordinal());
        if (more != null) {
            for (E one : more) {
                _bitmask &= ~(1 << one.ordinal());
            }
        }
    }

    @SafeVarargs
    public final boolean has(E first, E... more) {
        if ((_bitmask & (1 << first.ordinal())) == 0) {
            return false;
        }

        for (E one : more) {
            if ((_bitmask & (1 << one.ordinal())) == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public EnumIntBitmask<E> clone() {
        return new EnumIntBitmask<>(_enumClass, _bitmask);
    }

    public int getBitmask() {
        return _bitmask;
    }

    public void setBitmask(int bitmask) {
        _bitmask = bitmask;
    }
}

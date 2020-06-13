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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;

/**
 * @param <T>
 * @author UnAfraid
 */
public abstract class AbstractMaskPacket<T extends IUpdateTypeComponent> extends ServerPacket {
    protected static final byte[] DEFAULT_FLAG_ARRAY = { (byte) 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01 };

    protected abstract byte[] getMasks();

    protected void onNewMaskAdded(T component) {
    }

    @SafeVarargs
    public final void addComponentType(T... updateComponents) {
        for (T component : updateComponents) {
            if (!containsMask(component)) {
                addMask(component.getMask());
                onNewMaskAdded(component);
            }
        }
    }

    protected void addMask(int mask) {
        getMasks()[mask >> 3] |= DEFAULT_FLAG_ARRAY[mask & 7];
    }

    public boolean containsMask(T component) {
        return containsMask(component.getMask());
    }

    public boolean containsMask(int mask) {
        return (getMasks()[mask >> 3] & DEFAULT_FLAG_ARRAY[mask & 7]) != 0;
    }

    public boolean containsMask(int masks, T type) {
        return (masks & type.getMask()) == type.getMask();
    }
}

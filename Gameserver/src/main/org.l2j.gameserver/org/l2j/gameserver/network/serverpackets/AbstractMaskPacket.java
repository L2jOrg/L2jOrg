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

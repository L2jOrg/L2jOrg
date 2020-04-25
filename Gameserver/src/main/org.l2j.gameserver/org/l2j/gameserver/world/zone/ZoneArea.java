package org.l2j.gameserver.world.zone;

import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * Abstract base class for any zone area
 *
 * @author durgus
 */
public abstract class ZoneArea {
    protected static final int STEP = 10;

    public abstract boolean isInsideZone(int x, int y, int z);

    public abstract boolean intersectsRectangle(int x1, int x2, int y1, int y2);

    public abstract double getDistanceToZone(int x, int y);

    public abstract int getLowZ(); // Support for the ability to extract the z coordinates of zones.

    public abstract int getHighZ(); // New fishing patch makes use of that to get the Z for the hook

    public abstract void visualizeZone(int z);

    // TODO Drop this
    protected final void dropDebugItem(int itemId, int num, int x, int y, int z) {

        final Item item = new Item(IdFactory.getInstance().getNextId(), itemId);
        item.setCount(num);
        item.spawnMe(x, y, z + 5);
        ZoneManager.getInstance().getDebugItems().add(item);
    }

    public abstract Location getRandomPoint();
}

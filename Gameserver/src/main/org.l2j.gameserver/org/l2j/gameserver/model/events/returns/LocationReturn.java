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
package org.l2j.gameserver.model.events.returns;

import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.interfaces.ILocational;

/**
 * @author Nik
 */
public class LocationReturn extends TerminateReturn {
    private final boolean _overrideLocation;
    private int _x;
    private int _y;
    private int _z;
    private int _heading;
    private Instance _instance;

    public LocationReturn(boolean terminate, boolean overrideLocation) {
        super(terminate, false, false);
        _overrideLocation = overrideLocation;
    }

    public LocationReturn(boolean terminate, boolean overrideLocation, ILocational targetLocation, Instance instance) {
        super(terminate, false, false);
        _overrideLocation = overrideLocation;

        if (targetLocation != null) {
            setX(targetLocation.getX());
            setY(targetLocation.getY());
            setZ(targetLocation.getZ());
            setHeading(targetLocation.getHeading());
            setInstance(instance);
        }
    }

    public boolean overrideLocation() {
        return _overrideLocation;
    }

    public int getX() {
        return _x;
    }

    public void setX(int x) {
        _x = x;
    }

    public int getY() {
        return _y;
    }

    public void setY(int y) {
        _y = y;
    }

    public int getZ() {
        return _z;
    }

    public void setZ(int z) {
        _z = z;
    }

    public int getHeading() {
        return _heading;
    }

    public void setHeading(int heading) {
        _heading = heading;
    }

    public Instance getInstance() {
        return _instance;
    }

    public void setInstance(Instance instance) {
        _instance = instance;
    }
}

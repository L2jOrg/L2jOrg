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
package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.instancezone.Instance;

/**
 * @author Nik
 */
public class OnCreatureTeleport implements IBaseEvent {
    private final Creature _creature;
    private final int _destX;
    private final int _destY;
    private final int _destZ;
    private final int _destHeading;
    private final Instance _destInstance;

    public OnCreatureTeleport(Creature creature, int destX, int destY, int destZ, int destHeading, Instance destInstance) {
        _creature = creature;
        _destX = destX;
        _destY = destY;
        _destZ = destZ;
        _destHeading = destHeading;
        _destInstance = destInstance;
    }

    public Creature getCreature() {
        return _creature;
    }

    public int getDestX() {
        return _destX;
    }

    public int getDestY() {
        return _destY;
    }

    public int getDestZ() {
        return _destZ;
    }

    public int getDestHeading() {
        return _destHeading;
    }

    public Instance getDestInstance() {
        return _destInstance;
    }

    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_TELEPORT;
    }
}

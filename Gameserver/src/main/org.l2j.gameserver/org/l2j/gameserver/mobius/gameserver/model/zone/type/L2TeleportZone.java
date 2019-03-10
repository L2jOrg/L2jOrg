/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.zone.type;

import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.zone.L2ZoneType;

/**
 * @author Sdw
 */
public class L2TeleportZone extends L2ZoneType {
    private int _x = -1;
    private int _y = -1;
    private int _z = -1;

    public L2TeleportZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        switch (name) {
            case "oustX": {
                _x = Integer.parseInt(value);
                break;
            }
            case "oustY": {
                _y = Integer.parseInt(value);
                break;
            }
            case "oustZ": {
                _z = Integer.parseInt(value);
                break;
            }
            default: {
                super.setParameter(name, value);
            }
        }
    }

    @Override
    protected void onEnter(L2Character character) {
        character.teleToLocation(new Location(_x, _y, _z));
    }

    @Override
    protected void onExit(L2Character character) {
    }
}
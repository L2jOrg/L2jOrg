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
package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A simple no restart zone
 *
 * @author GKR
 */
public class NoRestartZone extends Zone {
    private int restartAllowedTime = 0;
    private int restartTime = 0;
    private boolean enabled = true;

    public NoRestartZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equalsIgnoreCase("default_enabled")) {
            enabled = Boolean.parseBoolean(value);
        } else if (name.equalsIgnoreCase("restartAllowedTime")) {
            restartAllowedTime = Integer.parseInt(value) * 1000;
        } else if (name.equalsIgnoreCase("restartTime")) {
            restartTime = Integer.parseInt(value) * 1000;
        } else if (!name.equalsIgnoreCase("instanceId")) {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature creature) {
        if (!enabled) {
            return;
        }

        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.NO_RESTART, true);
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (!enabled) {
            return;
        }

        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.NO_RESTART, false);
        }
    }

    @Override
    public void onPlayerLoginInside(Player player) {
        if (!enabled) {
            return;
        }

        if (((System.currentTimeMillis() - player.getLastAccess()) > restartTime) && ((System.currentTimeMillis() - player.getLastAccess()) > restartAllowedTime)) {
            player.teleToLocation(TeleportWhereType.TOWN);
        }
    }
}

/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author UnAfraid
 */
public class TeleportTask implements Runnable {
    private final Player _activeChar;
    private final Location _loc;

    public TeleportTask(Player player, Location loc) {
        _activeChar = player;
        _loc = loc;
    }

    @Override
    public void run() {
        if ((_activeChar != null) && _activeChar.isOnline()) {
            _activeChar.teleToLocation(_loc, true);
        }
    }
}

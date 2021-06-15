/*
 * Copyright © 2019-2021 L2JOrg
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
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

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

    private NoRestartZone(int id) {
        super(id);
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

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            var zone = new NoRestartZone(id);
            for (var node = zoneNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeName().equals("attributes")) {
                    var attr = node.getAttributes();
                    zone.enabled = reader.parseBoolean(attr,"enabled");
                    zone.restartAllowedTime = reader.parseInt(attr, "allow-time") * 1000;
                    zone.restartTime = reader.parseInt(attr, "restart-time") * 1000;
                    break;
                }
            }
            return zone;
        }

        @Override
        public String type() {
            return "no-restart";
        }
    }
}

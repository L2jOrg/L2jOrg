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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.tasks.player.TeleportTask;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A jail zone
 *
 * @author durgus
 */
public class JailZone extends Zone {
    private static final Location JAIL_IN_LOC = new Location(-114356, -249645, -2984);
    private static final Location JAIL_OUT_LOC = new Location(17836, 170178, -3507);

    public JailZone(int id) {
        super(id);
    }

    public static Location getLocationIn() {
        return JAIL_IN_LOC;
    }

    public static Location getLocationOut() {
        return JAIL_OUT_LOC;
    }

    @Override
    protected void onEnter(Creature creature) {
        if (isPlayer(creature)) {
            creature.setInsideZone(ZoneType.JAIL, true);
            creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, true);
            if (Config.JAIL_IS_PVP) {
                creature.setInsideZone(ZoneType.PVP, true);
                creature.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
            }
            if (Config.JAIL_DISABLE_TRANSACTION) {
                creature.setInsideZone(ZoneType.NO_STORE, true);
            }
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            player.setInsideZone(ZoneType.JAIL, false);
            player.setInsideZone(ZoneType.NO_SUMMON_FRIEND, false);

            if (Config.JAIL_IS_PVP) {
                creature.setInsideZone(ZoneType.PVP, false);
                creature.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
            }

            if (player.isJailed()) {
                // when a player wants to exit jail even if he is still jailed, teleport him back to jail
                ThreadPool.schedule(new TeleportTask(player, JAIL_IN_LOC), 2000);
                creature.sendMessage("You cannot cheat your way out of here. You must wait until your jail time is over.");
            }
            if (Config.JAIL_DISABLE_TRANSACTION) {
                creature.setInsideZone(ZoneType.NO_STORE, false);
            }
        }
    }
}

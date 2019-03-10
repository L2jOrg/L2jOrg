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

import com.l2jmobius.Config;
import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.tasks.player.TeleportTask;
import org.l2j.gameserver.mobius.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;

/**
 * A jail zone
 *
 * @author durgus
 */
public class L2JailZone extends L2ZoneType {
    private static final Location JAIL_IN_LOC = new Location(-114356, -249645, -2984);
    private static final Location JAIL_OUT_LOC = new Location(17836, 170178, -3507);

    public L2JailZone(int id) {
        super(id);
    }

    public static Location getLocationIn() {
        return JAIL_IN_LOC;
    }

    public static Location getLocationOut() {
        return JAIL_OUT_LOC;
    }

    @Override
    protected void onEnter(L2Character character) {
        if (character.isPlayer()) {
            character.setInsideZone(ZoneId.JAIL, true);
            character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
            if (Config.JAIL_IS_PVP) {
                character.setInsideZone(ZoneId.PVP, true);
                character.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
            }
            if (Config.JAIL_DISABLE_TRANSACTION) {
                character.setInsideZone(ZoneId.NO_STORE, true);
            }
        }
    }

    @Override
    protected void onExit(L2Character character) {
        if (character.isPlayer()) {
            final L2PcInstance player = character.getActingPlayer();
            player.setInsideZone(ZoneId.JAIL, false);
            player.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);

            if (Config.JAIL_IS_PVP) {
                character.setInsideZone(ZoneId.PVP, false);
                character.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
            }

            if (player.isJailed()) {
                // when a player wants to exit jail even if he is still jailed, teleport him back to jail
                ThreadPoolManager.getInstance().schedule(new TeleportTask(player, JAIL_IN_LOC), 2000);
                character.sendMessage("You cannot cheat your way out of here. You must wait until your jail time is over.");
            }
            if (Config.JAIL_DISABLE_TRANSACTION) {
                character.setInsideZone(ZoneId.NO_STORE, false);
            }
        }
    }
}

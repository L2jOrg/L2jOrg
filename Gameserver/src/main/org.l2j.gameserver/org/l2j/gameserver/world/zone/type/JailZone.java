package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.tasks.player.TeleportTask;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;

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
    protected void onEnter(Creature character) {
        if (isPlayer(character)) {
            character.setInsideZone(ZoneType.JAIL, true);
            character.setInsideZone(ZoneType.NO_SUMMON_FRIEND, true);
            if (Config.JAIL_IS_PVP) {
                character.setInsideZone(ZoneType.PVP, true);
                character.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
            }
            if (Config.JAIL_DISABLE_TRANSACTION) {
                character.setInsideZone(ZoneType.NO_STORE, true);
            }
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (isPlayer(character)) {
            final Player player = character.getActingPlayer();
            player.setInsideZone(ZoneType.JAIL, false);
            player.setInsideZone(ZoneType.NO_SUMMON_FRIEND, false);

            if (Config.JAIL_IS_PVP) {
                character.setInsideZone(ZoneType.PVP, false);
                character.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
            }

            if (player.isJailed()) {
                // when a player wants to exit jail even if he is still jailed, teleport him back to jail
                ThreadPoolManager.schedule(new TeleportTask(player, JAIL_IN_LOC), 2000);
                character.sendMessage("You cannot cheat your way out of here. You must wait until your jail time is over.");
            }
            if (Config.JAIL_DISABLE_TRANSACTION) {
                character.setInsideZone(ZoneType.NO_STORE, false);
            }
        }
    }
}

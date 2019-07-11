package org.l2j.gameserver.model.zone.type;

import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.tasks.player.TeleportTask;
import org.l2j.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.SystemMessageId;

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
    protected void onEnter(Creature character) {
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
    protected void onExit(Creature character) {
        if (character.isPlayer()) {
            final Player player = character.getActingPlayer();
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

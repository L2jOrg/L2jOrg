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

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneId;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A simple no restart zone
 *
 * @author GKR
 */
public class NoRestartZone extends Zone {
    private int _restartAllowedTime = 0;
    private int _restartTime = 0;
    private boolean _enabled = true;

    public NoRestartZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equalsIgnoreCase("default_enabled")) {
            _enabled = Boolean.parseBoolean(value);
        } else if (name.equalsIgnoreCase("restartAllowedTime")) {
            _restartAllowedTime = Integer.parseInt(value) * 1000;
        } else if (name.equalsIgnoreCase("restartTime")) {
            _restartTime = Integer.parseInt(value) * 1000;
        } else if (name.equalsIgnoreCase("instanceId")) {
            // Do nothing.
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature character) {
        if (!_enabled) {
            return;
        }

        if (isPlayer(character)) {
            character.setInsideZone(ZoneId.NO_RESTART, true);
        }
    }

    @Override
    protected void onExit(Creature character) {
        if (!_enabled) {
            return;
        }

        if (isPlayer(character)) {
            character.setInsideZone(ZoneId.NO_RESTART, false);
        }
    }

    @Override
    public void onPlayerLoginInside(Player player) {
        if (!_enabled) {
            return;
        }

        if (((System.currentTimeMillis() - player.getLastAccess()) > _restartTime) && ((System.currentTimeMillis() - player.getLastAccess()) > _restartAllowedTime)) {
            player.teleToLocation(TeleportWhereType.TOWN);
        }
    }

    public int getRestartAllowedTime() {
        return _restartAllowedTime;
    }

    public void setRestartAllowedTime(int time) {
        _restartAllowedTime = time;
    }

    public int getRestartTime() {
        return _restartTime;
    }

    public void setRestartTime(int time) {
        _restartTime = time;
    }
}

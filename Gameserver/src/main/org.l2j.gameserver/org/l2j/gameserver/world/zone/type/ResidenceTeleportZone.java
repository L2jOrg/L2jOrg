package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneType;

/**
 * based on Kerberos work for custom L2CastleTeleportZone
 *
 * @author Nyaran
 */
public class ResidenceTeleportZone extends ZoneRespawn {
    private int residenceId;

    public ResidenceTeleportZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("residenceId")) {
            residenceId = Integer.parseInt(value);
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(Creature creature) {
        creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, true); // FIXME: Custom ?
    }

    @Override
    protected void onExit(Creature creature) {
        creature.setInsideZone(ZoneType.NO_SUMMON_FRIEND, false); // FIXME: Custom ?
    }

    @Override
    public void oustAllPlayers() {
        getPlayersInside().stream().filter(Player::isOnline).forEach(player -> player.teleToLocation(getSpawnLoc(), 200));
    }

    public int getResidenceId() {
        return residenceId;
    }
}

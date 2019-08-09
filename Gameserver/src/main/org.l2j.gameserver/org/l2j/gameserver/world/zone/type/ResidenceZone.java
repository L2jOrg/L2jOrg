package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author xban1x
 */
public abstract class ResidenceZone extends ZoneRespawn {
    private int _residenceId;

    protected ResidenceZone(int id) {
        super(id);
    }

    public void banishForeigners(int owningClanId) {
        for (Player temp : getPlayersInside()) {
            if ((owningClanId != 0) && (temp.getClanId() == owningClanId)) {
                continue;
            }
            temp.teleToLocation(getBanishSpawnLoc(), true);
        }
    }

    public int getResidenceId() {
        return _residenceId;
    }

    protected void setResidenceId(int residenceId) {
        _residenceId = residenceId;
    }
}

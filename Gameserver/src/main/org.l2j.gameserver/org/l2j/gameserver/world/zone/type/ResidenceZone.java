package org.l2j.gameserver.world.zone.type;

/**
 * @author xban1x
 */
public abstract class ResidenceZone extends ZoneRespawn {
    private int residenceId;

    ResidenceZone(int id) {
        super(id);
    }

    public void banishForeigners(int owningClanId) {
        getPlayersInside().stream()
                .filter(p -> p.getClanId() == owningClanId && owningClanId != 0)
                .forEach(p -> p.teleToLocation(getBanishSpawnLoc(), true));
    }

    public int getResidenceId() {
        return residenceId;
    }

    void setResidenceId(int residenceId) {
        this.residenceId = residenceId;
    }
}

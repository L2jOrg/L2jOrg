package org.l2j.gameserver.world.zone.type;

/**
 * @author xban1x
 */
public abstract class ResidenceZone extends SpawnZone {
    private int residenceId;

    ResidenceZone(int id) {
        super(id);
    }

    public void banishForeigners(int owningClanId) {
        forEachPlayer(p -> p.teleToLocation(getBanishSpawnLoc(), true), p -> p.getClanId() == owningClanId && owningClanId != 0);
    }

    public int getResidenceId() {
        return residenceId;
    }

    void setResidenceId(int residenceId) {
        this.residenceId = residenceId;
    }
}

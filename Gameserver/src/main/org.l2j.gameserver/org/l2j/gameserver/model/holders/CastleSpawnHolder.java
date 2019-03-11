package org.l2j.gameserver.model.holders;


import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.model.Location;

/**
 * @author St3eT
 */
public class CastleSpawnHolder extends Location {
    private final int _npcId;
    private final CastleSide _side;

    public CastleSpawnHolder(int npcId, CastleSide side, int x, int y, int z, int heading) {
        super(x, y, z, heading);
        _npcId = npcId;
        _side = side;
    }

    public final int getNpcId() {
        return _npcId;
    }

    public final CastleSide getSide() {
        return _side;
    }
}

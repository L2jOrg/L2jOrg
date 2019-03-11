package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.enums.SiegeGuardType;

/**
 * @author St3eT
 */
public class SiegeGuardHolder {
    private final int _castleId;
    private final int _itemId;
    private final SiegeGuardType _type;
    private final boolean _stationary;
    private final int _npcId;
    private final int _maxNpcAmount;

    public SiegeGuardHolder(int castleId, int itemId, SiegeGuardType type, boolean stationary, int npcId, int maxNpcAmount) {
        _castleId = castleId;
        _itemId = itemId;
        _type = type;
        _stationary = stationary;
        _npcId = npcId;
        _maxNpcAmount = maxNpcAmount;
    }

    public final int getCastleId() {
        return _castleId;
    }

    public final int getItemId() {
        return _itemId;
    }

    public final SiegeGuardType getType() {
        return _type;
    }

    public final boolean isStationary() {
        return _stationary;
    }

    public final int getNpcId() {
        return _npcId;
    }

    public final int getMaxNpcAmout() {
        return _maxNpcAmount;
    }
}

package org.l2j.gameserver.mobius.gameserver.model.ceremonyofchaos;

import org.l2j.gameserver.mobius.gameserver.enums.CeremonyOfChaosResult;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.eventengine.AbstractEventMember;

/**
 * @author UnAfraid
 */
public class CeremonyOfChaosMember extends AbstractEventMember<CeremonyOfChaosEvent> {
    private final int _position;
    private int _lifeTime = 0;
    private CeremonyOfChaosResult _resultType = CeremonyOfChaosResult.LOSE;
    private boolean _isDefeated = false;

    public CeremonyOfChaosMember(L2PcInstance player, CeremonyOfChaosEvent event, int position) {
        super(player, event);
        _position = position;
    }

    public int getPosition() {
        return _position;
    }

    public int getLifeTime() {
        return _lifeTime;
    }

    public void setLifeTime(int time) {
        _lifeTime = time;
    }

    public CeremonyOfChaosResult getResultType() {
        return _resultType;
    }

    public void setResultType(CeremonyOfChaosResult resultType) {
        _resultType = resultType;
    }

    public boolean isDefeated() {
        return _isDefeated;
    }

    public void setDefeated(boolean isDefeated) {
        _isDefeated = isDefeated;
    }
}

/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.ceremonyofchaos;

import org.l2j.gameserver.enums.CeremonyOfChaosResult;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.eventengine.AbstractEventMember;

/**
 * @author UnAfraid
 */
public class CeremonyOfChaosMember extends AbstractEventMember<CeremonyOfChaosEvent> {
    private final int _position;
    private int _lifeTime = 0;
    private CeremonyOfChaosResult _resultType = CeremonyOfChaosResult.LOSE;
    private boolean _isDefeated = false;

    public CeremonyOfChaosMember(Player player, CeremonyOfChaosEvent event, int position) {
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

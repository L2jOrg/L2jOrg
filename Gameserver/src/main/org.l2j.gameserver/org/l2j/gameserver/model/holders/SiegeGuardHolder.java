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

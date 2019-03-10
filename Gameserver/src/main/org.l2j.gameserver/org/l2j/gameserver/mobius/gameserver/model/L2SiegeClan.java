/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model;

import org.l2j.gameserver.mobius.gameserver.enums.SiegeClanType;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class L2SiegeClan {
    private final Set<L2Npc> _flags = ConcurrentHashMap.newKeySet();
    private int _clanId = 0;
    private SiegeClanType _type;

    public L2SiegeClan(int clanId, SiegeClanType type) {
        _clanId = clanId;
        _type = type;
    }

    public int getNumFlags() {
        return _flags.size();
    }

    public void addFlag(L2Npc flag) {
        _flags.add(flag);
    }

    public boolean removeFlag(L2Npc flag) {
        if (flag == null) {
            return false;
        }

        flag.deleteMe();

        return _flags.remove(flag);
    }

    public void removeFlags() {
        for (L2Npc flag : _flags) {
            removeFlag(flag);
        }
    }

    public final int getClanId() {
        return _clanId;
    }

    public final Set<L2Npc> getFlag() {
        return _flags;
    }

    public SiegeClanType getType() {
        return _type;
    }

    public void setType(SiegeClanType setType) {
        _type = setType;
    }
}

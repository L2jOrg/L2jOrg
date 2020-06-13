/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model;

import org.l2j.gameserver.model.actor.Creature;

/**
 * @author xban1x
 */
public final class AggroInfo {
    private final Creature _attacker;
    private int _hate = 0;
    private int _damage = 0;

    public AggroInfo(Creature pAttacker) {
        _attacker = pAttacker;
    }

    public Creature getAttacker() {
        return _attacker;
    }

    public int getHate() {
        return _hate;
    }

    public int checkHate(Creature owner) {
        if (_attacker.isAlikeDead() || !_attacker.isSpawned() || !owner.isInSurroundingRegion(_attacker)) {
            _hate = 0;
        }

        return _hate;
    }

    public void addHate(int value) {
        _hate = (int) Math.min(_hate + (long) value, 999999999);
    }

    public void stopHate() {
        _hate = 0;
    }

    public int getDamage() {
        return _damage;
    }

    public void addDamage(int value) {
        _damage = (int) Math.min(_damage + (long) value, 999999999);
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AggroInfo) {
            return (((AggroInfo) obj).getAttacker() == _attacker);
        }

        return false;
    }

    @Override
    public final int hashCode() {
        return _attacker.getObjectId();
    }

    @Override
    public String toString() {
        return "AggroInfo [attacker=" + _attacker + ", hate=" + _hate + ", damage=" + _damage + "]";
    }
}

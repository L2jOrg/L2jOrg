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
package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.AttackType;
import org.l2j.gameserver.model.actor.Creature;

import java.lang.ref.WeakReference;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author UnAfraid
 */
public class Hit {
    private final WeakReference<WorldObject> _target;
    private final int _targetId;
    private final int _damage;
    private final int _ssGrade;
    private int _flags = 0;

    public Hit(WorldObject target, int damage, boolean miss, boolean crit, byte shld, boolean soulshot, int ssGrade) {
        _target = new WeakReference<>(target);
        _targetId = target.getObjectId();
        _damage = damage;
        _ssGrade = ssGrade;

        if (miss) {
            addMask(AttackType.MISSED);
            return;
        }

        if (crit) {
            addMask(AttackType.CRITICAL);
        }

        if (soulshot) {
            addMask(AttackType.SHOT_USED);
        }

        if ((isCreature(target) && ((Creature) target).isHpBlocked()) || (shld > 0)) {
            addMask(AttackType.BLOCKED);
        }
    }

    private void addMask(AttackType type) {
        _flags |= type.getMask();
    }

    public WorldObject getTarget() {
        return _target.get();
    }

    public int getTargetId() {
        return _targetId;
    }

    public int getDamage() {
        return _damage;
    }

    public int getFlags() {
        return _flags;
    }

    public int getGrade() {
        return _ssGrade;
    }

    public boolean isMiss() {
        return (AttackType.MISSED.getMask() & _flags) != 0;
    }

    public boolean isCritical() {
        return (AttackType.CRITICAL.getMask() & _flags) != 0;
    }

    public boolean isShotUsed() {
        return (AttackType.SHOT_USED.getMask() & _flags) != 0;
    }

    public boolean isBlocked() {
        return (AttackType.BLOCKED.getMask() & _flags) != 0;
    }
}

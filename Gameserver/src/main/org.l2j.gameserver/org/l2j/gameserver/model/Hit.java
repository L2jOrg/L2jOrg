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
package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.AttackType;
import org.l2j.gameserver.model.actor.Creature;

import java.lang.ref.WeakReference;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class Hit {
    private final WeakReference<WorldObject> target;
    private final int targetId;
    private final int damage;
    private final int grade;
    private int flags = 0;

    public Hit(WorldObject target, int damage, boolean miss, boolean crit, byte shld, int soulshotGrade) {
        this.target = new WeakReference<>(target);
        targetId = target.getObjectId();
        this.damage = damage;
        grade = soulshotGrade;

        if (miss) {
            addMask(AttackType.MISSED);
            return;
        }

        if (crit) {
            addMask(AttackType.CRITICAL);
        }

        if (soulshotGrade >= 0) {
            addMask(AttackType.SHOT_USED);
        }

        if ((isCreature(target) && ((Creature) target).isHpBlocked()) || (shld > 0)) {
            addMask(AttackType.BLOCKED);
        }
    }

    private void addMask(AttackType type) {
        flags |= type.getMask();
    }

    public WorldObject getTarget() {
        return target.get();
    }

    public int getTargetId() {
        return targetId;
    }

    public int getDamage() {
        return damage;
    }

    public int getFlags() {
        return flags;
    }

    public int getGrade() {
        return !isMiss() ? grade : -1;
    }

    public boolean isMiss() {
        return (AttackType.MISSED.getMask() & flags) != 0;
    }

    public boolean isCritical() {
        return (AttackType.CRITICAL.getMask() & flags) != 0;
    }

    public boolean isShotUsed() {
        return (AttackType.SHOT_USED.getMask() & flags) != 0;
    }

    public boolean isBlocked() {
        return (AttackType.BLOCKED.getMask() & flags) != 0;
    }
}

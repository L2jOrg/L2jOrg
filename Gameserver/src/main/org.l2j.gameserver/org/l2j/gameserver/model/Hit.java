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
    private final WeakReference<WorldObject> _target;
    private final int _targetId;
    private final int _damage;
    private int _flags = 0;

    public Hit(WorldObject target, int damage, boolean miss, boolean crit, byte shld, boolean soulshot) {
        _target = new WeakReference<>(target);
        _targetId = target.getObjectId();
        _damage = damage;

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
        return isShotUsed() ? 5 : -1;
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

package org.l2j.gameserver.model;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author xban1x
 */
public final class DamageDoneInfo {
    private final Player _attacker;
    private long _damage = 0;

    public DamageDoneInfo(Player attacker) {
        _attacker = attacker;
    }

    public Player getAttacker() {
        return _attacker;
    }

    public void addDamage(long damage) {
        _damage += damage;
    }

    public long getDamage() {
        return _damage;
    }

    @Override
    public final boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof DamageDoneInfo) && (((DamageDoneInfo) obj).getAttacker() == _attacker));
    }

    @Override
    public final int hashCode() {
        return _attacker.getObjectId();
    }
}

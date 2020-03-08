package org.l2j.gameserver.model.events.returns;

/**
 * @author Sdw
 */
public class DamageReturn extends TerminateReturn {
    private final double _damage;

    public DamageReturn(boolean terminate, boolean override, boolean abort, double damage) {
        super(terminate, override, abort);
        _damage = damage;
    }

    public double getDamage() {
        return _damage;
    }
}

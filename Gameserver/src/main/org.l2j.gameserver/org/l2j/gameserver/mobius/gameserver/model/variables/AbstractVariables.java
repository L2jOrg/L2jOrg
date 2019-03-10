package org.l2j.gameserver.mobius.gameserver.model.variables;

import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.interfaces.IDeletable;
import org.l2j.gameserver.mobius.gameserver.model.interfaces.IRestorable;
import org.l2j.gameserver.mobius.gameserver.model.interfaces.IStorable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author UnAfraid
 */
public abstract class AbstractVariables extends StatsSet implements IRestorable, IStorable, IDeletable {
    private final AtomicBoolean _hasChanges = new AtomicBoolean(false);

    public AbstractVariables() {
        super(new ConcurrentHashMap<>());
    }

    /**
     * Overriding following methods to prevent from doing useless database operations if there is no changes since player's login.
     */

    @Override
    public final StatsSet set(String name, boolean value) {
        _hasChanges.compareAndSet(false, true);
        return super.set(name, value);
    }

    @Override
    public final StatsSet set(String name, double value) {
        _hasChanges.compareAndSet(false, true);
        return super.set(name, value);
    }

    @Override
    public final StatsSet set(String name, Enum<?> value) {
        _hasChanges.compareAndSet(false, true);
        return super.set(name, value);
    }

    @Override
    public final StatsSet set(String name, int value) {
        _hasChanges.compareAndSet(false, true);
        return super.set(name, value);
    }

    @Override
    public final StatsSet set(String name, long value) {
        _hasChanges.compareAndSet(false, true);
        return super.set(name, value);
    }

    @Override
    public final StatsSet set(String name, String value) {
        _hasChanges.compareAndSet(false, true);
        return super.set(name, value);
    }

    /**
     * Put's entry to the variables and marks as changed if required (<i>Useful when restoring to do not save them again</i>).
     *
     * @param name
     * @param value
     * @param markAsChanged
     * @return
     */
    public final StatsSet set(String name, String value, boolean markAsChanged) {
        if (markAsChanged) {
            _hasChanges.compareAndSet(false, true);
        }
        return super.set(name, value);
    }

    /**
     * Return true if there exists a record for the variable name.
     *
     * @param name
     * @return
     */
    public boolean hasVariable(String name) {
        return getSet().keySet().contains(name);
    }

    /**
     * @return {@code true} if changes are made since last load/save.
     */
    public final boolean hasChanges() {
        return _hasChanges.get();
    }

    /**
     * Atomically sets the value to the given updated value if the current value {@code ==} the expected value.
     *
     * @param expect
     * @param update
     * @return {@code true} if successful. {@code false} return indicates that the actual value was not equal to the expected value.
     */
    public final boolean compareAndSetChanges(boolean expect, boolean update) {
        return _hasChanges.compareAndSet(expect, update);
    }

    /**
     * Removes variable
     *
     * @param name
     */
    @Override
    public final void remove(String name) {
        _hasChanges.compareAndSet(false, true);
        getSet().remove(name);
    }
}

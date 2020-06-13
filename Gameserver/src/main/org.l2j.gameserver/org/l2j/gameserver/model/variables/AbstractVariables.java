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
package org.l2j.gameserver.model.variables;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.interfaces.IDeletable;
import org.l2j.gameserver.model.interfaces.IRestorable;
import org.l2j.gameserver.model.interfaces.IStorable;

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

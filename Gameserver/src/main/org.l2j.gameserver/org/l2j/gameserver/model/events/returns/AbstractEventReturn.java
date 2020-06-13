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
package org.l2j.gameserver.model.events.returns;

/**
 * @author UnAfraid
 */
public abstract class AbstractEventReturn {
    private final boolean _override;
    private final boolean _abort;

    public AbstractEventReturn(boolean override, boolean abort) {
        _override = override;
        _abort = abort;
    }

    /**
     * @return {@code true} if return back object must be overridden by this object, {@code false} otherwise.
     */
    public boolean override() {
        return _override;
    }

    /**
     * @return {@code true} if notification has to be terminated, {@code false} otherwise.
     */
    public boolean abort() {
        return _abort;
    }
}

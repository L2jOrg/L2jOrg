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
package org.l2j.gameserver.enums;

/**
 * @author Nik64
 */
public enum FenceState {
    HIDDEN(0),
    OPENED(1),
    CLOSED(2),
    CLOSED_HIDDEN(0);

    final int _clientId;

    FenceState(int clientId) {
        _clientId = clientId;
    }

    public int getClientId() {
        return _clientId;
    }

    public boolean isGeodataEnabled() {
        return (this == CLOSED_HIDDEN) || (this == CLOSED);
    }
}
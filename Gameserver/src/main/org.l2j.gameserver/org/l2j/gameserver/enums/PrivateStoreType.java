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
package org.l2j.gameserver.enums;

/**
 * @author NosBit
 */
public enum PrivateStoreType {
    NONE(0),
    SELL(1),
    SELL_MANAGE(2),
    BUY(3),
    BUY_MANAGE(4),
    MANUFACTURE(5),
    PACKAGE_SELL(8),
    SELL_BUFFS(9);

    private int _id;

    PrivateStoreType(int id) {
        _id = id;
    }

    public static PrivateStoreType findById(int id) {
        for (PrivateStoreType privateStoreType : values()) {
            if (privateStoreType.getId() == id) {
                return privateStoreType;
            }
        }
        return null;
    }

    public int getId() {
        return _id;
    }
}

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
package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

/**
 * @author NosBit
 * @author JoeAlisson
 */
@Table("crests")
public final class CrestData implements IIdentifiable {

    private int id;
    private byte[] data;
    private CrestType type;

    public CrestData() {
    }

    public CrestData(int id, byte[] data, CrestType type) {
        this.id = id;
        this.data = data;
        this.type = type;
    }

    @Override
    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public CrestType getType() {
        return type;
    }

    public enum CrestType {
        PLEDGE(1),
        PLEDGE_LARGE(2),
        ALLY(3);

        private final int _id;

        CrestType(int id) {
            _id = id;
        }

        public static CrestType getById(int id) {
            for (CrestType crestType : values()) {
                if (crestType.getId() == id) {
                    return crestType;
                }
            }
            return null;
        }

        public int getId() {
            return _id;
        }
    }
}
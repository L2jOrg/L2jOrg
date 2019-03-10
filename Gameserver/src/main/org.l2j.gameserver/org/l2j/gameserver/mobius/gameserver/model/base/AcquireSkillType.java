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
package org.l2j.gameserver.mobius.gameserver.model.base;

/**
 * Learning skill types.
 *
 * @author Zoey76
 */
public enum AcquireSkillType {
    CLASS(0),
    DUMMY(1),
    PLEDGE(2),
    SUBPLEDGE(3),
    TRANSFORM(4),
    TRANSFER(5),
    SUBCLASS(6),
    COLLECT(7),
    DUMMY2(8),
    DUMMY3(9),
    FISHING(10),
    REVELATION(11), // Need proper ID
    REVELATION_DUALCLASS(12), // Need proper ID
    DUALCLASS(13), // Need proper ID
    ALCHEMY(140);

    private final int _id;

    private AcquireSkillType(int id) {
        _id = id;
    }

    public static AcquireSkillType getAcquireSkillType(int id) {
        for (AcquireSkillType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

    public int getId() {
        return _id;
    }
}

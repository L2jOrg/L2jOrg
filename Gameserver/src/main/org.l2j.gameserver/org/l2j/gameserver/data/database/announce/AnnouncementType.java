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
package org.l2j.gameserver.data.database.announce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public enum AnnouncementType {
    NORMAL,
    CRITICAL,
    EVENT,
    AUTO_NORMAL,
    AUTO_CRITICAL;
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementType.class);

    public static AnnouncementType findById(int id) {
        for (AnnouncementType type : values()) {
            if (type.ordinal() == id) {
                return type;
            }
        }
        LOGGER.warn(AnnouncementType.class.getSimpleName() + ": Unexistent id specified: " + id + "!", new IllegalStateException());
        return NORMAL;
    }

    public static AnnouncementType findByName(String name) {
        for (AnnouncementType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        LOGGER.warn(AnnouncementType.class.getSimpleName() + ": Unexistent name specified: " + name + "!", new IllegalStateException());
        return NORMAL;
    }

    public static boolean isAutoAnnounce(Announce announce) {
        var type = announce.getType();
        return  type == AUTO_CRITICAL || type == AUTO_NORMAL;
    }

    public boolean isAutoAnnounce() {
        return this == AUTO_CRITICAL || this == AUTO_NORMAL;
    }
}

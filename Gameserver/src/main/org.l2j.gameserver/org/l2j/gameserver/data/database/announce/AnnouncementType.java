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

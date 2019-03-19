package org.l2j.gameserver.data.sql.impl;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.announce.Announcement;
import org.l2j.gameserver.model.announce.AnnouncementType;
import org.l2j.gameserver.model.announce.AutoAnnouncement;
import org.l2j.gameserver.model.announce.IAnnouncement;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Loads announcements from database.
 *
 * @author UnAfraid
 */
public final class AnnouncementsTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementsTable.class);

    private final Map<Integer, IAnnouncement> _announcements = new ConcurrentSkipListMap<>();

    private AnnouncementsTable() {
        load();
    }

    private void load() {
        _announcements.clear();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rset = st.executeQuery("SELECT * FROM announcements")) {
            while (rset.next()) {
                final AnnouncementType type = AnnouncementType.findById(rset.getInt("type"));
                final Announcement announce;
                switch (type) {
                    case NORMAL:
                    case CRITICAL: {
                        announce = new Announcement(rset);
                        break;
                    }
                    case AUTO_NORMAL:
                    case AUTO_CRITICAL: {
                        announce = new AutoAnnouncement(rset);
                        break;
                    }
                    default: {
                        continue;
                    }
                }
                _announcements.put(announce.getId(), announce);
            }
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Failed loading announcements:", e);
        }
    }

    /**
     * Sending all announcements to the player
     *
     * @param player
     */
    public void showAnnouncements(L2PcInstance player) {
        sendAnnouncements(player, AnnouncementType.NORMAL);
        sendAnnouncements(player, AnnouncementType.CRITICAL);
        sendAnnouncements(player, AnnouncementType.EVENT);
    }

    /**
     * Sends all announcements to the player by the specified type
     *
     * @param player
     * @param type
     */
    private void sendAnnouncements(L2PcInstance player, AnnouncementType type) {
        for (IAnnouncement announce : _announcements.values()) {
            if (announce.isValid() && (announce.getType() == type)) {
                player.sendPacket(new CreatureSay(0, //
                        type == AnnouncementType.CRITICAL ? ChatType.CRITICAL_ANNOUNCE : ChatType.ANNOUNCEMENT, //
                        player.getName(), announce.getContent()));
            }
        }
    }

    /**
     * Adds announcement
     *
     * @param announce
     */
    public void addAnnouncement(IAnnouncement announce) {
        if (announce.storeMe()) {
            _announcements.put(announce.getId(), announce);
        }
    }

    /**
     * Removes announcement by id
     *
     * @param id
     * @return {@code true} if announcement exists and was deleted successfully, {@code false} otherwise.
     */
    public boolean deleteAnnouncement(int id) {
        final IAnnouncement announce = _announcements.remove(id);
        return (announce != null) && announce.deleteMe();
    }

    /**
     * @param id
     * @return {@link IAnnouncement} by id
     */
    public IAnnouncement getAnnounce(int id) {
        return _announcements.get(id);
    }

    /**
     * @return {@link Collection} containing all announcements
     */
    public Collection<IAnnouncement> getAllAnnouncements() {
        return _announcements.values();
    }

    public static AnnouncementsTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final AnnouncementsTable INSTANCE = new AnnouncementsTable();
    }
}

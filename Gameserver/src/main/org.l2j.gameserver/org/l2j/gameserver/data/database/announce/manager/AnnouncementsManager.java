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
package org.l2j.gameserver.data.database.announce.manager;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.announce.Announce;
import org.l2j.gameserver.data.database.announce.AnnouncementType;
import org.l2j.gameserver.data.database.dao.AnnounceDAO;
import org.l2j.gameserver.data.database.data.AnnounceData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.util.Broadcast;

import java.util.Collection;
import java.util.concurrent.ScheduledFuture;

import static java.lang.Math.max;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.data.database.announce.AnnouncementType.*;
import static org.l2j.gameserver.enums.ChatType.ANNOUNCEMENT;
import static org.l2j.gameserver.enums.ChatType.CRITICAL_ANNOUNCE;

/**
 * Loads announcements from database.
 *
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class AnnouncementsManager {

    private final IntMap<Announce> announcements = new CHashIntMap<>();
    private static final IntMap<ScheduledFuture<?>> schedules = new CHashIntMap<>();

    private AnnouncementsManager() {
    }

    private void load() {
        announcements.clear();
        var announces = getDAO(AnnounceDAO.class).findAll();

        for (AnnounceData announce : announces) {
            announcements.put(announce.getId(), announce);

            if(isAutoAnnounce(announce)) {
                scheduleAnnounce(announce);
            }
        }
    }

    public void scheduleAnnounce(AnnounceData announce) {
        var task = schedules.get(announce.getId());

        if(nonNull(task) && !task.isCancelled()) {
            task.cancel(false);
        }

        schedules.put(announce.getId(), ThreadPool.schedule(new AutoAnnounce(announce), announce.getInitial()));
    }

    /**
     * Sending all announcements to the player
     *
     * @param player
     */
    public void showAnnouncements(Player player) {
        sendAnnouncements(player, NORMAL);
        sendAnnouncements(player, CRITICAL);
        sendAnnouncements(player, EVENT);
    }

    /**
     * Sends all announcements to the player by the specified type
     *
     * @param player
     * @param type
     */
    private void sendAnnouncements(Player player, AnnouncementType type) {
        announcements.values().stream().filter(a -> a.getType() == type && a.isValid())
                .map(a -> new CreatureSay(0, type == CRITICAL ? CRITICAL_ANNOUNCE : ANNOUNCEMENT, player.getName(), a.getContent()))
                .forEach(player::sendPacket);
    }

    /**
     * Adds announcement
     *
     * @param announce
     */
    public void addAnnouncement(Announce announce) {
        if(announce instanceof AnnounceData) {
            getDAO(AnnounceDAO.class).save((AnnounceData) announce);
        }
        announcements.put(announce.getId(), announce);
    }

    /**
     * Removes announcement by id
     *
     * @param id
     * @return {@code true} if announcement exists and was deleted successfully, {@code false} otherwise.
     */
    public boolean deleteAnnouncement(int id) {
        var announce = announcements.remove(id);
        if(announce instanceof AnnounceData) {
            getDAO(AnnounceDAO.class).deleteById(id);
            return true;
        }
        return false;
    }

    public void updateAnnouncement(Announce announce) {
        if(announce instanceof AnnounceData) {
            getDAO(AnnounceDAO.class).save((AnnounceData) announce);
        }
        announcements.putIfAbsent(announce.getId(), announce);
    }

    public Announce getAnnounce(int id) {
        return announcements.get(id);
    }

    public void restartAutoAnnounce() {
        announcements.values().stream().filter(AnnouncementType::isAutoAnnounce).map(AnnounceData.class::cast).forEach(this::scheduleAnnounce);
    }

    /**
     * @return {@link Collection} containing all announcements
     */
    public Collection<Announce> getAllAnnouncements() {
        return announcements.values();
    }

    public static void init() {
        getInstance().load();
    }

    public static AnnouncementsManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final AnnouncementsManager INSTANCE = new AnnouncementsManager();
    }

    private static class AutoAnnounce implements Runnable {

        private final AnnounceData data;
        private int runs;

        private AutoAnnounce(AnnounceData data) {
            this.data = data;
            runs = max(1, data.getRepeat());
        }

        @Override
        public void run() {
            ScheduledFuture task;
            if (data.getRepeat() == -1 || runs > 0) {
                for (String content : data.getContent().split(System.lineSeparator())) {
                    Broadcast.toAllOnlinePlayers(content, (data.getType() == AUTO_CRITICAL));
                }

                if (data.getRepeat() != -1) {
                    runs--;
                }

                if(data.getDelay() > 0) {
                    task = schedules.put(data.getId(), ThreadPool.schedule(this, data.getDelay()));
                } else {
                    task = schedules.remove(data.getId());
                }
            } else {
                task = schedules.remove(data.getId());
            }

            if(nonNull(task)) {
                task.cancel(false);
            }
        }
    }
}

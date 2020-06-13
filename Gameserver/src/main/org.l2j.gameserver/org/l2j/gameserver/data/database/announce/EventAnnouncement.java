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

import org.l2j.commons.util.DateRange;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author UnAfraid
 */
public class EventAnnouncement implements Announce {

    private static final AtomicInteger virtualId = new AtomicInteger(-1);

    private final int id;
    private final DateRange range;
    private String content;

    public EventAnnouncement(DateRange range, String content) {
        id = virtualId.decrementAndGet();
        this.range = range;
        this.content = content;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public AnnouncementType getType() {
        return AnnouncementType.EVENT;
    }

    @Override
    public boolean isValid() {
        return range.isWithinRange(LocalDate.now());
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return "System";
    }

    @Override
    public void setType(AnnouncementType type) {

    }

    public void setAuthor(String author) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canBeStored() {
        return false;
    }
}

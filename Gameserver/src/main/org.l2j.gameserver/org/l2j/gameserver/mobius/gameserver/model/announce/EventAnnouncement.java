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
package org.l2j.gameserver.mobius.gameserver.model.announce;

import org.l2j.gameserver.mobius.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.mobius.gameserver.script.DateRange;

import java.util.Date;

/**
 * @author UnAfraid
 */
public class EventAnnouncement implements IAnnouncement {
    private final int _id;
    private final DateRange _range;
    private String _content;

    public EventAnnouncement(DateRange range, String content) {
        _id = IdFactory.getInstance().getNextId();
        _range = range;
        _content = content;
    }

    @Override
    public int getId() {
        return _id;
    }

    @Override
    public AnnouncementType getType() {
        return AnnouncementType.EVENT;
    }

    @Override
    public void setType(AnnouncementType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValid() {
        return _range.isWithinRange(new Date());
    }

    @Override
    public String getContent() {
        return _content;
    }

    @Override
    public void setContent(String content) {
        _content = content;
    }

    @Override
    public String getAuthor() {
        return "N/A";
    }

    @Override
    public void setAuthor(String author) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteMe() {
        IdFactory.getInstance().releaseId(_id);
        return true;
    }

    @Override
    public boolean storeMe() {
        return true;
    }

    @Override
    public boolean updateMe() {
        throw new UnsupportedOperationException();
    }
}

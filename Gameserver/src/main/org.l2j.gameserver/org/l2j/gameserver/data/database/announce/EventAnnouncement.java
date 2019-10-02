package org.l2j.gameserver.data.database.announce;

import org.l2j.gameserver.script.DateRange;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author UnAfraid
 */
public class EventAnnouncement implements Announce {

    private volatile static AtomicInteger virtualId = new AtomicInteger(-1);

    private final int id;
    private final DateRange range;
    private String content;

    public EventAnnouncement(DateRange range, String content) {
        id = virtualId.getAndDecrement();
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
        return range.isWithinRange(new Date());
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

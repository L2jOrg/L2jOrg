package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("olympiad_data")
public class OlympiadData {

    private int id;

    @Column("current_cycle")
    private int cycle;
    private int period;

    @Column("olympiad_end")
    private long olympiadEnd;

    @Column("next_weekly_change")
    private long nexWeeklyChange;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public long getOlympiadEnd() {
        return olympiadEnd;
    }

    public void setOlympiadEnd(long olympiadEnd) {
        this.olympiadEnd = olympiadEnd;
    }

    public long getNexWeeklyChange() {
        return nexWeeklyChange;
    }

    public void setNexWeeklyChange(long nexWeeklyChange) {
        this.nexWeeklyChange = nexWeeklyChange;
    }
}

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

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;

import java.time.LocalDate;

/**
 * @author JoeAlisson
 */
@Table("olympiad_data")
public class OlympiadData {

    private int id;

    @Column("current_cycle")
    private int season;
    private int period;

    @Column("olympiad_end")
    private long olympiadEnd;

    @Column("validation_end")
    private long validationEnd;

    @Column("next_weekly_change")
    private long nextWeeklyChange;
    @NonUpdatable
    private LocalDate nextSeasonDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
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

    public long getNextWeeklyChange() {
        return nextWeeklyChange;
    }

    public void setNextWeeklyChange(long nextWeeklyChange) {
        this.nextWeeklyChange = nextWeeklyChange;
    }

    public void increaseSeason() {
        season++;
    }

    public long getValidationEnd() {
        return validationEnd;
    }

    public void setValidationEnd(long validationEnd) {
        this.validationEnd = validationEnd;
    }

    public void setNextSeasonDate(LocalDate nextSeasonDate) {
        this.nextSeasonDate = nextSeasonDate;
    }

    public LocalDate getNextSeasonDate() {
        return nextSeasonDate;
    }
}

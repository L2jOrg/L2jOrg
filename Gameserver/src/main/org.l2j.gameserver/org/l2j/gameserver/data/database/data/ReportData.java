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
import org.l2j.commons.database.annotation.Table;

import java.time.LocalDate;

@Table("bbs_reports")
public class ReportData {

    @Column("report_id")
    private int id;

    @Column("player_id")
    private int playerId;

    String report;

    @Column("report_date")
    LocalDate data = LocalDate.now();

    boolean pending;

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}

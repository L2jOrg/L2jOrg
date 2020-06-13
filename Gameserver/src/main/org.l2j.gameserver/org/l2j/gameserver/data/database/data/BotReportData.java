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

import java.time.LocalDateTime;

@Table("bot_reported_char_data")
public class BotReportData {

    @Column("bot_id")
    private int botId;

    @Column("reporter_id")
    private int reporterId;

    private String type;

    @Column("report_date")
    private LocalDateTime reportDate;

    public BotReportData() {

    }

    public BotReportData(int botId, int reporterId, String type) {
        this.botId = botId;
        this.reporterId = reporterId;
        reportDate = LocalDateTime.now();
        this.type = type;
    }

    public int getBotId() {
        return botId;
    }

    public int getReporterId() {
        return reporterId;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }
}

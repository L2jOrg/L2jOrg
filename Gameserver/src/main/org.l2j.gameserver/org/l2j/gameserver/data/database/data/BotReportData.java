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

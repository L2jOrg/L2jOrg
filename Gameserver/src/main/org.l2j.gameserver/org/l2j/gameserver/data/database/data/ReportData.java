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

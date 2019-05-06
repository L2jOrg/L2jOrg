package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.annotation.Transient;

import java.util.Date;

@Table("bbs_reports")
public class ReportData {

    @Transient
    @Column("report_id")
    private int id;

    @Column("player_id")
    private int playerId;

    String report;

    @Transient
    @Column("report_date")
    Date data;

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setReport(String report) {
        this.report = report;
    }
}

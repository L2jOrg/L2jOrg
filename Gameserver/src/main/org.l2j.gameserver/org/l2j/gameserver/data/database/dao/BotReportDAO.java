package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.BotReportData;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author JoeAlisson
 */
public interface BotReportDAO extends DAO<BotReportData> {

    @Query("SELECT * FROM bot_reported_char_data")
    List<BotReportData> findAll();

    @Query("DELETE FROM bot_reported_char_data WHERE report_date < :dateLimit:")
    void removeExpiredReports(LocalDateTime dateLimit);
}

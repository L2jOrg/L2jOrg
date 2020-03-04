package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.enums.CastleSide;

import java.time.LocalDateTime;

/**
 * @author JoeAlisson
 */
@Table("castle")
public class CastleData {

    private int id;
    private String name;
    private CastleSide side;
    private long treasury;

    @Column("siege_date")
    private LocalDateTime siegeDate;

    @Column("siege_time_registration_end")
    private LocalDateTime siegeTimeRegistrationEnd;

    @Column("show_npc_crest")
    private boolean showNpcCrest;

    @Column("ticket_buy_count")
    private int ticketBuyCount;

    public String getName() {
        return name;
    }

    public CastleSide getSide() {
        return side;
    }

    public void setSide(CastleSide side) {
        this.side = side;
    }

    public long getTreasury() {
        return treasury;
    }

    public void setTreasury(long treasury) {
        this.treasury = treasury;
    }

    public void updateTreasury(long amount) {
        treasury += amount;
    }

    public LocalDateTime getSiegeDate() {
        return siegeDate;
    }

    public void setSiegeDate(LocalDateTime siegeDate) {
        this.siegeDate = siegeDate;
    }

    public LocalDateTime getSiegeTimeRegistrationEnd() {
        return siegeTimeRegistrationEnd;
    }

    public void setSiegeTimeRegistrationEnd(LocalDateTime date) {
        siegeTimeRegistrationEnd = date;
    }

    public boolean isShowNpcCrest() {
        return showNpcCrest;
    }

    public void setShowNpcCrest(boolean showNpcCrest) {
        this.showNpcCrest = showNpcCrest;
    }

    public int getTicketBuyCount() {
        return ticketBuyCount;
    }

    public void setTicketBuyCount(int count) {
        ticketBuyCount = count;
    }
}

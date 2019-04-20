package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("account_data")
public class AccountData {

    private String account;

    private int coin;

    @Column("vip_point")
    private long vipPoints;

    @Column("vip_tier_expiration")
    private long vipTierExpiration;

    public void setAccount(String accountName) {
        this.account = accountName;
    }

    public long getVipPoints() {
        return vipPoints;
    }

    public long getVipTierExpiration() {
        return vipTierExpiration;
    }

    public void updateVipPoints(long points) {
        this.vipPoints += points;
    }

    public int getCoin() {
        return coin;
    }

    public void updateCoins(int coins) {
        this.coin += coins;
    }

    public void setVipTierExpiration(long expiration) {
        this.vipTierExpiration = expiration;
    }
}

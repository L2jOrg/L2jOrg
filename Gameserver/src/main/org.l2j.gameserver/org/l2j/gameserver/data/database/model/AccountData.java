package org.l2j.gameserver.data.database.model;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("account_data")
public class AccountData {

    private String account;

    @Column("vip_point")
    private long vipPoints;

    @Column("vip_tier_expiration")
    private long vipTierExpiration;

    @Column("silver_coin")
    private long silverCoin;

    @Column("rusty_coin")
    private long rustyCoin;

    public void setAccount(String accountName) {
        this.account = accountName;
    }

    public long getVipPoints() {
        return vipPoints;
    }

    public long getVipTierExpiration() {
        return vipTierExpiration;
    }

    public long getRustyCoin() {
        return rustyCoin;
    }

    public long getSilverCoin() {
        return silverCoin;
    }
}

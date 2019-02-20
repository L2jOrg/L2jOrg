package org.l2j.gameserver.data.model;

import org.l2j.commons.database.annotation.Column;

public class AccountInfo {
    private String account;
    private int premium;
    @Column("premium_expire")
    private long premiumExpire;

    public int getPremium() {
        return premium;
    }

    public void setPremium(int premium) {
        this.premium = premium;
    }

    public long getPremiumExpire() {
        return premiumExpire;
    }

    public void setPremiumExpire(long expire) {
        this.premiumExpire = expire;
    }
}

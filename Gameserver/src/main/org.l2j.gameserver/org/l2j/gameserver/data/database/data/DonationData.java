package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Table;

@Table("donations")
public class DonationData {
    private String payerid;
    private String paymentid;
    private String email;
    private int amount;
    private boolean claimed;

    public String getPayerId() {
        return payerid;
    }

    public String getPaymentId() {
        return paymentid;
    }

    public String getEmail() {
        return email;
    }

    public int getAmount() {
        return amount;
    }
}

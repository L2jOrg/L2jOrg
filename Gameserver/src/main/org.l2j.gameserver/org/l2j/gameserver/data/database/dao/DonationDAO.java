package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.DonationData;

import java.util.List;

public interface DonationDAO  extends DAO<DonationData> {
    @Query("SELECT * FROM donations WHERE email = :email: AND claimed = 0")
    List<DonationData> unClaimedDonations(String email);

    @Query("UPDATE donations SET claimed = 1 WHERE email = :email: AND paymentid = :paymentid: AND payerid = :payerid: ")
    DonationData claimDonation(String paymentid, String payerid, String email);
}

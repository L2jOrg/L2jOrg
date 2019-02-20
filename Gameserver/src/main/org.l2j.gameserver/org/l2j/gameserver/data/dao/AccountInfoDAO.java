package org.l2j.gameserver.data.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.model.AccountInfo;

public interface AccountInfoDAO extends DAO {

    @Query("SELECT * FROM account_info WHERE account=:account:")
    AccountInfo findById(String account);

    @Query("DELETE FROM account_info WHERE account=:account:")
    void delete(String account);

    @Query("INSERT INTO account_info (account, premium, premium_expire) VALUES (:account:,:premium:,:premiumExpire:)")
    void save(String account, int premium, long premiumExpire);
}

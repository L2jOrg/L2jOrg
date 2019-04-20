package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.AccountData;

public interface AccountDAO extends DAO {

    @Query("DELETE FROM account_gsdata WHERE account_gsdata.account_name NOT IN (SELECT account_name FROM characters);")
    int deleteWithoutAccount();

    @Query("SELECT * FROM account_data WHERE account = :account:")
    AccountData findById(String account);
}

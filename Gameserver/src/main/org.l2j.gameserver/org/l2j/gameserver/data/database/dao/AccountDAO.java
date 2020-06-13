/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.AccountData;

/**
 * @author JoeAlisson
 */
public interface AccountDAO extends DAO<AccountData> {

    @Query("DELETE a1, a FROM account_gsdata a1 JOIN account_data a ON a.account = a1.account_name WHERE a.account NOT IN (SELECT account_name FROM characters);")
    int deleteWithoutAccount();

    @Query("SELECT * FROM account_data WHERE account = :account:")
    AccountData findById(String account);
}

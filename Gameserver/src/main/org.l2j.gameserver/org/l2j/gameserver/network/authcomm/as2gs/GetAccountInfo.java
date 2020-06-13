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
package org.l2j.gameserver.network.authcomm.as2gs;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.SetAccountInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * 21:05/25.03.2011
 */
public class GetAccountInfo extends ReceivablePacket
{
	private static final Logger _log = LoggerFactory.getLogger(GetAccountInfo.class);
	private String _account;

	@Override
	protected void readImpl()
	{
		_account = readString();
	}

	@Override
	protected void runImpl() {
		int playerSize = 0;
		try(var con = DatabaseFactory.getInstance().getConnection();
			var statement = con.prepareStatement("SELECT COUNT(1) FROM characters WHERE account_name=?")) {
			statement.setString(1, _account);
			var rset = statement.executeQuery();
			if(rset.next()) {
				playerSize = rset.getInt(1);
			}
			AuthServerCommunication.getInstance().sendPacket(new SetAccountInfo(_account, playerSize));


		} catch(Exception e) {
			_log.error("GetAccountInfo:runImpl():" + e, e);
		}

	}
}

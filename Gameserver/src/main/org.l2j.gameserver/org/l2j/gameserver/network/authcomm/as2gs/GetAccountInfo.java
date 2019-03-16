package org.l2j.gameserver.network.authcomm.as2gs;

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.lists.IntList;
import io.github.joealisson.primitive.lists.impl.ArrayIntList;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.SetAccountInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 * 21:05/25.03.2011
 */
public class GetAccountInfo extends ReceivablePacket
{
	private static final Logger _log = LoggerFactory.getLogger(GetAccountInfo.class);
	private String _account;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_account = readString(buffer);
	}

	@Override
	protected void runImpl()
	{
		int playerSize = 0;
		IntList deleteChars = Containers.EMPTY_INT_LIST;
		try(var con = DatabaseFactory.getInstance().getConnection();
			var statement = con.prepareStatement("SELECT deletetime FROM characters WHERE account_name=?")) {
			statement.setString(1, _account);

			var rset = statement.executeQuery();

			while (rset.next()) {
				playerSize++;
				int d = rset.getInt("deletetime");
				if (d > 0) {
					if (deleteChars.isEmpty())
						deleteChars = new ArrayIntList(3);

					deleteChars.add(d + Config.CHARACTER_DELETE_AFTER_HOURS * 60 * 60);
				}
			}
		} catch(Exception e) {
			_log.error("GetAccountInfo:runImpl():" + e, e);
		}
		AuthServerCommunication.getInstance().sendPacket(new SetAccountInfo(_account, playerSize, deleteChars.toArray()));
	}
}

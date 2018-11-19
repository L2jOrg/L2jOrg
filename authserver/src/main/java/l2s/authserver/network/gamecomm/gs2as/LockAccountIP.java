package l2s.authserver.network.gamecomm.gs2as;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.authserver.database.DatabaseFactory;
import l2s.authserver.network.gamecomm.ReceivablePacket;
import l2s.commons.dbutils.DbUtils;

/**
 * @Author: SYS
 * @Date: 10/4/2007
 */
public class LockAccountIP extends ReceivablePacket
{
	private static final Logger _log = LoggerFactory.getLogger(LockAccountIP.class);

	String _accname;
	String _IP;
	int _time;

	@Override
	protected void readImpl()
	{
		_accname = readS();
		_IP = readS();
		_time = readD();
	}

	@Override
	protected void runImpl()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE accounts SET allow_ip = ?, lock_expire = ? WHERE login = ?");
			statement.setString(1, _IP);
			statement.setInt(2, _time);
			statement.setString(3, _accname);
			statement.executeUpdate();
			DbUtils.closeQuietly(statement);
		}
		catch(Exception e)
		{
			_log.error("Failed to lock/unlock account: " + e.getMessage());
		}
		finally
		{
			DbUtils.closeQuietly(con);
		}
	}
}
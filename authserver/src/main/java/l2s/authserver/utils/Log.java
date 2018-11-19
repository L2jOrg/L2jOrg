package l2s.authserver.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;

import l2s.authserver.Config;
import l2s.authserver.accounts.Account;
import l2s.authserver.database.DatabaseFactory;
import l2s.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log
{
	private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");

	private final static Logger _log = LoggerFactory.getLogger(Log.class);
	private static final Logger _logAuth = LoggerFactory.getLogger("auth");

	public static void LogAccount(Account account)
	{
		if(!Config.LOGIN_LOG)
			return;

		StringBuilder output = new StringBuilder();
		output.append("ACCOUNT[");
		output.append(account.getLogin());
		output.append("] IP[");
		output.append(account.getLastIP());
		output.append("] LAST_ACCESS_TIME[");
		output.append(SIMPLE_FORMAT.format(account.getLastAccess() * 1000L));
		output.append("]");
		_logAuth.info(output.toString());
			
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO account_log (time, login, ip) VALUES(?,?,?)");
			statement.setInt(1, account.getLastAccess());
			statement.setString(2, account.getLogin());
			statement.setString(3, account.getLastIP());			
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}

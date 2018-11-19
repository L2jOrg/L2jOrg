package l2s.authserver.network.gamecomm.gs2as;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.authserver.Config;
import l2s.authserver.database.DatabaseFactory;
import l2s.authserver.network.l2.L2LoginClient;
import l2s.authserver.network.gamecomm.ReceivablePacket;
import l2s.authserver.network.gamecomm.as2gs.ChangePasswordResponse;
import l2s.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePassword extends ReceivablePacket
{
	private static final Logger _log = LoggerFactory.getLogger(ChangePassword.class);

	public String _accname;
	public String _oldPass;
	public String _newPass;
	public String _hwid;

	@Override
	protected void readImpl()
	{
		_accname = readS();
		_oldPass = readS();
		_newPass = readS();
		_hwid = readS();
	}	

	@Override
	protected void runImpl()
	{
		String dbPassword = null;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			try
			{
				statement = con.prepareStatement("SELECT * FROM accounts WHERE login = ?");
				statement.setString(1, _accname);
				rs = statement.executeQuery();
				if(rs.next())
					dbPassword = rs.getString("password");
			}
			catch(Exception e)
			{
				_log.warn("Can't recive old password for account " + _accname + ", exciption :" + e);
			}
			finally
			{
				DbUtils.closeQuietly(statement, rs);
			}

			//Encode old password and compare it to sended one, send packet to determine changed or not.
			try
			{
				if(!Config.DEFAULT_CRYPT.compare(_oldPass, dbPassword))
				{
					ChangePasswordResponse cp1;
					cp1 = new ChangePasswordResponse(_accname, false);
					sendPacket(cp1);
				}
				else
				{
					statement = con.prepareStatement("UPDATE accounts SET password = ? WHERE login = ?");
					statement.setString(1, Config.DEFAULT_CRYPT.encrypt(_newPass));
					statement.setString(2, _accname);
					int result = statement.executeUpdate();
					ChangePasswordResponse cp1;
					cp1 = new ChangePasswordResponse(_accname, result != 0);
					sendPacket(cp1);
				}
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			finally
			{
				DbUtils.closeQuietly(statement);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con);
		}
	}
}

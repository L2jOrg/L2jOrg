package l2s.authserver.network.gamecomm.gs2as;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import l2s.authserver.database.DatabaseFactory;
import l2s.authserver.network.gamecomm.ReceivablePacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePhoneNumber extends ReceivablePacket
{
	private static final Logger _log = LoggerFactory.getLogger(ChangePhoneNumber.class);
	private String account;
	private long phoneNumber;

	@Override
	protected void readImpl()
	{
		account = readS();
		phoneNumber = readQ();
	}

	@Override
	protected void runImpl()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE accounts SET phone_nubmer=? WHERE login=?");
			statement.setLong(1, phoneNumber);
			statement.setString(2, account);
			statement.execute();
			statement.close();
		}
		catch(SQLException e)
		{
			_log.warn("ChangePhoneNumber: Could not write data. Reason: " + e);
		}
		finally
		{
			try
			{
				if(con != null)
					con.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
}
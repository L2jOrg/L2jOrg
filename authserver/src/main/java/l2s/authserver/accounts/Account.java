package l2s.authserver.accounts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.authserver.database.DatabaseFactory;
import l2s.commons.dbutils.DbUtils;
import l2s.commons.net.utils.Net;
import l2s.commons.net.utils.NetList;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Account
{
	private final static Logger _log = LoggerFactory.getLogger(Account.class);

	private final String login;

	private String passwordHash;
	private String allowedIP;
	private String allowedHwid;
	private NetList allowedIpList = new NetList();
	private int accessLevel;

	private int banExpire;

	private int bonus;
	private int bonusExpire;

	private String lastIP;
	private int lastAccess;
	private int lastServer;

	private int points;

    private long phoneNumber;

	private IntObjectMap<Pair<Integer, int[]>> _serversInfo = new HashIntObjectMap<Pair<Integer, int[]>>(2);

	public Account(String login)
	{
		this.login = login;
	}

	public String getLogin()
	{
		return login;
	}

	public String getPasswordHash()
	{
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash)
	{
		this.passwordHash = passwordHash;
	}

	public String getAllowedIP()
	{
		return allowedIP;
	}

	public String getAllowedHwid()
	{
		return allowedHwid;
	}

	public boolean isAllowedIP(String ip)
	{
		return allowedIpList.isEmpty() || allowedIpList.isInRange(ip);
	}

	public void setAllowedIP(String allowedIP)
	{
		this.allowedIpList.clear();
		this.allowedIP = allowedIP;

		if(allowedIP.isEmpty())
			return;

		String[] masks = allowedIP.split("[\\s,;]+");
		for(String mask : masks)
			this.allowedIpList.add(Net.valueOf(mask));
	}

	public void setAllowedHwid(String allowedHwid)
	{
		this.allowedHwid = allowedHwid;
	}

	public int getAccessLevel()
	{
		return accessLevel;
	}

	public void setAccessLevel(int accessLevel)
	{
		this.accessLevel = accessLevel;
	}

	public int getBonus()
	{
		return bonus;
	}

	public void setBonus(int bonus)
	{
		this.bonus = bonus;
	}

	public int getBonusExpire()
	{
		return bonusExpire;
	}

	public void setBonusExpire(int bonusExpire)
	{
		this.bonusExpire = bonusExpire;
	}

	public int getBanExpire()
	{
		return banExpire;
	}

	public void setBanExpire(int banExpire)
	{
		this.banExpire = banExpire;
	}

	public void setLastIP(String lastIP)
	{
		this.lastIP = lastIP;
	}

	public String getLastIP()
	{
		return lastIP;
	}

	public int getLastAccess()
	{
		return lastAccess;
	}

	public void setLastAccess(int lastAccess)
	{
		this.lastAccess = lastAccess;
	}

	public int getLastServer()
	{
		return lastServer;
	}

	public void setLastServer(int lastServer)
	{
		this.lastServer = lastServer;
	}

	public void addAccountInfo(int serverId, int size, int[] deleteChars)
	{
		_serversInfo.put(serverId, new ImmutablePair<Integer, int[]>(size, deleteChars));
	}

	public Pair<Integer, int[]> getAccountInfo(int serverId)
	{
		return _serversInfo.get(serverId);
	}

	public void setPoints(int points)
	{
		this.points = points;
	}

	public int getPoints()
	{
		return points;
	}

    public void setPhoneNumber(long phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public long getPhoneNumber()
    {
        return phoneNumber;
    }

	@Override
	public String toString()
	{
		return login;
	}

	public void restore()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT password, access_level, ban_expire, allow_ip, allow_hwid, bonus, bonus_expire, last_server, last_ip, last_access, points, phone_nubmer FROM accounts WHERE login = ?");
			statement.setString(1, login);
			rset = statement.executeQuery();

			if(rset.next())
			{
				setPasswordHash(rset.getString("password"));
				setAccessLevel(rset.getInt("access_level"));
				setBanExpire(rset.getInt("ban_expire"));
				setAllowedIP(rset.getString("allow_ip"));
				setAllowedHwid(rset.getString("allow_hwid"));
				setBonus(rset.getInt("bonus"));
				setBonusExpire(rset.getInt("bonus_expire"));
				setLastServer(rset.getInt("last_server"));
				setLastIP(rset.getString("last_ip"));
				setLastAccess(rset.getInt("last_access"));
				setPoints(rset.getInt("points"));
                setPhoneNumber(rset.getLong("phone_nubmer"));
			}
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void save()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO accounts (login, password) VALUES(?,?)");
			statement.setString(1, getLogin());
			statement.setString(2, getPasswordHash());
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

	public void update()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE accounts SET password = ?, access_level = ?, ban_expire = ?, allow_ip = ?, allow_hwid=?, bonus = ?, bonus_expire = ?, last_server = ?, last_ip = ?, last_access = ?, points = ? WHERE login = ?");
			statement.setString(1, getPasswordHash());
			statement.setInt(2, getAccessLevel());
			statement.setInt(3, getBanExpire());
			statement.setString(4, getAllowedIP());
			statement.setString(5, getAllowedHwid());
			statement.setInt(6, getBonus());
			statement.setInt(7, getBonusExpire());
			statement.setInt(8, getLastServer());
			statement.setString(9, getLastIP());
			statement.setInt(10, getLastAccess());
			statement.setInt(11, getPoints());
			statement.setString(12, getLogin());
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

	public static void reducePoints(String account, int count)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE accounts SET points = (points - ?) WHERE login = ?");
			statement.setInt(1, count);
			statement.setString(2, account);
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

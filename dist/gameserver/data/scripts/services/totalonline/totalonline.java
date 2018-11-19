package services.totalonline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TreeSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.listener.script.OnInitScriptListener;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.tables.FakePlayersTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Online -> real + fake
 * by l2scripts
 */
public class totalonline implements OnInitScriptListener
{
	private static final Logger _log = LoggerFactory.getLogger(totalonline.class);

	@Override
	public void onInit()
	{
		_log.info(getClass().getSimpleName() + ": Loaded Service: Parse Online [" + (Config.ALLOW_ONLINE_PARSE ? "enabled]" : "disabled]"));

		if(Config.ALLOW_ONLINE_PARSE)
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new updateOnline(), Config.FIRST_UPDATE * 60000, Config.DELAY_UPDATE * 60000);
	}

	private class updateOnline implements Runnable
	{
		public void run()
		{
			int members = getOnlineMembers();
			int offMembers = getOfflineMembers();
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("update online set totalOnline =?, totalOffline = ? where 'index' =0");
				statement.setInt(1, members);
				statement.setInt(2, offMembers);
				statement.execute();
				DbUtils.closeQuietly(statement);		
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}			
		}
	}
	//for future possibility of parsing names of players method is taking also name to array for init
	private int getOnlineMembers()
	{
		int i = 0;
		for(Player player : GameObjectsStorage.getPlayers())
		{
			i++;
		}
		i = i + FakePlayersTable.getActiveFakePlayersCount();
		
		return i;	
	}
	private int getOfflineMembers()
	{
		int i = 0;
		for(Player player : GameObjectsStorage.getPlayers())
		{
			if(player.isInOfflineMode())
				i++;
		}
		
		return i;	
	}
}
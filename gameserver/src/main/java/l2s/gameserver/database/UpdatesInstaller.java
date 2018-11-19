package l2s.gameserver.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FilenameFilter;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.List;

import l2s.commons.dbutils.DbUtils;
import l2s.commons.dbutils.ScriptRunner;
import l2s.gameserver.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdatesInstaller
{
	private static final Logger _log = LoggerFactory.getLogger(UpdatesInstaller.class);;

	public static void checkAndInstall()
	{
		if(!Config.DATABASE_AUTOUPDATE)
		{
			_log.info("Disabled.");
			return;
		}

		List<String> installedUpdates = new ArrayList<String>();
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT file_name FROM installed_updates");
			rset = statement.executeQuery();
			while(rset.next())
				installedUpdates.add(rset.getString("file_name").trim().toLowerCase());
		}
		catch(Exception e)
		{
			_log.error("Error while restore installed updates from database: " + e, e);
			return;
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		File updatesDir = new File(Config.DATAPACK_ROOT, "sql/updates/");
		if(updatesDir == null || !updatesDir.isDirectory())
		{
			_log.warn("Cannot find " + Config.DATAPACK_ROOT.getPath() + "/sql/updates/ directory!");
			return;
		}

		File[] updateFiles = updatesDir.listFiles(new UpdateFilenameFilter());
		Arrays.sort(updateFiles);
		for(File f : updateFiles)
		{
			String name = f.getName().trim().toLowerCase();
			if(!installedUpdates.stream().anyMatch(str -> name.matches("^\\s*" + str + "\\s*\\.sql$")))
			{
				try
				{
					con = DatabaseFactory.getInstance().getConnection();
					ScriptRunner runner = new ScriptRunner(con, false, true);
					runner.runScript(new BufferedReader(new FileReader(f)));
				}
				catch(Exception e)
				{
					_log.error("Error while install database update [" + name + "]: " + e, e);
					return;
				}
				finally
				{
					DbUtils.closeQuietly(con, statement, rset);
					_log.info("Installed update: " + name);
				}
			}
		}
	}

	private static class UpdateFilenameFilter implements FilenameFilter
	{
		@Override
		public boolean accept(File dir, String name)
		{
			return name.matches(".+\\.sql");
		}
	}
}

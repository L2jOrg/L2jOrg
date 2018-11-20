package org.l2j.gameserver.network.telnet.commands;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.Set;

import org.l2j.gameserver.utils.GameStats;
import net.sf.ehcache.Cache;
import net.sf.ehcache.statistics.LiveCacheStatistics;

import org.l2j.commons.dao.JdbcEntityStats;
import org.l2j.commons.lang.StatsUtils;
import org.l2j.commons.net.nio.impl.SelectorStats;
import org.l2j.commons.threading.RunnableStatsManager;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.GameServer;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.dao.ItemsDAO;
import org.l2j.gameserver.dao.MailDAO;
import org.l2j.gameserver.database.DatabaseFactory;
import org.l2j.gameserver.geodata.PathFindBuffers;
import org.l2j.gameserver.network.telnet.TelnetCommand;
import org.l2j.gameserver.network.telnet.TelnetCommandHolder;
import org.l2j.gameserver.taskmanager.AiTaskManager;
import org.l2j.gameserver.taskmanager.EffectTaskManager;

import org.apache.commons.io.FileUtils;

public class TelnetPerfomance implements TelnetCommandHolder
{
	private Set<TelnetCommand> _commands = new LinkedHashSet<TelnetCommand>();

	public TelnetPerfomance()
	{
		_commands.add(new TelnetCommand("pool", "p"){
			@Override
			public String getUsage()
			{
				return "pool [dump]";
			}

			@Override
			public String handle(String[] args)
			{
				StringBuilder sb = new StringBuilder();

				if(args.length == 0 || args[0].isEmpty())
				{
					sb.append(ThreadPoolManager.getInstance().getStats());
				}
				else if(args[0].equals("dump") || args[0].equals("d"))
					try
					{
						new File("stats").mkdir();
						FileUtils.writeStringToFile(new File("stats/RunnableStats-" + new SimpleDateFormat("MMddHHmmss").format(System.currentTimeMillis()) + ".txt"), RunnableStatsManager.getInstance().getStats().toString());
						sb.append("Runnable stats saved.\n");
					}
					catch(IOException e)
					{
						sb.append("Exception: " + e.getMessage() + "!\n");
					}
				else
					return null;

				return sb.toString();
			}

		});

		_commands.add(new TelnetCommand("mem", "m"){
			@Override
			public String getUsage()
			{
				return "mem";
			}

			@Override
			public String handle(String[] args)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(StatsUtils.getMemUsage());

				return sb.toString();
			}
		});

		_commands.add(new TelnetCommand("threads", "t"){
			@Override
			public String getUsage()
			{
				return "threads [dump]";
			}

			@Override
			public String handle(String[] args)
			{
				StringBuilder sb = new StringBuilder();

				if(args.length == 0 || args[0].isEmpty())
				{
					sb.append(StatsUtils.getThreadStats());
				}
				else if(args[0].equals("dump") || args[0].equals("d"))
					try
					{
						new File("stats").mkdir();
						FileUtils.writeStringToFile(new File("stats/ThreadsDump-" + new SimpleDateFormat("MMddHHmmss").format(System.currentTimeMillis()) + ".txt"), StatsUtils.getThreadStats(true, true, true).toString());
						sb.append("Threads stats saved.\n");
					}
					catch(IOException e)
					{
						sb.append("Exception: " + e.getMessage() + "!\n");
					}
				else
					return null;

				return sb.toString();
			}
		});

		_commands.add(new TelnetCommand("gc"){
			@Override
			public String getUsage()
			{
				return "gc";
			}

			@Override
			public String handle(String[] args)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(StatsUtils.getGCStats());

				return sb.toString();
			}
		});

		_commands.add(new TelnetCommand("net", "ns"){
			@Override
			public String getUsage()
			{
				return "net";
			}

			@Override
			public String handle(String[] args)
			{
				StringBuilder sb = new StringBuilder();

				SelectorStats sts = GameServer.getInstance().getSelectorStats();
				sb.append("selectorThreadCount: .... ").append(GameServer.getInstance().getSelectorThreads().size()).append("\n");
				sb.append("=================================================\n");
				sb.append("getTotalConnections: .... ").append(sts.getTotalConnections()).append("\n");
				sb.append("getCurrentConnections: .. ").append(sts.getCurrentConnections()).append("\n");
				sb.append("getMaximumConnections: .. ").append(sts.getMaximumConnections()).append("\n");
				sb.append("getIncomingBytesTotal: .. ").append(sts.getIncomingBytesTotal()).append("\n");
				sb.append("getOutgoingBytesTotal: .. ").append(sts.getOutgoingBytesTotal()).append("\n");
				sb.append("getIncomingPacketsTotal:  ").append(sts.getIncomingPacketsTotal()).append("\n");
				sb.append("getOutgoingPacketsTotal:  ").append(sts.getOutgoingPacketsTotal()).append("\n");
				sb.append("getMaxBytesPerRead: ..... ").append(sts.getMaxBytesPerRead()).append("\n");
				sb.append("getMaxBytesPerWrite: .... ").append(sts.getMaxBytesPerWrite()).append("\n");
				sb.append("=================================================\n");

				return sb.toString();
			}

		});

		_commands.add(new TelnetCommand("pathfind", "pfs"){

			@Override
			public String getUsage()
			{
				return "pathfind";
			}

			@Override
			public String handle(String[] args)
			{
				StringBuilder sb = new StringBuilder();

				sb.append(PathFindBuffers.getStats());

				return sb.toString();
			}

		});

		_commands.add(new TelnetCommand("dbstats", "ds"){

			@Override
			public String getUsage()
			{
				return "dbstats";
			}

			@Override
			public String handle(String[] args)
			{
				StringBuilder sb = new StringBuilder();

				sb.append("Basic database usage\n");
				sb.append("=================================================\n");
				sb.append("Connections").append("\n");
				try
				{
					sb.append("     Busy: ........................ ").append(DatabaseFactory.getInstance().getBusyConnectionCount()).append("\n");
					sb.append("     Idle: ........................ ").append(DatabaseFactory.getInstance().getIdleConnectionCount()).append("\n");
				}
				catch(SQLException e)
				{
					return "Error: " + e.getMessage() + "\n";
				}

				sb.append("Players").append("\n");
				sb.append("     Update: ...................... ").append(GameStats.getUpdatePlayerBase()).append("\n");

				double cacheHitCount, cacheMissCount, cacheHitRatio;
				Cache cache;
				LiveCacheStatistics cacheStats;
				JdbcEntityStats entityStats;

				cache = ItemsDAO.getInstance().getCache();
				cacheStats = cache.getLiveCacheStatistics();
				entityStats = ItemsDAO.getInstance().getStats();

				cacheHitCount = cacheStats.getCacheHitCount();
				cacheMissCount = cacheStats.getCacheMissCount();
				cacheHitRatio = cacheHitCount / (cacheHitCount + cacheMissCount);

				sb.append("Items").append("\n");
				sb.append("     getLoadCount: ................ ").append(entityStats.getLoadCount()).append("\n");
				sb.append("     getInsertCount: .............. ").append(entityStats.getInsertCount()).append("\n");
				sb.append("     getUpdateCount: .............. ").append(entityStats.getUpdateCount()).append("\n");
				sb.append("     getDeleteCount: .............. ").append(entityStats.getDeleteCount()).append("\n");
				sb.append("Cache").append("\n");
				sb.append("     getPutCount: ................. ").append(cacheStats.getPutCount()).append("\n");
				sb.append("     getUpdateCount: .............. ").append(cacheStats.getUpdateCount()).append("\n");
				sb.append("     getRemovedCount: ............. ").append(cacheStats.getRemovedCount()).append("\n");
				sb.append("     getEvictedCount: ............. ").append(cacheStats.getEvictedCount()).append("\n");
				sb.append("     getExpiredCount: ............. ").append(cacheStats.getExpiredCount()).append("\n");
				sb.append("     getSize: ..................... ").append(cacheStats.getSize()).append("\n");
				sb.append("     getInMemorySize: ............. ").append(cacheStats.getLocalHeapSize()).append("\n");
				sb.append("     getOnDiskSize: ............... ").append(cacheStats.getLocalDiskSize()).append("\n");
				sb.append("     cacheHitRatio: ............... ").append(String.format("%2.2f", cacheHitRatio)).append("\n");
				sb.append("=================================================\n");

				cache = MailDAO.getInstance().getCache();
				cacheStats = cache.getLiveCacheStatistics();
				entityStats = MailDAO.getInstance().getStats();

				cacheHitCount = cacheStats.getCacheHitCount();
				cacheMissCount = cacheStats.getCacheMissCount();
				cacheHitRatio = cacheHitCount / (cacheHitCount + cacheMissCount);

				sb.append("Mail").append("\n");
				sb.append("     getLoadCount: ................ ").append(entityStats.getLoadCount()).append("\n");
				sb.append("     getInsertCount: .............. ").append(entityStats.getInsertCount()).append("\n");
				sb.append("     getUpdateCount: .............. ").append(entityStats.getUpdateCount()).append("\n");
				sb.append("     getDeleteCount: .............. ").append(entityStats.getDeleteCount()).append("\n");
				sb.append("Cache").append("\n");
				sb.append("     getPutCount: ................. ").append(cacheStats.getPutCount()).append("\n");
				sb.append("     getUpdateCount: .............. ").append(cacheStats.getUpdateCount()).append("\n");
				sb.append("     getRemovedCount: ............. ").append(cacheStats.getRemovedCount()).append("\n");
				sb.append("     getEvictedCount: ............. ").append(cacheStats.getEvictedCount()).append("\n");
				sb.append("     getExpiredCount: ............. ").append(cacheStats.getExpiredCount()).append("\n");
				sb.append("     getSize: ..................... ").append(cacheStats.getSize()).append("\n");
				sb.append("     getInMemorySize: ............. ").append(cacheStats.getLocalHeapSize()).append("\n");
				sb.append("     getOnDiskSize: ............... ").append(cacheStats.getLocalDiskSize()).append("\n");
				sb.append("     cacheHitRatio: ............... ").append(String.format("%2.2f", cacheHitRatio)).append("\n");
				sb.append("=================================================\n");

				return sb.toString();
			}

		});

		_commands.add(new TelnetCommand("aistats", "as"){

			@Override
			public String getUsage()
			{
				return "aistats";
			}

			@Override
			public String handle(String[] args)
			{
				StringBuilder sb = new StringBuilder();

				for(int i = 0; i < Config.AI_TASK_MANAGER_COUNT; i++)
				{
					sb.append("AiTaskManager #").append(i + 1).append("\n");
					sb.append("=================================================\n");
					sb.append(AiTaskManager.getInstance().getStats(i));
					sb.append("=================================================\n");
				}

				return sb.toString();
			}

		});
		_commands.add(new TelnetCommand("effectstats", "es"){

			@Override
			public String getUsage()
			{
				return "effectstats";
			}

			@Override
			public String handle(String[] args)
			{
				StringBuilder sb = new StringBuilder();

				for(int i = 0; i < Config.EFFECT_TASK_MANAGER_COUNT; i++)
				{
					sb.append("EffectTaskManager #").append(i + 1).append("\n");
					sb.append("=================================================\n");
					sb.append(EffectTaskManager.getInstance().getStats(i));
					sb.append("=================================================\n");
				}

				return sb.toString();
			}

		});
	}

	@Override
	public Set<TelnetCommand> getCommands()
	{
		return _commands;
	}
}
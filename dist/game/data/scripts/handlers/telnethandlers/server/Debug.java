/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.telnethandlers.server;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmobius.commons.concurrent.ThreadPool;
import com.l2jmobius.gameserver.GameServer;
import com.l2jmobius.gameserver.GameTimeController;
import com.l2jmobius.gameserver.LoginServerThread;
import com.l2jmobius.gameserver.data.xml.impl.AdminData;
import com.l2jmobius.gameserver.enums.ItemLocation;
import com.l2jmobius.gameserver.model.L2Object;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.network.serverpackets.AdminForgePacket;
import com.l2jmobius.gameserver.network.telnet.ITelnetCommand;
import com.l2jmobius.gameserver.taskmanager.DecayTaskManager;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class Debug implements ITelnetCommand
{
	private static final Logger LOGGER = Logger.getLogger(Debug.class.getName());
	
	@Override
	public String getCommand()
	{
		return "debug";
	}
	
	@Override
	public String getUsage()
	{
		return "Debug <decay/packetsend/full>";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		if ((args.length == 0) || args[0].isEmpty())
		{
			return null;
		}
		switch (args[0])
		{
			case "decay":
			{
				return DecayTaskManager.getInstance().toString();
			}
			case "packetsend":
			{
				if (args.length < 4)
				{
					return "Usage: debug packetsend <charName> <packetData>";
				}
				final L2PcInstance player = L2World.getInstance().getPlayer(args[1]);
				if (player == null)
				{
					return "Couldn't find player with such name.";
				}
				
				final AdminForgePacket sp = new AdminForgePacket();
				for (int i = 2; i < args.length; i++)
				{
					final String b = args[i];
					if (!b.isEmpty())
					{
						sp.addPart("C".getBytes()[0], "0x" + b);
					}
				}
				player.sendPacket(sp);
				return "Packet has been sent!";
			}
			case "full":
			{
				final Calendar cal = Calendar.getInstance();
				final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
				
				final StringBuilder sb = new StringBuilder();
				sb.append(sdf.format(cal.getTime()));
				sb.append("\r\nServer");
				sb.append("\r\n");
				sb.append(getServerStatus());
				sb.append("\r\n");
				sb.append("\r\n## Java Platform Information ##");
				sb.append("\r\nJava Runtime Name: " + System.getProperty("java.runtime.name"));
				sb.append("\r\nJava Version: " + System.getProperty("java.version"));
				sb.append("\r\nJava Class Version: " + System.getProperty("java.class.version"));
				sb.append("\r\n");
				sb.append("\r\n## Virtual Machine Information ##");
				sb.append("\r\nVM Name: " + System.getProperty("java.vm.name"));
				sb.append("\r\nVM Version: " + System.getProperty("java.vm.version"));
				sb.append("\r\nVM Vendor: " + System.getProperty("java.vm.vendor"));
				sb.append("\r\nVM Info: " + System.getProperty("java.vm.info"));
				sb.append("\r\n");
				sb.append("\r\n## OS Information ##");
				sb.append("\r\nName: " + System.getProperty("os.name"));
				sb.append("\r\nArchiteture: " + System.getProperty("os.arch"));
				sb.append("\r\nVersion: " + System.getProperty("os.version"));
				sb.append("\r\n");
				sb.append("\r\n## Runtime Information ##");
				sb.append("\r\nCPU Count: " + Runtime.getRuntime().availableProcessors());
				sb.append("\r\nCurrent Free Heap Size: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " mb");
				sb.append("\r\nCurrent Heap Size: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " mb");
				sb.append("\r\nMaximum Heap Size: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " mb");
				
				sb.append("\r\n");
				sb.append("\r\n## Class Path Information ##\r\n");
				final String cp = System.getProperty("java.class.path");
				final String[] libs = cp.split(File.pathSeparator);
				for (String lib : libs)
				{
					sb.append(lib);
					sb.append("\r\n");
				}
				
				sb.append("\r\n");
				sb.append("## Threads Information ##\r\n");
				final Map<Thread, StackTraceElement[]> allThread = Thread.getAllStackTraces();
				
				final List<Entry<Thread, StackTraceElement[]>> entries = new ArrayList<>(allThread.entrySet());
				Collections.sort(entries, (e1, e2) -> e1.getKey().getName().compareTo(e2.getKey().getName()));
				
				for (Entry<Thread, StackTraceElement[]> entry : entries)
				{
					final StackTraceElement[] stes = entry.getValue();
					final Thread t = entry.getKey();
					sb.append("--------------\r\n");
					sb.append(t + " (" + t.getId() + ")\r\n");
					sb.append("State: " + t.getState() + "\r\n");
					sb.append("isAlive: " + t.isAlive() + " | isDaemon: " + t.isDaemon() + " | isInterrupted: " + t.isInterrupted() + "\r\n");
					sb.append("\r\n");
					for (StackTraceElement ste : stes)
					{
						sb.append(ste.toString());
						sb.append("\r\n");
					}
					sb.append("\r\n");
				}
				
				sb.append("\r\n");
				final ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
				final long[] ids = findDeadlockedThreads(mbean);
				if ((ids != null) && (ids.length > 0))
				{
					final Thread[] threads = new Thread[ids.length];
					for (int i = 0; i < threads.length; i++)
					{
						threads[i] = findMatchingThread(mbean.getThreadInfo(ids[i]));
					}
					sb.append("Deadlocked Threads:\r\n");
					sb.append("-------------------\r\n");
					for (Thread thread : threads)
					{
						System.err.println(thread);
						for (StackTraceElement ste : thread.getStackTrace())
						{
							sb.append("\t" + ste);
							sb.append("\r\n");
						}
					}
				}
				
				sb.append("\r\n## Thread Pool Manager Statistics ##\r\n");
				for (String line : ThreadPool.getStats())
				{
					sb.append(line);
					sb.append("\r\n");
				}
				
				int i = 0;
				File f = new File("./log/Debug-" + i + ".txt");
				while (f.exists())
				{
					i++;
					f = new File("./log/Debug-" + i + ".txt");
				}
				f.getParentFile().mkdirs();
				
				try
				{
					Files.write(f.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
				}
				catch (IOException e)
				{
					LOGGER.log(Level.WARNING, "Couldn't write packet tp.", e);
				}
				return "Debug output saved to log/" + f.getName();
			}
		}
		return null;
	}
	
	private long[] findDeadlockedThreads(ThreadMXBean mbean)
	{
		// JDK 1.5 only supports the findMonitorDeadlockedThreads()
		// method, so you need to comment out the following three lines
		if (mbean.isSynchronizerUsageSupported())
		{
			return mbean.findDeadlockedThreads();
		}
		return mbean.findMonitorDeadlockedThreads();
	}
	
	private Thread findMatchingThread(ThreadInfo inf)
	{
		for (Thread thread : Thread.getAllStackTraces().keySet())
		{
			if (thread.getId() == inf.getThreadId())
			{
				return thread;
			}
		}
		throw new IllegalStateException("Deadlocked Thread not found");
	}
	
	static String getServerStatus()
	{
		int playerCount = 0;
		int objectCount = 0;
		final int max = LoginServerThread.getInstance().getMaxPlayer();
		
		playerCount = L2World.getInstance().getPlayers().size();
		objectCount = L2World.getInstance().getVisibleObjectsCount();
		
		int itemCount = 0;
		int itemVoidCount = 0;
		int monsterCount = 0;
		int minionCount = 0;
		int minionsGroupCount = 0;
		int npcCount = 0;
		int charCount = 0;
		int pcCount = 0;
		int detachedCount = 0;
		int doorCount = 0;
		int summonCount = 0;
		int AICount = 0;
		
		for (L2Object obj : L2World.getInstance().getVisibleObjects())
		{
			if (obj == null)
			{
				continue;
			}
			if (obj.isCharacter())
			{
				if (((L2Character) obj).hasAI())
				{
					AICount++;
				}
			}
			if (obj.isItem())
			{
				if (((L2ItemInstance) obj).getItemLocation() == ItemLocation.VOID)
				{
					itemVoidCount++;
				}
				else
				{
					itemCount++;
				}
			}
			else if (obj.isMonster())
			{
				monsterCount++;
				if (((L2MonsterInstance) obj).hasMinions())
				{
					minionCount += ((L2MonsterInstance) obj).getMinionList().countSpawnedMinions();
					minionsGroupCount += ((L2MonsterInstance) obj).getMinionList().lazyCountSpawnedMinionsGroups();
				}
			}
			else if (obj.isNpc())
			{
				npcCount++;
			}
			else if (obj.isPlayer())
			{
				pcCount++;
				if ((((L2PcInstance) obj).getClient() != null) && ((L2PcInstance) obj).getClient().isDetached())
				{
					detachedCount++;
				}
			}
			else if (obj.isSummon())
			{
				summonCount++;
			}
			else if (obj.isDoor())
			{
				doorCount++;
			}
			else if (obj.isCharacter())
			{
				charCount++;
			}
		}
		final StringBuilder sb = new StringBuilder();
		sb.append("Server Status: ");
		sb.append("\r\n  --->  Player Count: " + playerCount + "/" + max);
		sb.append("\r\n  ---> Offline Count: " + detachedCount + "/" + playerCount);
		sb.append("\r\n  +-->  Object Count: " + objectCount);
		sb.append("\r\n  +-->      AI Count: " + AICount);
		sb.append("\r\n  +.... L2Item(Void): " + itemVoidCount);
		sb.append("\r\n  +.......... L2Item: " + itemCount);
		sb.append("\r\n  +....... L2Monster: " + monsterCount);
		sb.append("\r\n  +......... Minions: " + minionCount);
		sb.append("\r\n  +.. Minions Groups: " + minionsGroupCount);
		sb.append("\r\n  +........... L2Npc: " + npcCount);
		sb.append("\r\n  +............ L2Pc: " + pcCount);
		sb.append("\r\n  +........ L2Summon: " + summonCount);
		sb.append("\r\n  +.......... L2Door: " + doorCount);
		sb.append("\r\n  +.......... L2Char: " + charCount);
		sb.append("\r\n  --->   Ingame Time: " + gameTime());
		sb.append("\r\n  ---> Server Uptime: " + GameServer.getInstance().getUptime());
		sb.append("\r\n  --->      GM Count: " + getOnlineGMS());
		sb.append("\r\n  --->       Threads: " + Thread.activeCount());
		sb.append("\r\n  RAM Used: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576)); // 1024 * 1024 = 1048576
		sb.append("\r\n");
		
		return sb.toString();
	}
	
	private static int getOnlineGMS()
	{
		return AdminData.getInstance().getAllGms(true).size();
	}
	
	private static String gameTime()
	{
		final int t = GameTimeController.getInstance().getGameTime();
		final int h = t / 60;
		final int m = t % 60;
		final SimpleDateFormat format = new SimpleDateFormat("H:mm");
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);
		return format.format(cal.getTime());
	}
}

package l2s.gameserver;

import java.util.Timer;
import java.util.TimerTask;

import l2s.commons.net.nio.impl.SelectorThread;
import l2s.commons.time.cron.SchedulingPattern;
import l2s.commons.time.cron.SchedulingPattern.InvalidPatternException;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.instancemanager.BotReportManager;
import l2s.gameserver.instancemanager.CoupleManager;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.olympiad.OlympiadDatabase;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.tables.ClanTable;
import l2s.gameserver.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс отвечает за запланированное отключение сервера и связанные с этим мероприятия.
 * 
 * @author G1ta0
 */
public class Shutdown extends Thread
{
	private static final Logger _log = LoggerFactory.getLogger(Shutdown.class);

	public static final int SHUTDOWN = 0;
	public static final int RESTART = 2;
	public static final int NONE = -1;

	public static final int FULL_ANNOUNCES = 2;
	public static final int OFFLIKE_ANNOUNCES = 1;

	private static final Shutdown _instance = new Shutdown();

	public static final Shutdown getInstance()
	{
		return _instance;
	}

	private Timer counter;

	private int shutdownMode;
	private int shutdownCounter;

	private class ShutdownCounter extends TimerTask
	{
		@Override
		public void run()
		{
			switch(shutdownCounter)
			{
				case 1800:
				case 900:
				case 600:
				case 300:
				case 240:
				case 180:
				case 120:
				case 60:
					if(Config.SHUTDOWN_ANN_TYPE == FULL_ANNOUNCES)
					{
						Announcements.announceToAllFromStringHolder("THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_MINUTES", String.valueOf(shutdownCounter / 60));
					}
					break;
				case 30:
				case 20:
				case 10:
				case 5:
					if(Config.SHUTDOWN_ANN_TYPE == FULL_ANNOUNCES || Config.SHUTDOWN_ANN_TYPE == OFFLIKE_ANNOUNCES)
					{
						Announcements.announceToAll(new SystemMessagePacket(SystemMsg.THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS__PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT).addInteger(shutdownCounter));
					}
					break;
				case 0:
					switch(shutdownMode)
					{
						case SHUTDOWN:
							Runtime.getRuntime().exit(SHUTDOWN);
							break;
						case RESTART:
							Runtime.getRuntime().exit(RESTART);
							break;
					}
					cancel();
					return;
			}

			shutdownCounter--;
		}
	}

	private Shutdown()
	{
		setName(getClass().getSimpleName());
		setDaemon(true);

		shutdownMode = NONE;
	}

	/**
	 * Время в секундах до отключения.
	 * 
	 * @return время в секундах до отключения сервера, -1 если отключение не запланировано
	 */
	public int getSeconds()
	{
		return shutdownMode == NONE ? -1 : shutdownCounter;
	}

	/**
	 * Режим отключения.
	 * 
	 * @return <code>SHUTDOWN</code> или <code>RESTART</code>, либо <code>NONE</code>, если отключение не запланировано.
	 */
	public int getMode()
	{
		return shutdownMode;
	}

	/**
	 * Запланировать отключение сервера через определенный промежуток времени.
	 * 
	 * @param seconds время в формате <code>hh:mm</code>
	 * @param shutdownMode  <code>SHUTDOWN</code> или <code>RESTART</code>
	 */
	public synchronized void schedule(int seconds, int shutdownMode)
	{
		if(seconds < 0)
			return;

		if(counter != null)
			counter.cancel();

		this.shutdownMode = shutdownMode;
		this.shutdownCounter = seconds;

		_log.info("Scheduled server " + (shutdownMode == SHUTDOWN ? "shutdown" : "restart") + " in " + Util.formatTime(seconds) + ".");

		counter = new Timer("ShutdownCounter", true);
		counter.scheduleAtFixedRate(new ShutdownCounter(), 0, 1000L);
	}

	/**
	 * Запланировать отключение сервера на определенное время.
	 * 
	 * @param time время в формате cron
	 * @param shutdownMode <code>SHUTDOWN</code> или <code>RESTART</code>
	 */
	public void schedule(String time, int shutdownMode)
	{
		SchedulingPattern cronTime;
		try
		{
			cronTime = new SchedulingPattern(time);
		}
		catch(InvalidPatternException e)
		{
			return;
		}

		int seconds = (int) (cronTime.next(System.currentTimeMillis()) / 1000L - System.currentTimeMillis() / 1000L);
		schedule(seconds, shutdownMode);
	}

	/**
	 * Отменить запланированное отключение сервера.
	 */
	public synchronized void cancel()
	{
		shutdownMode = NONE;
		if(counter != null)
			counter.cancel();
		counter = null;
	}

	@Override
	public void run()
	{
		System.out.println("Shutting down LS/GS communication...");
		AuthServerCommunication.getInstance().shutdown();

		System.out.println("Disconnecting players...");
		disconnectAllPlayers();

		GameServer gameServer = GameServer.getInstance();
		if(gameServer != null)
			gameServer.getListeners().onShutdown();

		System.out.println("Saving data...");
		saveData();

		try
		{
			System.out.println("Shutting down thread pool...");
			ThreadPoolManager.getInstance().shutdown();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		System.out.println("Shutting down selector...");
		if(gameServer != null)
			for(SelectorThread<GameClient> st : gameServer.getSelectorThreads())
				try
				{
					st.shutdown();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

		try
		{
			System.out.println("Shutting down database communication...");
			DatabaseFactory.getInstance().shutdown();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		System.out.println("Shutdown finished.");
	}

	private void saveData()
	{
		if(Config.ENABLE_OLYMPIAD)
			try
			{
				OlympiadDatabase.save();
				System.out.println("Olympiad: Data saved.");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		if(Config.ALLOW_WEDDING)
			try
			{
				CoupleManager.getInstance().store();
				System.out.println("CoupleManager: Data saved.");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		try
		{
			Hero.getInstance().shutdown();
			System.out.println("Hero: Data saved.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			ClanTable.getInstance().storeClanWars();
			System.out.println("Clan War: Data saved.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

        try
        {
            ClanTable.getInstance().saveClanHuntingProgress();
            System.out.println("Clan Data: Hunting progress saved.");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if(Config.BOTREPORT_ENABLED)
        {
            try
            {
                BotReportManager.getInstance().saveReportedCharData();
                System.out.println("BotReportManager: Data saved.");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
	}

    private void disconnectAllPlayers()
	{
		for(Player player : GameObjectsStorage.getPlayers())
			try
			{
				player.logout();
			}
			catch(Exception e)
			{
				System.out.println("Error while disconnecting: " + player + "!");
				e.printStackTrace();
			}
	}
}
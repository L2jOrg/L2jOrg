package org.l2j.gameserver;

import java.util.Calendar;

import org.l2j.commons.listener.Listener;
import org.l2j.commons.listener.ListenerList;
import org.l2j.commons.threading.RunnableImpl;
import org.l2j.gameserver.listener.GameListener;
import org.l2j.gameserver.listener.game.OnDayNightChangeListener;
import org.l2j.gameserver.listener.game.OnStartListener;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ClientSetTimePacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameTimeController
{
	private class OnStartListenerImpl implements OnStartListener
	{
		@Override
		public void onStart()
		{
			ThreadPoolManager.getInstance().execute(_dayChangeNotify);
		}
	}

	public class CheckSunState extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			if(isNowNight())
				getInstance().getListenerEngine().onNight();
			else
				getInstance().getListenerEngine().onDay();

			for(Player player : GameObjectsStorage.getPlayers())
			{
				player.checkDayNightMessages();
				player.sendPacket(new ClientSetTimePacket());
			}
		}
	}

	protected class GameTimeListenerList extends ListenerList<GameServer>
	{
		public void onDay()
		{
			for(Listener<GameServer> listener : getListeners())
				if(OnDayNightChangeListener.class.isInstance(listener))
					((OnDayNightChangeListener) listener).onDay();
		}

		public void onNight()
		{
			for(Listener<GameServer> listener : getListeners())
				if(OnDayNightChangeListener.class.isInstance(listener))
					((OnDayNightChangeListener) listener).onNight();
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(GameTimeController.class);

	public static final int TICKS_PER_SECOND = 10;
	public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;

	private static final GameTimeController _instance = new GameTimeController();

	private long _gameStartTime;

	private GameTimeListenerList listenerEngine = new GameTimeListenerList();
	private Runnable _dayChangeNotify = new CheckSunState();

	public static final GameTimeController getInstance()
	{
		return _instance;
	}

	private GameTimeController()
	{
		_gameStartTime = getDayStartTime();

		GameServer.getInstance().addListener(new OnStartListenerImpl());

		StringBuilder msg = new StringBuilder();
		msg.append("GameTimeController: initialized.").append(" ");
		msg.append("Current time is ");
		msg.append(getGameHour()).append(":");
		if(getGameMin() < 10)
			msg.append("0");
		msg.append(getGameMin());
		msg.append(" in the ");
		if(isNowNight())
			msg.append("night");
		else
			msg.append("day");
		msg.append(".");

		_log.info(msg.toString());

		long nightStart = 0;
		long dayStart = 60 * 60 * 1000;

		while(_gameStartTime + nightStart < System.currentTimeMillis())
			nightStart += 4 * 60 * 60 * 1000;

		while(_gameStartTime + dayStart < System.currentTimeMillis())
			dayStart += 4 * 60 * 60 * 1000;

		dayStart -= System.currentTimeMillis() - _gameStartTime;
		nightStart -= System.currentTimeMillis() - _gameStartTime;

		ThreadPoolManager.getInstance().scheduleAtFixedRate(_dayChangeNotify, nightStart, 4 * 60 * 60 * 1000L);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(_dayChangeNotify, dayStart, 4 * 60 * 60 * 1000L);
	}

	/**
	 * Вычисляем смещение до начала игровых суток
	 * 
	 * @return смещение в миллисекнду до начала игровых суток (6:00AM)
	 */
	private long getDayStartTime()
	{
		Calendar dayStart = Calendar.getInstance();

		int HOUR_OF_DAY = dayStart.get(Calendar.HOUR_OF_DAY);

		dayStart.add(Calendar.HOUR_OF_DAY, -(HOUR_OF_DAY + 1) % 4); //1 день в игре это 4 часа реального времени
		dayStart.set(Calendar.MINUTE, 0);
		dayStart.set(Calendar.SECOND, 0);
		dayStart.set(Calendar.MILLISECOND, 0);

		return dayStart.getTimeInMillis();
	}

	public boolean isNowNight()
	{
		return getGameHour() < 6;
	}

	public int getGameTime()
	{
		return getGameTicks() / MILLIS_IN_TICK;
	}

	public int getGameHour()
	{
		return getGameTime() / 60 % 24;
	}

	public int getGameMin()
	{
		return getGameTime() % 60;
	}

	public int getGameTicks()
	{
		return (int) ((System.currentTimeMillis() - _gameStartTime) / MILLIS_IN_TICK);
	}

	public GameTimeListenerList getListenerEngine()
	{
		return listenerEngine;
	}

	public <T extends GameListener> boolean addListener(T listener)
	{
		return listenerEngine.add(listener);
	}

	public <T extends GameListener> boolean removeListener(T listener)
	{
		return listenerEngine.remove(listener);
	}
}
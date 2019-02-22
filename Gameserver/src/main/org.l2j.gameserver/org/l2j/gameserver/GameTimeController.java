package org.l2j.gameserver;

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

import java.util.Calendar;

import static org.l2j.commons.util.Util.HOUR_IN_MILLIS;

public class GameTimeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GameTimeController.class);

	private static final int TICKS_PER_SECOND = 10;
	private static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
	private static final GameTimeController _instance = new GameTimeController();

	private GameTimeListenerList listenerEngine = new GameTimeListenerList();
	private Runnable _dayChangeNotify = new CheckSunState();
	private long _gameStartTime;

	private GameTimeController() {
		_gameStartTime = getDayStartTime();

		GameServer.getInstance().addListener((OnStartListener) () -> ThreadPoolManager.getInstance().execute(_dayChangeNotify));
		var min = getGameMinutes();
		LOGGER.info("Time Controller Initialized. Current time is {}:{} in the {}.", getGameHour(), min < 10 ? "0" + min : min, isNowNight() ? "night" : "day");

		long nightStart = 0;
		long dayStart = HOUR_IN_MILLIS;

		var currentTime = System.currentTimeMillis();
		while(_gameStartTime + nightStart < currentTime)
			nightStart += 4 * HOUR_IN_MILLIS;

		while(_gameStartTime + dayStart < currentTime)
			dayStart += 4 * HOUR_IN_MILLIS;

		dayStart -= currentTime - _gameStartTime;
		nightStart -= currentTime - _gameStartTime;

		ThreadPoolManager.getInstance().scheduleAtFixedRate(_dayChangeNotify, nightStart, 4 * HOUR_IN_MILLIS);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(_dayChangeNotify, dayStart, 4 * HOUR_IN_MILLIS);
	}

	private long getDayStartTime() {
		Calendar dayStart = Calendar.getInstance();

		int HOUR_OF_DAY = dayStart.get(Calendar.HOUR_OF_DAY);

		dayStart.add(Calendar.HOUR_OF_DAY, -(HOUR_OF_DAY + 1) % 4); //1 Day in Game is 4 hour in real time
		dayStart.set(Calendar.MINUTE, 0);
		dayStart.set(Calendar.SECOND, 0);
		dayStart.set(Calendar.MILLISECOND, 0);

		return dayStart.getTimeInMillis();
	}

	public boolean isNowNight() {
		return getGameHour() < 6;
	}

	public int getGameTime() {
		return getGameTicks() / MILLIS_IN_TICK;
	}

	public int getGameHour() {
		return getGameTime() / 60 % 24;
	}

	public int getGameMinutes() {
		return getGameTime() % 60;
	}

	private int getGameTicks() {
		return (int) ((System.currentTimeMillis() - _gameStartTime) / MILLIS_IN_TICK);
	}

	private GameTimeListenerList getListenerEngine() {
		return listenerEngine;
	}

	public <T extends GameListener> boolean addListener(T listener) {
		return listenerEngine.add(listener);
	}

	public <T extends GameListener> boolean removeListener(T listener) {
		return listenerEngine.remove(listener);
	}

	public class CheckSunState extends RunnableImpl {
		@Override
		public void runImpl() throws Exception {
			if(isNowNight()) {
				getInstance().getListenerEngine().onNight();
			} else {
				getInstance().getListenerEngine().onDay();
			}

			for(Player player : GameObjectsStorage.getPlayers()) {
				player.checkDayNightMessages();
				player.sendPacket(new ClientSetTimePacket());
			}
		}
	}


	public static GameTimeController getInstance() {
		return _instance;
	}

	private class GameTimeListenerList extends ListenerList<GameServer> {
		private void onDay() {
			listeners.stream().filter( l -> l instanceof OnDayNightChangeListener).forEach(l -> ((OnDayNightChangeListener)l).onDay());
		}

		private void onNight() {
			listeners.stream().filter( l -> l instanceof OnDayNightChangeListener).forEach(l -> ((OnDayNightChangeListener)l).onNight());
		}
	}
}
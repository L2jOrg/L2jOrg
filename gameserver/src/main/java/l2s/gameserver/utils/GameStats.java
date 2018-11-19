package l2s.gameserver.utils;

import java.util.concurrent.atomic.AtomicLong;

public class GameStats
{
	/* database statistics */
	private static AtomicLong _updatePlayerBase = new AtomicLong(0L);

	/* in-game statistics */
	private static AtomicLong _playerEnterGameCounter = new AtomicLong(0L);

	public static void increaseUpdatePlayerBase()
	{
		_updatePlayerBase.incrementAndGet();
	}

	public static long getUpdatePlayerBase()
	{
		return _updatePlayerBase.get();
	}

	public static void incrementPlayerEnterGame()
	{
		_playerEnterGameCounter.incrementAndGet();
	}

	public static long getPlayerEnterGame()
	{
		return _playerEnterGameCounter.get();
	}
}
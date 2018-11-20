package org.l2j.gameserver.taskmanager;

import java.util.concurrent.Future;

import org.l2j.commons.threading.SteppingRunnableQueueManager;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.model.Player;

/**
 * Менеджер автосохранения игроков, шаг выполенния задач 10с.
 * 
 * @author G1ta0
 */
public class AutoSaveManager extends SteppingRunnableQueueManager
{
	private static final AutoSaveManager _instance = new AutoSaveManager();

	public static final AutoSaveManager getInstance()
	{
		return _instance;
	}

	private AutoSaveManager()
	{
		super(10000L);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 10000L, 10000L);
		//Очистка каждые 60 секунд
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> AutoSaveManager.this.purge(), 60000L, 60000L);
	}

	public Future<?> addAutoSaveTask(final Player player)
	{
		long delay = Rnd.get(180, 360) * 1000L;

		return scheduleAtFixedRate(() ->
		{
			if(!player.isOnline())
				return;

			player.store(true);
		}, delay, delay);
	}
}
package org.l2j.gameserver.taskmanager;

import java.util.concurrent.Future;

import org.l2j.commons.threading.RunnableImpl;
import org.l2j.commons.threading.SteppingRunnableQueueManager;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.model.Creature;

/**
 * Менеджер задач по "исчезновению" убитых персонажей, шаг выполенния задач 500 мс.
 * 
 * @author G1ta0
 */
public class DecayTaskManager extends SteppingRunnableQueueManager
{
	private static final DecayTaskManager _instance = new DecayTaskManager();

	public static final DecayTaskManager getInstance()
	{
		return _instance;
	}

	private DecayTaskManager()
	{
		super(500L);

		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 500L, 500L);

		//Очистка каждую минуту
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> DecayTaskManager.this.purge(), 60000L, 60000L);
	}

	public Future<?> addDecayTask(final Creature actor, long delay)
	{
		return schedule(() -> actor.doDecay(), delay);
	}
}
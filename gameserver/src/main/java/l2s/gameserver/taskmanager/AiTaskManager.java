package l2s.gameserver.taskmanager;

import l2s.commons.threading.SteppingRunnableQueueManager;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;

/**
 * Менеджер задач AI, шаг выполенния задач 250 мс.
 * 
 * @author G1ta0
 */
public class AiTaskManager extends SteppingRunnableQueueManager
{
	private final static long TICK = 250L;
	private static int _randomizer;

	private final static AiTaskManager[] _instances = new AiTaskManager[Config.AI_TASK_MANAGER_COUNT];
	static
	{
		for(int i = 0; i < _instances.length; i++)
			_instances[i] = new AiTaskManager();
	}

	public final static AiTaskManager getInstance()
	{
		return _instances[_randomizer++ & _instances.length - 1];
	}

	private AiTaskManager()
	{
		super(TICK);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, Rnd.get(TICK), TICK);
		//Очистка каждую минуту
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> AiTaskManager.this.purge(), 60000L + 1000L * _randomizer++, 60000L);
	}

	public CharSequence getStats(int num)
	{
		return _instances[num].getStats();
	}
}
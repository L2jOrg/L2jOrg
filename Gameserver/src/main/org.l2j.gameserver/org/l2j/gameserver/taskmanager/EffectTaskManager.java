package org.l2j.gameserver.taskmanager;

import org.l2j.commons.threading.SteppingRunnableQueueManager;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;

/**
 * Менеджер задач для работы с эффектами, шаг выполенния задач 250 мс.
 * 
 * @author G1ta0
 */
public class EffectTaskManager extends SteppingRunnableQueueManager
{
	private final static long TICK = 250L;

	private static int _randomizer;

	private final static EffectTaskManager[] _instances = new EffectTaskManager[Config.EFFECT_TASK_MANAGER_COUNT];
	static
	{
		for(int i = 0; i < _instances.length; i++)
			_instances[i] = new EffectTaskManager();
	}

	public final static EffectTaskManager getInstance()
	{
		return _instances[_randomizer++ & _instances.length - 1];
	}

	private EffectTaskManager()
	{
		super(TICK);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, Rnd.get(TICK), TICK);
		//Очистка каждые 30 секунд
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> EffectTaskManager.this.purge(), 30000L + 1000L * _randomizer++, 30000L);
	}
}
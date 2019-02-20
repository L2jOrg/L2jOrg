package org.l2j.gameserver.taskmanager;

import java.util.concurrent.Future;

import org.l2j.commons.threading.SteppingRunnableQueueManager;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;

/**
 * Менеджер задач вспомогательных задач, шаг выполенния задач 1с.
 *
 * @author G1ta0
 */
public class LazyPrecisionTaskManager extends SteppingRunnableQueueManager
{
	private static final LazyPrecisionTaskManager _instance = new LazyPrecisionTaskManager();

	public static final LazyPrecisionTaskManager getInstance()
	{
		return _instance;
	}

	private LazyPrecisionTaskManager()
	{
		super(1000L);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
		//Очистка каждые 60 секунд
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> LazyPrecisionTaskManager.this.purge(), 60000L, 60000L);
	}

	public Future<?> addPCCafePointsTask(final Player player)
	{
		long delay = Config.ALT_PCBANG_POINTS_DELAY * 60000L;

		return scheduleAtFixedRate(() ->
		{
			if(player.getLevel() < Config.ALT_PCBANG_POINTS_MIN_LVL)
				return;

			if(Config.ALT_PCBANG_POINTS_ONLY_PREMIUM && !player.hasPremiumAccount())
				return;

				player.addPcBangPoints(Config.ALT_PCBANG_POINTS_BONUS, Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE > 0 && Rnd.chance(Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE), true);
		}, delay, delay);
	}

	public Future<?> startPremiumAccountExpirationTask(Player player, long expire)
	{
		long delay = expire * 1000L - System.currentTimeMillis();

		return schedule(() -> player.removePremiumAccount(), delay);
	}

	public Future<?> addNpcAnimationTask(final NpcInstance npc)
	{
		return scheduleAtFixedRate(() ->
		{
			if(npc.isVisible() && !npc.isActionsDisabled() && !npc.isMoving && !npc.isInCombat())
				npc.onRandomAnimation();
		}, 1000L, Rnd.get(Config.MIN_NPC_ANIMATION, Config.MAX_NPC_ANIMATION) * 1000L);
	}
}

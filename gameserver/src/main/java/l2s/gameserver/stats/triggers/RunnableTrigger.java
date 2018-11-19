package l2s.gameserver.stats.triggers;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.skills.SkillEntry;

/**
 * @author Yorie
 * Date: 09.02.12
 */
public class RunnableTrigger implements Runnable
{
	private final TriggerInfo _trigger;
	private final Creature _creature;
	private final int _delay;

	public RunnableTrigger(Creature creature, TriggerInfo trigger)
	{
		_creature = creature;
		_trigger = trigger;
		int delay = 0;

		SkillEntry skillEntry = _trigger.getSkill();
		if (skillEntry != null)
			delay = skillEntry.getTemplate().getReuseDelay();
		if(_trigger.getDelay() > delay)
			delay = _trigger.getDelay();
		if(delay <= 0)
			delay = 1000;
		_delay = delay;
	}

	public void run()
	{
		if(_creature.getTriggers() == null)
			return;

		if(!_creature.getTriggers().get(_trigger.getType()).contains(_trigger))
			return;

		_creature.useTriggerSkill(_creature, null, _trigger, null, 0);
		schedule();
	}

	public void schedule()
	{
		ThreadPoolManager.getInstance().schedule(this, _delay);
	}
}
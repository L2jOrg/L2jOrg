package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.items.LockType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author VISTALL
 * @date 14:13/16.05.2011
 */
public class EffectLockInventory extends Effect
{
	private LockType _lockType;
	private int[] _lockItems;

	public EffectLockInventory(Abnormal abnormal, Env env, EffectTemplate template)
	{
		super(abnormal, env, template);
		_lockType = template.getParam().getEnum("lockType", LockType.class);
		_lockItems = template.getParam().getIntegerArray("lockItems");
	}

	@Override
	public void onStart()
	{
		Player player = getEffector().getPlayer();
		player.getInventory().lockItems(_lockType, _lockItems);
	}

	@Override
	public void onExit()
	{
		Player player = getEffector().getPlayer();
		player.getInventory().unlock();
	}
}
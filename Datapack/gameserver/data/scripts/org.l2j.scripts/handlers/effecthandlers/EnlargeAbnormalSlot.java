package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Enlarge Abnormal Slot effect implementation.
 * @author Zoey76
 */
public final class EnlargeAbnormalSlot extends AbstractEffect {
	private final int slots;
	
	public EnlargeAbnormalSlot(StatsSet params)
	{
		slots = params.getInt("slots", 0);
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		return (effector != null) && isPlayer(effected);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.getStats().setMaxBuffCount(effected.getStats().getMaxBuffCount() + slots);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.getStats().setMaxBuffCount(Math.max(0, effected.getStats().getMaxBuffCount() - slots));
	}
}

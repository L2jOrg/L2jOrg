package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Talisman Slot effect implementation.
 * @author Adry_85
 */
public final class TalismanSlot extends AbstractEffect {
	private final int slots;
	
	public TalismanSlot(StatsSet params)
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
		effected.getActingPlayer().getStats().addTalismanSlots(slots);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.getActingPlayer().getStats().addTalismanSlots(-slots);
	}
}

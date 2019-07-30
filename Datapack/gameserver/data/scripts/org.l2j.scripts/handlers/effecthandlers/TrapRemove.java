package handlers.effecthandlers;

import org.l2j.gameserver.enums.TrapAction;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Trap;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnTrapAction;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isPlayer;
import static org.l2j.gameserver.util.GameUtils.isTrap;

/**
 * Trap Remove effect implementation.
 * @author UnAfraid
 */
public final class TrapRemove extends AbstractEffect
{
	private final int _power;
	
	public TrapRemove(StatsSet params)
	{
		if (params.isEmpty())
		{
			throw new IllegalArgumentException(getClass().getSimpleName() + ": effect without power!");
		}
		
		_power = params.getInt("power");
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (!isTrap(effected))
		{
			return;
		}
		
		if (effected.isAlikeDead())
		{
			return;
		}
		
		final Trap trap = (Trap) effected;
		if (!trap.canBeSeen(effector))
		{
			if (isPlayer(effector))
			{
				effector.sendPacket(SystemMessageId.INVALID_TARGET);
			}
			return;
		}
		
		if (trap.getLevel() > _power)
		{
			return;
		}
		
		// Notify to scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnTrapAction(trap, effector, TrapAction.TRAP_DISARMED), trap);
		
		trap.unSummon();
		if (isPlayer(effector))
		{
			effector.sendPacket(SystemMessageId.THE_TRAP_DEVICE_HAS_BEEN_STOPPED);
		}
	}
}

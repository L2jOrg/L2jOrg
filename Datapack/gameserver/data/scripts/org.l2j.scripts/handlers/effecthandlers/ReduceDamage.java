package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import org.l2j.gameserver.model.events.listeners.FunctionEventListener;
import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.l2j.gameserver.model.events.returns.DamageReturn;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.function.Function;

/**
 * @author Sdw
 */
public class ReduceDamage extends AbstractEffect {
	public final double amount;
	
	public ReduceDamage(StatsSet params)
	{
		amount = params.getDouble("amount");
	}
	
	private DamageReturn onDamageReceivedEvent(OnCreatureDamageReceived event) {
		// DOT effects are not taken into account.
		if (event.isDamageOverTime()) {
			return null;
		}
		
		final double newDamage = event.getDamage() * (amount / 100);
		return new DamageReturn(false, true, false, newDamage);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.removeListenerIf(EventType.ON_CREATURE_DAMAGE_RECEIVED, listener -> listener.getOwner() == this);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		effected.addListener(new FunctionEventListener(effected, EventType.ON_CREATURE_DAMAGE_RECEIVED, (Function<OnCreatureDamageReceived, AbstractEventReturn>) this::onDamageReceivedEvent, this));
	}
}

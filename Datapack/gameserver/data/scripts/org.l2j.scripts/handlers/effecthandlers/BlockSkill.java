package handlers.effecthandlers;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillUse;
import org.l2j.gameserver.model.events.listeners.FunctionEventListener;
import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.function.Function;

/**
 * Block Skills by isMagic type.
 * @author Nik
 */
public final class BlockSkill extends AbstractEffect {

	public final int[] magicTypes;
	
	public BlockSkill(StatsSet params)
	{
		magicTypes = params.getIntArray("magic-types", " ");
	}
	
	private TerminateReturn onSkillUseEvent(OnCreatureSkillUse event) {
		if (Util.contains(magicTypes, event.getSkill().getSkillType().ordinal())) {
			return new TerminateReturn(true, true, true);
		}
		return null;
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		if ((magicTypes == null) || (magicTypes.length == 0)) {
			return;
		}
		
		effected.addListener(new FunctionEventListener(effected, EventType.ON_CREATURE_SKILL_USE, (Function<OnCreatureSkillUse, AbstractEventReturn>) this::onSkillUseEvent, this));
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.removeListenerIf(EventType.ON_CREATURE_SKILL_USE, listener -> listener.getOwner() == this);
	}
}

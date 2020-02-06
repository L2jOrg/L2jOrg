package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.skills.targets.TargetType;

import java.util.function.Consumer;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * Trigger Skill By Damage effect implementation.
 * @author UnAfraid
 */
public final class TriggerSkillByDamage extends AbstractEffect {
	private final int minAttackerLevel;
	private final int maxAttackerLevel;
	public final int minDamage;
	public final int chance;
	public final SkillHolder skill;
	public final TargetType targetType;
	public final InstanceType attackerType;
	
	public TriggerSkillByDamage(StatsSet params) {
		minAttackerLevel = params.getInt("minAttackerLevel", 1);
		maxAttackerLevel = params.getInt("maxAttackerLevel", 127);
		minDamage = params.getInt("minDamage", 1);
		chance = params.getInt("chance", 100);
		skill = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel", 1));
		targetType = params.getEnum("targetType", TargetType.class, TargetType.SELF);
		attackerType = params.getEnum("attackerType", InstanceType.class, InstanceType.Creature);
	}
	
	private void onDamageReceivedEvent(OnCreatureDamageReceived event) {
		if (event.isDamageOverTime() || (chance == 0) || (skill.getLevel() == 0)) {
			return;
		}
		
		if (event.getAttacker() == event.getTarget()) {
			return;
		}
		
		if ((event.getAttacker().getLevel() < minAttackerLevel) || (event.getAttacker().getLevel() > maxAttackerLevel)) {
			return;
		}
		
		if ((event.getDamage() < minDamage) || ((chance < 100) && (Rnd.get(100) > chance)) || !event.getAttacker().getInstanceType().isType(attackerType)) {
			return;
		}
		
		final Skill triggerSkill = skill.getSkill();
		WorldObject target = null;
		try {
			target = TargetHandler.getInstance().getHandler(targetType).getTarget(event.getTarget(), event.getAttacker(), triggerSkill, false, false, false);
		} catch (Exception e) {
			LOGGER.warn("Exception in ITargetTypeHandler.getTarget(): " + e.getMessage(), e);
		}
		
		if (isCreature(target)) {
			SkillCaster.triggerCast(event.getTarget(), (Creature) target, triggerSkill);
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.removeListenerIf(EventType.ON_CREATURE_DAMAGE_RECEIVED, listener -> listener.getOwner() == this);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_DAMAGE_RECEIVED, (Consumer<OnCreatureDamageReceived>) this::onDamageReceivedEvent, this));
	}
}

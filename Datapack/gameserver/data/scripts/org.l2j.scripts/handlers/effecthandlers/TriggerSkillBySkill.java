package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillFinishCast;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.skills.targets.TargetType;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * Trigger Skill By Skill effect implementation.
 * @author Zealar
 */
public final class TriggerSkillBySkill extends AbstractEffect {
	private final int castSkillId;
	private final int chance;
	private final SkillHolder skill;
	private final int skillLevelScaleTo;
	private final TargetType targetType;
	
	public TriggerSkillBySkill(StatsSet params) {
		castSkillId = params.getInt("castSkillId");
		chance = params.getInt("chance", 100);
		skill = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel"));
		skillLevelScaleTo = params.getInt("skillLevelScaleTo", 0);
		targetType = params.getEnum("targetType", TargetType.class, TargetType.TARGET);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_SKILL_FINISH_CAST, (Consumer<OnCreatureSkillFinishCast>) this::onSkillUseEvent, this));
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.removeListenerIf(EventType.ON_CREATURE_SKILL_FINISH_CAST, listener -> listener.getOwner() == this);
	}
	
	private void onSkillUseEvent(OnCreatureSkillFinishCast event) {
		if (chance == 0 || skill.getSkillId() == 0 || skill.getLevel() == 0 || castSkillId == 0) {
			return;
		}
		
		if (castSkillId != event.getSkill().getId()) {
			return;
		}
		
		if (!isCreature(event.getTarget())) {
			return;
		}
		
		if (chance < 100 && Rnd.chance(chance)) {
			return;
		}
		
		final Skill triggerSkill;
		if (skillLevelScaleTo <= 0) {
			triggerSkill = skill.getSkill();
		} else {
			final BuffInfo buffInfo = ((Creature) event.getTarget()).getEffectList().getBuffInfoBySkillId(skill.getSkillId());
			if (nonNull(buffInfo)) {
				triggerSkill = SkillData.getInstance().getSkill(skill.getSkillId(), Math.min(skillLevelScaleTo, buffInfo.getSkill().getLevel() + 1));
			} else {
				triggerSkill = skill.getSkill();
			}
		}
		
		WorldObject target = null;
		try {
			target = TargetHandler.getInstance().getHandler(targetType).getTarget(event.getCaster(), event.getTarget(), triggerSkill, false, false, false);
		} catch (Exception e) {
			LOGGER.warn("Exception in ITargetTypeHandler.getTarget(): " + e.getMessage(), e);
		}
		
		if (isCreature(target)) {
			SkillCaster.triggerCast(event.getCaster(), (Creature) target, triggerSkill);
		}
	}
}

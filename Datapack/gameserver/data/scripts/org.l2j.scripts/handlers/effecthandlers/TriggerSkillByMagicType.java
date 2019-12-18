package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
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

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * Trigger skill by isMagic type.
 * @author Nik
 */
public final class TriggerSkillByMagicType extends AbstractEffect {
	private final int[] magicTypes;
	private final int chance;
	private final int skillLevelScaleTo;
	private final SkillHolder skill;
	private final TargetType targetType;

	public TriggerSkillByMagicType(StatsSet params) {
		chance = params.getInt("chance", 100);
		magicTypes = params.getIntArray("magicTypes", ";");
		skill = new SkillHolder(params.getInt("skillId", 0), params.getInt("skillLevel", 0));
		skillLevelScaleTo = params.getInt("skillLevelScaleTo", 0);
		targetType = params.getEnum("targetType", TargetType.class, TargetType.TARGET);
	}
	
	private void onSkillUseEvent(OnCreatureSkillFinishCast event) {
		if (!isCreature(event.getTarget())) {
			return;
		}
		
		if (!Util.contains(magicTypes, event.getSkill().getMagicType())) {
			return;
		}
		
		if ((chance < 100) && (Rnd.get(100) > chance)) {
			return;
		}
		
		final Skill triggerSkill;
		if (skillLevelScaleTo <= 0) {
			triggerSkill = skill.getSkill();
		}
		else {
			final BuffInfo buffInfo = ((Creature) event.getTarget()).getEffectList().getBuffInfoBySkillId(skill.getSkillId());
			if (buffInfo != null)
			{
				triggerSkill = SkillData.getInstance().getSkill(skill.getSkillId(), Math.min(skillLevelScaleTo, buffInfo.getSkill().getLevel() + 1));
			}
			else
			{
				triggerSkill = skill.getSkill();
			}
		}
		
		WorldObject target = null;
		try
		{
			target = TargetHandler.getInstance().getHandler(targetType).getTarget(event.getCaster(), event.getTarget(), triggerSkill, false, false, false);
		}
		catch (Exception e)
		{
			LOGGER.warn("Exception in ITargetTypeHandler.getTarget(): " + e.getMessage(), e);
		}
		
		if (isCreature(target))
		{
			SkillCaster.triggerCast(event.getCaster(), (Creature) target, triggerSkill);
		}
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		if ((chance == 0) || (this.skill.getSkillId() == 0) || (this.skill.getLevel() == 0) || (magicTypes.length == 0))
		{
			return;
		}
		
		effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_SKILL_FINISH_CAST, (OnCreatureSkillFinishCast event) -> onSkillUseEvent(event), this));
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.removeListenerIf(EventType.ON_CREATURE_SKILL_FINISH_CAST, listener -> listener.getOwner() == this);
	}
}

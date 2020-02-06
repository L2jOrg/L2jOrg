package handlers.effecthandlers;

import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;

import static java.util.Objects.nonNull;

/**
 * Call Skill effect implementation.
 * @author NosBit
 */
public final class CallSkill extends AbstractEffect {

	public final SkillHolder skill;
	public final int skillLevelScaleTo;
	
	public CallSkill(StatsSet params) {
		skill = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel", 1), params.getInt("skillSubLevel", 0));
		skillLevelScaleTo = params.getInt("skillLevelScaleTo", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		Skill triggerSkill = null;
		if (skillLevelScaleTo <= 0) {
			// Mobius: Use 0 to trigger max effector learned skill level.
			if (this.skill.getLevel() == 0) {
				final int knownLevel = effector.getSkillLevel(this.skill.getSkillId());

				if (knownLevel > 0) {
					triggerSkill = SkillData.getInstance().getSkill(this.skill.getSkillId(), knownLevel, this.skill.getSkillSubLevel());
				} else {
					LOGGER.warn("Player {} called unknown skill {} triggered by {} CallSkill.", effector, this.skill, skill);
				}
			} else {
				triggerSkill = this.skill.getSkill();
			}
		} else {
			final BuffInfo buffInfo = effected.getEffectList().getBuffInfoBySkillId(this.skill.getSkillId());
			if (nonNull(buffInfo)) {
				triggerSkill = SkillData.getInstance().getSkill(this.skill.getSkillId(), Math.min(skillLevelScaleTo, buffInfo.getSkill().getLevel() + 1));
			} else {
				triggerSkill = this.skill.getSkill();
			}
		}
		
		if (nonNull(triggerSkill)) {
			SkillCaster.triggerCast(effector, effected, triggerSkill);
		} else {
			LOGGER.warn("Skill not found effect called from {}", skill);
		}
	}
}

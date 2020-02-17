package handlers.effecthandlers;

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.StreamUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.Arrays;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Block Actions effect implementation.
 * @author mkizub
 * @author JoeAlisson
 */
public final class BlockActions extends AbstractEffect {

	private final IntSet allowedSkills;
	
	private BlockActions(StatsSet params) {
		final String[] allowedSkills = params.getString("allowed-skills", "").split(" ");
		this.allowedSkills = StreamUtil.collectToSet(Arrays.stream(allowedSkills).filter(Util::isInteger).mapToInt(Integer::parseInt));
	}
	
	@Override
	public long getEffectFlags()
	{
		return allowedSkills.isEmpty() ? EffectFlag.BLOCK_ACTIONS.getMask() : EffectFlag.CONDITIONAL_BLOCK_ACTIONS.getMask();
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.BLOCK_ACTIONS;
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		allowedSkills.stream().forEach(effected::addBlockActionsAllowedSkill);
		effected.startParalyze();
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill) {
		allowedSkills.forEach(effected::removeBlockActionsAllowedSkill);
		if (isPlayable(effected)) {
			if (isSummon(effected)) {
				if (nonNull(effector) && !effector.isDead()) {
					((Summon) effected).doAttack(effector);
				} else {
					effected.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, effected.getActingPlayer());
				}
			} else {
				effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			}
		} else {
			effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new BlockActions(data);
		}

		@Override
		public String effectName() {
			return "block-all-actions";
		}
	}

}

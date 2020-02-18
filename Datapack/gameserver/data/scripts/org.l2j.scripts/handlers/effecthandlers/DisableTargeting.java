package handlers.effecthandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * Targeting disable effect implementation. When affected, player will lose target and be unable to target for the duration.
 * @author Nik
 * @author JoeAlisson
 */
public final class DisableTargeting extends AbstractEffect {
    private DisableTargeting() {
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        effected.setTarget(null);
        effected.abortAttack();
        effected.abortCast();
        effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }

    @Override
    public long getEffectFlags()
    {
        return EffectFlag.TARGETING_DISABLED.getMask();
    }

    public static class Factory implements SkillEffectFactory {
        private static final DisableTargeting INSTANCE = new DisableTargeting();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "DisableTargeting";
        }
    }
}

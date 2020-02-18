package handlers.effecthandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;

import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * Delete Hate effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class DeleteHate extends AbstractEffect {
    private final int power;

    private DeleteHate(StatsSet params)
    {
        power = params.getInt("power", 100);
    }

    @Override
    public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
    {
        return Formulas.calcProbability(power, effector, effected, skill);
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.HATE;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isAttackable(effected)) {
            return;
        }

        final Attackable target = (Attackable) effected;
        target.clearAggroList();
        target.setWalking();
        target.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new DeleteHate(data);
        }

        @Override
        public String effectName() {
            return "DeleteHate";
        }
    }
}

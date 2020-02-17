package handlers.effecthandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;
import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Betray effect implementation.
 * @author decad
 * @author JoeAlisson
 */
public final class Betray extends AbstractEffect {

    private Betray() {
    }

    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill)
    {
        return isPlayer(effector) && isSummon(effected);
    }

    @Override
    public long getEffectFlags()
    {
        return EffectFlag.BETRAYED.getMask();
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item)
    {
        effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, effected.getActingPlayer());
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }

    public static class Factory implements SkillEffectFactory {
        private static final Betray INSTANCE = new Betray();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "Betray";
        }
    }
}

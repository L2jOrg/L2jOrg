package handlers.effecthandlers;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Root effect implementation.
 * @author mkizub
 * @author JoeAlisson
 */
public final class Root extends AbstractEffect {
    private Root() {
    }

    @Override
    public long getEffectFlags()
    {
        return EffectFlag.ROOTED.getMask();
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.ROOT;
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        if (!isPlayer(effected)) {
            effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (isNull(effected) || effected.isRaid()) {
            return;
        }

        effected.stopMove(null);
        effected.getAI().notifyEvent(CtrlEvent.EVT_ROOTED);
    }

    public static class Factory implements SkillEffectFactory {

        private static final Root INSTANCE = new Root();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "Root";
        }
    }
}

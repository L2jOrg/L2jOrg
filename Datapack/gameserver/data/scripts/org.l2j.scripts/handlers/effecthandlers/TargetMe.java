package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * Target Me effect implementation.
 * @author -Nemesiss-
 * @author JoeAlisson
 */
public final class TargetMe extends AbstractEffect {
    private TargetMe() {
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        if (isPlayable(effected)) {
            ((Playable) effected).setLockedTarget(null);
        }
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (isPlayable(effected)) {
            if (effected.getTarget() != effector) {
                effected.setTarget(effector);
            }

            ((Playable) effected).setLockedTarget(effector);
        }
    }

    public static class Factory implements SkillEffectFactory {

        private static final TargetMe INSTANCE = new TargetMe();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "TargetMe";
        }
    }
}

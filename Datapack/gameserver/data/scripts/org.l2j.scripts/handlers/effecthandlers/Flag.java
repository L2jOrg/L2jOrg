package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Flag effect implementation.
 * @author BiggBoss
 * @author JoeAlisson
 */
public final class Flag extends AbstractEffect {
    private Flag() {
    }

    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill)
    {
        return isPlayer(effected);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item)
    {
        effected.updatePvPFlag(1);
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.getActingPlayer().updatePvPFlag(0);
    }

    public static class Factory implements SkillEffectFactory {
        private static final Flag INSTANCE = new Flag();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "Flag";
        }
    }
}

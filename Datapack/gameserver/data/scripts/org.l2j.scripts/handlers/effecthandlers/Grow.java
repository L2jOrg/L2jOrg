package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isNpc;

/**
 * Grow effect implementation.
 * @author JoeAlisson
 */
public final class Grow extends AbstractEffect {

    private Grow() {
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (isNpc(effected)) {
            final Npc npc = (Npc) effected;
            npc.setCollisionHeight(npc.getTemplate().getCollisionHeightGrown());
            npc.setCollisionRadius(npc.getTemplate().getCollisionRadiusGrown());
        }
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        if (isNpc(effected)) {
            final Npc npc = (Npc) effected;
            npc.setCollisionHeight(npc.getTemplate().getCollisionHeight());
            npc.setCollisionRadius(npc.getTemplate().getfCollisionRadius());
        }
    }

    public static class Factory implements SkillEffectFactory {
        private static final Grow INSTANCE = new Grow();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "Grow";
        }
    }
}

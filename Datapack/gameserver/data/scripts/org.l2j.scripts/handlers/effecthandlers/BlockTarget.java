package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.world.World;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class BlockTarget extends AbstractEffect {

    private BlockTarget() {
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        effected.setTargetable(false);
        World.getInstance().forEachVisibleObject(effected, Creature.class, target -> {
            if (target.getTarget() == effected) {
                target.setTarget(null);
            }
        });
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.setTargetable(true);
    }

    public static class Factory implements SkillEffectFactory {
        private static final BlockTarget INSTANCE = new BlockTarget();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "BlockTarget";
        }
    }
}

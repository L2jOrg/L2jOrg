package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.world.World;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * Randomize Hate effect implementation.
 * @author JoeAlisson
 */
public final class RandomizeHate extends AbstractEffect {
    private final double power;

    private RandomizeHate(StatsSet params) {
        power = params.getDouble("power", 100);
    }

    @Override
    public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
    {
        return Formulas.calcProbability(power, effector, effected, skill);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected == effector || !isAttackable(effected)) {
            return;
        }

        final Attackable effectedMob = (Attackable) effected;

        var target = World.getInstance().findAnyVisibleObject(effected, Creature.class, getSettings(CharacterSettings.class).partyRange() / 2, false,
                creature -> creature != effector && (!isAttackable(creature) || !((Attackable)creature).isInMyClan(effectedMob)));

        final int hate = effectedMob.getHating(effector);
        effectedMob.stopHating(effector);
        effectedMob.addDamageHate(target, 0, hate);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new RandomizeHate(data);
        }

        @Override
        public String effectName() {
            return "RandomizeHate";
        }
    }
}
package handlers.effecthandlers;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.world.World;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * Confuse effect implementation.
 * @author littlecrow
 * @author JoeAlisson
 */
public final class Confuse extends AbstractEffect {

    private final int chance;

    private Confuse(StatsSet params)
    {
        chance = params.getInt("power", 100);
    }

    @Override
    public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
    {
        return Formulas.calcProbability(chance, effector, effected, skill);
    }

    @Override
    public long getEffectFlags()
    {
        return EffectFlag.CONFUSED.getMask();
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        effected.getAI().notifyEvent(CtrlEvent.EVT_CONFUSED);

        var creature = World.getInstance().findAnyVisibleObject(effected, Creature.class, getSettings(CharacterSettings.class).partyRange(), false,
                c -> GeoEngine.getInstance().canSeeTarget(effected, c));
        if(nonNull(creature)) {
            effected.setTarget(creature);
            effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, creature);
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new Confuse(data);
        }

        @Override
        public String effectName() {
            return "Confuse";
        }
    }
}

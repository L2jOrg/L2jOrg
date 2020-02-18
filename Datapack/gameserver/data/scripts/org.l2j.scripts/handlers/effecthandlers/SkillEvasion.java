package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.engine.skill.api.SkillType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * Note: In retail this effect doesn't stack. It appears that the active value is taken from the last such effect.
 * @author Sdw
 * @author JoeAlisson
 */
public class SkillEvasion extends AbstractEffect {
    private final SkillType magicType;
    private final double power;

    private SkillEvasion(StatsSet params){
        magicType = params.getEnum("type", SkillType.class);
        power = params.getDouble("power", 0);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item)
    {
        effected.getStats().addSkillEvasionTypeValue(magicType, power);
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.getStats().removeSkillEvasionTypeValue(magicType, power);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new SkillEvasion(data);
        }

        @Override
        public String effectName() {
            return "skill-evasion";
        }
    }
}

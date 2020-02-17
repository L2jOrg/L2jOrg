package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.util.MathUtil;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class Reuse extends AbstractEffect {
    public final SkillType magicType;
    public final double power;

    public Reuse(StatsSet params) {
        magicType = params.getEnum("skill-type", SkillType.class);
        power = params.getDouble("power", 0);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item)
    {
        effected.getStats().mergeReuseTypeValue(magicType, (power / 100) + 1, MathUtil::mul);
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.getStats().mergeReuseTypeValue(magicType, (power / 100) + 1, MathUtil::div);
    }
}

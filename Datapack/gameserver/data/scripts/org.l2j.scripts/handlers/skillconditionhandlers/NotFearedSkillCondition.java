package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

/**
 * @author Mobius
 */
public class NotFearedSkillCondition implements ISkillCondition
{
    public NotFearedSkillCondition(StatsSet params)
    {
    }

    @Override
    public boolean canUse(L2Character caster, Skill skill, L2Object target)
    {
        return (target != null) && target.isCharacter() && !((L2Character) target).isAffected(EffectFlag.FEAR);
    }
}

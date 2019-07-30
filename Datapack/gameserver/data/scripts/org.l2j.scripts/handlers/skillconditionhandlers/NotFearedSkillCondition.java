package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author Mobius
 */
public class NotFearedSkillCondition implements ISkillCondition
{
    public NotFearedSkillCondition(StatsSet params)
    {
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target)
    {
        return isCreature(target) && !((Creature) target).isAffected(EffectFlag.FEAR);
    }
}

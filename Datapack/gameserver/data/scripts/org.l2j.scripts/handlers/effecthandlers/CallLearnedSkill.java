package handlers.effecthandlers;


import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;

/**
 * Call Learned Skill by Level effect implementation.
 * @author Kazumi
 */
public final class CallLearnedSkill extends AbstractEffect
{
    private final int _skillId;

    public CallLearnedSkill(StatsSet params)
    {
        _skillId = params.getInt("skillId");
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, L2ItemInstance item)
    {
        final Skill knownSkill = effector.getKnownSkill(_skillId);
        if (knownSkill != null)
        {
            SkillCaster.triggerCast(effector, effected, knownSkill);
        }
    }
}
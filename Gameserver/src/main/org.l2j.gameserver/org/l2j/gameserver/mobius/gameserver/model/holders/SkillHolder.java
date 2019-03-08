package org.l2j.gameserver.mobius.gameserver.model.holders;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;

/**
 * Simple class for storing skill id/level.
 * @author BiggBoss
 */
public class SkillHolder
{
    private final int _skillId;
    private final int _skillLevel;
    private final int _skillSubLevel;

    public SkillHolder(int skillId, int skillLevel)
    {
        _skillId = skillId;
        _skillLevel = skillLevel;
        _skillSubLevel = 0;
    }

    public SkillHolder(int skillId, int skillLevel, int skillSubLevel)
    {
        _skillId = skillId;
        _skillLevel = skillLevel;
        _skillSubLevel = skillSubLevel;
    }

    public SkillHolder(Skill skill)
    {
        _skillId = skill.getId();
        _skillLevel = skill.getLevel();
        _skillSubLevel = skill.getSubLevel();
    }

    public final int getSkillId()
    {
        return _skillId;
    }

    public final int getSkillLevel()
    {
        return _skillLevel;
    }

    public final int getSkillSubLevel()
    {
        return _skillSubLevel;
    }

    public final Skill getSkill()
    {
        return SkillData.getInstance().getSkill(_skillId, Math.max(_skillLevel, 1), _skillSubLevel);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof SkillHolder))
        {
            return false;
        }

        final SkillHolder holder = (SkillHolder) obj;
        return (holder.getSkillId() == _skillId) && (holder.getSkillLevel() == _skillLevel) && (holder.getSkillSubLevel() == _skillSubLevel);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + _skillId;
        result = (prime * result) + _skillLevel;
        result = (prime * result) + _skillSubLevel;
        return result;
    }

    @Override
    public String toString()
    {
        return "[SkillId: " + _skillId + " Level: " + _skillLevel + "]";
    }
}
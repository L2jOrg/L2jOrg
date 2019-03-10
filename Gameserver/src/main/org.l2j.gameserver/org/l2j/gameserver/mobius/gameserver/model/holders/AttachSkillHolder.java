package org.l2j.gameserver.mobius.gameserver.model.holders;

import org.l2j.gameserver.mobius.gameserver.model.StatsSet;

/**
 * @author Nik
 */
public class AttachSkillHolder extends SkillHolder {
    private final int _requiredSkillId;
    private final int _requiredSkillLevel;

    public AttachSkillHolder(int skillId, int skillLevel, int requiredSkillId, int requiredSkillLevel) {
        super(skillId, skillLevel);
        _requiredSkillId = requiredSkillId;
        _requiredSkillLevel = requiredSkillLevel;
    }

    public static AttachSkillHolder fromStatsSet(StatsSet set) {
        return new AttachSkillHolder(set.getInt("skillId"), set.getInt("skillLevel", 1), set.getInt("requiredSkillId"), set.getInt("requiredSkillLevel", 1));
    }

    public int getRequiredSkillId() {
        return _requiredSkillId;
    }

    public int getRequiredSkillLevel() {
        return _requiredSkillLevel;
    }
}

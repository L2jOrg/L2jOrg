package org.l2j.gameserver.model.options;

import org.l2j.gameserver.model.holders.SkillHolder;

/**
 * @author UnAfraid
 */
public class OptionsSkillHolder extends SkillHolder {
    private final OptionsSkillType _type;
    private final double _chance;

    /**
     * @param skillId
     * @param skillLvl
     * @param type
     * @param chance
     */
    public OptionsSkillHolder(int skillId, int skillLvl, double chance, OptionsSkillType type) {
        super(skillId, skillLvl);
        _chance = chance;
        _type = type;
    }

    public OptionsSkillType getSkillType() {
        return _type;
    }

    public double getChance() {
        return _chance;
    }
}

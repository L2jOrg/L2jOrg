package org.l2j.gameserver.model.options;

import org.l2j.gameserver.model.holders.SkillHolder;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class OptionsSkillHolder extends SkillHolder {
    private final OptionsSkillType type;
    private final double chance;

    public OptionsSkillHolder(SkillHolder skill, double chance, OptionsSkillType type) {
        super(skill.getSkillId(), skill.getLevel());
        this.chance = chance;
        this.type = type;
    }

    public OptionsSkillType getSkillType() {
        return type;
    }

    public double getChance() {
        return chance;
    }
}

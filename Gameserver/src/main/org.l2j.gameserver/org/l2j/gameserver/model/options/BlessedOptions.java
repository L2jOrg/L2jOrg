package org.l2j.gameserver.model.options;

import org.l2j.gameserver.model.holders.SkillHolder;

public class BlessedOptions {
    private final int _enchant;
    private final SkillHolder _skill;

    public BlessedOptions(int enchant, SkillHolder skill) {
        _enchant = enchant;
        _skill = skill;
    }

    public int getEnchant() {
        return _enchant;
    }

    public SkillHolder getSkill() {
        return _skill;
    }
}

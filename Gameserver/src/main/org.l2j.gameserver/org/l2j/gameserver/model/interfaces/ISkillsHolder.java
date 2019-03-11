package org.l2j.gameserver.model.interfaces;

import org.l2j.gameserver.model.skills.Skill;

import java.util.Map;

/**
 * @author UnAfraid
 */
public interface ISkillsHolder {
    Map<Integer, Skill> getSkills();

    Skill addSkill(Skill skill);

    Skill getKnownSkill(int skillId);

    int getSkillLevel(int skillId);
}

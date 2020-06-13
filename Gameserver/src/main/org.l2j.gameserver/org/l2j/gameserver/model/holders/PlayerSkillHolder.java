/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ISkillsHolder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author UnAfraid
 */
public class PlayerSkillHolder implements ISkillsHolder {
    private final Map<Integer, Skill> _skills = new HashMap<>();

    public PlayerSkillHolder(Player player) {
        for (Skill skill : player.getSkills().values()) {
            // Adding only skills that can be learned by the player.
            if (SkillTreesData.getInstance().isSkillAllowed(player, skill)) {
                addSkill(skill);
            }
        }
    }

    /**
     * @return the map containing this character skills.
     */
    @Override
    public Map<Integer, Skill> getSkills() {
        return _skills;
    }

    /**
     * Add a skill to the skills map.<br>
     *
     * @param skill
     */
    @Override
    public Skill addSkill(Skill skill) {
        return _skills.put(skill.getId(), skill);
    }

    /**
     * Return the level of a skill owned by the Creature.
     *
     * @param skillId The identifier of the L2Skill whose level must be returned
     * @return The level of the L2Skill identified by skillId
     */
    @Override
    public int getSkillLevel(int skillId) {
        final Skill skill = getKnownSkill(skillId);
        return (skill == null) ? 0 : skill.getLevel();
    }

    /**
     * @param skillId The identifier of the L2Skill to check the knowledge
     * @return the skill from the known skill.
     */
    @Override
    public Skill getKnownSkill(int skillId) {
        return _skills.get(skillId);
    }

    public Skill removeSkill(Skill skill) {
        return _skills.remove(skill.getId());
    }
}

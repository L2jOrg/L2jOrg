/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.model;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.base.SocialStatus;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.SkillHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Zoey76
 */
public final class SkillLearn {
    private final int _skillId;
    private final int _skillLvl;
    private final int _getLevel;
    private final int _getDualClassLevel;
    private final boolean _autoGet;
    private final long _levelUpSp;
    private final List<ItemHolder> _requiredItems = new ArrayList<>();
    private final List<Race> _races = new ArrayList<>();
    private final List<SkillHolder> _preReqSkills = new ArrayList<>();
    private final boolean _residenceSkill;
    private final List<Integer> _residenceIds = new ArrayList<>();
    private final boolean _learnedByNpc;
    private final boolean _learnedByFS;
    private final Set<Integer> _removeSkills = new HashSet<>(1);
    private final int _treeId;
    private final int _row;
    private final int _column;
    private final int _pointsRequired;
    private SocialStatus socialStatus;

    /**
     * Constructor for SkillLearn.
     *
     * @param set the set with the SkillLearn data.
     */
    public SkillLearn(StatsSet set) {
        _skillId = set.getInt("skillId");
        _skillLvl = set.getInt("skillLvl");
        _getLevel = set.getInt("getLevel");
        _getDualClassLevel = set.getInt("getDualClassLevel", 0);
        _autoGet = set.getBoolean("autoGet", false);
        _levelUpSp = set.getLong("levelUpSp", 0);
        _residenceSkill = set.getBoolean("residenceSkill", false);
        _learnedByNpc = set.getBoolean("learnedByNpc", false);
        _learnedByFS = set.getBoolean("learnedByFS", false);
        _treeId = set.getInt("treeId", 0);
        _row = set.getInt("row", 0);
        _column = set.getInt("row", 0);
        _pointsRequired = set.getInt("pointsRequired", 0);
    }

    /**
     * @return the ID of this skill.
     */
    public int getSkillId() {
        return _skillId;
    }

    /**
     * @return the level of this skill.
     */
    public int getSkillLevel() {
        return _skillLvl;
    }

    /**
     * @return the minimum level required to acquire this skill.
     */
    public int getGetLevel() {
        return _getLevel;
    }

    /**
     * @return the amount of SP/Clan Reputation to acquire this skill.
     */
    public long getLevelUpSp() {
        return _levelUpSp;
    }

    /**
     * @return {@code true} if the skill is auto-get, this skill is automatically delivered.
     */
    public boolean isAutoGet() {
        return _autoGet;
    }

    /**
     * @return the list with the item holders required to acquire this skill.
     */
    public List<ItemHolder> getRequiredItems() {
        return _requiredItems;
    }

    /**
     * Adds a required item holder to learn this skill.
     *
     * @param item the required item holder.
     */
    public void addRequiredItem(ItemHolder item) {
        _requiredItems.add(item);
    }

    /**
     * @return a list with the races that can acquire this skill.
     */
    public List<Race> getRaces() {
        return _races;
    }

    /**
     * Adds a required race to learn this skill.
     *
     * @param race the required race.
     */
    public void addRace(Race race) {
        _races.add(race);
    }

    /**
     * @return the list of skill holders required to acquire this skill.
     */
    public List<SkillHolder> getPreReqSkills() {
        return _preReqSkills;
    }

    /**
     * Adds a required skill holder to learn this skill.
     *
     * @param skill the required skill holder.
     */
    public void addPreReqSkill(SkillHolder skill) {
        _preReqSkills.add(skill);
    }

    /**
     * @return the social class required to get this skill.
     */
    public SocialStatus getSocialClass() {
        return socialStatus;
    }

    /**
     * Sets the social class if hasn't been set before.
     *
     * @param socialStatus the social class to set.
     */
    public void setSocialStatus(SocialStatus socialStatus) {
        if (this.socialStatus == null) {
            this.socialStatus = socialStatus;
        }
    }

    /**
     * @return {@code true} if this skill is a Residence skill.
     */
    public boolean isResidencialSkill() {
        return _residenceSkill;
    }

    /**
     * @return a list with the Ids where this skill is available.
     */
    public List<Integer> getResidenceIds() {
        return _residenceIds;
    }

    /**
     * Adds a required residence Id.
     *
     * @param id the residence Id to add.
     */
    public void addResidenceId(Integer id) {
        _residenceIds.add(id);
    }

    /**
     * @return {@code true} if this skill is learned from Npc.
     */
    public boolean isLearnedByNpc() {
        return _learnedByNpc;
    }

    /**
     * @return {@code true} if this skill is learned by Forgotten Scroll.
     */
    public boolean isLearnedByFS() {
        return _learnedByFS;
    }

    public void addRemoveSkills(int skillId) {
        _removeSkills.add(skillId);
    }

    public Set<Integer> getRemoveSkills() {
        return _removeSkills;
    }

    public Skill getSkill() {
        return SkillEngine.getInstance().getSkill(_skillId, _skillLvl);
    }

    @Override
    public String toString() {
        return "[" + getSkill() + " treeId: " + _treeId + " row: " + _row + " column: " + _column + " pointsRequired:" + _pointsRequired + "]";
    }
}

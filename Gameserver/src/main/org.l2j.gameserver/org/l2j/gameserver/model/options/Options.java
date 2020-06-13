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
package org.l2j.gameserver.model.options;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;

import java.util.ArrayList;
import java.util.List;

/**
 * @author UnAfraid
 */
public class Options {
    private final int _id;
    private List<AbstractEffect> _effects = null;
    private List<SkillHolder> _activeSkill = null;
    private List<SkillHolder> _passiveSkill = null;
    private List<OptionsSkillHolder> _activationSkills = null;

    /**
     * @param id
     */
    public Options(int id) {
        _id = id;
    }

    public final int getId() {
        return _id;
    }

    public void addEffect(AbstractEffect effect) {
        if (_effects == null) {
            _effects = new ArrayList<>();
        }
        _effects.add(effect);
    }

    public List<AbstractEffect> getEffects() {
        return _effects;
    }

    public boolean hasEffects() {
        return _effects != null;
    }

    public boolean hasActiveSkills() {
        return _activeSkill != null;
    }

    public List<SkillHolder> getActiveSkills() {
        return _activeSkill;
    }

    public void addActiveSkill(SkillHolder holder) {
        if (_activeSkill == null) {
            _activeSkill = new ArrayList<>();
        }
        _activeSkill.add(holder);
    }

    public boolean hasPassiveSkills() {
        return _passiveSkill != null;
    }

    public List<SkillHolder> getPassiveSkills() {
        return _passiveSkill;
    }

    public void addPassiveSkill(SkillHolder holder) {
        if (_passiveSkill == null) {
            _passiveSkill = new ArrayList<>();
        }
        _passiveSkill.add(holder);
    }

    public boolean hasActivationSkills() {
        return _activationSkills != null;
    }

    public boolean hasActivationSkills(OptionsSkillType type) {
        if (_activationSkills != null) {
            for (OptionsSkillHolder holder : _activationSkills) {
                if (holder.getSkillType() == type) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<OptionsSkillHolder> getActivationsSkills() {
        return _activationSkills;
    }

    public List<OptionsSkillHolder> getActivationsSkills(OptionsSkillType type) {
        List<OptionsSkillHolder> temp = new ArrayList<>();
        if (_activationSkills != null) {
            for (OptionsSkillHolder holder : _activationSkills) {
                if (holder.getSkillType() == type) {
                    temp.add(holder);
                }
            }
        }
        return temp;
    }

    public void addActivationSkill(OptionsSkillHolder holder) {
        if (_activationSkills == null) {
            _activationSkills = new ArrayList<>();
        }
        _activationSkills.add(holder);
    }

    public void apply(Player player) {
        if (hasEffects()) {
            final BuffInfo info = new BuffInfo(player, player, null, true, null, this);
            for (AbstractEffect effect : _effects) {
                if (effect.isInstant()) {
                    if (effect.calcSuccess(info.getEffector(), info.getEffected(), info.getSkill())) {
                        effect.instant(info.getEffector(), info.getEffected(), info.getSkill(), info.getItem());
                    }
                } else {
                    effect.continuousInstant(info.getEffector(), info.getEffected(), info.getSkill(), info.getItem());
                    effect.pump(player, info.getSkill());

                    if (effect.canStart(info.getEffector(), info.getEffected(), info.getSkill())) {
                        info.addEffect(effect);
                    }
                }
            }
            if (!info.getEffects().isEmpty()) {
                player.getEffectList().add(info);
            }
        }
        if (hasActiveSkills()) {
            for (SkillHolder holder : _activeSkill) {
                addSkill(player, holder.getSkill());
            }
        }
        if (hasPassiveSkills()) {
            for (SkillHolder holder : _passiveSkill) {
                addSkill(player, holder.getSkill());
            }
        }
        if (hasActivationSkills()) {
            for (OptionsSkillHolder holder : _activationSkills) {
                player.addTriggerSkill(holder);
            }
        }

        player.getStats().recalculateStats(true);
        player.sendSkillList();
    }

    public void remove(Player player) {
        if (hasEffects()) {
            for (BuffInfo info : player.getEffectList().getOptions()) {
                if (info.getOption() == this) {
                    player.getEffectList().remove(info, false, true, true);
                }
            }
        }
        if (hasActiveSkills()) {
            for (SkillHolder holder : _activeSkill) {
                player.removeSkill(holder.getSkill(), false, false);
            }
        }
        if (hasPassiveSkills()) {
            for (SkillHolder holder : _passiveSkill) {
                player.removeSkill(holder.getSkill(), false, true);
            }
        }
        if (hasActivationSkills()) {
            for (OptionsSkillHolder holder : _activationSkills) {
                player.removeTriggerSkill(holder);
            }
        }

        player.getStats().recalculateStats(true);
        player.sendSkillList();
    }

    private void addSkill(Player player, Skill skill) {
        boolean updateTimeStamp = false;

        player.addSkill(skill, false);

        if (skill.isActive()) {
            final long remainingTime = player.getSkillRemainingReuseTime(skill.getReuseHashCode());
            if (remainingTime > 0) {
                player.addTimeStamp(skill, remainingTime);
                player.disableSkill(skill, remainingTime);
            }
            updateTimeStamp = true;
        }
        if (updateTimeStamp) {
            player.sendPacket(new SkillCoolTime(player));
        }
    }
}

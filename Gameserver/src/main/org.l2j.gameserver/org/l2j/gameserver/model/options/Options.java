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
package org.l2j.gameserver.model.options;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;

import java.util.ArrayList;
import java.util.List;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class Options {
    private final int _id;
    private List<AbstractEffect> _effects = null;
    private List<Skill> activeSkills = null;
    private List<Skill> passiveSkills = null;
    private List<OptionsSkillInfo> activationSkills = null;

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

    public boolean hasEffects() {
        return _effects != null;
    }

    public boolean hasActiveSkills() {
        return activeSkills != null;
    }

    public void addActiveSkill(Skill holder) {
        if (activeSkills == null) {
            activeSkills = new ArrayList<>();
        }
        activeSkills.add(holder);
    }

    public boolean hasPassiveSkills() {
        return passiveSkills != null;
    }

    public void addPassiveSkill(Skill holder) {
        if (passiveSkills == null) {
            passiveSkills = new ArrayList<>();
        }
        passiveSkills.add(holder);
    }

    public boolean hasActivationSkills() {
        return activationSkills != null;
    }

    public void addActivationSkill(OptionsSkillInfo holder) {
        if (activationSkills == null) {
            activationSkills = new ArrayList<>();
        }
        activationSkills.add(holder);
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
            for (var skill : activeSkills) {
                addSkill(player, skill);
            }
        }
        if (hasPassiveSkills()) {
            for (var skill : passiveSkills) {
                addSkill(player, skill);
            }
        }
        if (hasActivationSkills()) {
            for (var holder : activationSkills) {
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
            for (var skill : activeSkills) {
                player.removeSkill(skill, false, false);
            }
        }
        if (hasPassiveSkills()) {
            for (var skill : passiveSkills) {
                player.removeSkill(skill, false, true);
            }
        }
        if (hasActivationSkills()) {
            for (OptionsSkillInfo holder : activationSkills) {
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

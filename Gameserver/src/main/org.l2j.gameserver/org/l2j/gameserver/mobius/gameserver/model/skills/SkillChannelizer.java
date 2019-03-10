/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.skills;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.mobius.gameserver.enums.ShotType;
import org.l2j.gameserver.mobius.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.MagicSkillLaunched;
import org.l2j.gameserver.mobius.gameserver.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Skill Channelizer implementation.
 *
 * @author UnAfraid
 */
public class SkillChannelizer implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(SkillChannelizer.class.getName());

    private final L2Character _channelizer;
    private List<L2Character> _channelized;

    private Skill _skill;
    private volatile ScheduledFuture<?> _task = null;

    public SkillChannelizer(L2Character channelizer) {
        _channelizer = channelizer;
    }

    public L2Character getChannelizer() {
        return _channelizer;
    }

    public List<L2Character> getChannelized() {
        return _channelized;
    }

    public boolean hasChannelized() {
        return _channelized != null;
    }

    public void startChanneling(Skill skill) {
        // Verify for same status.
        if (isChanneling()) {
            LOGGER.warning("Character: " + toString() + " is attempting to channel skill but he already does!");
            return;
        }

        // Start channeling.
        _skill = skill;
        _task = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, skill.getChannelingTickInitialDelay(), skill.getChannelingTickInterval());
    }

    public void stopChanneling() {
        // Verify for same status.
        if (!isChanneling()) {
            LOGGER.warning("Character: " + toString() + " is attempting to stop channel skill but he does not!");
            return;
        }

        // Cancel the task and unset it.
        _task.cancel(false);
        _task = null;

        // Cancel target channelization and unset it.
        if (_channelized != null) {
            for (L2Character chars : _channelized) {
                chars.getSkillChannelized().removeChannelizer(_skill.getChannelingSkillId(), _channelizer);
            }
            _channelized = null;
        }

        // unset skill.
        _skill = null;
    }

    public Skill getSkill() {
        return _skill;
    }

    public boolean isChanneling() {
        return _task != null;
    }

    @Override
    public void run() {
        if (!isChanneling()) {
            return;
        }

        final Skill skill = _skill;
        List<L2Character> channelized = _channelized;

        try {
            if (skill.getMpPerChanneling() > 0) {
                // Validate mana per tick.
                if (_channelizer.getCurrentMp() < skill.getMpPerChanneling()) {
                    if (_channelizer.isPlayer()) {
                        _channelizer.sendPacket(SystemMessageId.YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP);
                    }
                    _channelizer.abortCast();
                    return;
                }

                // Reduce mana per tick
                _channelizer.reduceCurrentMp(skill.getMpPerChanneling());
            }

            // Apply channeling skills on the targets.
            final List<L2Character> targetList = new ArrayList<>();
            final L2Object target = skill.getTarget(_channelizer, false, false, false);
            if (target != null) {
                skill.forEachTargetAffected(_channelizer, target, o ->
                {
                    if (o.isCharacter()) {
                        targetList.add((L2Character) o);
                        ((L2Character) o).getSkillChannelized().addChannelizer(skill.getChannelingSkillId(), _channelizer);
                    }
                });
            }

            if (targetList.isEmpty()) {
                return;
            }
            channelized = targetList;

            for (L2Character character : channelized) {
                if (!Util.checkIfInRange(skill.getEffectRange(), _channelizer, character, true)) {
                    continue;
                } else if (!GeoEngine.getInstance().canSeeTarget(_channelizer, character)) {
                    continue;
                }

                if (skill.getChannelingSkillId() > 0) {
                    final int maxSkillLevel = SkillData.getInstance().getMaxLevel(skill.getChannelingSkillId());
                    final int skillLevel = Math.min(character.getSkillChannelized().getChannerlizersSize(skill.getChannelingSkillId()), maxSkillLevel);
                    final BuffInfo info = character.getEffectList().getBuffInfoBySkillId(skill.getChannelingSkillId());

                    if ((info == null) || (info.getSkill().getLevel() < skillLevel)) {
                        final Skill channeledSkill = SkillData.getInstance().getSkill(skill.getChannelingSkillId(), skillLevel);
                        if (channeledSkill == null) {
                            LOGGER.warning(getClass().getSimpleName() + ": Non existent channeling skill requested: " + skill);
                            _channelizer.abortCast();
                            return;
                        }

                        // Update PvP status
                        if (character.isPlayable() && _channelizer.isPlayer()) {
                            ((L2PcInstance) _channelizer).updatePvPStatus(character);
                        }

                        // Be warned, this method has the possibility to call doDie->abortCast->stopChanneling method. Variable cache above try{} is used in this case to avoid NPEs.
                        channeledSkill.applyEffects(_channelizer, character);
                    }
                    if (!skill.isToggle()) {
                        _channelizer.broadcastPacket(new MagicSkillLaunched(_channelizer, skill.getId(), skill.getLevel(), SkillCastingType.NORMAL, character));
                    }
                } else {
                    skill.applyChannelingEffects(_channelizer, character);
                }

                // Reduce shots.
                if (skill.useSpiritShot()) {
                    _channelizer.unchargeShot(_channelizer.isChargedShot(ShotType.BLESSED_SPIRITSHOTS) ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS);
                } else {
                    _channelizer.unchargeShot(_channelizer.isChargedShot(ShotType.BLESSED_SOULSHOTS) ? ShotType.BLESSED_SOULSHOTS : ShotType.SOULSHOTS);
                }

                // Shots are re-charged every cast.
                _channelizer.rechargeShots(skill.useSoulShot(), skill.useSpiritShot(), false);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error while channelizing skill: " + skill + " channelizer: " + _channelizer + " channelized: " + channelized, e);
        }
    }
}

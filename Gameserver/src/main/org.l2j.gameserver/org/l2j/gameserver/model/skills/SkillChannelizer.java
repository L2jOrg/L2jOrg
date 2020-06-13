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
package org.l2j.gameserver.model.skills;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.MagicSkillLaunched;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.GameUtils.isPlayer;


/**
 * Skill Channelizer implementation.
 *
 * @author UnAfraid
 */
public class SkillChannelizer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkillChannelizer.class);

    private final Creature _channelizer;
    private List<Creature> _channelized;

    private Skill _skill;
    private volatile ScheduledFuture<?> _task = null;

    public SkillChannelizer(Creature channelizer) {
        _channelizer = channelizer;
    }

    public boolean hasChannelized() {
        return _channelized != null;
    }

    public void startChanneling(Skill skill) {
        // Verify for same status.
        if (isChanneling()) {
            LOGGER.warn("Character: {} is attempting to channel skill but he already does!", _channelizer);
            return;
        }

        // Start channeling.
        _skill = skill;
        _task = ThreadPool.scheduleAtFixedRate(this, skill.getChannelingTickInitialDelay(), skill.getChannelingTickInterval());
    }

    public void stopChanneling() {
        // Verify for same status.
        if (!isChanneling()) {
            LOGGER.warn("Character: " + toString() + " is attempting to stop channel skill but he does not!");
            return;
        }

        // Cancel the task and unset it.
        _task.cancel(false);
        _task = null;

        // Cancel target channelization and unset it.
        if (_channelized != null) {
            for (Creature chars : _channelized) {
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
        List<Creature> channelized = _channelized;

        try {
            if (skill.getMpPerChanneling() > 0) {
                // Validate mana per tick.
                if (_channelizer.getCurrentMp() < skill.getMpPerChanneling()) {
                    if (isPlayer(_channelizer)) {
                        _channelizer.sendPacket(SystemMessageId.YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP);
                    }
                    _channelizer.abortCast();
                    return;
                }

                // Reduce mana per tick
                _channelizer.reduceCurrentMp(skill.getMpPerChanneling());
            }

            // Apply channeling skills on the targets.
            final List<Creature> targetList = new ArrayList<>();
            final WorldObject target = skill.getTarget(_channelizer, false, false, false);
            if (target != null) {
                skill.forEachTargetAffected(_channelizer, target, o ->
                {
                    if (isCreature(o)) {
                        targetList.add((Creature) o);
                        ((Creature) o).getSkillChannelized().addChannelizer(skill.getChannelingSkillId(), _channelizer);
                    }
                });
            }

            if (targetList.isEmpty()) {
                return;
            }
            channelized = targetList;

            for (Creature character : channelized) {
                if (!GameUtils.checkIfInRange(skill.getEffectRange(), _channelizer, character, true)) {
                    continue;
                } else if (!GeoEngine.getInstance().canSeeTarget(_channelizer, character)) {
                    continue;
                }

                if (skill.getChannelingSkillId() > 0) {
                    final int maxSkillLevel = SkillEngine.getInstance().getMaxLevel(skill.getChannelingSkillId());
                    final int skillLevel = Math.min(character.getSkillChannelized().getChannerlizersSize(skill.getChannelingSkillId()), maxSkillLevel);
                    if (skillLevel == 0) {
                        continue;
                    }
                    final BuffInfo info = character.getEffectList().getBuffInfoBySkillId(skill.getChannelingSkillId());

                    if ((info == null) || (info.getSkill().getLevel() < skillLevel)) {
                        final Skill channeledSkill = SkillEngine.getInstance().getSkill(skill.getChannelingSkillId(), skillLevel);
                        if (channeledSkill == null) {
                            LOGGER.warn(": Non existent channeling skill requested: " + skill);
                            _channelizer.abortCast();
                            return;
                        }

                        // Update PvP status
                        if (isPlayer(_channelizer)) {
                            ((Player) _channelizer).updatePvPStatus(character);
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
            }

            if(skill.useSoulShot()) {
                _channelizer.consumeAndRechargeShots(ShotType.SOULSHOTS, targetList.size());
            }
            if(skill.useSpiritShot()) {
                _channelizer.consumeAndRechargeShots(ShotType.SPIRITSHOTS, targetList.size());
            }
        } catch (Exception e) {
            LOGGER.warn("Error while channelizing skill: {} channelizer: {} channelized: {}", skill, _channelizer, channelized, e);
        }
    }
}

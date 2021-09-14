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
package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.holders.ItemSkillInfo;
import org.l2j.gameserver.model.item.type.ActionType;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.gameserver.util.GameUtils.isPet;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Template for item skills handler.
 * @author Zoey76
 * @author JoeAlisson
 */
public class ItemSkillsTemplate implements IItemHandler {

    @Override
    public boolean useItem(Playable playable, Item item, boolean forceUse) {
        if ((!isPlayer(playable) && !isPet(playable)) || !isSkillReducer(item)) {
            return false;
        }

        // Pets can use items only when they are tradeable.
        if (isPet(playable) && !item.isTradeable()) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }

        if((playable.isAttackingNow() || playable.isCastingNow()) && !isAutoConsumeItem(item, forceUse)) {
            return false;
        }

        // Verify that item is not under reuse.
        if (!isAvailableToUse(playable, null, item)) {
            return false;
        }

        final List<ItemSkillInfo> skills = item.getSkills(ItemSkillType.NORMAL);
        if(isNullOrEmpty(skills)) {
            LOGGER.info("Item {} does not have registered any skill for handler.", item);
            return false;
        }

        if(!item.isInfinite() && item.getAction() != ActionType.SKILL_REDUCE_ON_SKILL_SUCCESS) {
            if (!playable.destroyItem("Consume", item.getObjectId(), 1, playable, false) && !isAutoConsumeItem(item, forceUse)) {
                playable.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
                return false;
            }
        }

        boolean successfulUse = false;

        for (var skillInfo : skills) {

            if (isNull(skillInfo)) {
                continue;
            }

            var itemSkill = skillInfo.skill();

            if (nonNull(itemSkill)) {

                var player  = playable.getActingPlayer();

                if (itemSkill.hasAnyEffectType(EffectType.EXTRACT_ITEM) && nonNull(player) && !player.isInventoryUnder80()) {
                    player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                    return false;
                }

                if (!checkUseSkill(playable, item, itemSkill)) {
                    continue;
                }

                // Send message to the master.
                if (isPet(playable)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_USES_S1);
                    sm.addSkillName(itemSkill);
                    playable.sendPacket(sm);
                }

                if (isPlayer(playable) && itemSkill.hasAnyEffectType(EffectType.SUMMON_PET)) {
                    playable.doCast(itemSkill);
                    successfulUse = true;
                }
                else if (itemSkill.isWithoutAction() || item.getTemplate().hasImmediateEffect() || item.getTemplate().hasExImmediateEffect()) {
                    SkillCaster.triggerCast(playable, null, itemSkill, item, false);
                    successfulUse = true;
                }
                else {
                    playable.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                    if (playable.useSkill(itemSkill, item, forceUse, false)) {
                        successfulUse = true;
                    } else {
                        continue;
                    }
                }

                if (itemSkill.getReuseDelay() > 0) {
                    playable.addTimeStamp(itemSkill, itemSkill.getReuseDelay());
                }
            }
        }

        if (successfulUse && !item.isInfinite() && item.getAction() == ActionType.SKILL_REDUCE_ON_SKILL_SUCCESS) {
            if (!playable.destroyItem("Consume", item.getObjectId(), 1, playable, false) && !(isAutoConsumeItem(item, forceUse))) {
                playable.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
                LOGGER.warn("Failed to consume item {} of {}", item, playable);
                return false;
            }
        }

        return successfulUse;
    }

    private boolean isAutoConsumeItem(Item item, boolean forceUse) {
        return item.getTemplate().hasExImmediateEffect() && forceUse;
    }

    private boolean isSkillReducer(Item item) {
        return switch (item.getAction()) {
            case CALL_SKILL, SKILL_REDUCE, SKILL_REDUCE_ON_SKILL_SUCCESS, NONE -> true;
            default -> false;
        };
    }

    private boolean checkUseSkill(Playable playable, Item item, Skill itemSkill) {
        if (!itemSkill.hasAnyEffectType(EffectType.SUMMON_PET) && !itemSkill.checkCondition(playable, playable.getTarget())) {
            return false;
        }

        if (playable.isSkillDisabled(itemSkill)) {
            return false;
        }

        // Verify that skill is not under reuse.
        return isAvailableToUse(playable, itemSkill, item);
    }

    /**
     * @param playable the character using the item or skill
     * @param skill the skill being used, can be null
     * @param item the item being used
     * @return {@code true} if the the item or skill to check is available, {@code false} otherwise
     */
    private boolean isAvailableToUse(Playable playable, Skill skill, Item item) {
        final long remainingTime = nonNull(skill) ? playable.getSkillRemainingReuseTime(skill.getReuseHashCode()) : playable.getItemRemainingReuseTime(item.getObjectId());
        final boolean isAvailable = remainingTime <= 0;
        if (isPlayer(playable) && !isAvailable) {
            sendReuseMessage(playable, skill, item, remainingTime);
        }
        return isAvailable;
    }

    private void sendReuseMessage(Playable playable, Skill skill, Item item, long remainingTime) {
        final int hours = (int) (remainingTime / 3600000);
        final int minutes = (int) (remainingTime % 3600000) / 60000;
        final int seconds = (int) ((remainingTime / 1000) % 60);
        SystemMessage sm;

        boolean addItemName = skill == null || skill.isStatic();

        if (hours > 0) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_HOUR_S_S3_MINUTE_S_AND_S4_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);

            if (addItemName) {
                sm.addItemName(item);
            } else {
                sm.addSkillName(skill);
            }
            sm.addInt(hours);
            sm.addInt(minutes);
        } else if (minutes > 0) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_MINUTE_S_S3_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
            if (addItemName) {
                sm.addItemName(item);
            } else {
                sm.addSkillName(skill);
            }
            sm.addInt(minutes);
        }
        else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
            if (addItemName) {
                sm.addItemName(item);
            } else {
                sm.addSkillName(skill);
            }
        }
        sm.addInt(seconds);
        playable.sendPacket(sm);
    }
}

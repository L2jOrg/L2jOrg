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
package handlers.itemhandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.item.instance.Item;
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
 */
public class ItemSkillsTemplate implements IItemHandler {

    @Override
    public boolean useItem(Playable playable, Item item, boolean forceUse) {

        if (!isPlayer(playable) && !isPet(playable)) {
            return false;
        }

        // Pets can use items only when they are tradable.
        if (isPet(playable) && !item.isTradeable()) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }

        // Verify that item is not under reuse.
        if (!checkReuse(playable, null, item)) {
            return false;
        }

        final List<ItemSkillHolder> skills = item.getSkills(ItemSkillType.NORMAL);
        if(isNullOrEmpty(skills)) {
            LOGGER.info("Item {} does not have registered any skill for handler.", item);
            return false;
        }

        boolean hasConsumeSkill = false;
        boolean successfulUse = false;

        for (var skillInfo : skills) {

            if (isNull(skillInfo)) {
                continue;
            }

            var itemSkill = skillInfo.getSkill();

            if (nonNull(itemSkill)) {

                var player  = playable.getActingPlayer();

                if (itemSkill.hasAnyEffectType(EffectType.EXTRACT_ITEM) && nonNull(player) && !playable.getActingPlayer().isInventoryUnder80(false)) {
                    player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                    return false;
                }

                if (itemSkill.getItemConsumeId() > 0) {
                    hasConsumeSkill = true;
                }

                if (!itemSkill.hasAnyEffectType(EffectType.SUMMON_PET) && !itemSkill.checkCondition(playable, playable.getTarget())) {
                    continue;
                }

                if (playable.isSkillDisabled(itemSkill)) {
                    continue;
                }

                // Verify that skill is not under reuse.
                if (!checkReuse(playable, itemSkill, item)) {
                    continue;
                }

                if (!item.isPotion() && !item.isElixir() && !item.isScroll() && playable.isCastingNow()) {
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
                    if (playable.useMagic(itemSkill, item, forceUse, false)) {
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

        if (successfulUse && checkConsume(item, hasConsumeSkill))
        {
            if (!playable.destroyItem("Consume", item.getObjectId(), 1, playable, false))
            {
                playable.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
                return false;
            }
        }

        return successfulUse;
    }

    /**
     * @param item the item being used
     * @param hasConsumeSkill
     * @return {@code true} check if item use consume item, {@code false} otherwise
     */
    private boolean checkConsume(Item item, boolean hasConsumeSkill)
    {
        switch (item.getAction()) {
            case CAPSULE:
            case SKILL_REDUCE:
            {
                if (!hasConsumeSkill && item.getTemplate().hasImmediateEffect())
                {
                    return true;
                }
                break;
            }
            case SKILL_REDUCE_ON_SKILL_SUCCESS:
            {
                return false;
            }
        }
        return hasConsumeSkill;
    }

    /**
     * @param playable the character using the item or skill
     * @param skill the skill being used, can be null
     * @param item the item being used
     * @return {@code true} if the the item or skill to check is available, {@code false} otherwise
     */
    private boolean checkReuse(Playable playable, Skill skill, Item item)
    {
        final long remainingTime = (skill != null) ? playable.getSkillRemainingReuseTime(skill.getReuseHashCode()) : playable.getItemRemainingReuseTime(item.getObjectId());
        final boolean isAvailable = remainingTime <= 0;
        if (isPlayer(playable))
        {
            if (!isAvailable)
            {
                final int hours = (int) (remainingTime / 3600000);
                final int minutes = (int) (remainingTime % 3600000) / 60000;
                final int seconds = (int) ((remainingTime / 1000) % 60);
                SystemMessage sm = null;
                if (hours > 0)
                {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_HOUR_S_S3_MINUTE_S_AND_S4_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
                    if ((skill == null) || skill.isStatic())
                    {
                        sm.addItemName(item);
                    }
                    else
                    {
                        sm.addSkillName(skill);
                    }
                    sm.addInt(hours);
                    sm.addInt(minutes);
                }
                else if (minutes > 0)
                {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_MINUTE_S_S3_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
                    if ((skill == null) || skill.isStatic())
                    {
                        sm.addItemName(item);
                    }
                    else
                    {
                        sm.addSkillName(skill);
                    }
                    sm.addInt(minutes);
                }
                else
                {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
                    if ((skill == null) || skill.isStatic())
                    {
                        sm.addItemName(item);
                    }
                    else
                    {
                        sm.addSkillName(skill);
                    }
                }
                sm.addInt(seconds);
                playable.sendPacket(sm);
            }
        }
        return isAvailable;
    }
}

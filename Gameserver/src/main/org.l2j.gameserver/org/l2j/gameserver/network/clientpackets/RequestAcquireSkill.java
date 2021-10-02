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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.IllegalActionPunishmentType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.engine.skill.api.SkillLearn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Fisherman;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.VillageMaster;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSkillLearn;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.SystemMessageId.YOU_MUST_LEARN_THE_NECESSARY_SKILLS_FIRST;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isNpc;

/**
 * Request Acquire Skill client packet implementation.
 *
 * @author Zoey76
 */
public final class RequestAcquireSkill extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAcquireSkill.class);
    public static final String LEVEL = " level ";

    private int id;
    private int level;
    private AcquireSkillType skillType;

    @Override
    public void readImpl() throws InvalidDataPacketException {
        id = readInt();
        level = readInt();
        skillType = AcquireSkillType.getAcquireSkillType(readInt()); // if type is sub pledge subType, so read d

        if (level < 1 || level > 1000 || id < 1 || id > 64000) {
            GameUtils.handleIllegalPlayerAction(client.getPlayer(), "Wrong Packet Data in Acquired Skill");
            throw new InvalidDataPacketException(String.format("Received Wrong Packet Data in Acquired Skill - id: %d level: %d", id, level));
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();

        final Npc trainer = player.getLastFolkNPC();
        if (skillType != AcquireSkillType.CLASS && ( !isNpc(trainer) || ( !trainer.canInteract(player) && !player.isGM()))) {
            return;
        }

        final Skill skill = SkillEngine.getInstance().getSkill(id, level);
        if (isNull(skill)) {
            LOGGER.warn("Player {} is trying to learn a null skill id: {} level: {}!", player, id, level);
            return;
        }

        if (skillType != AcquireSkillType.SUBPLEDGE) {
            final int prevSkillLevel = player.getSkillLevel(id);
            if (prevSkillLevel == level) {
                LOGGER.warn("Player {} is trying to learn a skill that already knows {} !", player, skill);
                return;
            }

            if (prevSkillLevel != level - 1) {
                player.sendPacket(SystemMessageId.THE_PREVIOUS_LEVEL_SKILL_HAS_NOT_BEEN_LEARNED);
                GameUtils.handleIllegalPlayerAction(player, player + " is requesting skill Id: " + id + LEVEL + level + " without knowing it's previous level!", IllegalActionPunishmentType.NONE);
                return;
            }
        }

        final var skillLearn = SkillTreesData.getInstance().getSkillLearn(skillType, id, level, player);
        if (isNull(skillLearn)) {
            return;
        }

        tryAcquireSkill(player, trainer, skill, skillLearn);
    }

    private void tryAcquireSkill(Player player, Npc trainer, Skill skill, SkillLearn skillLearn) {
        switch (skillType) {
            case CLASS, TRANSFORM, FISHING -> acquirePlayerSkill(player, trainer, skill, skillLearn);
            case PLEDGE -> acquirePledgeSkill(player, trainer, skill, skillLearn);
            default -> LOGGER.warn("Received Wrong Packet Data in Acquired Skill, unknown skill type: {}", skillType);
        }
    }

    private void acquirePlayerSkill(Player player, Npc trainer, Skill skill, SkillLearn skillLearn) {
        if (checkPlayerSkill(player, trainer, skillLearn)) {
            for (var replacedSkill : skillLearn.replaceSkills()) {
                player.removeSkill(replacedSkill, true);
            }
            giveSkill(player, trainer, skill);
        }
    }

    private void acquirePledgeSkill(Player player, Npc trainer, Skill skill, SkillLearn skillLearn) {
        if (!player.isClanLeader()) {
            return;
        }

        final Clan clan = player.getClan();
        final int repCost = skillLearn.sp() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) skillLearn.sp();
        if (clan.getReputationScore() >= repCost) {
            if (!consumeRequiredItems(player, trainer, skillLearn)) {
                return;
            }

            clan.takeReputationScore(repCost, true);
            player.sendPacket(getSystemMessage(SystemMessageId.S1_POINT_S_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_S_REPUTATION).addInt(repCost));
            clan.addNewSkill(skill);
            clan.broadcastToOnlineMembers(new PledgeSkillList(clan));
            player.sendPacket(new AcquireSkillDone());
        } else {
            player.sendPacket(SystemMessageId.THE_ATTEMPT_TO_ACQUIRE_THE_SKILL_HAS_FAILED_BECAUSE_OF_AN_INSUFFICIENT_CLAN_REPUTATION);
        }
        VillageMaster.showPledgeSkillList(player);
    }

    private boolean consumeRequiredItems(Player player, Npc trainer, SkillLearn skillLearn) {
        if (CharacterSettings.pledgeSkillsItemNeeded()) {
            for (ItemHolder item : skillLearn.requiredItems()) {
                if (!player.destroyItemByItemId("Consume", item.getId(), item.getCount(), trainer, false)) {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                    VillageMaster.showPledgeSkillList(player);
                    return false;
                }

                player.sendPacket(getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED).addItemName(item.getId()).addLong(item.getCount()));
            }
        }
        return true;
    }

    /**
     * Perform a simple check for current player and skill.<br>
     * Takes the needed SP if the skill require it and all requirements are meet.<br>
     * Consume required items if the skill require it and all requirements are meet.<br>
     *
     * @param player     the skill learning player.
     * @param trainer    the skills teaching Npc.
     * @param skillLearn the skill to be learned.
     * @return {@code true} if all requirements are meet, {@code false} otherwise.
     */
    private boolean checkPlayerSkill(Player player, Npc trainer, SkillLearn skillLearn) {
        if (skillLearn.requiredLevel() > player.getLevel()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_SKILL_LEVEL_REQUIREMENTS);
            GameUtils.handleIllegalPlayerAction(player, player + ", level " + player.getLevel() + " is requesting skill Id: " + id + LEVEL + level + " without having minimum required level, " + skillLearn.requiredLevel() + "!", IllegalActionPunishmentType.NONE);
            return false;
        }

        if(!checkSkillRequirements(player, trainer, skillLearn)) {
            return false;
        }

        if (skillLearn.sp() > 0) {
            player.setSp(player.getSp() - skillLearn.sp());
            player.sendPacket(new UserInfo(player, UserInfoType.CURRENT_HPMPCP_EXP_SP));
        }
        return true;
    }

    private boolean checkSkillRequirements(Player player, Npc trainer, SkillLearn skillLearn) {
        var levelUpSp = skillLearn.sp();
        if ((levelUpSp > 0) && (levelUpSp > player.getSp())) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
            showSkillList(trainer, player);
            return false;
        }

        for (var skill : skillLearn.replaceSkills()) {
            if (player.getSkillLevel(skill.getId()) < skill.getLevel()) {
                player.sendPacket(YOU_MUST_LEARN_THE_NECESSARY_SKILLS_FIRST);
                return false;
            }
        }

        if (id == CommonSkill.DIVINE_INSPIRATION.getId() && !CharacterSettings.divineInspirationBookNeeded()) {
            return true;
        }

        return checkItemRequirements(player, trainer, skillLearn);
    }

    private boolean checkItemRequirements(Player player, Npc trainer, SkillLearn skillLearn) {
        // Check for required items.
        if (!skillLearn.requiredItems().isEmpty()) {
            // Then checks that the player has all the items
            long reqItemCount;
            for (ItemHolder item : skillLearn.requiredItems()) {
                reqItemCount = player.getInventory().getInventoryItemCount(item.getId(), -1);
                if (reqItemCount < item.getCount()) {
                    // Player doesn't have required item.
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                    showSkillList(trainer, player);
                    return false;
                }
            }

            // If the player has all required items, they are consumed.
            for (ItemHolder itemIdCount : skillLearn.requiredItems()) {
                if (!player.destroyItemByItemId("SkillLearn", itemIdCount.getId(), itemIdCount.getCount(), trainer, true)) {
                    GameUtils.handleIllegalPlayerAction(player, "Somehow player " + player.getName() + ", level " + player.getLevel() + " lose required item Id: " + itemIdCount.getId() + " to learn skill while learning skill Id: " + id + LEVEL + level + "!", IllegalActionPunishmentType.NONE);
                }
            }
        }
        return true;
    }

    /**
     * Add the skill to the player and makes proper updates.
     *
     * @param player  the player acquiring a skill.
     * @param trainer the Npc teaching a skill.
     * @param skill   the skill to be learned.
     */
    private void giveSkill(Player player, Npc trainer, Skill skill) {
        player.sendPacket(getSystemMessage(SystemMessageId.LEARNED_S1_LV_S2).addSkillName(skill).addInt(skill.getLevel()));
        player.addSkill(skill, true);

        player.sendItemList();
        player.sendPacket(new ExBasicActionList(ExBasicActionList.DEFAULT_ACTION_LIST));
        player.sendSkillList(skill.getId());

        showSkillList(trainer, player);

        if (id >= 1368 && id <= 1372) {
            player.sendPacket(new ExStorageMaxCount(player));
        }

        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSkillLearn(trainer, player, skill, skillType), Objects.requireNonNullElse(trainer, player));
    }

    /**
     * Wrapper for returning the skill list to the player after it's done with current skill.
     *
     * @param trainer the Npc which the {@code player} is interacting
     * @param player  the active character
     */
    private void showSkillList(Npc trainer, Player player) {
        if (trainer instanceof Fisherman) {
            Fisherman.showFishSkillList(player);
        }
    }
}

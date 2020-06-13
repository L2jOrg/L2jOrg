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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.IllegalActionPunishmentType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Fisherman;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.VillageMaster;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.model.base.SubClass;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSkillLearn;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isNpc;

/**
 * Request Acquire Skill client packet implementation.
 *
 * @author Zoey76
 */
public final class RequestAcquireSkill extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAcquireSkill.class);

    private int id;
    private int level;
    private AcquireSkillType skillType;
    private int subType;

    @Override
    public void readImpl() {
        id = readInt();
        level = readInt();
        skillType = AcquireSkillType.getAcquireSkillType(readInt());
        if (skillType == AcquireSkillType.SUBPLEDGE) {
            subType = readInt();
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();

        if (level < 1 || level > 1000 || id < 1 || id > 64000) {
            GameUtils.handleIllegalPlayerAction(player, "Wrong Packet Data in Acquired Skill");
            LOGGER.warn("Received Wrong Packet Data in Acquired Skill - id: {} level: {} from {}", id, level,  player);
            return;
        }

        final Npc trainer = player.getLastFolkNPC();
        if (skillType != AcquireSkillType.CLASS && ( !isNpc(trainer) || ( !trainer.canInteract(player) && !player.isGM()))) {
            return;
        }

        final Skill skill = SkillEngine.getInstance().getSkill(id, level);
        if (isNull(skill)) {
            LOGGER.warn("Player {} is trying to learn a null skill id: {} level: {}!", player, id, level);
            return;
        }

        if ((skillType != AcquireSkillType.SUBPLEDGE)) {
            final int prevSkillLevel = player.getSkillLevel(id);
            if (prevSkillLevel == level) {
                LOGGER.warn("Player {} is trying to learn a skill that already knows {} !", player, skill);
                return;
            }

            if (prevSkillLevel != level - 1) {
                player.sendPacket(SystemMessageId.THE_PREVIOUS_LEVEL_SKILL_HAS_NOT_BEEN_LEARNED);
                GameUtils.handleIllegalPlayerAction(player, "Player " + player + " is requesting skill Id: " + id + " level " + level + " without knowing it's previous level!", IllegalActionPunishmentType.NONE);
                return;
            }
        }

        final var skillLearn = SkillTreesData.getInstance().getSkillLearn(skillType, id, level, player);
        if (isNull(skillLearn)) {
            return;
        }

        switch (skillType) {
            case CLASS: {
                if (checkPlayerSkill(player, trainer, skillLearn)) {
                    giveSkill(player, trainer, skill);
                }
                break;
            }
            case TRANSFORM: {
                // Hack check.
                if (!canTransform(player)) {
                    player.sendPacket(SystemMessageId.YOU_HAVE_NOT_COMPLETED_THE_NECESSARY_QUEST_FOR_SKILL_ACQUISITION);
                    GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " is requesting skill Id: " + id + " level " + level + " without required quests!", IllegalActionPunishmentType.NONE);
                    return;
                }

                if (checkPlayerSkill(player, trainer, skillLearn)) {
                    giveSkill(player, trainer, skill);
                }
                break;
            }
            case FISHING: {
                if (checkPlayerSkill(player, trainer, skillLearn)) {
                    giveSkill(player, trainer, skill);
                }
                break;
            }
            case PLEDGE: {
                if (!player.isClanLeader()) {
                    return;
                }

                final Clan clan = player.getClan();
                final int repCost = skillLearn.getLevelUpSp() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) skillLearn.getLevelUpSp();
                if (clan.getReputationScore() >= repCost) {
                    if (Config.LIFE_CRYSTAL_NEEDED) {
                        for (ItemHolder item : skillLearn.getRequiredItems()) {
                            if (!player.destroyItemByItemId("Consume", item.getId(), item.getCount(), trainer, false)) {
                                // Doesn't have required item.
                                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                                VillageMaster.showPledgeSkillList(player);
                                return;
                            }

                            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                            sm.addItemName(item.getId());
                            sm.addLong(item.getCount());
                            player.sendPacket(sm);
                        }
                    }

                    clan.takeReputationScore(repCost, true);

                    final SystemMessage cr = SystemMessage.getSystemMessage(SystemMessageId.S1_POINT_S_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_S_REPUTATION);
                    cr.addInt(repCost);
                    player.sendPacket(cr);

                    clan.addNewSkill(skill);

                    clan.broadcastToOnlineMembers(new PledgeSkillList(clan));

                    player.sendPacket(new AcquireSkillDone());

                    VillageMaster.showPledgeSkillList(player);
                } else {
                    player.sendPacket(SystemMessageId.THE_ATTEMPT_TO_ACQUIRE_THE_SKILL_HAS_FAILED_BECAUSE_OF_AN_INSUFFICIENT_CLAN_REPUTATION);
                    VillageMaster.showPledgeSkillList(player);
                }
                break;
            }
            case SUBPLEDGE: {
                if (!player.isClanLeader() || !player.hasClanPrivilege(ClanPrivilege.CL_TROOPS_FAME)) {
                    return;
                }

                final Clan clan = player.getClan();
                if (clan.getCastleId() == 0) {
                    return;
                }

                // Hack check. Check if SubPledge can accept the new skill:
                if (!clan.isLearnableSubPledgeSkill(skill, subType)) {
                    player.sendPacket(SystemMessageId.THIS_SQUAD_SKILL_HAS_ALREADY_BEEN_LEARNED);
                    GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " is requesting skill Id: " + id + " level " + level + " without knowing it's previous level!", IllegalActionPunishmentType.NONE);
                    return;
                }

                final int repCost = skillLearn.getLevelUpSp() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) skillLearn.getLevelUpSp();
                if (clan.getReputationScore() < repCost) {
                    player.sendPacket(SystemMessageId.THE_ATTEMPT_TO_ACQUIRE_THE_SKILL_HAS_FAILED_BECAUSE_OF_AN_INSUFFICIENT_CLAN_REPUTATION);
                    return;
                }

                for (ItemHolder item : skillLearn.getRequiredItems()) {
                    if (!player.destroyItemByItemId("SubSkills", item.getId(), item.getCount(), trainer, false)) {
                        player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                        return;
                    }

                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                    sm.addItemName(item.getId());
                    sm.addLong(item.getCount());
                    player.sendPacket(sm);
                }

                if (repCost > 0) {
                    clan.takeReputationScore(repCost, true);
                    final SystemMessage cr = SystemMessage.getSystemMessage(SystemMessageId.S1_POINT_S_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_S_REPUTATION);
                    cr.addInt(repCost);
                    player.sendPacket(cr);
                }

                clan.addNewSkill(skill, subType);
                clan.broadcastToOnlineMembers(new PledgeSkillList(clan));
                player.sendPacket(new AcquireSkillDone());

                showSubUnitSkillList(player);
                break;
            }
            default: {
                LOGGER.warn("Recived Wrong Packet Data in Aquired Skill, unknown skill type:" + skillType);
                break;
            }
        }
    }

    /**
     * Perform a simple check for current player and skill.<br>
     * Takes the needed SP if the skill require it and all requirements are meet.<br>
     * Consume required items if the skill require it and all requirements are meet.<br>
     *
     * @param player     the skill learning player.
     * @param trainer    the skills teaching Npc.
     * @param skillLearn the skill to be learn.
     * @return {@code true} if all requirements are meet, {@code false} otherwise.
     */
    private boolean checkPlayerSkill(Player player, Npc trainer, SkillLearn skillLearn) {
        if (skillLearn != null) {
            if ((skillLearn.getSkillId() == id) && (skillLearn.getSkillLevel() == level)) {
                // Hack check.
                if (skillLearn.getGetLevel() > player.getLevel()) {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_SKILL_LEVEL_REQUIREMENTS);
                    GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + ", level " + player.getLevel() + " is requesting skill Id: " + id + " level " + level + " without having minimum required level, " + skillLearn.getGetLevel() + "!", IllegalActionPunishmentType.NONE);
                    return false;
                }

                if (skillLearn.getDualClassLevel() > 0) {
                    final SubClass playerDualClass = player.getDualClass();
                    if ((playerDualClass == null) || (playerDualClass.getLevel() < skillLearn.getDualClassLevel())) {
                        return false;
                    }
                }

                // First it checks that the skill require SP and the player has enough SP to learn it.
                final long levelUpSp = skillLearn.getLevelUpSp();
                if ((levelUpSp > 0) && (levelUpSp > player.getSp())) {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
                    showSkillList(trainer, player);
                    return false;
                }

                if (!Config.DIVINE_SP_BOOK_NEEDED && (id == CommonSkill.DIVINE_INSPIRATION.getId())) {
                    return true;
                }

                // Check for required skills.
                if (!skillLearn.getPreReqSkills().isEmpty()) {
                    for (SkillHolder skill : skillLearn.getPreReqSkills()) {
                        if (player.getSkillLevel(skill.getSkillId()) < skill.getLevel()) {
                            if (skill.getSkillId() == CommonSkill.ONYX_BEAST_TRANSFORMATION.getId()) {
                                player.sendPacket(SystemMessageId.YOU_MUST_LEARN_THE_ONYX_BEAST_SKILL_BEFORE_YOU_CAN_LEARN_FURTHER_SKILLS);
                            } else {
                                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                            }
                            return false;
                        }
                    }
                }

                // Check for required items.
                if (!skillLearn.getRequiredItems().isEmpty()) {
                    // Then checks that the player has all the items
                    long reqItemCount = 0;
                    for (ItemHolder item : skillLearn.getRequiredItems()) {
                        reqItemCount = player.getInventory().getInventoryItemCount(item.getId(), -1);
                        if (reqItemCount < item.getCount()) {
                            // Player doesn't have required item.
                            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                            showSkillList(trainer, player);
                            return false;
                        }
                    }

                    // If the player has all required items, they are consumed.
                    for (ItemHolder itemIdCount : skillLearn.getRequiredItems()) {
                        if (!player.destroyItemByItemId("SkillLearn", itemIdCount.getId(), itemIdCount.getCount(), trainer, true)) {
                            GameUtils.handleIllegalPlayerAction(player, "Somehow player " + player.getName() + ", level " + player.getLevel() + " lose required item Id: " + itemIdCount.getId() + " to learn skill while learning skill Id: " + id + " level " + level + "!", IllegalActionPunishmentType.NONE);
                        }
                    }
                }

                if (!skillLearn.getRemoveSkills().isEmpty()) {
                    skillLearn.getRemoveSkills().forEach(skillId ->
                    {
                        if (player.getSkillLevel(skillId) > 0) {
                            final Skill skillToRemove = player.getKnownSkill(skillId);
                            if (skillToRemove != null) {
                                player.removeSkill(skillToRemove, true);
                            }
                        }
                    });
                }

                // If the player has SP and all required items then consume SP.
                if (levelUpSp > 0) {
                    player.setSp(player.getSp() - levelUpSp);
                    final UserInfo ui = new UserInfo(player);
                    ui.addComponentType(UserInfoType.CURRENT_HPMPCP_EXP_SP);
                    player.sendPacket(ui);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Add the skill to the player and makes proper updates.
     *
     * @param player  the player acquiring a skill.
     * @param trainer the Npc teaching a skill.
     * @param skill   the skill to be learn.
     */
    private void giveSkill(Player player, Npc trainer, Skill skill) {
        giveSkill(player, trainer, skill, true);
    }

    /**
     * Add the skill to the player and makes proper updates.
     *
     * @param player  the player acquiring a skill.
     * @param trainer the Npc teaching a skill.
     * @param skill   the skill to be learn.
     * @param store
     */
    private void giveSkill(Player player, Npc trainer, Skill skill, boolean store) {
        // Send message.
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.LEARNED_S1_LV_S2);
        sm.addSkillName(skill);
        player.sendPacket(sm);

        player.addSkill(skill, store);

        player.sendItemList();
        player.sendPacket(new ShortCutInit());
        player.sendPacket(new ExBasicActionList(ExBasicActionList.DEFAULT_ACTION_LIST));
        player.sendSkillList(skill.getId());

        player.updateShortCuts(id, level, 0);
        showSkillList(trainer, player);

        // If skill is expand type then sends packet:
        if ((id >= 1368) && (id <= 1372)) {
            player.sendPacket(new ExStorageMaxCount(player));
        }

        // Notify scripts of the skill learn.
        if (trainer != null) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSkillLearn(trainer, player, skill, skillType), trainer);
        } else {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSkillLearn(trainer, player, skill, skillType), player);
        }
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

    private void showSubUnitSkillList(Player activeChar) {
        final List<SkillLearn> skills = SkillTreesData.getInstance().getAvailableSubPledgeSkills(activeChar.getClan());

        if (skills.isEmpty()) {
            activeChar.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
        } else {
            activeChar.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.SUBPLEDGE));
        }
    }

    // TODO remove this
    private boolean canTransform(Player player) {
        if (Config.ALLOW_TRANSFORM_WITHOUT_QUEST) {
            return true;
        }
        final QuestState qs = player.getQuestState("Q00136_MoreThanMeetsTheEye");
        return (qs != null) && qs.isCompleted();
    }
}

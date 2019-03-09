package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.mobius.gameserver.enums.*;
import org.l2j.gameserver.mobius.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2SkillLearn;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2FishermanInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2VillageMasterInstance;
import org.l2j.gameserver.mobius.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.mobius.gameserver.model.base.SubClass;
import org.l2j.gameserver.mobius.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player.OnPlayerSkillLearn;
import org.l2j.gameserver.mobius.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.mobius.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.mobius.gameserver.model.quest.QuestState;
import org.l2j.gameserver.mobius.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.model.variables.PlayerVariables;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.*;
import org.l2j.gameserver.mobius.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Request Acquire Skill client packet implementation.
 * @author Zoey76
 */
public final class RequestAcquireSkill extends IClientIncomingPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAcquireSkill.class);
    private static final String[] REVELATION_VAR_NAMES =
            {
                    PlayerVariables.REVELATION_SKILL_1_MAIN_CLASS,
                    PlayerVariables.REVELATION_SKILL_2_MAIN_CLASS
            };

    private static final String[] DUALCLASS_REVELATION_VAR_NAMES =
            {
                    PlayerVariables.REVELATION_SKILL_1_DUAL_CLASS,
                    PlayerVariables.REVELATION_SKILL_2_DUAL_CLASS
            };

    private int _id;
    private int _level;
    private AcquireSkillType _skillType;
    private int _subType;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _id = packet.getInt();
        _level = packet.getInt();
        _skillType = AcquireSkillType.getAcquireSkillType(packet.getInt());
        if (_skillType == AcquireSkillType.SUBPLEDGE)
        {
            _subType = packet.getInt();
        }
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        if ((_level < 1) || (_level > 1000) || (_id < 1) || (_id > 32000))
        {
            Util.handleIllegalPlayerAction(activeChar, "Wrong Packet Data in Aquired Skill", Config.DEFAULT_PUNISH);
            LOGGER.warn("Recived Wrong Packet Data in Aquired Skill - id: " + _id + " level: " + _level + " for " + activeChar);
            return;
        }

        final L2Npc trainer = activeChar.getLastFolkNPC();
        if ((_skillType != AcquireSkillType.CLASS) && ((trainer == null) || !trainer.isNpc() || (!trainer.canInteract(activeChar) && !activeChar.isGM())))
        {
            return;
        }

        final Skill existingSkill = activeChar.getKnownSkill(_id); // Mobius: Keep existing sublevel.
        final Skill skill = SkillData.getInstance().getSkill(_id, _level, existingSkill == null ? 0 : existingSkill.getSubLevel());
        if (skill == null)
        {
            LOGGER.warn(RequestAcquireSkill.class.getSimpleName() + ": Player " + activeChar.getName() + " is trying to learn a null skill Id: " + _id + " level: " + _level + "!");
            return;
        }

        // Hack check. Doesn't apply to all Skill Types
        final int prevSkillLevel = activeChar.getSkillLevel(_id);
        if ((_skillType != AcquireSkillType.TRANSFER) && (_skillType != AcquireSkillType.SUBPLEDGE))
        {
            if (prevSkillLevel == _level)
            {
                LOGGER.warn("Player " + activeChar.getName() + " is trying to learn a skill that already knows, Id: " + _id + " level: " + _level + "!");
                return;
            }

            if (prevSkillLevel != (_level - 1))
            {
                // The previous level skill has not been learned.
                activeChar.sendPacket(SystemMessageId.THE_PREVIOUS_LEVEL_SKILL_HAS_NOT_BEEN_LEARNED);
                Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " is requesting skill Id: " + _id + " level " + _level + " without knowing it's previous level!", IllegalActionPunishmentType.NONE);
                return;
            }
        }

        final L2SkillLearn s = SkillTreesData.getInstance().getSkillLearn(_skillType, _id, _level, activeChar);
        if (s == null)
        {
            return;
        }

        switch (_skillType)
        {
            case CLASS:
            {
                if (checkPlayerSkill(activeChar, trainer, s))
                {
                    giveSkill(activeChar, trainer, skill);
                }
                break;
            }
            case TRANSFORM:
            {
                // Hack check.
                if (!canTransform(activeChar))
                {
                    activeChar.sendPacket(SystemMessageId.YOU_HAVE_NOT_COMPLETED_THE_NECESSARY_QUEST_FOR_SKILL_ACQUISITION);
                    Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " is requesting skill Id: " + _id + " level " + _level + " without required quests!", IllegalActionPunishmentType.NONE);
                    return;
                }

                if (checkPlayerSkill(activeChar, trainer, s))
                {
                    giveSkill(activeChar, trainer, skill);
                }
                break;
            }
            case FISHING:
            {
                if (checkPlayerSkill(activeChar, trainer, s))
                {
                    giveSkill(activeChar, trainer, skill);
                }
                break;
            }
            case PLEDGE:
            {
                if (!activeChar.isClanLeader())
                {
                    return;
                }

                final L2Clan clan = activeChar.getClan();
                final int repCost = s.getLevelUpSp();
                if (clan.getReputationScore() >= repCost)
                {
                    if (Config.LIFE_CRYSTAL_NEEDED)
                    {
                        for (ItemHolder item : s.getRequiredItems())
                        {
                            if (!activeChar.destroyItemByItemId("Consume", item.getId(), item.getCount(), trainer, false))
                            {
                                // Doesn't have required item.
                                activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                                L2VillageMasterInstance.showPledgeSkillList(activeChar);
                                return;
                            }

                            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                            sm.addItemName(item.getId());
                            sm.addLong(item.getCount());
                            activeChar.sendPacket(sm);
                        }
                    }

                    clan.takeReputationScore(repCost, true);

                    final SystemMessage cr = SystemMessage.getSystemMessage(SystemMessageId.S1_POINT_S_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_S_REPUTATION);
                    cr.addInt(repCost);
                    activeChar.sendPacket(cr);

                    clan.addNewSkill(skill);

                    clan.broadcastToOnlineMembers(new PledgeSkillList(clan));

                    activeChar.sendPacket(new AcquireSkillDone());

                    L2VillageMasterInstance.showPledgeSkillList(activeChar);
                }
                else
                {
                    activeChar.sendPacket(SystemMessageId.THE_ATTEMPT_TO_ACQUIRE_THE_SKILL_HAS_FAILED_BECAUSE_OF_AN_INSUFFICIENT_CLAN_REPUTATION);
                    L2VillageMasterInstance.showPledgeSkillList(activeChar);
                }
                break;
            }
            case SUBPLEDGE:
            {
                if (!activeChar.isClanLeader() || !activeChar.hasClanPrivilege(ClanPrivilege.CL_TROOPS_FAME))
                {
                    return;
                }

                final L2Clan clan = activeChar.getClan();
                if ((clan.getFortId() == 0) && (clan.getCastleId() == 0))
                {
                    return;
                }

                // Hack check. Check if SubPledge can accept the new skill:
                if (!clan.isLearnableSubPledgeSkill(skill, _subType))
                {
                    activeChar.sendPacket(SystemMessageId.THIS_SQUAD_SKILL_HAS_ALREADY_BEEN_LEARNED);
                    Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " is requesting skill Id: " + _id + " level " + _level + " without knowing it's previous level!", IllegalActionPunishmentType.NONE);
                    return;
                }

                final int repCost = s.getLevelUpSp();
                if (clan.getReputationScore() < repCost)
                {
                    activeChar.sendPacket(SystemMessageId.THE_ATTEMPT_TO_ACQUIRE_THE_SKILL_HAS_FAILED_BECAUSE_OF_AN_INSUFFICIENT_CLAN_REPUTATION);
                    return;
                }

                for (ItemHolder item : s.getRequiredItems())
                {
                    if (!activeChar.destroyItemByItemId("SubSkills", item.getId(), item.getCount(), trainer, false))
                    {
                        activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                        return;
                    }

                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                    sm.addItemName(item.getId());
                    sm.addLong(item.getCount());
                    activeChar.sendPacket(sm);
                }

                if (repCost > 0)
                {
                    clan.takeReputationScore(repCost, true);
                    final SystemMessage cr = SystemMessage.getSystemMessage(SystemMessageId.S1_POINT_S_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_S_REPUTATION);
                    cr.addInt(repCost);
                    activeChar.sendPacket(cr);
                }

                clan.addNewSkill(skill, _subType);
                clan.broadcastToOnlineMembers(new PledgeSkillList(clan));
                activeChar.sendPacket(new AcquireSkillDone());

                showSubUnitSkillList(activeChar);
                break;
            }
            case TRANSFER:
            {
                if (checkPlayerSkill(activeChar, trainer, s))
                {
                    giveSkill(activeChar, trainer, skill);
                }

                final List<L2SkillLearn> skills = SkillTreesData.getInstance().getAvailableTransferSkills(activeChar);
                if (skills.isEmpty())
                {
                    activeChar.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
                }
                else
                {
                    activeChar.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.TRANSFER));
                }
                break;
            }
            case SUBCLASS:
            {
                if (activeChar.isSubClassActive())
                {
                    activeChar.sendPacket(SystemMessageId.THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUBCLASS_STATE_PLEASE_TRY_AGAIN_AFTER_CHANGING_TO_THE_MAIN_CLASS);
                    Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " is requesting skill Id: " + _id + " level " + _level + " while Sub-Class is active!", IllegalActionPunishmentType.NONE);
                    return;
                }

                if (checkPlayerSkill(activeChar, trainer, s))
                {
                    final PlayerVariables vars = activeChar.getVariables();
                    String list = vars.getString("SubSkillList", "");
                    if ((prevSkillLevel > 0) && list.contains(_id + "-" + prevSkillLevel))
                    {
                        list = list.replace(_id + "-" + prevSkillLevel, _id + "-" + _level);
                    }
                    else
                    {
                        if (!list.isEmpty())
                        {
                            list += ";";
                        }
                        list += _id + "-" + _level;
                    }
                    vars.set("SubSkillList", list);
                    giveSkill(activeChar, trainer, skill, false);
                }
                break;
            }
            case DUALCLASS:
            {
                if (activeChar.isSubClassActive())
                {
                    activeChar.sendPacket(SystemMessageId.THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUBCLASS_STATE_PLEASE_TRY_AGAIN_AFTER_CHANGING_TO_THE_MAIN_CLASS);
                    Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " is requesting skill Id: " + _id + " level " + _level + " while Sub-Class is active!", IllegalActionPunishmentType.NONE);
                    return;
                }

                if (checkPlayerSkill(activeChar, trainer, s))
                {
                    final PlayerVariables vars = activeChar.getVariables();
                    String list = vars.getString("DualSkillList", "");
                    if ((prevSkillLevel > 0) && list.contains(_id + "-" + prevSkillLevel))
                    {
                        list = list.replace(_id + "-" + prevSkillLevel, _id + "-" + _level);
                    }
                    else
                    {
                        if (!list.isEmpty())
                        {
                            list += ";";
                        }
                        list += _id + "-" + _level;
                    }
                    vars.set("DualSkillList", list);
                    giveSkill(activeChar, trainer, skill, false);
                }
                break;
            }
            case COLLECT:
            {
                if (checkPlayerSkill(activeChar, trainer, s))
                {
                    giveSkill(activeChar, trainer, skill);
                }
                break;
            }
            case ALCHEMY:
            {
                if (activeChar.getRace() != Race.ERTHEIA)
                {
                    return;
                }

                if (checkPlayerSkill(activeChar, trainer, s))
                {
                    giveSkill(activeChar, trainer, skill);

                    activeChar.sendPacket(new AcquireSkillDone());
                    activeChar.sendPacket(new ExAlchemySkillList(activeChar));

                    final List<L2SkillLearn> alchemySkills = SkillTreesData.getInstance().getAvailableAlchemySkills(activeChar);

                    if (alchemySkills.isEmpty())
                    {
                        activeChar.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
                    }
                    else
                    {
                        activeChar.sendPacket(new ExAcquirableSkillListByClass(alchemySkills, AcquireSkillType.ALCHEMY));
                    }
                }

                break;
            }
            case REVELATION:
            {
                if (activeChar.isSubClassActive())
                {
                    activeChar.sendPacket(SystemMessageId.THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUBCLASS_STATE_PLEASE_TRY_AGAIN_AFTER_CHANGING_TO_THE_MAIN_CLASS);
                    Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " is requesting skill Id: " + _id + " level " + _level + " while Sub-Class is active!", IllegalActionPunishmentType.NONE);
                    return;
                }
                if ((activeChar.getLevel() < 85) || !activeChar.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
                {
                    activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                    Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " is requesting skill Id: " + _id + " level " + _level + " while not being level 85 or awaken!", IllegalActionPunishmentType.NONE);
                    return;
                }

                int count = 0;

                for (String varName : REVELATION_VAR_NAMES)
                {
                    if (activeChar.getVariables().getInt(varName, 0) > 0)
                    {
                        count++;
                    }
                }

                if (count >= 2)
                {
                    activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                    Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " is requesting skill Id: " + _id + " level " + _level + " while having already learned 2 skills!", IllegalActionPunishmentType.NONE);
                    return;
                }

                if (checkPlayerSkill(activeChar, trainer, s))
                {
                    final String varName = count == 0 ? REVELATION_VAR_NAMES[0] : REVELATION_VAR_NAMES[1];

                    activeChar.getVariables().set(varName, skill.getId());

                    giveSkill(activeChar, trainer, skill);
                }

                final List<L2SkillLearn> skills = SkillTreesData.getInstance().getAvailableRevelationSkills(activeChar, SubclassType.BASECLASS);
                if (skills.size() > 0)
                {
                    activeChar.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.REVELATION));
                }
                else
                {
                    activeChar.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
                }
                break;
            }
            case REVELATION_DUALCLASS:
            {
                if (activeChar.isSubClassActive() && !activeChar.isDualClassActive())
                {
                    activeChar.sendPacket(SystemMessageId.THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUBCLASS_STATE_PLEASE_TRY_AGAIN_AFTER_CHANGING_TO_THE_MAIN_CLASS);
                    Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " is requesting skill Id: " + _id + " level " + _level + " while Sub-Class is active!", IllegalActionPunishmentType.NONE);
                    return;
                }

                if ((activeChar.getLevel() < 85) || !activeChar.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
                {
                    activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                    Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " is requesting skill Id: " + _id + " level " + _level + " while not being level 85 or awaken!", IllegalActionPunishmentType.NONE);
                    return;
                }

                int count = 0;

                for (String varName : DUALCLASS_REVELATION_VAR_NAMES)
                {
                    if (activeChar.getVariables().getInt(varName, 0) > 0)
                    {
                        count++;
                    }
                }

                if (count >= 2)
                {
                    activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                    Util.handleIllegalPlayerAction(activeChar, "Player " + activeChar.getName() + " is requesting skill Id: " + _id + " level " + _level + " while having already learned 2 skills!", IllegalActionPunishmentType.NONE);
                    return;
                }

                if (checkPlayerSkill(activeChar, trainer, s))
                {
                    final String varName = count == 0 ? DUALCLASS_REVELATION_VAR_NAMES[0] : DUALCLASS_REVELATION_VAR_NAMES[1];

                    activeChar.getVariables().set(varName, skill.getId());

                    giveSkill(activeChar, trainer, skill);
                }

                final List<L2SkillLearn> skills = SkillTreesData.getInstance().getAvailableRevelationSkills(activeChar, SubclassType.DUALCLASS);
                if (skills.size() > 0)
                {
                    activeChar.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.REVELATION_DUALCLASS));
                }
                else
                {
                    activeChar.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
                }
                break;
            }
            default:
            {
                LOGGER.warn("Recived Wrong Packet Data in Aquired Skill, unknown skill type:" + _skillType);
                break;
            }
        }
    }

    public static void showSubUnitSkillList(L2PcInstance activeChar)
    {
        final List<L2SkillLearn> skills = SkillTreesData.getInstance().getAvailableSubPledgeSkills(activeChar.getClan());

        if (skills.isEmpty())
        {
            activeChar.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
        }
        else
        {
            activeChar.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.SUBPLEDGE));
        }
    }

    public static void showSubSkillList(L2PcInstance activeChar)
    {
        final List<L2SkillLearn> skills = SkillTreesData.getInstance().getAvailableSubClassSkills(activeChar);
        if (!skills.isEmpty())
        {
            activeChar.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.SUBCLASS));
        }
        else
        {
            activeChar.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
        }
    }

    public static void showDualSkillList(L2PcInstance activeChar)
    {
        final List<L2SkillLearn> skills = SkillTreesData.getInstance().getAvailableDualClassSkills(activeChar);
        if (!skills.isEmpty())
        {
            activeChar.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.DUALCLASS));
        }
        else
        {
            activeChar.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
        }
    }

    /**
     * Perform a simple check for current player and skill.<br>
     * Takes the needed SP if the skill require it and all requirements are meet.<br>
     * Consume required items if the skill require it and all requirements are meet.<br>
     * @param player the skill learning player.
     * @param trainer the skills teaching Npc.
     * @param skillLearn the skill to be learn.
     * @return {@code true} if all requirements are meet, {@code false} otherwise.
     */
    private boolean checkPlayerSkill(L2PcInstance player, L2Npc trainer, L2SkillLearn skillLearn)
    {
        if (skillLearn != null)
        {
            if ((skillLearn.getSkillId() == _id) && (skillLearn.getSkillLevel() == _level))
            {
                // Hack check.
                if (skillLearn.getGetLevel() > player.getLevel())
                {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_SKILL_LEVEL_REQUIREMENTS);
                    Util.handleIllegalPlayerAction(player, "Player " + player.getName() + ", level " + player.getLevel() + " is requesting skill Id: " + _id + " level " + _level + " without having minimum required level, " + skillLearn.getGetLevel() + "!", IllegalActionPunishmentType.NONE);
                    return false;
                }

                if (skillLearn.getDualClassLevel() > 0)
                {
                    final SubClass playerDualClass = player.getDualClass();
                    if ((playerDualClass == null) || (playerDualClass.getLevel() < skillLearn.getDualClassLevel()))
                    {
                        return false;
                    }
                }

                // First it checks that the skill require SP and the player has enough SP to learn it.
                final int levelUpSp = skillLearn.getLevelUpSp();
                if ((levelUpSp > 0) && (levelUpSp > player.getSp()))
                {
                    player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
                    showSkillList(trainer, player);
                    return false;
                }

                if (!Config.DIVINE_SP_BOOK_NEEDED && (_id == CommonSkill.DIVINE_INSPIRATION.getId()))
                {
                    return true;
                }

                // Check for required skills.
                if (!skillLearn.getPreReqSkills().isEmpty())
                {
                    for (SkillHolder skill : skillLearn.getPreReqSkills())
                    {
                        if (player.getSkillLevel(skill.getSkillId()) != skill.getSkillLevel())
                        {
                            if (skill.getSkillId() == CommonSkill.ONYX_BEAST_TRANSFORMATION.getId())
                            {
                                player.sendPacket(SystemMessageId.YOU_MUST_LEARN_THE_ONYX_BEAST_SKILL_BEFORE_YOU_CAN_LEARN_FURTHER_SKILLS);
                            }
                            else
                            {
                                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                            }
                            return false;
                        }
                    }
                }

                // Check for required items.
                if (!skillLearn.getRequiredItems().isEmpty())
                {
                    // Then checks that the player has all the items
                    long reqItemCount = 0;
                    for (ItemHolder item : skillLearn.getRequiredItems())
                    {
                        reqItemCount = player.getInventory().getInventoryItemCount(item.getId(), -1);
                        if (reqItemCount < item.getCount())
                        {
                            // Player doesn't have required item.
                            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL);
                            showSkillList(trainer, player);
                            return false;
                        }
                    }

                    // If the player has all required items, they are consumed.
                    for (ItemHolder itemIdCount : skillLearn.getRequiredItems())
                    {
                        if (!player.destroyItemByItemId("SkillLearn", itemIdCount.getId(), itemIdCount.getCount(), trainer, true))
                        {
                            Util.handleIllegalPlayerAction(player, "Somehow player " + player.getName() + ", level " + player.getLevel() + " lose required item Id: " + itemIdCount.getId() + " to learn skill while learning skill Id: " + _id + " level " + _level + "!", IllegalActionPunishmentType.NONE);
                        }
                    }
                }

                if (!skillLearn.getRemoveSkills().isEmpty())
                {
                    skillLearn.getRemoveSkills().forEach(skillId ->
                    {
                        if (player.getSkillLevel(skillId) > 0)
                        {
                            final Skill skillToRemove = player.getKnownSkill(skillId);
                            if (skillToRemove != null)
                            {
                                player.removeSkill(skillToRemove, true);
                            }
                        }
                    });
                }

                // If the player has SP and all required items then consume SP.
                if (levelUpSp > 0)
                {
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
     * @param player the player acquiring a skill.
     * @param trainer the Npc teaching a skill.
     * @param skill the skill to be learn.
     */
    private void giveSkill(L2PcInstance player, L2Npc trainer, Skill skill)
    {
        giveSkill(player, trainer, skill, true);
    }

    /**
     * Add the skill to the player and makes proper updates.
     * @param player the player acquiring a skill.
     * @param trainer the Npc teaching a skill.
     * @param skill the skill to be learn.
     * @param store
     */
    private void giveSkill(L2PcInstance player, L2Npc trainer, Skill skill, boolean store)
    {
        // Send message.
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_LEARNED_S1);
        sm.addSkillName(skill);
        player.sendPacket(sm);

        player.addSkill(skill, store);

        player.sendItemList();
        player.sendPacket(new ShortCutInit(player));
        player.sendPacket(new ExBasicActionList(ExBasicActionList.DEFAULT_ACTION_LIST));
        player.sendSkillList(skill.getId());

        player.updateShortCuts(_id, _level, 0);
        showSkillList(trainer, player);

        // If skill is expand type then sends packet:
        if ((_id >= 1368) && (_id <= 1372))
        {
            player.sendPacket(new ExStorageMaxCount(player));
        }

        // Notify scripts of the skill learn.
        if (trainer != null)
        {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSkillLearn(trainer, player, skill, _skillType), trainer);
        }
        else
        {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSkillLearn(trainer, player, skill, _skillType), player);
        }
    }

    /**
     * Wrapper for returning the skill list to the player after it's done with current skill.
     * @param trainer the Npc which the {@code player} is interacting
     * @param player the active character
     */
    private void showSkillList(L2Npc trainer, L2PcInstance player)
    {
        if (_skillType == AcquireSkillType.SUBCLASS)
        {
            showSubSkillList(player);
        }
        else if (_skillType == AcquireSkillType.DUALCLASS)
        {
            showDualSkillList(player);
        }
        else if (trainer instanceof L2FishermanInstance)
        {
            L2FishermanInstance.showFishSkillList(player);
        }
    }

    /**
     * Verify if the player can transform.
     * @param player the player to verify
     * @return {@code true} if the player meets the required conditions to learn a transformation, {@code false} otherwise
     */
    public static boolean canTransform(L2PcInstance player)
    {
        if (Config.ALLOW_TRANSFORM_WITHOUT_QUEST)
        {
            return true;
        }
        final QuestState qs = player.getQuestState("Q00136_MoreThanMeetsTheEye");
        return (qs != null) && qs.isCompleted();
    }
}

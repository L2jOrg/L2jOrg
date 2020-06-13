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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.SubClass;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isAlphaNumeric;

/**
 * This class ...
 *
 * @version $Revision: 1.4.2.3.2.8 $ $Date: 2005/03/29 23:15:15 $
 */
public class VillageMaster extends Folk {
    private static final Logger LOGGER = LoggerFactory.getLogger(VillageMaster.class);

    private static final Set<ClassId> mainSubclassSet;
    private static final Set<ClassId> neverSubclassed = EnumSet.of(ClassId.OVERLORD, ClassId.WARSMITH);
    private static final Set<ClassId> subclasseSet1 = EnumSet.of(ClassId.DARK_AVENGER, ClassId.PALADIN, ClassId.TEMPLE_KNIGHT, ClassId.SHILLIEN_KNIGHT);
    private static final Set<ClassId> subclasseSet2 = EnumSet.of(ClassId.TREASURE_HUNTER, ClassId.ABYSS_WALKER, ClassId.PLAINS_WALKER);
    private static final Set<ClassId> subclasseSet3 = EnumSet.of(ClassId.HAWKEYE, ClassId.SILVER_RANGER, ClassId.PHANTOM_RANGER);
    private static final Set<ClassId> subclasseSet4 = EnumSet.of(ClassId.WARLOCK, ClassId.ELEMENTAL_SUMMONER, ClassId.PHANTOM_SUMMONER);
    private static final Set<ClassId> subclasseSet5 = EnumSet.of(ClassId.SORCERER, ClassId.SPELLSINGER, ClassId.SPELLHOWLER);
    private static final EnumMap<ClassId, Set<ClassId>> subclassSetMap = new EnumMap<>(ClassId.class);
    static
    {
        final Set<ClassId> subclasses = CategoryManager.getInstance().getCategoryByType(CategoryType.THIRD_CLASS_GROUP).stream().mapToObj(ClassId::getClassId).collect(Collectors.toSet());
        subclasses.removeAll(neverSubclassed);
        mainSubclassSet = subclasses;
        subclassSetMap.put(ClassId.DARK_AVENGER, subclasseSet1);
        subclassSetMap.put(ClassId.PALADIN, subclasseSet1);
        subclassSetMap.put(ClassId.TEMPLE_KNIGHT, subclasseSet1);
        subclassSetMap.put(ClassId.SHILLIEN_KNIGHT, subclasseSet1);
        subclassSetMap.put(ClassId.TREASURE_HUNTER, subclasseSet2);
        subclassSetMap.put(ClassId.ABYSS_WALKER, subclasseSet2);
        subclassSetMap.put(ClassId.PLAINS_WALKER, subclasseSet2);
        subclassSetMap.put(ClassId.HAWKEYE, subclasseSet3);
        subclassSetMap.put(ClassId.SILVER_RANGER, subclasseSet3);
        subclassSetMap.put(ClassId.PHANTOM_RANGER, subclasseSet3);
        subclassSetMap.put(ClassId.WARLOCK, subclasseSet4);
        subclassSetMap.put(ClassId.ELEMENTAL_SUMMONER, subclasseSet4);
        subclassSetMap.put(ClassId.PHANTOM_SUMMONER, subclasseSet4);
        subclassSetMap.put(ClassId.SORCERER, subclasseSet5);
        subclassSetMap.put(ClassId.SPELLSINGER, subclasseSet5);
        subclassSetMap.put(ClassId.SPELLHOWLER, subclasseSet5);
    }
    /**
     * Creates a village master.
     *
     * @param template the village master NPC template
     */
    public VillageMaster(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2VillageMasterInstance);
    }

    private static Iterator<SubClass> iterSubClasses(Player player) {
        return player.getSubClasses().values().iterator();
    }

    private static void dissolveClan(Player player, int clanId) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        final Clan clan = player.getClan();
        if (clan.getAllyId() != 0) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISPERSE_THE_CLANS_IN_YOUR_ALLIANCE);
            return;
        }
        if (clan.isAtWar()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_ENGAGED_IN_A_WAR);
            return;
        }
        if (clan.getCastleId() != 0 || clan.getHideoutId() != 0) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_OWNING_A_CLAN_HALL_OR_CASTLE);
            return;
        }

        for (Castle castle : CastleManager.getInstance().getCastles()) {
            if (SiegeManager.getInstance().checkIsRegistered(clan, castle.getId())) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_DISSOLVE_A_CLAN_DURING_A_SIEGE_OR_WHILE_PROTECTING_A_CASTLE);
                return;
            }
        }

        if (player.isInsideZone(ZoneType.SIEGE)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISSOLVE_A_CLAN_DURING_A_SIEGE_OR_WHILE_PROTECTING_A_CASTLE);
            return;
        }
        if (clan.getDissolvingExpiryTime() > System.currentTimeMillis()) {
            player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_THE_DISSOLUTION_OF_YOUR_CLAN);
            return;
        }

        clan.setDissolvingExpiryTime(System.currentTimeMillis() + (Config.ALT_CLAN_DISSOLVE_DAYS * 86400000)); // 24*60*60*1000 = 86400000
        clan.updateClanInDB();

        // The clan leader should take the XP penalty of a full death.
        player.calculateDeathExpPenalty(null);
        ClanTable.getInstance().scheduleRemoveClan(clan);
    }

    private static void recoverClan(Player player, int clanId) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        final Clan clan = player.getClan();
        clan.setDissolvingExpiryTime(0);
        clan.updateClanInDB();
    }

    private static void createSubPledge(Player player, String clanName, String leaderName, int pledgeType, int minClanLvl) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        final Clan clan = player.getClan();
        if (clan.getLevel() < minClanLvl) {
            if (pledgeType == Clan.SUBUNIT_ACADEMY) {
                player.sendPacket(SystemMessageId.TO_ESTABLISH_A_CLAN_ACADEMY_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER);
            } else {
                player.sendPacket(SystemMessageId.THE_CONDITIONS_NECESSARY_TO_CREATE_A_MILITARY_UNIT_HAVE_NOT_BEEN_MET);
            }

            return;
        }
        if (!isAlphaNumeric(clanName) || !isValidName(clanName) || (2 > clanName.length())) {
            player.sendPacket(SystemMessageId.CLAN_NAME_IS_INVALID);
            return;
        }
        if (clanName.length() > 16) {
            player.sendPacket(SystemMessageId.CLAN_NAME_S_LENGTH_IS_INCORRECT);
            return;
        }

        for (Clan tempClan : ClanTable.getInstance().getClans()) {
            if (tempClan.getSubPledge(clanName) != null) {
                if (pledgeType == Clan.SUBUNIT_ACADEMY) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_EXISTS);
                    sm.addString(clanName);
                    player.sendPacket(sm);
                } else {
                    player.sendPacket(SystemMessageId.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME_PLEASE_ENTER_A_DIFFERENT_NAME);
                }

                return;
            }
        }

        if (pledgeType != Clan.SUBUNIT_ACADEMY) {
            if ((clan.getClanMember(leaderName) == null) || (clan.getClanMember(leaderName).getPledgeType() != 0)) {
                if (pledgeType >= Clan.SUBUNIT_KNIGHT1) {
                    player.sendPacket(SystemMessageId.THE_CAPTAIN_OF_THE_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED);
                } else if (pledgeType >= Clan.SUBUNIT_ROYAL1) {
                    player.sendPacket(SystemMessageId.THE_ROYAL_GUARD_CAPTAIN_CANNOT_BE_APPOINTED);
                }

                return;
            }
        }

        final int leaderId = pledgeType != Clan.SUBUNIT_ACADEMY ? clan.getClanMember(leaderName).getObjectId() : 0;

        if (clan.createSubPledge(player, pledgeType, leaderId, clanName) == null) {
            return;
        }

        SystemMessage sm;
        if (pledgeType == Clan.SUBUNIT_ACADEMY) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.CONGRATULATIONS_THE_S1_S_CLAN_ACADEMY_HAS_BEEN_CREATED);
            sm.addString(player.getClan().getName());
        } else if (pledgeType >= Clan.SUBUNIT_KNIGHT1) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THE_KNIGHTS_OF_S1_HAVE_BEEN_CREATED);
            sm.addString(player.getClan().getName());
        } else if (pledgeType >= Clan.SUBUNIT_ROYAL1) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THE_ROYAL_GUARD_OF_S1_HAVE_BEEN_CREATED);
            sm.addString(player.getClan().getName());
        } else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_BEEN_CREATED);
        }
        player.sendPacket(sm);

        if (pledgeType != Clan.SUBUNIT_ACADEMY) {
            final ClanMember leaderSubPledge = clan.getClanMember(leaderName);
            final Player leaderPlayer = leaderSubPledge.getPlayerInstance();
            if (leaderPlayer != null) {
                leaderPlayer.setPledgeClass(ClanMember.calculatePledgeClass(leaderPlayer));
                leaderPlayer.sendPacket(new UserInfo(leaderPlayer));
            }
        }
    }

    private static void renameSubPledge(Player player, int pledgeType, String pledgeName) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        final Clan clan = player.getClan();
        final var subPledge = player.getClan().getSubPledge(pledgeType);

        if (subPledge == null) {
            player.sendMessage("Pledge don't exists.");
            return;
        }
        if (!isAlphaNumeric(pledgeName) || !isValidName(pledgeName) || (2 > pledgeName.length())) {
            player.sendPacket(SystemMessageId.CLAN_NAME_IS_INVALID);
            return;
        }
        if (pledgeName.length() > 16) {
            player.sendPacket(SystemMessageId.CLAN_NAME_S_LENGTH_IS_INCORRECT);
            return;
        }

        subPledge.setName(pledgeName);
        clan.updateSubPledgeInDB(subPledge.getId());
        clan.broadcastClanStatus();
        player.sendMessage("Pledge name changed.");
    }

    private static void assignSubPledgeLeader(Player player, String clanName, String leaderName) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        if (leaderName.length() > 16) {
            player.sendPacket(SystemMessageId.YOUR_TITLE_CANNOT_EXCEED_16_CHARACTERS_IN_LENGTH_PLEASE_TRY_AGAIN);
            return;
        }
        if (player.getName().equals(leaderName)) {
            player.sendPacket(SystemMessageId.THE_ROYAL_GUARD_CAPTAIN_CANNOT_BE_APPOINTED);
            return;
        }

        final Clan clan = player.getClan();
        final var subPledge = player.getClan().getSubPledge(clanName);

        if ((null == subPledge) || (subPledge.getId() == Clan.SUBUNIT_ACADEMY)) {
            player.sendPacket(SystemMessageId.CLAN_NAME_IS_INVALID);
            return;
        }
        if ((clan.getClanMember(leaderName) == null) || (clan.getClanMember(leaderName).getPledgeType() != 0)) {
            if (subPledge.getId() >= Clan.SUBUNIT_KNIGHT1) {
                player.sendPacket(SystemMessageId.THE_CAPTAIN_OF_THE_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED);
            } else if (subPledge.getId() >= Clan.SUBUNIT_ROYAL1) {
                player.sendPacket(SystemMessageId.THE_ROYAL_GUARD_CAPTAIN_CANNOT_BE_APPOINTED);
            }

            return;
        }

        subPledge.setLeaderId(clan.getClanMember(leaderName).getObjectId());
        clan.updateSubPledgeInDB(subPledge.getId());

        final ClanMember leaderSubPledge = clan.getClanMember(leaderName);
        final Player leaderPlayer = leaderSubPledge.getPlayerInstance();
        if (leaderPlayer != null) {
            leaderPlayer.setPledgeClass(ClanMember.calculatePledgeClass(leaderPlayer));
            leaderPlayer.sendPacket(new UserInfo(leaderPlayer));
        }

        clan.broadcastClanStatus();
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BEEN_SELECTED_AS_THE_CAPTAIN_OF_S2);
        sm.addString(leaderName);
        sm.addString(clanName);
        clan.broadcastToOnlineMembers(sm);
    }

    /**
     * this displays PledgeSkillList to the player.
     *
     * @param player
     */
    public static void showPledgeSkillList(Player player) {
        if (!player.isClanLeader()) {
            final NpcHtmlMessage html = new NpcHtmlMessage();
            html.setFile(player, "data/html/villagemaster/NotClanLeader.htm");
            player.sendPacket(html);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final List<SkillLearn> skills = SkillTreesData.getInstance().getAvailablePledgeSkills(player.getClan());

        if (skills.isEmpty()) {
            if (player.getClan().getLevel() < 8) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN_COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1);
                if (player.getClan().getLevel() < 5) {
                    sm.addInt(5);
                } else {
                    sm.addInt(player.getClan().getLevel() + 1);
                }
                player.sendPacket(sm);
            } else {
                final NpcHtmlMessage html = new NpcHtmlMessage();
                html.setFile(player, "data/html/villagemaster/NoMoreSkills.htm");
                player.sendPacket(html);
            }
        } else {
            player.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.PLEDGE));
        }
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    private static boolean isValidName(String name) {
        Pattern pattern;
        try {
            pattern = Pattern.compile(Config.CLAN_NAME_TEMPLATE);
        } catch (PatternSyntaxException e) {
            LOGGER.warn("ERROR: Wrong pattern for clan name!");
            pattern = Pattern.compile(".*");
        }
        return pattern.matcher(name).matches();
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        if (GameUtils.isMonster(attacker)) {
            return true;
        }

        return super.isAutoAttackable(attacker);
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        String pom = "";

        if (val == 0) {
            pom = Integer.toString(npcId);
        } else {
            pom = npcId + "-" + val;
        }

        return "data/html/villagemaster/" + pom + ".htm";
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        final String[] commandStr = command.split(" ");
        final String actualCommand = commandStr[0]; // Get actual command

        String cmdParams = "";
        String cmdParams2 = "";

        if (commandStr.length >= 2) {
            cmdParams = commandStr[1];
        }
        if (commandStr.length >= 3) {
            cmdParams2 = commandStr[2];
        }

        if (actualCommand.equalsIgnoreCase("create_clan")) {
            if (cmdParams.isEmpty()) {
                return;
            }

            if (!cmdParams2.isEmpty() || !isValidName(cmdParams)) {
                player.sendPacket(SystemMessageId.CLAN_NAME_IS_INVALID);
                return;
            }

            ClanTable.getInstance().createClan(player, cmdParams);
        } else if (actualCommand.equalsIgnoreCase("create_academy")) {
            if (cmdParams.isEmpty()) {
                return;
            }

            createSubPledge(player, cmdParams, null, Clan.SUBUNIT_ACADEMY, 5);
        } else if (actualCommand.equalsIgnoreCase("rename_pledge")) {
            if (cmdParams.isEmpty() || cmdParams2.isEmpty()) {
                return;
            }

            renameSubPledge(player, Integer.parseInt(cmdParams), cmdParams2);
        } else if (actualCommand.equalsIgnoreCase("create_royal")) {
            if (cmdParams.isEmpty()) {
                return;
            }

            createSubPledge(player, cmdParams, cmdParams2, Clan.SUBUNIT_ROYAL1, 6);
        } else if (actualCommand.equalsIgnoreCase("create_knight")) {
            if (cmdParams.isEmpty()) {
                return;
            }

            createSubPledge(player, cmdParams, cmdParams2, Clan.SUBUNIT_KNIGHT1, 7);
        } else if (actualCommand.equalsIgnoreCase("assign_subpl_leader")) {
            if (cmdParams.isEmpty()) {
                return;
            }

            assignSubPledgeLeader(player, cmdParams, cmdParams2);
        } else if (actualCommand.equalsIgnoreCase("create_ally")) {
            if (cmdParams.isEmpty()) {
                return;
            }

            if (player.getClan() == null) {
                player.sendPacket(SystemMessageId.ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES);
            } else {
                player.getClan().createAlly(player, cmdParams);
            }
        } else if (actualCommand.equalsIgnoreCase("dissolve_ally")) {
            player.getClan().dissolveAlly(player);
        } else if (actualCommand.equalsIgnoreCase("dissolve_clan")) {
            dissolveClan(player, player.getClanId());
        } else if (actualCommand.equalsIgnoreCase("change_clan_leader")) {
            if (cmdParams.isEmpty()) {
                return;
            }

            if (!player.isClanLeader()) {
                player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }

            if (player.getName().equalsIgnoreCase(cmdParams)) {
                return;
            }

            final Clan clan = player.getClan();
            final ClanMember member = clan.getClanMember(cmdParams);
            if (member == null) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DOES_NOT_EXIST);
                sm.addString(cmdParams);
                player.sendPacket(sm);
                return;
            }

            if (!member.isOnline()) {
                player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
                return;
            }

            // To avoid clans with null clan leader, academy members shouldn't be eligible for clan leader.
            if (member.getPlayerInstance().isAcademyMember()) {
                player.sendPacket(SystemMessageId.THAT_PRIVILEGE_CANNOT_BE_GRANTED_TO_A_CLAN_ACADEMY_MEMBER);
                return;
            }

            if (Config.ALT_CLAN_LEADER_INSTANT_ACTIVATION) {
                clan.setNewLeader(member);
            } else {
                final NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
                if (clan.getNewLeaderId() == 0) {
                    clan.setNewLeaderId(member.getObjectId(), true);
                    msg.setFile(player, "data/scripts/village_master/Clan/9000-07-success.htm");
                } else {
                    msg.setFile(player, "data/scripts/village_master/Clan/9000-07-in-progress.htm");
                }
                player.sendPacket(msg);
            }
        } else if (actualCommand.equalsIgnoreCase("cancel_clan_leader_change")) {
            if (!player.isClanLeader()) {
                player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }

            final Clan clan = player.getClan();
            final NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
            if (clan.getNewLeaderId() != 0) {
                clan.setNewLeaderId(0, true);
                msg.setFile(player, "data/scripts/village_master/Clan/9000-07-canceled.htm");
            } else {
                msg.setHtml("<html><body>You don't have clan leader delegation applications submitted yet!</body></html>");
            }

            player.sendPacket(msg);
        } else if (actualCommand.equalsIgnoreCase("recover_clan")) {
            recoverClan(player, player.getClanId());
        } else if (actualCommand.equalsIgnoreCase("increase_clan_level")) {
            if (player.getClan().levelUpClan(player)) {
                player.broadcastPacket(new MagicSkillUse(player, 5103, 1, 0, 0));
                player.broadcastPacket(new MagicSkillLaunched(player, 5103, 1));
            }
        } else if (actualCommand.equalsIgnoreCase("learn_clan_skills")) {
            showPledgeSkillList(player);
        } else if (command.startsWith("Subclass")) {
            // Subclasses may not be changed while a skill is in use.
            if (player.isCastingNow() || player.isAllSkillsDisabled()) {
                player.sendPacket(SystemMessageId.SUBCLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE);
                return;
            }
            final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
            // Subclasses may not be changed while a transformated state.
            if (player.getTransformation() != null) {
                html.setFile(player, "data/html/villagemaster/SubClass_NoTransformed.htm");
                player.sendPacket(html);
                return;
            }
            // Subclasses may not be changed while a summon is active.
            if (player.hasSummon()) {
                html.setFile(player, "data/html/villagemaster/SubClass_NoSummon.htm");
                player.sendPacket(html);
                return;
            }
            // Subclasses may not be changed while you have exceeded your inventory limit.
            if (!player.isInventoryUnder90(true)) {
                player.sendPacket(SystemMessageId.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT);
                return;
            }
            // Subclasses may not be changed while a you are over your weight limit.
            if (player.getWeightPenalty() >= 2) {
                player.sendPacket(SystemMessageId.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT);
                return;
            }

            int cmdChoice = 0;
            int paramOne = 0;
            int paramTwo = 0;
            try {
                cmdChoice = Integer.parseInt(command.substring(9, 10).trim());

                int endIndex = command.indexOf(' ', 11);
                if (endIndex == -1) {
                    endIndex = command.length();
                }

                if (command.length() > 11) {
                    paramOne = Integer.parseInt(command.substring(11, endIndex).trim());
                    if (command.length() > endIndex) {
                        paramTwo = Integer.parseInt(command.substring(endIndex).trim());
                    }
                }
            } catch (Exception NumberFormatException) {
                LOGGER.warn(VillageMaster.class.getName() + ": Wrong numeric values for command " + command);
            }

            Set<ClassId> subsAvailable = null;
            switch (cmdChoice) {
                case 0: // Subclass change menu
                    html.setFile(player, getSubClassMenu(player.getRace()));
                    break;
                case 1: // Add Subclass - Initial
                    // Avoid giving player an option to add a new sub class, if they have max sub-classes already.
                    if (player.getTotalSubClasses() >= Config.MAX_SUBCLASS) {
                        html.setFile(player, getSubClassFail());
                        break;
                    }

                    subsAvailable = getAvailableSubClasses(player);
                    if ((subsAvailable != null) && !subsAvailable.isEmpty()) {
                        html.setFile(player, "data/html/villagemaster/SubClass_Add.htm");
                        final StringBuilder content1 = new StringBuilder(200);
                        for (var subClass : subsAvailable) {
                            var playerClass = ClassListData.getInstance().getClass(subClass.getId());
                            if(nonNull(playerClass)) {
                                content1.append("<a action=\"bypass -h npc_%objectId%_Subclass 4 ").append(subClass.getId()).append("\" msg=\"1268;").append(playerClass.getClassName()).append("\">").append(playerClass.getClientCode()).append("</a><br>");
                            }
                        }
                        html.replace("%list%", content1.toString());
                    } else {
                        if ((player.getRace() == Race.ELF) || (player.getRace() == Race.DARK_ELF)) {
                            html.setFile(player, "data/html/villagemaster/SubClass_Fail_Elves.htm");
                            player.sendPacket(html);
                        } else {
                            // TODO: Retail message
                            player.sendMessage("There are no sub classes available at this time.");
                        }
                        return;
                    }
                    break;
                case 2: // Change Class - Initial
                    if (player.getSubClasses().isEmpty()) {
                        html.setFile(player, "data/html/villagemaster/SubClass_ChangeNo.htm");
                    } else {
                        final StringBuilder content2 = new StringBuilder(200);
                        if (checkVillageMaster(player.getBaseClass())) {
                            content2.append("<a action=\"bypass -h npc_%objectId%_Subclass 5 0\">" + ClassListData.getInstance().getClass(player.getBaseClass()).getClientCode() + "</a><br>");
                        }

                        for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext(); ) {
                            final SubClass subClass = subList.next();
                            if (checkVillageMaster(subClass.getClassDefinition())) {
                                content2.append("<a action=\"bypass -h npc_%objectId%_Subclass 5 " + subClass.getClassIndex() + "\">" + ClassListData.getInstance().getClass(subClass.getClassId()).getClientCode() + "</a><br>");
                            }
                        }

                        if (content2.length() > 0) {
                            html.setFile(player, "data/html/villagemaster/SubClass_Change.htm");
                            html.replace("%list%", content2.toString());
                        } else {
                            html.setFile(player, "data/html/villagemaster/SubClass_ChangeNotFound.htm");
                        }
                    }
                    break;
                case 3: // Change/Cancel Subclass - Initial
                    if ((player.getSubClasses() == null) || player.getSubClasses().isEmpty()) {
                        html.setFile(player, "data/html/villagemaster/SubClass_ModifyEmpty.htm");
                        break;
                    }

                    // custom value
                    if (player.getTotalSubClasses() > 3) {
                        html.setFile(player, "data/html/villagemaster/SubClass_ModifyCustom.htm");
                        final StringBuilder content3 = new StringBuilder(200);
                        int classIndex = 1;

                        for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext(); ) {
                            final SubClass subClass = subList.next();

                            content3.append("Sub-class " + classIndex++ + "<br><a action=\"bypass -h npc_%objectId%_Subclass 6 " + subClass.getClassIndex() + "\">" + ClassListData.getInstance().getClass(subClass.getClassId()).getClientCode() + "</a><br>");
                        }
                        html.replace("%list%", content3.toString());
                    } else {
                        // retail html contain only 3 subclasses
                        html.setFile(player, "data/html/villagemaster/SubClass_Modify.htm");
                        if (player.getSubClasses().containsKey(1)) {
                            html.replace("%sub1%", ClassListData.getInstance().getClass(player.getSubClasses().get(1).getClassId()).getClientCode());
                        } else {
                            html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 1\">%sub1%</a><br>", "");
                        }

                        if (player.getSubClasses().containsKey(2)) {
                            html.replace("%sub2%", ClassListData.getInstance().getClass(player.getSubClasses().get(2).getClassId()).getClientCode());
                        } else {
                            html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 2\">%sub2%</a><br>", "");
                        }

                        if (player.getSubClasses().containsKey(3)) {
                            html.replace("%sub3%", ClassListData.getInstance().getClass(player.getSubClasses().get(3).getClassId()).getClientCode());
                        } else {
                            html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 3\">%sub3%</a><br>", "");
                        }
                    }
                    break;
                case 4: // Add Subclass - Action (Subclass 4 x[x])
                    /**
                     * If the character is less than level 75 on any of their previously chosen classes then disallow them to change to their most recently added sub-class choice.
                     */
                    if (!player.getFloodProtectors().getSubclass().tryPerformAction("add subclass")) {
                        LOGGER.warn(VillageMaster.class.getName() + ": Player " + player.getName() + " has performed a subclass change too fast");
                        return;
                    }

                    boolean allowAddition = true;

                    if (player.getTotalSubClasses() >= Config.MAX_SUBCLASS) {
                        allowAddition = false;
                    }

                    if (player.getLevel() < 75) {
                        allowAddition = false;
                    }

                    if (allowAddition) {
                        if (!player.getSubClasses().isEmpty()) {
                            for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext(); ) {
                                final SubClass subClass = subList.next();

                                if (subClass.getLevel() < 75) {
                                    allowAddition = false;
                                    break;
                                }
                            }
                        }
                    }

                    /**
                     * If quest checking is enabled, verify if the character has completed the Mimir's Elixir (Path to Subclass) and Fate's Whisper (A Grade Weapon) quests by checking for instances of their unique reward items. If they both exist, remove both unique items and continue with adding
                     * the sub-class.
                     */
                    if (allowAddition && !Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS) {
                        allowAddition = checkQuests(player);
                    }

                    if (allowAddition && isValidNewSubClass(player, paramOne)) {
                        if (!player.addSubClass(paramOne, player.getTotalSubClasses() + 1, false)) {
                            return;
                        }

                        player.setActiveClass(player.getTotalSubClasses());

                        html.setFile(player, "data/html/villagemaster/SubClass_AddOk.htm");

                        player.sendPacket(SystemMessageId.THE_NEW_SUBCLASS_HAS_BEEN_ADDED); // Subclass added.
                    } else {
                        html.setFile(player, getSubClassFail());
                    }
                    break;
                case 5: // Change Class - Action
                    /**
                     * If the character is less than level 75 on any of their previously chosen classes then disallow them to change to their most recently added sub-class choice. Note: paramOne = classIndex
                     */
                    if (!player.getFloodProtectors().getSubclass().tryPerformAction("change class")) {
                        LOGGER.warn(VillageMaster.class.getName() + ": Player " + player.getName() + " has performed a subclass change too fast");
                        return;
                    }

                    if (player.getClassIndex() == paramOne) {
                        html.setFile(player, "data/html/villagemaster/SubClass_Current.htm");
                        break;
                    }

                    if (paramOne == 0) {
                        if (!checkVillageMaster(player.getBaseClass())) {
                            return;
                        }
                    } else {
                        try {
                            if (!checkVillageMaster(player.getSubClasses().get(paramOne).getClassDefinition())) {
                                return;
                            }
                        } catch (NullPointerException e) {
                            return;
                        }
                    }

                    player.setActiveClass(paramOne);
                    // TODO: Retail message YOU_HAVE_SUCCESSFULLY_SWITCHED_S1_TO_S2
                    player.sendMessage("You have successfully switched to your subclass.");
                    return;
                case 6: // Change/Cancel Subclass - Choice
                    // validity check
                    if ((paramOne < 1) || (paramOne > Config.MAX_SUBCLASS)) {
                        return;
                    }

                    subsAvailable = getAvailableSubClasses(player);
                    // another validity check
                    if ((subsAvailable == null) || subsAvailable.isEmpty()) {
                        // TODO: Retail message
                        player.sendMessage("There are no sub classes available at this time.");
                        return;
                    }

                    final StringBuilder content6 = new StringBuilder(200);
                    for (var subClass : subsAvailable) {
                        content6.append("<a action=\"bypass -h npc_%objectId%_Subclass 7 ").append(paramOne).append(" ").append(subClass.getId()).append("\" msg=\"1445;\">").append(ClassListData.getInstance().getClass(subClass.ordinal()).getClientCode()).append("</a><br>");
                    }

                    switch (paramOne) {
                        case 1:
                            html.setFile(player, "data/html/villagemaster/SubClass_ModifyChoice1.htm");
                            break;
                        case 2:
                            html.setFile(player, "data/html/villagemaster/SubClass_ModifyChoice2.htm");
                            break;
                        case 3:
                            html.setFile(player, "data/html/villagemaster/SubClass_ModifyChoice3.htm");
                            break;
                        default:
                            html.setFile(player, "data/html/villagemaster/SubClass_ModifyChoice.htm");
                    }
                    html.replace("%list%", content6.toString());
                    break;
                case 7: // Change Subclass - Action
                    /**
                     * Warning: the information about this subclass will be removed from the subclass list even if false!
                     */
                    if (!player.getFloodProtectors().getSubclass().tryPerformAction("change class")) {
                        LOGGER.warn(VillageMaster.class.getName() + ": Player " + player.getName() + " has performed a subclass change too fast");
                        return;
                    }

                    if (!isValidNewSubClass(player, paramTwo)) {
                        return;
                    }

                    if (player.modifySubClass(paramOne, paramTwo, false)) {
                        player.abortCast();
                        player.stopAllEffectsExceptThoseThatLastThroughDeath(); // all effects from old subclass stopped!
                        player.stopAllEffects();
                        player.stopCubics();
                        player.setActiveClass(paramOne);

                        html.setFile(player, "data/html/villagemaster/SubClass_ModifyOk.htm");
                        html.replace("%name%", ClassListData.getInstance().getClass(paramTwo).getClientCode());

                        player.sendPacket(SystemMessageId.THE_NEW_SUBCLASS_HAS_BEEN_ADDED); // Subclass added.
                    } else {
                        /**
                         * This isn't good! modifySubClass() removed subclass from memory we must update _classIndex! Else IndexOutOfBoundsException can turn up some place down the line along with other seemingly unrelated problems.
                         */
                        player.setActiveClass(0); // Also updates _classIndex plus switching _classid to baseclass.

                        player.sendMessage("The sub class could not be added, you have been reverted to your base class.");
                        return;
                    }
                    break;
            }
            html.replace("%objectId%", String.valueOf(getObjectId()));
            player.sendPacket(html);
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    protected String getSubClassMenu(Race race) {
        return "data/html/villagemaster/SubClass.htm";
    }

    protected String getSubClassFail() {
        return "data/html/villagemaster/SubClass_Fail.htm";
    }

    protected boolean checkQuests(Player player) {
        // Noble players can add Sub-Classes without quests
        if (player.isNoble()) {
            return true;
        }

        QuestState qs = player.getQuestState("Q00234_FatesWhisper"); // TODO: Not added with Saviors.
        if ((qs == null) || !qs.isCompleted()) {
            return false;
        }

        qs = player.getQuestState("Q00235_MimirsElixir"); // TODO: Not added with Saviors.
        if ((qs == null) || !qs.isCompleted()) {
            return false;
        }

        return true;
    }

    /**
     * Returns list of available subclasses Base class and already used subclasses removed
     *
     * @param player
     * @return
     */
    private Set<ClassId> getAvailableSubClasses(Player player) {
        // get player base class
        final int currentBaseId = player.getBaseClass();
        final ClassId baseCID = ClassId.getClassId(currentBaseId);

        // we need 2nd occupation ID
        final int baseClassId;
        if (baseCID.level() > 2) {
            baseClassId = baseCID.getParent().getId();
        } else {
            baseClassId = currentBaseId;
        }

        /**
         * If the race of your main class is Elf or Dark Elf, you may not select each class as a subclass to the other class.
         * You may not select Overlord and Warsmith class as a subclass. You may not select a similar class as the subclass. The occupations classified as similar classes are as follows: Treasure Hunter, Plainswalker and Abyss Walker Hawkeye, Silver Ranger and Phantom Ranger Paladin, Dark Avenger, Temple Knight
         * and Shillien Knight Warlocks, Elemental Summoner and Phantom Summoner Elder and Shillien Elder Swordsinger and Bladedancer Sorcerer, Spellsinger and Spellhowler Also,
         */
        final Set<ClassId> availSubs = getSubclasses(player, baseClassId);

        if ((availSubs != null) && !availSubs.isEmpty()) {
            for (Iterator<ClassId> availSub = availSubs.iterator(); availSub.hasNext(); ) {
                var pclass = availSub.next();

                // check for the village master
                if (!checkVillageMaster(pclass)) {
                    availSub.remove();
                    continue;
                }

                // scan for already used subclasses
                final int availClassId = pclass.getId();
                final ClassId cid = ClassId.getClassId(availClassId);
                SubClass prevSubClass;
                ClassId subClassId;
                for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext(); ) {
                    prevSubClass = subList.next();
                    subClassId = ClassId.getClassId(prevSubClass.getClassId());

                    if (subClassId.equalsOrChildOf(cid)) {
                        availSub.remove();
                        break;
                    }
                }
            }
        }

        return availSubs;
    }

    public final Set<ClassId> getSubclasses(Player player, int classId)
    {
        Set<ClassId> subclasses = null;
        final ClassId pClass = ClassId.getClassId(classId);

        if (CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, classId) || (CategoryManager.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, classId)))
        {
            subclasses = EnumSet.copyOf(mainSubclassSet);
            subclasses.remove(pClass);

            switch (player.getRace()) {
                case JIN_KAMAEL -> subclasses.removeIf(sub -> sub.getRace() != Race.JIN_KAMAEL);
                case ELF -> subclasses.removeIf(sub -> sub.getRace() == Race.DARK_ELF || sub.getRace() == Race.JIN_KAMAEL);
                case DARK_ELF -> subclasses.removeIf(sub -> sub.getRace() == Race.ELF || sub.getRace() == Race.JIN_KAMAEL);
                default -> subclasses.removeIf(sub -> sub.getRace() == Race.JIN_KAMAEL);
            }

            Set<ClassId> unavailableClasses = subclassSetMap.get(pClass);

            if (unavailableClasses != null)
            {
                subclasses.removeAll(unavailableClasses);
            }
        }

        if (subclasses != null)
        {
            final ClassId currClassId = player.getClassId();
            subclasses.removeIf(currClassId::equalsOrChildOf);
        }
        return subclasses;
    }

    /**
     * Check new subclass classId for validity (villagemaster race/type, is not contains in previous subclasses, is contains in allowed subclasses) Base class not added into allowed subclasses.
     *
     * @param player
     * @param classId
     * @return
     */
    private boolean isValidNewSubClass(Player player, int classId) {
        if (!checkVillageMaster(classId)) {
            return false;
        }

        final ClassId cid = ClassId.getClassId(classId);
        SubClass sub;
        ClassId subClassId;
        for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext(); ) {
            sub = subList.next();
            subClassId = ClassId.getClassId(sub.getClassId());

            if (subClassId.equalsOrChildOf(cid)) {
                return false;
            }
        }

        // get player base class
        final int currentBaseId = player.getBaseClass();
        final ClassId baseCID = ClassId.getClassId(currentBaseId);

        // we need 2nd occupation ID
        final int baseClassId;
        if (baseCID.level() > 2) {
            baseClassId = baseCID.getParent().getId();
        } else {
            baseClassId = currentBaseId;
        }

        final Set<ClassId> availSubs = getSubclasses(player, baseClassId);
        if ((availSubs == null) || availSubs.isEmpty()) {
            return false;
        }

        boolean found = false;
        for (var pclass : availSubs) {
            if (pclass.getId() == classId) {
                found = true;
                break;
            }
        }
        return found;
    }

    protected boolean checkVillageMasterRace(ClassId pclass) {
        return true;
    }

    protected boolean checkVillageMasterTeachType(ClassId pclass) {
        return true;
    }

    /**
     * Returns true if this classId allowed for master
     *
     * @param classId
     * @return
     */
    public final boolean checkVillageMaster(int classId) {
        return checkVillageMaster(ClassId.getClassId(classId));
    }

    /**
     * Returns true if this PlayerClass is allowed for master
     *
     * @param pclass
     * @return
     */
    public final boolean checkVillageMaster(ClassId pclass) {
        if (Config.ALT_GAME_SUBCLASS_EVERYWHERE) {
            return true;
        }

        return checkVillageMasterRace(pclass) && checkVillageMasterTeachType(pclass);
    }
}

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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.List;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.isAlphaNumeric;

/**
 * @author JoeAlisson
 */
public class VillageMaster extends Folk {

    /**
     * Creates a village master.
     *
     * @param template the village master NPC template
     */
    public VillageMaster(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2VillageMasterInstance);
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
        return getSettings(ServerSettings.class).acceptClanName(name);
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
        String pom;

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
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}

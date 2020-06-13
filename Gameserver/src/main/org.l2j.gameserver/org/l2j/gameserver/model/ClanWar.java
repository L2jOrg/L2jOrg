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
package org.l2j.gameserver.model;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.ClanDAO;
import org.l2j.gameserver.data.database.data.ClanWarData;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.ClanWarState;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.clan.OnClanWarStart;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SurrenderPledgeWar;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public final class ClanWar {
    public static final long TIME_TO_CANCEL_NON_MUTUAL_CLAN_WAR = TimeUnit.DAYS.toMillis(7);
    public static final long TIME_TO_DELETION_AFTER_CANCELLATION = TimeUnit.DAYS.toMillis(5);
    public static final long TIME_TO_DELETION_AFTER_DEFEAT = TimeUnit.DAYS.toMillis(21);

    private Future<?> _cancelTask;
    private final ClanWarData data;

    public ClanWar(Clan attacker, Clan attacked) {
        data = ClanWarData.of(attacker, attacked);

        _cancelTask = ThreadPool.schedule(this::clanWarTimeout, (data.getStartTime() + TIME_TO_CANCEL_NON_MUTUAL_CLAN_WAR) - System.currentTimeMillis());

        attacker.addWar(attacked.getId(), this);
        attacked.addWar(attacker.getId(), this);

        EventDispatcher.getInstance().notifyEventAsync(new OnClanWarStart(attacker, attacked));

        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_DECLARED_A_CLAN_WAR_WITH_S1);
        sm.addString(attacked.getName());
        attacker.broadcastToOnlineMembers(sm);

        sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_DECLARED_A_CLAN_WAR_THE_WAR_WILL_AUTOMATICALLY_START_IF_YOU_KILL_S1_CLAN_MEMBERS_5_TIMES_WITHIN_A_WEEK);
        sm.addString(attacker.getName());
        attacked.broadcastToOnlineMembers(sm);
    }

    public ClanWar(ClanWarData warData) {
        this.data = warData;

        if (warData.getStartTime() + TIME_TO_CANCEL_NON_MUTUAL_CLAN_WAR > System.currentTimeMillis()) {
            _cancelTask = ThreadPool.schedule(this::clanWarTimeout, warData.getStartTime() + TIME_TO_CANCEL_NON_MUTUAL_CLAN_WAR - System.currentTimeMillis());
        }

        if (warData.getEndTime() > 0) {
            long endTimePeriod = warData.getEndTime() + (data.getState() == ClanWarState.TIE ? TIME_TO_DELETION_AFTER_CANCELLATION : TIME_TO_DELETION_AFTER_DEFEAT);

            if (endTimePeriod > System.currentTimeMillis()) {
                endTimePeriod = 10000;
            }
            ThreadPool.schedule(() -> ClanTable.getInstance().deleteClanWars(warData.getAttacker(), warData.getAttacked()), endTimePeriod);
        }
    }

    public void onKill(Player killer, Player victim) {
        final Clan victimClan = victim.getClan();
        final Clan killerClan = killer.getClan();

        // Reputation increase by killing an enemy (over level 4) in a clan war under the condition of mutual war declaration
        if ((victim.getLevel() > 4) && (data.getState() == ClanWarState.MUTUAL)) {
            // however, when the other side reputation score is 0 or below, your clan cannot acquire any reputation points from them.
            if (victimClan.getReputationScore() > 0) {
                victimClan.takeReputationScore(Config.REPUTATION_SCORE_PER_KILL, false);
                killerClan.addReputationScore(Config.REPUTATION_SCORE_PER_KILL, false);
            }

            // System Message notification to clan members
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.BECAUSE_C1_WAS_KILLED_BY_A_CLAN_MEMBER_OF_S2_CLAN_REPUTATION_DECREASED_BY_1);
            sm.addPcName(victim);
            sm.addString(killerClan.getName());
            victimClan.broadcastToOtherOnlineMembers(sm, victim);

            sm = SystemMessage.getSystemMessage(SystemMessageId.BECAUSE_A_CLAN_MEMBER_OF_S1_WAS_KILLED_BY_C2_CLAN_REPUTATION_INCREASED_BY_1);
            sm.addString(victimClan.getName());
            sm.addPcName(killer);
            killerClan.broadcastToOtherOnlineMembers(sm, killer);

            if (killerClan.getId() == data.getAttacker()) {
                data.incrementAttackerKill();
            } else {
                data.incrementAttackedKill();
            }
        } else if ((data.getState() == ClanWarState.BLOOD_DECLARATION) && (victimClan.getId() == data.getAttacker()) && (victim.getReputation() >= 0)) {
            final int killCount = data.incrementAttackedKill();

            if (killCount >= 5) {
                data.setState(ClanWarState.MUTUAL);

                SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_WITH_CLAN_S1_HAS_STARTED_THE_CLAN_THAT_CANCELS_THE_WAR_FIRST_WILL_LOSE_500_CLAN_REPUTATION_ANY_CLAN_THAT_CANCELS_THE_WAR_WILL_BE_UNABLE_TO_DECLARE_A_WAR_FOR_1_WEEK_IF_YOUR_CLAN_MEMBER_GETS_KILLED_BY_THE_OTHER_CLAN_XP_DECREASES_BY_1_4_OF_THE_AMOUNT_THAT_DECREASES_IN_THE_HUNTING_GROUND);
                sm.addString(victimClan.getName());
                killerClan.broadcastToOnlineMembers(sm);

                sm = SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_WITH_CLAN_S1_HAS_STARTED_THE_CLAN_THAT_CANCELS_THE_WAR_FIRST_WILL_LOSE_500_CLAN_REPUTATION_ANY_CLAN_THAT_CANCELS_THE_WAR_WILL_BE_UNABLE_TO_DECLARE_A_WAR_FOR_1_WEEK_IF_YOUR_CLAN_MEMBER_GETS_KILLED_BY_THE_OTHER_CLAN_XP_DECREASES_BY_1_4_OF_THE_AMOUNT_THAT_DECREASES_IN_THE_HUNTING_GROUND);
                sm.addString(killerClan.getName());
                victimClan.broadcastToOnlineMembers(sm);

                if (_cancelTask != null) {
                    _cancelTask.cancel(true);
                    _cancelTask = null;
                }
            } else {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_MEMBER_OF_S1_WAS_KILLED_BY_YOUR_CLAN_MEMBER_IF_YOUR_CLAN_KILLS_S2_MEMBERS_OF_CLAN_S1_A_CLAN_WAR_WITH_CLAN_S1_WILL_START);
                sm.addString(victimClan.getName());
                sm.addInt(5 - killCount);
                killerClan.broadcastToOnlineMembers(sm);
            }
        }
    }

    public void cancel(Player player, Clan cancelor) {
        final Clan winnerClan = cancelor.getId() == data.getAttacker() ? ClanTable.getInstance().getClan(data.getAttacked()) : ClanTable.getInstance().getClan(data.getAttacker());

        // Reduce reputation.
        cancelor.takeReputationScore(500, true);

        player.sendPacket(new SurrenderPledgeWar(cancelor.getName(), player.getName()));

        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_WAR_ENDED_BY_YOUR_DEFEAT_DECLARATION_WITH_THE_S1_CLAN);
        sm.addString(winnerClan.getName());
        cancelor.broadcastToOnlineMembers(sm);

        sm = SystemMessage.getSystemMessage(SystemMessageId.THE_WAR_ENDED_BY_THE_S1_CLAN_S_DEFEAT_DECLARATION_YOU_HAVE_WON_THE_CLAN_WAR_OVER_THE_S1_CLAN);
        sm.addString(cancelor.getName());
        winnerClan.broadcastToOnlineMembers(sm);

        data.setWinnerClan(winnerClan.getId());
        data.setEndTime(System.currentTimeMillis());

        ThreadPool.schedule(() -> ClanTable.getInstance().deleteClanWars(cancelor.getId(), winnerClan.getId()), (data.getEndTime() + TIME_TO_DELETION_AFTER_DEFEAT) - System.currentTimeMillis());
    }

    public void clanWarTimeout() {
        final Clan attackerClan = ClanTable.getInstance().getClan(data.getAttacker());
        final Clan attackedClan = ClanTable.getInstance().getClan(data.getAttacked());

        if ((attackerClan != null) && (attackedClan != null)) {
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_DECLARED_BY_CLAN_S1_WAS_CANCELLED);
            sm.addString(attackerClan.getName());
            attackedClan.broadcastToOnlineMembers(sm);

            sm = SystemMessage.getSystemMessage(SystemMessageId.BECAUSE_CLAN_S1_DID_NOT_FIGHT_BACK_FOR_1_WEEK_THE_CLAN_WAR_WAS_CANCELLED);
            sm.addString(attackedClan.getName());
            attackerClan.broadcastToOnlineMembers(sm);

            data.setState(ClanWarState.TIE);
            data.setEndTime(System.currentTimeMillis());

            ThreadPool.schedule(() ->
                    ClanTable.getInstance().deleteClanWars(attackerClan.getId(), attackedClan.getId()), (data.getEndTime() + TIME_TO_DELETION_AFTER_CANCELLATION) - System.currentTimeMillis());
        }
    }

    public void mutualClanWarAccepted(Clan attacker, Clan attacked) {
        data.setState(ClanWarState.MUTUAL);

        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_WITH_CLAN_S1_HAS_STARTED_THE_CLAN_THAT_CANCELS_THE_WAR_FIRST_WILL_LOSE_500_CLAN_REPUTATION_ANY_CLAN_THAT_CANCELS_THE_WAR_WILL_BE_UNABLE_TO_DECLARE_A_WAR_FOR_1_WEEK_IF_YOUR_CLAN_MEMBER_GETS_KILLED_BY_THE_OTHER_CLAN_XP_DECREASES_BY_1_4_OF_THE_AMOUNT_THAT_DECREASES_IN_THE_HUNTING_GROUND);
        sm.addString(attacker.getName());
        attacked.broadcastToOnlineMembers(sm);

        sm = SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_WITH_CLAN_S1_HAS_STARTED_THE_CLAN_THAT_CANCELS_THE_WAR_FIRST_WILL_LOSE_500_CLAN_REPUTATION_ANY_CLAN_THAT_CANCELS_THE_WAR_WILL_BE_UNABLE_TO_DECLARE_A_WAR_FOR_1_WEEK_IF_YOUR_CLAN_MEMBER_GETS_KILLED_BY_THE_OTHER_CLAN_XP_DECREASES_BY_1_4_OF_THE_AMOUNT_THAT_DECREASES_IN_THE_HUNTING_GROUND);
        sm.addString(attacked.getName());
        attacker.broadcastToOnlineMembers(sm);

        if (_cancelTask != null) {
            _cancelTask.cancel(true);
            _cancelTask = null;
        }
    }

    public int getKillDifference(Clan clan) {
        return data.getAttacker() == clan.getId() ? data.getAttackerKills() - data.getAttackedKills() : data.getAttackedKills() - data.getAttackerKills();
    }

    public ClanWarState getClanWarState(Clan clan) {
        if (data.getWinnerClan() > 0) {
            return data.getWinnerClan() == clan.getId() ? ClanWarState.WIN : ClanWarState.LOSS;
        }
        return data.getState();
    }

    public int getAttackerClanId() {
        return data.getAttacker();
    }

    public int getAttackedClanId() {
        return data.getAttacked();
    }

    public int getAttackerKillCount() {
        return data.getAttackerKills();
    }

    public int getAttackedKillCount() {
        return data.getAttackedKills();
    }

    public int getWinnerClanId() {
        return data.getWinnerClan();
    }

    public long getStartTime() {
        return data.getStartTime();
    }

    public long getEndTime() {
        return data.getEndTime();
    }

    public ClanWarState getState() {
        return data.getState();
    }

    public int getKillToStart() {
        return data.getState() == ClanWarState.BLOOD_DECLARATION ? 5 - data.getAttackedKills() : 0;
    }

    public int getRemainingTime() {
        return (int) TimeUnit.SECONDS.convert(data.getStartTime() + TIME_TO_CANCEL_NON_MUTUAL_CLAN_WAR, TimeUnit.MILLISECONDS);
    }

    public Clan getOpposingClan(Clan clan) {
        return data.getAttacker() == clan.getId() ? ClanTable.getInstance().getClan(data.getAttacked()) : ClanTable.getInstance().getClan(data.getAttacker());
    }

    public void save() {
        getDAO(ClanDAO.class).save(data);
    }
}

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
package org.l2j.gameserver.engine.siege;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.CastleDAO;
import org.l2j.gameserver.data.database.dao.SiegeDAO;
import org.l2j.gameserver.data.database.data.SiegeParticipant;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.network.SystemMessageId;

import java.util.Collection;

import static java.util.Objects.*;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author JoeAlisson
 */
public class Siege extends AbstractEvent {

    private final Castle castle;
    private SiegeState state = SiegeState.NONE;
    private final IntMap<SiegeParticipant> attackers = new HashIntMap<>();
    private final IntMap<SiegeParticipant> defenders = new HashIntMap<>();

    public Siege(Castle castle) {
        this.castle = requireNonNull(castle);
        initOwner(castle);
    }

    private void initOwner(Castle castle) {
        final var owner = castle.getOwner();
        if(nonNull(owner)) {
            final var siegeClan = new SiegeParticipant(owner.getId(), SiegeParticipantStatus.OWNER, castle.getId());
            defenders.put(owner.getId(), siegeClan);
            getDAO(SiegeDAO.class).save(siegeClan);
        }
    }

    @Override
    public void sendMessage(SystemMessageId messageId) {

    }

    void registerAttacker(Clan clan) {
        var siegeClan = new SiegeParticipant(clan.getId(), SiegeParticipantStatus.ATTACKER, castle.getId());
        attackers.put(clan.getId(), siegeClan);
        getDAO(SiegeDAO.class).save(siegeClan);
    }

    void registerDefender(Clan clan) {
        var siegeClan = new SiegeParticipant(clan.getId(), SiegeParticipantStatus.WAITING, castle.getId());
        defenders.put(clan.getId(), siegeClan);
        getDAO(SiegeDAO.class).save(siegeClan);
    }

    void removeSiegeClan(Clan clan) {
        attackers.remove(clan.getId());
        defenders.remove(clan.getId());
        getDAO(CastleDAO.class).deleteSiegeClanByCastle(clan.getId(), castle.getId());
    }

    public boolean isRegistered(Clan clan) {
        return attackers.containsKey(clan.getId()) || defenders.containsKey(clan.getId());
    }

    public Castle getCastle() {
        return castle;
    }

    public SiegeState getState() {
        return state;
    }

    boolean isInPreparation() {
        return state == SiegeState.PREPARATION;
    }

    int registeredAttackersAmount() {
        return attackers.size();
    }

    int registeredDefendersAmount() {
        return defenders.size();
    }

    void setState(SiegeState state) {
        this.state = state;
    }

    public int currentStateRemainTime() {
        return switch (state) {
            case PREPARATION -> SiegeEngine.getInstance().remainTimeToStart();
            case STARTED -> SiegeEngine.getInstance().remainTimeToFinish();
            default -> 0;
        };
    }

    public Collection<SiegeParticipant> getDefenderClans() {
        return defenders.values();
    }

    public Collection<SiegeParticipant> getAttackerClans() {
        return attackers.values();
    }

    void registerMercenaryRecruitment(Clan clan, long reward) {
        SiegeParticipant clanSiegeData = getSiegeParticipant(clan);

        if(nonNull(clanSiegeData)) {
            clanSiegeData.setRecruitingMercenary(true);
            clanSiegeData.setMercenaryReward(reward);
            getDAO(SiegeDAO.class).save(clanSiegeData);
        }
    }

    boolean isRecruitingMercenary(Clan clan) {
        SiegeParticipant clanSiegeData = getSiegeParticipant(clan);
        return nonNull(clanSiegeData) && clanSiegeData.isRecruitingMercenary();
    }

    SiegeParticipant getSiegeParticipant(Clan clan) {
        var siegeClan = attackers.get(clan.getId());
        if (isNull(siegeClan)) {
            siegeClan = defenders.get(clan.getId());
        }
        return siegeClan;
    }

    void removeMercenaryRecruitment(Clan clan) {
         final var siegeClanData = getSiegeParticipant(clan);
         siegeClanData.setMercenaryReward(0);
         siegeClanData.setRecruitingMercenary(false);
         getDAO(SiegeDAO.class).save(siegeClanData);
    }

    void joinMercenary(Player player, Clan clan) {
        final var participant = getSiegeParticipant(clan);
        if(nonNull(participant)) {
            participant.addMercenary(Mercenary.of(player));
        }
    }

    public void leaveMercenary(Player player, Clan clan) {
        final var participant = getSiegeParticipant(clan);
        if(nonNull(participant)) {
            participant.removeMercenary(player.getId());
        }
    }

    boolean isAttacker(Clan clan) {
        return attackers.containsKey(clan.getId());
    }

    boolean isDefender(Clan clan) {
        return defenders.containsKey(clan.getId());
    }

    boolean hasStarted() {
        return state == SiegeState.STARTED;
    }

    boolean isMercenary(int playerId) {
        for (SiegeParticipant clan : attackers.values()) {
            if(clan.hasMercenary(playerId)) {
                return true;
            }
        }

        for (SiegeParticipant clan : defenders.values()) {
            if(clan.hasMercenary(playerId)) {
                return true;
            }
        }
        return false;
    }

    Collection<Mercenary> getMercenaries(Clan clan) {
        final var siegeClan = getSiegeParticipant(clan);
        return siegeClan.getMercenaries();
    }
}

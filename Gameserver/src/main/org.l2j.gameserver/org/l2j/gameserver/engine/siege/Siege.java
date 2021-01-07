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
package org.l2j.gameserver.engine.siege;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.CastleDAO;
import org.l2j.gameserver.data.database.dao.SiegeDAO;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;

import static java.util.Objects.*;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author JoeAlisson
 */
public class Siege extends AbstractEvent {

    private final Castle castle;
    private SiegeState state = SiegeState.NONE;
    private final IntMap<SiegeClanData> attackers = new HashIntMap<>();
    private final IntMap<SiegeClanData> defenders = new HashIntMap<>();

    public Siege(Castle castle) {
        this.castle = requireNonNull(castle);
        initOwner(castle);
    }

    private void initOwner(Castle castle) {
        final var owner = castle.getOwner();
        if(nonNull(owner)) {
            final var siegeClan = new SiegeClanData(owner.getId(), SiegeClanStatus.OWNER, castle.getId());
            defenders.put(owner.getId(), siegeClan);
            getDAO(SiegeDAO.class).save(siegeClan);
        }
    }

    @Override
    public void sendMessage(SystemMessageId messageId) {

    }

    void registerAttacker(Clan clan) {
        var siegeClan = new SiegeClanData(clan.getId(), SiegeClanStatus.ATTACKER, castle.getId());
        attackers.put(clan.getId(), siegeClan);
        getDAO(SiegeDAO.class).save(siegeClan);
    }

    void registerDefender(Clan clan) {
        var siegeClan = new SiegeClanData(clan.getId(), SiegeClanStatus.WAITING, castle.getId());
        defenders.put(clan.getId(), siegeClan);
        getDAO(SiegeDAO.class).save(siegeClan);
    }


    void removeSiegeClan(Clan clan) {
        attackers.remove(clan.getId());
        defenders.remove(clan.getId());
        getDAO(CastleDAO.class).deleteSiegeClanByCastle(clan.getId(), castle.getId());
    }

    @Override
    public void sendPacket(ServerPacket packet) {

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

    public boolean isInPreparation() {
        return state == SiegeState.PREPARATION;
    }

    public int registeredAttackersAmount() {
        return attackers.size();
    }

    public int registeredDefendersAmount() {
        return defenders.size();
    }

    public void setState(SiegeState state) {
        this.state = state;
    }

    public int currentStateRemainTime() {
        return switch (state) {
            case PREPARATION -> SiegeEngine.getInstance().remainTimeToStart();
            case STARTED -> SiegeEngine.getInstance().remainTimeToFinish();
            default -> 0;
        };
    }

    public Collection<SiegeClanData> getDefenderClans() {
        return defenders.values();
    }

    public Collection<SiegeClanData> getAttackerClans() {
        return attackers.values();
    }

    public void registerMercenaryRecruitment(Clan clan, long reward) {
        SiegeClanData clanSiegeData = getSiegeClanData(clan);

        if(nonNull(clanSiegeData)) {
            clanSiegeData.setRecruitingMercenary(true);
            clanSiegeData.setMercenaryReward(reward);
            getDAO(SiegeDAO.class).save(clanSiegeData);
        }
    }

    public boolean isRecruitingMercenary(Clan clan) {
        SiegeClanData clanSiegeData = getSiegeClanData(clan);
        return nonNull(clanSiegeData) && clanSiegeData.isRecruitingMercenary();
    }

    private SiegeClanData getSiegeClanData(Clan clan) {
        var siegeClanData = attackers.get(clan.getId());
        if (isNull(siegeClanData)) {
            siegeClanData = defenders.get(clan.getId());
        }
        return siegeClanData;
    }

    public void removeMercenaryRecruitment(Clan clan) {
         final var siegeClanData = getSiegeClanData(clan);
         siegeClanData.setMercenaryReward(0);
         siegeClanData.setRecruitingMercenary(false);
         getDAO(SiegeDAO.class).save(siegeClanData);
    }

    public boolean isAttacker(Clan clan) {
        return attackers.containsKey(clan.getId());
    }

    public boolean isDefender(Clan clan) {
        return defenders.containsKey(clan.getId());
    }
}

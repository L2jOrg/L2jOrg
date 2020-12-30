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
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.network.SystemMessageId;

import java.util.Collection;

import static java.util.Objects.requireNonNull;
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
    }

    @Override
    public void sendMessage(SystemMessageId messageId) {

    }

    void registerAttacker(Clan clan) {
        var siegeClan = new SiegeClanData(clan.getId(), SiegeClanType.ATTACKER, castle.getId());
        attackers.put(clan.getId(), siegeClan);
        getDAO(SiegeDAO.class).save(siegeClan);
    }

    void registerDefender(Clan clan) {
        var siegeClan = new SiegeClanData(clan.getId(), SiegeClanType.DEFENDER_PENDING, castle.getId());
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
}

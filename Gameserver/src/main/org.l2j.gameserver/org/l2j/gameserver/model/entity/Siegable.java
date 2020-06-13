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
package org.l2j.gameserver.model.entity;

import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.Npc;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author JIV
 */
public interface Siegable {
    void startSiege();

    void endSiege();

    SiegeClanData getAttackerClan(int clanId);

    SiegeClanData getAttackerClan(Clan clan);

    IntMap<SiegeClanData> getAttackerClans();

    boolean checkIsAttacker(Clan clan);

    SiegeClanData getDefenderClan(int clanId);

    SiegeClanData getDefenderClan(Clan clan);

    IntMap<SiegeClanData> getDefenderClans();

    boolean checkIsDefender(Clan clan);

    Set<Npc> getFlag(Clan clan);

    LocalDateTime getSiegeDate();

    boolean giveFame();

    int getFameFrequency();

    int getFameAmount();

    void updateSiege();
}

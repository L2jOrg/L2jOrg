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
package org.l2j.gameserver.enums;

import org.l2j.gameserver.data.xml.ClanRewardManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;

import java.util.function.ToIntFunction;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public enum ClanRewardType {
    MEMBERS_ONLINE(0, Clan::getPreviousMaxOnlinePlayers),
    HUNTING_MONSTERS(1, Clan::getPreviousHuntingPoints),
    ARENA(-1, Clan::getArenaProgress);

    final int _clientId;
    final int _mask;
    final ToIntFunction<Clan> _pointsFunction;

    ClanRewardType(int clientId, ToIntFunction<Clan> pointsFunction) {
        _clientId = clientId;
        _mask = 1 << clientId;
        _pointsFunction = pointsFunction;
    }

    public static int getDefaultMask() {
        int mask = 0;
        for (ClanRewardType type : values()) {
            mask |= type.getMask();
        }
        return mask;
    }

    public int getClientId() {
        return _clientId;
    }

    public int getMask() {
        return _mask;
    }

    public ClanRewardBonus getAvailableBonus(Clan clan) {
        ClanRewardBonus availableBonus = null;
        for (ClanRewardBonus bonus : ClanRewardManager.getInstance().getClanRewardBonuses(this)) {
            if (bonus.getRequiredAmount() <= _pointsFunction.applyAsInt(clan)) {
                if ((availableBonus == null) || (availableBonus.getLevel() < bonus.getLevel())) {
                    availableBonus = bonus;
                }
            }
        }
        return availableBonus;
    }
}

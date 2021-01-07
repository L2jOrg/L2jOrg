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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.engine.siege.SiegeEngine;

/**
 * @author KenM
 * @author JoeAlisson
 */
public final class RequestJoinSiege extends ClientPacket {
    private int castleId;
    private boolean isAttacker;
    private boolean isJoining;

    @Override
    public void readImpl() {
        castleId = readInt();
        isAttacker = readIntAsBoolean();
        isJoining = readIntAsBoolean();
    }

    @Override
    public void runImpl() {
        final var siegeEngine = SiegeEngine.getInstance();
        if(isJoining) {
            joinSiege(siegeEngine);
        } else {
            leaveSiege(siegeEngine);
        }
    }

    private void joinSiege(SiegeEngine siegeEngine) {
        if(isAttacker) {
            siegeEngine.registerAttacker(client.getPlayer(), castleId);
        } else {
            siegeEngine.registerDefender(client.getPlayer(), castleId);
        }
    }

    private void leaveSiege(SiegeEngine siegeEngine) {
        if(isAttacker) {
            siegeEngine.cancelAttacker(client.getPlayer(), castleId);
        } else {
            siegeEngine.cancelDefender(client.getPlayer(), castleId);
        }
    }
}

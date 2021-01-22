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
package org.l2j.gameserver.network.clientpackets.siege;

import org.l2j.gameserver.engine.siege.SiegeEngine;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

/**
 * @author JoeAlisson
 */
public class ExRequestPledgeMercenaryMemberJoin extends ClientPacket {

    private boolean joining;
    private int castleId;
    private int clanId;

    @Override
    protected void readImpl() throws Exception {
        readInt(); //playerId
        joining = readIntAsBoolean()    ;
        castleId = readInt();
        clanId = readInt();
    }

    @Override
    protected void runImpl() throws Exception {
        final var siegeEngine = SiegeEngine.getInstance();
        if(joining) {
            siegeEngine.joinMercenaries(client.getPlayer(), castleId, clanId);
        } else {
            siegeEngine.leaveMercenaries(client.getPlayer(), castleId, clanId);
        }
    }
}
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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ManagePledgePower;

public final class RequestPledgePower extends ClientPacket {
    private int _rank;
    private int _action;
    private int _privs;

    @Override
    public void readImpl() {
        _rank = readInt();
        _action = readInt();
        if (_action == 2) {
            _privs = readInt();
        } else {
            _privs = 0;
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        player.sendPacket(new ManagePledgePower(client.getPlayer().getClan(), _action, _rank));
        if (_action == 2) {
            if (player.isClanLeader()) {
                if (_rank == 9) {
                    // The rights below cannot be bestowed upon Academy members:
                    // Join a clan or be dismissed
                    // Title management, crest management, master management, level management,
                    // bulletin board administration
                    // Clan war, right to dismiss, set functions
                    // Auction, manage taxes, attack/defend registration, mercenary management
                    // => Leaves only CP_CL_VIEW_WAREHOUSE, CP_CH_OPEN_DOOR, CP_CS_OPEN_DOOR?
                    _privs &= ClanPrivilege.CL_VIEW_WAREHOUSE.getBitmask() | ClanPrivilege.CH_OPEN_DOOR.getBitmask() | ClanPrivilege.CS_OPEN_DOOR.getBitmask();
                }
                player.getClan().setRankPrivs(_rank, _privs);
            }
        }
    }
}
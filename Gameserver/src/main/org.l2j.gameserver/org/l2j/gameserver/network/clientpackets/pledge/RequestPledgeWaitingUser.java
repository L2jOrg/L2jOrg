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
package org.l2j.gameserver.network.clientpackets.pledge;

import org.l2j.gameserver.data.database.data.PledgeApplicantData;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeWaitingList;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeWaitingUser;

/**
 * @author Sdw
 */
public class RequestPledgeWaitingUser extends ClientPacket {
    private int _clanId;
    private int _playerId;

    @Override
    public void readImpl() {
        _clanId = readInt();
        _playerId = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if ((player == null) || (player.getClanId() != _clanId)) {
            return;
        }

        final PledgeApplicantData infos = ClanEntryManager.getInstance().getPlayerApplication(_clanId, _playerId);
        if (infos == null) {
            client.sendPacket(new ExPledgeWaitingList(_clanId));
        } else {
            client.sendPacket(new ExPledgeWaitingUser(infos));
        }
    }
}
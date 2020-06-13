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

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PledgeReceiveMemberInfo;

/**
 * Format: (ch) dS
 *
 * @author -Wooden-
 */
public final class RequestPledgeMemberInfo extends ClientPacket {
    @SuppressWarnings("unused")
    private int _unk1;
    private String _player;

    @Override
    public void readImpl() {
        _unk1 = readInt();
        _player = readString();
    }

    @Override
    public void runImpl() {
        // LOGGER.info("C5: RequestPledgeMemberInfo d:"+_unk1);
        // LOGGER.info("C5: RequestPledgeMemberInfo S:"+_player);
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        // do we need powers to do that??
        final Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }

        final ClanMember member = clan.getClanMember(_player);
        if (member == null) {
            return;
        }
        client.sendPacket(new PledgeReceiveMemberInfo(member));
    }

}
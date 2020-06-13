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

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.AskJoinPledge;
import org.l2j.gameserver.world.World;

/**
 * @author Mobius
 */
public class RequestClanAskJoinByName extends ClientPacket {
    private String _playerName;
    private int _pledgeType;

    @Override
    public void readImpl() {
        _playerName = readString();
        _pledgeType = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if ((activeChar == null) || (activeChar.getClan() == null)) {
            return;
        }

        final Player invitedPlayer = World.getInstance().findPlayer(_playerName);
        if (!activeChar.getClan().checkClanJoinCondition(activeChar, invitedPlayer, _pledgeType)) {
            return;
        }
        if (!activeChar.getRequest().setRequest(invitedPlayer, this)) {
            return;
        }

        invitedPlayer.sendPacket(new AskJoinPledge(activeChar, _pledgeType, activeChar.getClan().getName()));
    }

    public int getPledgeType()
    {
        return _pledgeType;
    }
}
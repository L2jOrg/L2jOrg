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
import org.l2j.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import org.l2j.gameserver.util.GameUtils;

/**
 * This class ...
 *
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:29:30 $
 */
public final class SetPrivateStoreMsgBuy extends ClientPacket {
    private static final int MAX_MSG_LENGTH = 29;

    private String _storeMsg;

    @Override
    public void readImpl() {
        _storeMsg = readString();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if ((player == null) || (player.getBuyList() == null)) {
            return;
        }

        if ((_storeMsg != null) && (_storeMsg.length() > MAX_MSG_LENGTH)) {
            GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to overflow private store buy message");
            return;
        }

        player.getBuyList().setTitle(_storeMsg);
        client.sendPacket(new PrivateStoreMsgBuy(player));
    }
}

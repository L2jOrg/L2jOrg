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
package org.l2j.gameserver.network.auth.as2gs;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.NetworkService;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.auth.ReceivablePacket;
import org.l2j.gameserver.network.serverpackets.ServerClose;

public class KickPlayer extends ReceivablePacket {
    private String account;

    @Override
    public void readImpl() {
        account = readString();
    }

    @Override
    protected void runImpl() {
        GameClient gameClient = NetworkService.getInstance().removeWaitingClient(client.getAuthKey(), account);
        if (gameClient == null)
            gameClient = NetworkService.getInstance().removeAuthedClient(client.getAuthKey(), account);
        if (gameClient == null)
            return;

        Player activeChar = gameClient.getPlayer();
        if (activeChar != null) {
            activeChar.sendPacket(SystemMessageId.YOU_ARE_LOGGED_IN_TO_TWO_PLACES_IF_YOU_SUSPECT_ACCOUNT_THEFT_WE_RECOMMEND_CHANGING_YOUR_PASSWORD_SCANNING_YOUR_COMPUTER_FOR_VIRUSES_AND_USING_AN_ANTI_VIRUS_SOFTWARE);
            Disconnection.of(activeChar).logout(false);
        } else {
            gameClient.close(ServerClose.STATIC_PACKET);
        }
    }
}
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

import org.l2j.commons.network.SessionKey;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerAuthRequest;
import org.l2j.gameserver.network.serverpackets.ServerClose;

/**
 * This class ...
 *
 * @version $Revision: 1.9.2.3.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class AuthLogin extends ClientPacket {

    // account + keys must match what the loginserver used.
    private String account;
    /*
     * private final long _key1; private final long _key2; private final long _key3; private final long _key4;
     */
    private int sessionId;
    private int accountId;
    private int authAccountId;
    private int authKey;

    @Override
    public void readImpl() {
        account = readString().toLowerCase();
        accountId = readInt();
        sessionId = readInt();
        authAccountId = readInt();
        authKey = readInt();
        // packet.ge
    }

    @Override
    public void runImpl() {
        if (account.isEmpty() || !client.isProtocolOk()) {
            client.closeNow();
            return;
        }


        // avoid potential exploits
        if (client.getAccountName() == null) {
            // Preventing duplicate login in case client login server socket was disconnected or this packet was not sent yet

            client.setAccountName(account);
            final SessionKey key = new SessionKey(authAccountId, authKey, sessionId, accountId);
            client.setSessionId(key);

            GameClient oldClient = AuthServerCommunication.getInstance().addWaitingClient(client);
            if(oldClient != null)
                oldClient.close(ServerClose.STATIC_PACKET);

            AuthServerCommunication.getInstance().sendPacket(new PlayerAuthRequest(client));

        }
    }
}

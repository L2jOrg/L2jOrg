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
package org.l2j.authserver.network.client.packet.client2auth;

import org.l2j.authserver.network.client.packet.AuthClientPacket;
import org.l2j.authserver.network.client.packet.auth2client.GGAuth;
import org.l2j.authserver.network.client.packet.auth2client.LoginFail.LoginFailReason;

import java.util.Objects;

import static org.l2j.authserver.network.client.AuthClientState.AUTHED_GG;

/**
 * @author -Wooden- Format: ddddd
 */
public class AuthGameGuard extends AuthClientPacket {

    private int _sessionId;


    @Override
    protected boolean readImpl() {
        if (available() >= 20) {
            _sessionId = readInt();
            int _data1 = readInt();
            int _data2 = readInt();
            int _data3 = readInt();
            int _data4 = readInt();
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        if (Objects.equals(_sessionId, client.getSessionId())) {
            client.setState(AUTHED_GG);
            client.sendPacket(new GGAuth(_sessionId));
        } else {
            client.close(LoginFailReason.REASON_ACCESS_FAILED);
        }
    }
}
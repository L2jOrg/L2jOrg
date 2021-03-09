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
package org.l2j.gameserver.network.authcomm;

import io.github.joealisson.mmocore.Buffer;
import io.github.joealisson.mmocore.Client;
import io.github.joealisson.mmocore.Connection;
import org.l2j.gameserver.network.authcomm.gs2as.AuthRequest;

/**
 * @author JoeAlisson
 */
public class AuthServerClient extends Client<Connection<AuthServerClient>> {

    AuthServerClient(Connection<AuthServerClient> connection) {
        super(connection);
    }

    public void sendPacket(SendablePacket packet) {
        writePacket(packet);
    }


    @Override
    public boolean encrypt(Buffer data, int offset, int size) {
        return true;
    }

    @Override
    public boolean decrypt(Buffer data, int offset, int size) {
        return true;
    }

    @Override
    protected void onDisconnection() {
        AuthServerCommunication.getInstance().restart();
    }

    @Override
    public void onConnected() {
        writePacket(new AuthRequest());
    }
}

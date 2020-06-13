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

import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerLogout;
import org.l2j.gameserver.network.serverpackets.KeyPacket;
import org.l2j.gameserver.settings.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.contains;

/**
 * @author JoeAlisson
 */
public final class ProtocolVersion extends ClientPacket {
    private static final Logger LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");

    private int version;

    @Override
    public void readImpl() {
        version = readInt();
    }

    @Override
    public void runImpl() {
        // this packet is never encrypted
        if (version == -2) {
            // this is just a ping attempt from the new C2 client
            client.closeNow();
        } else if (!contains(getSettings(ServerSettings.class).acceptedProtocols(), version)) {
            LOGGER_ACCOUNTING.warn("Wrong protocol version {}, {}", version, client);
            AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(client.getAccountName()));
            client.setProtocolOk(false);
            client.close(new KeyPacket(client.enableCrypt(), 0));
        } else {
            client.setProtocolOk(true);
            client.sendPacket(new KeyPacket(client.enableCrypt(), 1));
        }
    }
}

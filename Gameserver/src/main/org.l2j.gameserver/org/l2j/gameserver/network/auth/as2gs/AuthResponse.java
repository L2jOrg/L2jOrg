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

import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.auth.AuthServerClient;
import org.l2j.gameserver.network.auth.ReceivablePacket;
import org.l2j.gameserver.network.auth.gs2as.PlayerInGame;
import org.l2j.gameserver.settings.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * @author JoeAlisson
 */
public class AuthResponse extends ReceivablePacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthResponse.class);

    private String serverName;

    @Override
    protected void readImpl() {
        serverName = readString();
    }

    @Override
    protected void runImpl() {
        String[] accounts = client.authService().getAccounts();
        if(!isNullOrEmpty(accounts)) {
            sendPacket(new PlayerInGame(accounts));
        }
        LOGGER.info("Registered on authserver as {} [{}]", ServerSettings.serverId(), serverName);
    }
}
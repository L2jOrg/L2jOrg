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

import org.l2j.gameserver.network.auth.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthFail extends ReceivablePacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthFail.class);

    private static final String[] REASONS = {
        "IP banned",
        "IP reserved",
        "ID reserved",
        "Not authed",
        "Bad data",
        "Missing Key"
    };

    private byte reasonId;

    @Override
    protected void readImpl() {
        reasonId = readByte();
    }

    protected void runImpl() {
        LOGGER.error("Auth server registration failed! Reason: {}", REASONS[reasonId]);
        client.authService().shutdown();
    }
}
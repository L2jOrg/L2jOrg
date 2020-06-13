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
package org.l2j.gameserver.network.authcomm.as2gs;

import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServerFail extends ReceivablePacket {

    private static final Logger logger = LoggerFactory.getLogger(LoginServerFail.class);

    private static final String[] REASONS = {
        "none",
        "IP banned",
        "IP reserved",
        "wrong hexid",
        "ID reserved",
        "no free ID",
        "not authed",
        "already logged in"
    };

    private String _reason;
    private boolean _restartConnection = true;

    @Override
    protected void readImpl() {
        int reasonId = readByte();
        if(available() <= 0) {
            _reason = "Authserver registration failed! Reason: " + REASONS[reasonId];
        } else {
            _reason = readString();
            _restartConnection = readByte() > 0;
        }
    }

    protected void runImpl() {
        logger.warn(_reason);
        if(!_restartConnection)
            AuthServerCommunication.getInstance().shutdown();
    }
}
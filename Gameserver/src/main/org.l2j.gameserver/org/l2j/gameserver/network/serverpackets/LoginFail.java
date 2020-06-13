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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class LoginFail extends ServerPacket {
    public static final int NO_TEXT = 0;
    public static final int SYSTEM_ERROR_LOGIN_LATER = 1;
    public static final int PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT = 2;
    public static final int PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT2 = 3;
    public static final int ACCESS_FAILED_TRY_LATER = 4;
    public static final int INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT = 5;
    public static final int ACCESS_FAILED_TRY_LATER2 = 6;
    public static final int ACOUNT_ALREADY_IN_USE = 7;
    public static final int ACCESS_FAILED_TRY_LATER3 = 8;
    public static final int ACCESS_FAILED_TRY_LATER4 = 9;
    public static final int ACCESS_FAILED_TRY_LATER5 = 10;

    public static final LoginFail LOGIN_SUCCESS = new LoginFail(-1, NO_TEXT);

    private final int _reason;
    private final int _success;

    public LoginFail(int reason) {
        _success = 0;
        _reason = reason;
    }

    public LoginFail(int success, int reason) {
        _success = success;
        _reason = reason;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.LOGIN_RESULT);

        writeInt(_success);
        writeInt(_reason);
    }

}

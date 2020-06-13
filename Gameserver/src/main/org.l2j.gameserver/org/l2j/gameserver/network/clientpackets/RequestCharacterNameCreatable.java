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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.network.serverpackets.ExIsCharNameCreatable;

import static org.l2j.commons.util.Util.isAlphaNumeric;

/**
 * @author UnAfraid
 */
public class RequestCharacterNameCreatable extends ClientPacket {
    public static int CHARACTER_CREATE_FAILED = 1;
    public static int NAME_ALREADY_EXISTS = 2;
    public static int INVALID_LENGTH = 3;
    public static int INVALID_NAME = 4;
    public static int CANNOT_CREATE_SERVER = 5;
    private String _name;
    private int result;

    @Override
    public void readImpl() {
        _name = readString();
    }

    @Override
    public void runImpl() {
        final int charId = PlayerNameTable.getInstance().getIdByName(_name);

        if (!isAlphaNumeric(_name) || !isValidName(_name)) {
            result = INVALID_NAME;
        } else if (charId > 0) {
            result = NAME_ALREADY_EXISTS;
        } else if (_name.length() > 16) {
            result = INVALID_LENGTH;
        } else {
            result = -1;
        }

        client.sendPacket(new ExIsCharNameCreatable(result));
    }

    private boolean isValidName(String text) {
        return Config.CHARNAME_TEMPLATE_PATTERN.matcher(text).matches();
    }
}
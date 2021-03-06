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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class CharCreateFail extends ServerPacket {

    private final int _error;

    public CharCreateFail(CharacterCreateFailReason reason) {
        _error = reason.getCode();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.CHARACTER_CREATE_FAIL, buffer );

        buffer.writeInt(_error);
    }

    public enum CharacterCreateFailReason  {
        REASON_CREATION_FAILED(0), // "Your character creation has failed."
        REASON_TOO_MANY_CHARACTERS(1), // "You cannot create another character. Please delete the existing character and try again." Removes all settings that were selected (race, class, etc).
        REASON_NAME_ALREADY_EXISTS(2), // "This name already exists."
        REASON_16_ENG_CHARS(3), // "Your title cannot exceed 16 characters in length. Please try again."
        REASON_INCORRECT_NAME(4), // "Incorrect name. Please try again."
        REASON_CREATE_NOT_ALLOWED(5), // "Characters cannot be created from this server."
        REASON_CHOOSE_ANOTHER_SVR(6); // "Unable to create character. You are unable to create a new character on the selected server. A restriction is in place which restricts users from creating characters on different servers where no previous character exists. Please choose another server."

        private final int _code;

        CharacterCreateFailReason(int code)
        {
            _code = code;
        }

        public final int getCode()
        {
            return _code;
        }
        }

}

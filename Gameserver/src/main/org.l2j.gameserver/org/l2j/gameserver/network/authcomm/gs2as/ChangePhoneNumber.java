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
package org.l2j.gameserver.network.authcomm.gs2as;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class ChangePhoneNumber extends SendablePacket
{
    private final String _account;
    private final long _phoneNumber;

    public ChangePhoneNumber(String account, long phoneNumber)
    {
        _account = account;
        _phoneNumber = phoneNumber;
    }

    @Override
    protected void writeImpl(AuthServerClient client, WritableBuffer buffer) {
        buffer.writeByte(0x0c);
        buffer.writeString(_account);
        buffer.writeLong(_phoneNumber);
    }
}
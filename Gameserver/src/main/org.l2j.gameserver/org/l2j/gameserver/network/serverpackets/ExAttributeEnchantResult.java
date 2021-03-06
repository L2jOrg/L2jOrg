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
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

public class ExAttributeEnchantResult extends ServerPacket {
    private final int _result;
    private final int _isWeapon;
    private final int _type;
    private final int _before;
    private final int _after;
    private final int _successCount;
    private final int _failedCount;

    public ExAttributeEnchantResult(int result, boolean isWeapon, AttributeType type, int before, int after, int successCount, int failedCount) {
        _result = result;
        _isWeapon = isWeapon ? 1 : 0;
        _type = type.getClientId();
        _before = before;
        _after = after;
        _successCount = successCount;
        _failedCount = failedCount;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_ATTRIBUTE_ENCHANT_RESULT, buffer );

        buffer.writeInt(_result);
        buffer.writeByte(_isWeapon);
        buffer.writeShort(_type);
        buffer.writeShort(_before);
        buffer.writeShort(_after);
        buffer.writeShort(_successCount);
        buffer.writeShort(_failedCount);
    }

}

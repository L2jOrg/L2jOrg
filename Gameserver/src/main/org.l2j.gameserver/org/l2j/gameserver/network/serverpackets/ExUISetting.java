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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Objects;

/**
 * @author Mobius
 */
public class ExUISetting extends ServerPacket {
    public static final String UI_KEY_MAPPING_VAR = "UI_KEY_MAPPING";
    public static final String SPLIT_VAR = "	";
    private final byte[] _uiKeyMapping;

    public ExUISetting(Player player) {
        if (player.getUiKeyMapping() != null && !player.getUiKeyMapping().trim().equalsIgnoreCase("")) {
            _uiKeyMapping = getByteArray(player.getUiKeyMapping(), UI_KEY_MAPPING_VAR, SPLIT_VAR);
        } else {
            _uiKeyMapping = null;
        }
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_UI_SETTING, buffer );
        if (_uiKeyMapping != null) {
            buffer.writeInt(_uiKeyMapping.length);
            buffer.writeBytes(_uiKeyMapping);
        } else {
            buffer.writeInt(0);
        }
    }

    public byte[] getByteArray(Object val, String key, String splitOn) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(splitOn);
        if (val == null) {
            throw new IllegalArgumentException("Byte value required, but not specified");
        }
        if (val instanceof Number) {
            return new byte[] {
                    ((Number) val).byteValue()
            };
        }
        int c = 0;
        final String[] vals = ((String) val).split(splitOn);
        final byte[] result = new byte[vals.length];
        for (String v : vals) {
            try {
                result[c++] = Byte.parseByte(v);
            } catch (Exception e) {
                throw new IllegalArgumentException("Byte value required, but found: " + val);
            }
        }
        return result;
    }

}

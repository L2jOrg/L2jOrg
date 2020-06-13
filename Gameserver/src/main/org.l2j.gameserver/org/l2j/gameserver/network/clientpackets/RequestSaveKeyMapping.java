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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.ConnectionState;

/**
 * Request Save Key Mapping client packet.
 *
 * @author Mobius
 */
public class RequestSaveKeyMapping extends ClientPacket {
    public static final String UI_KEY_MAPPING_VAR = "UI_KEY_MAPPING";
    public static final String SPLIT_VAR = "	";
    private byte[] _uiKeyMapping;

    @Override
    public void readImpl() {
        final int dataSize = readInt();
        if (dataSize > 0) {
            _uiKeyMapping = new byte[dataSize];
            readBytes(_uiKeyMapping);
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (!Config.STORE_UI_SETTINGS || //
                (player == null) || //
                (_uiKeyMapping == null) || //
                (client.getConnectionState() != ConnectionState.IN_GAME)) {
            return;
        }

        String uiKeyMapping = "";
        for (Byte b : _uiKeyMapping) {
            uiKeyMapping += b + SPLIT_VAR;
        }
        player.getVariables().set(UI_KEY_MAPPING_VAR, uiKeyMapping);
    }
}

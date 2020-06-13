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

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author Sdw
 */
public class ExShowBeautyMenu extends ServerPacket {
    // TODO: Enum
    public static final int MODIFY_APPEARANCE = 0;
    public static final int RESTORE_APPEARANCE = 1;
    private final Player _activeChar;
    private final int _type;

    public ExShowBeautyMenu(Player activeChar, int type) {
        _activeChar = activeChar;
        _type = type;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_BEAUTY_MENU);

        writeInt(_type);
        writeInt(_activeChar.getVisualHair());
        writeInt(_activeChar.getVisualHairColor());
        writeInt(_activeChar.getVisualFace());
    }

}
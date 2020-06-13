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
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author mrTJO
 */
public class Ex2ndPasswordVerify extends ServerPacket {
    // TODO: Enum
    public static final int PASSWORD_OK = 0x00;
    public static final int PASSWORD_WRONG = 0x01;
    public static final int PASSWORD_BAN = 0x02;

    private final int _wrongTentatives;
    private final int _mode;

    public Ex2ndPasswordVerify(int mode, int wrongTentatives) {
        _mode = mode;
        _wrongTentatives = wrongTentatives;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_2ND_PASSWORD_VERIFY);

        writeInt(_mode);
        writeInt(_wrongTentatives);
    }

}

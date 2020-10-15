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
package org.l2j.gameserver.network.serverpackets.olympiad;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExOlympiadHeroesInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_OLYMPIAD_HERO_AND_LEGEND_INFO, buffer );

        buffer.writeShort(1024); // size ??
        //Legend Info
        buffer.writeSizedString("Legend name");
        buffer.writeSizedString("Legend clan name");
        buffer.writeInt(1); // legend world id
        buffer.writeInt(0); // legend race
        buffer.writeInt(0); // legend sex
        buffer.writeInt(88); // legend class id
        buffer.writeInt(85); // legend level

        buffer.writeInt(5); // count
        buffer.writeInt(4); // win count
        buffer.writeInt(1); // lose count
        buffer.writeInt(100); // olympiad points
        buffer.writeInt(4); // clan level

        buffer.writeInt(40); // heroes size

        for (int i = 0; i < 40; i++) {
            buffer.writeSizedString("Hero name" + i);
            buffer.writeSizedString("Hero clan name" + i);
            buffer.writeInt((i % 2) + 1); // hero world id
            buffer.writeInt(0); // hero race
            buffer.writeInt(i % 2); // hero sex
            buffer.writeInt(88 + i); // hero class id
            buffer.writeInt(85); // hero level

            buffer.writeInt( (i % 4) + 1); // count
            buffer.writeInt(4 + i); // win count
            buffer.writeInt(1 + i); // lose count
            buffer.writeInt(100 + i); // olympiad points
            buffer.writeInt(5); // clan level
        }


    }
}

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
package org.l2j.gameserver.network.serverpackets.olympiad;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author godson
 * @author JoeAlisson
 */
public class ExOlympiadUserInfo extends ServerPacket {
    private final Player player;
    private final int currentHp;
    private final int maxHp;
    private final int currentCp;
    private final int maxCp;

    public ExOlympiadUserInfo(Player player) {
        this.player = player;
        currentHp = (int) this.player.getCurrentHp();
        maxHp = this.player.getMaxHp();
        currentCp = (int) this.player.getCurrentCp();
        maxCp = this.player.getMaxCp();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_OLYMPIAD_USER_INFO, buffer );
        buffer.writeByte(player.getOlympiadSide());
        buffer.writeInt(player.getObjectId());
        buffer.writeString(player.getName());
        buffer.writeInt(player.getClassId().getId());

        buffer.writeInt(currentHp);
        buffer.writeInt(maxHp);
        buffer.writeInt(currentCp);
        buffer.writeInt(maxCp);
    }

}

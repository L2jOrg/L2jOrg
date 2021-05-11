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
import org.l2j.gameserver.engine.clan.clanhall.ClanHall;
import org.l2j.gameserver.engine.clan.clanhall.ClanHallEngine;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import static org.l2j.commons.util.Util.emptyIfNullOrElse;

/**
 * @author KenM
 * @author JoeAlisson
 */
public class ExShowAgitInfo extends ServerPacket {
    public static final ExShowAgitInfo STATIC_PACKET = new ExShowAgitInfo();

    private ExShowAgitInfo() {
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SHOW_AGIT_INFO, buffer );

        var clanHallEngine = ClanHallEngine.getInstance();
        buffer.writeInt(clanHallEngine.getClanHallAmount());
        clanHallEngine.forEachClanHall(clanHall -> writeClanHallInfo(clanHall, buffer));
    }

    private void writeClanHallInfo(ClanHall clanHall, WritableBuffer buffer) {
        buffer.writeInt(clanHall.getId());
        buffer.writeString(emptyIfNullOrElse(clanHall.getOwner(), Clan::getName));
        buffer.writeString(emptyIfNullOrElse(clanHall.getOwner(), Clan::getLeaderName));
        buffer.writeInt(clanHall.getType().ordinal());
    }

}

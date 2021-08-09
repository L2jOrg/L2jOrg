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
package org.l2j.gameserver.network.clientpackets.pvpbook;

import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.pvpbook.PvpBookList;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author JoeAlisson
 */
public class ExRequestPvpBookList extends ClientPacket {

    @Override
    protected void readImpl() {
        // dummy byte
    }

    @Override
    protected void runImpl()  {
        var since = Instant.now().minus(1, ChronoUnit.DAYS).getEpochSecond();
        var killers = getDAO(PlayerDAO.class).findKillersByPlayer(client.getPlayer().getObjectId(), since);
        client.sendPacket(new PvpBookList(killers));
    }
}

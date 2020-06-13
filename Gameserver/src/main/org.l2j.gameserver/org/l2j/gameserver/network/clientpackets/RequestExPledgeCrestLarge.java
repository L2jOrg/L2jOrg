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

import org.l2j.gameserver.data.database.data.CrestData;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.network.serverpackets.ExPledgeEmblem;

/**
 * @author -Wooden-, Sdw
 */
public final class RequestExPledgeCrestLarge extends ClientPacket {
    private int _crestId;
    private int _clanId;

    @Override
    public void readImpl() {
        _crestId = readInt();
        _clanId = readInt();
    }

    @Override
    public void runImpl() {
        final CrestData crest = CrestTable.getInstance().getCrest(_crestId);
        final byte[] data = crest != null ? crest.getData() : null;
        if (data != null) {
            for (int i = 0; i <= 4; i++) {
                final int size = Math.max(Math.min(14336, data.length - (14336 * i)), 0);
                if (size == 0)
                {
                    continue;
                }
                final byte[] chunk = new byte[size];
                System.arraycopy(data, (14336 * i), chunk, 0, size);
                client.sendPacket(new ExPledgeEmblem(_crestId, chunk, _clanId, i));
            }
        }
    }
}

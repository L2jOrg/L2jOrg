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
package org.l2j.gameserver.network.serverpackets.trade;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Yme
 * @author JoeAlisson
 */
public final class TradeOwnAdd extends TradeAdd {

    public TradeOwnAdd(int type, TradeItem item) {
        super(type, item);

    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.TRADE_OWN_ADD);
        writeItemAdd();
    }

}

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
package org.l2j.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRProductList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gnacik, UnAfraid
 */
public final class RequestBRProductList extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBRProductList.class);
    private int _type;

    @Override
    public void readImpl() {
        _type = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player != null) {

            switch (_type) {
                case 0: // Home page
                {
                    player.sendPacket(new ExBRProductList(player, 0, PrimeShopData.getInstance().getPrimeItems().values()));
                    break;
                }
                case 1: // History
                {
                    break;
                }
                case 2: // Favorites
                {
                    break;
                }
                default: {
                    LOGGER.warn(player + " send unhandled product list type: " + _type);
                    break;
                }
            }
        }
    }
}
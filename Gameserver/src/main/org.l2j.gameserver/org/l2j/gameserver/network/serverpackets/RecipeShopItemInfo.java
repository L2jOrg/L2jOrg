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
import org.l2j.gameserver.network.ServerPacketId;

public class RecipeShopItemInfo extends ServerPacket {
    private final Player _player;
    private final int _recipeId;

    public RecipeShopItemInfo(Player player, int recipeId) {
        _player = player;
        _recipeId = recipeId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.RECIPE_SHOP_ITEM_INFO);

        writeInt(_player.getObjectId());
        writeInt(_recipeId);
        writeInt((int) _player.getCurrentMp());
        writeInt(_player.getMaxMp());
        writeInt(0xffffffff);
        writeLong(0x00);
        writeByte((byte) 0x00); // Trigger offering window if 1
        writeLong(0x00);
    }

}

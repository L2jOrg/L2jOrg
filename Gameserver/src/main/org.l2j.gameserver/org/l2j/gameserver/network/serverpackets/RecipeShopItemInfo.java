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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
public class RecipeShopItemInfo extends ServerPacket {
    private final Player player;
    private final int recipeId;

    public RecipeShopItemInfo(Player player, int recipeId) {
        this.player = player;
        this.recipeId = recipeId;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.RECIPE_SHOP_ITEM_INFO, buffer );

        buffer.writeInt(player.getObjectId());
        buffer.writeInt(recipeId);
        buffer.writeInt((int) player.getCurrentMp());
        buffer.writeInt(player.getMaxMp());
        buffer.writeInt(-0x01);
        buffer.writeLong(0x00);
        buffer.writeByte(0x00); // show addSuccess (deprecated)
        buffer.writeDouble(0x00);

        buffer.writeDouble(player.getStats().getValue(Stat.CRAFT_RATE_MASTER));
        buffer.writeByte(1); // show crit rate
        buffer.writeDouble(player.getStats().getValue(Stat.CRAFT_RATE_CRITICAL));
    }

}

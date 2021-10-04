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
import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.model.RecipeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.ServerPacketId;

import static java.util.Objects.requireNonNull;

public class RecipeItemMakeInfo extends ServerPacket {

    private final int id;
    private final Player player;
    private final boolean success;
    private final RecipeList recipe;

    public RecipeItemMakeInfo(int id, Player player, boolean success) {
        this.id = id;
        this.player = player;
        this.success = success;
        recipe = requireNonNull(RecipeData.getInstance().getRecipeList(id));
    }

    public RecipeItemMakeInfo(int id, Player player) {
        this(id, player, true);
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) throws InvalidDataPacketException {
        writeId(ServerPacketId.RECIPE_ITEM_MAKE_INFO, buffer );
        buffer.writeInt(id);
        buffer.writeInt(!recipe.isDwarvenRecipe());
        buffer.writeInt((int) player.getCurrentMp());
        buffer.writeInt(player.getMaxMp());
        buffer.writeInt(success);
        buffer.writeByte(0x00); // activate or deactivate a line named "addSuccess: +0%" , maybe deprecated
        buffer.writeLong(0x00); // might need a Double here, addSuccess chance

        buffer.writeDouble(player.getStats().getValue(Stat.CRAFT_RATE_MASTER));
        buffer.writeByte(1); // show crit rate
        buffer.writeDouble(player.getStats().getValue(Stat.CRAFT_RATE_CRITICAL));
    }

}

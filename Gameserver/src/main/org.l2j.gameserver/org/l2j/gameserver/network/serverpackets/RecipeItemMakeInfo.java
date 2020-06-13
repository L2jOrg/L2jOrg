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

import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.model.RecipeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.ServerPacketId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeItemMakeInfo extends ServerPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecipeItemMakeInfo.class);

    private final int _id;
    private final Player _activeChar;
    private final boolean _success;

    public RecipeItemMakeInfo(int id, Player player, boolean success) {
        _id = id;
        _activeChar = player;
        _success = success;
    }

    public RecipeItemMakeInfo(int id, Player player) {
        _id = id;
        _activeChar = player;
        _success = true;
    }

    @Override
    public void writeImpl(GameClient client) throws InvalidDataPacketException {
        final RecipeList recipe = RecipeData.getInstance().getRecipeList(_id);
        if (recipe != null) {
            writeId(ServerPacketId.RECIPE_ITEM_MAKE_INFO);
            writeInt(_id);
            writeInt(recipe.isDwarvenRecipe() ? 0 : 1); // 0 = Dwarven - 1 = Common
            writeInt((int) _activeChar.getCurrentMp());
            writeInt(_activeChar.getMaxMp());
            writeInt(_success ? 1 : 0); // item creation success/failed
            writeByte(0x00); // activate or deactivate a line named "addSuccess: +0%" , maybe deprecated
            writeLong(0x00); // might need a Double here, addSuccess chance

            writeDouble(_activeChar.getStats().getValue(Stat.CRAFT_RATE_MASTER)); // Chance Bonus craft chance rate
            writeByte(1); // activate or deactivate Critical rate info line
            writeDouble(_activeChar.getStats().getValue(Stat.CRAFT_RATE_CRITICAL)); // Critical rate chance
        } else {
            LOGGER.info("Character: " + _activeChar + ": Requested unexisting recipe with id = " + _id);
            throw new InvalidDataPacketException();
        }
    }

}

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
import org.l2j.gameserver.model.RecipeList;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class RecipeBookItemList extends ServerPacket {
    private final boolean _isDwarvenCraft;
    private final int _maxMp;
    private RecipeList[] _recipes;

    public RecipeBookItemList(boolean isDwarvenCraft, int maxMp) {
        _isDwarvenCraft = isDwarvenCraft;
        _maxMp = maxMp;
    }

    public void addRecipes(RecipeList[] recipeBook) {
        _recipes = recipeBook;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.RECIPE_BOOK_ITEM_LIST, buffer );

        buffer.writeInt(!_isDwarvenCraft); // 0 = Dwarven - 1 = Common
        buffer.writeInt(_maxMp);

        if (_recipes == null) {
            buffer.writeInt(0);
        } else {
            buffer.writeInt(_recipes.length); // number of items in recipe book
            for (int i = 0; i < _recipes.length; i++) {
                buffer.writeInt(_recipes[i].getId());
                buffer.writeInt(i + 1);
            }
        }
    }

}

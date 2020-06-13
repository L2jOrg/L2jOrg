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

import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.model.RecipeList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.RecipeBookItemList;

public final class RequestRecipeBookDestroy extends ClientPacket {
    private int _recipeID;

    @Override
    public void readImpl() {
        _recipeID = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("RecipeDestroy")) {
            return;
        }

        final RecipeList rp = RecipeData.getInstance().getRecipeList(_recipeID);
        if (rp == null) {
            return;
        }
        activeChar.unregisterRecipeList(_recipeID);

        final RecipeBookItemList response = new RecipeBookItemList(rp.isDwarvenRecipe(), activeChar.getMaxMp());
        if (rp.isDwarvenRecipe()) {
            response.addRecipes(activeChar.getDwarvenRecipeBook());
        } else {
            response.addRecipes(activeChar.getCommonRecipeBook());
        }

        activeChar.sendPacket(response);
    }
}
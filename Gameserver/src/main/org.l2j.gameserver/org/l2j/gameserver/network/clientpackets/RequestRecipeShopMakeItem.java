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

import org.l2j.gameserver.RecipeController;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;

/**
 * @author Administrator
 */
public final class RequestRecipeShopMakeItem extends ClientPacket {
    private int _id;
    private int _recipeId;
    @SuppressWarnings("unused")
    private long _unknown;

    @Override
    public void readImpl() {
        _id = readInt();
        _recipeId = readInt();
        _unknown = readLong();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        if (!client.getFloodProtectors().getManufacture().tryPerformAction("RecipeShopMake")) {
            return;
        }

        final Player manufacturer = World.getInstance().findPlayer(_id);
        if (manufacturer == null) {
            return;
        }

        if (manufacturer.getInstanceWorld() != activeChar.getInstanceWorld()) {
            return;
        }

        if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE) {
            activeChar.sendMessage("You cannot create items while trading.");
            return;
        }
        if (manufacturer.getPrivateStoreType() != PrivateStoreType.MANUFACTURE) {
            // activeChar.sendMessage("You cannot create items while trading.");
            return;
        }

        if (activeChar.isCrafting() || manufacturer.isCrafting()) {
            activeChar.sendMessage("You are currently in Craft Mode.");
            return;
        }
        if (GameUtils.checkIfInRange(150, activeChar, manufacturer, true)) {
            RecipeController.getInstance().requestManufactureItem(manufacturer, _recipeId, activeChar);
        }
    }
}

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
package handlers.admincommandhandlers;

import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author Mobius
 */
public class AdminDestroyItems implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS =
            {
                    "admin_destroy_items",
                    "admin_destroy_all_items",
                    "admin_destroyitems",
                    "admin_destroyallitems"
            };

    @Override
    public boolean useAdminCommand(String command, Player activeChar)
    {
        final PlayerInventory inventory = activeChar.getInventory();
        final InventoryUpdate iu = new InventoryUpdate();
        for (Item item : inventory.getItems())
        {
            if (item.isEquipped() && !command.contains("all"))
            {
                continue;
            }
            iu.addRemovedItem(item);
            inventory.destroyItem("Admin Destroy", item, activeChar, null);
        }
        activeChar.sendPacket(iu);
        return true;
    }

    @Override
    public String[] getAdminCommandList()
    {
        return ADMIN_COMMANDS;
    }
}

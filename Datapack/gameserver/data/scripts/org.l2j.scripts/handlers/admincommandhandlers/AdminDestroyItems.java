package handlers.admincommandhandlers;

import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.itemcontainer.PcInventory;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
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
        final PcInventory inventory = activeChar.getInventory();
        final InventoryUpdate iu = new InventoryUpdate();
        for (L2ItemInstance item : inventory.getItems())
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

package org.l2j.gameserver.network.clientpackets.equipmentupgrade;

import org.l2j.gameserver.data.xml.impl.EquipmentUpgradeData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.EquipmentUpgradeHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.equipmentupgrade.ExUpgradeSystemResult;

/**
 * @author Mobius
 */
public class RequestUpgradeSystemResult extends ClientPacket
{
    private int _objectId;
    private int _upgradeId;

    @Override
    public void readImpl() {
        _objectId = readInt();
        _upgradeId = readInt();
    }

    @Override
    public void runImpl()
    {
        final Player player = client.getPlayer();
        if (player == null)
        {
            return;
        }

        final Item existingItem = player.getInventory().getItemByObjectId(_objectId);
        if (existingItem == null)
        {
            player.sendPacket(new ExUpgradeSystemResult(0, 0));
            return;
        }

        final EquipmentUpgradeHolder upgradeHolder = EquipmentUpgradeData.getInstance().getUpgrade(_upgradeId);
        if (upgradeHolder == null)
        {
            player.sendPacket(new ExUpgradeSystemResult(0, 0));
            return;
        }

        for (ItemHolder material : upgradeHolder.getMaterials())
        {
            if (player.getInventory().getInventoryItemCount(material.getId(), -1) < material.getCount())
            {
                player.sendPacket(new ExUpgradeSystemResult(0, 0));
                return;
            }
        }

        final long adena = upgradeHolder.getAdena();
        if ((adena > 0) && (player.getAdena() < adena))
        {
            player.sendPacket(new ExUpgradeSystemResult(0, 0));
            return;
        }

        if ((existingItem.getTemplate().getId() != upgradeHolder.getRequiredItemId()) || (existingItem.getEnchantLevel() != upgradeHolder.getRequiredItemEnchant()))
        {
            player.sendPacket(new ExUpgradeSystemResult(0, 0));
            return;
        }

        // Get materials.
        player.destroyItem("UpgradeEquipment", _objectId, 1, player, true);
        for (ItemHolder material : upgradeHolder.getMaterials())
        {
            player.destroyItemByItemId("UpgradeEquipment", material.getId(), material.getCount(), player, true);
        }
        if (adena > 0)
        {
            player.reduceAdena("UpgradeEquipment", adena, player, true);
        }

        // Give item.
        final Item newItem = player.addItem("UpgradeEquipment", upgradeHolder.getResultItemId(), 1, player, true);
        final int enchantLevel = upgradeHolder.getResultItemEnchant();
        if (enchantLevel > 0)
        {
            newItem.setEnchantLevel(enchantLevel);
        }

        player.sendPacket(new ExUpgradeSystemResult(newItem.getObjectId(), 1));
    }
}

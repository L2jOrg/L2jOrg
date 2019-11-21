package org.l2j.gameserver.model;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

public class CombatFlag {
    private final Location _location;
    private final int _itemId;
    @SuppressWarnings("unused")
    private final int _fortId;
    private Player _player = null;
    private int _playerId = 0;
    private Item _item = null;
    private Item _itemInstance;

    public CombatFlag(int fort_id, int x, int y, int z, int heading, int item_id) {
        _fortId = fort_id;
        _location = new Location(x, y, z, heading);
        _itemId = item_id;
    }

    public synchronized void spawnMe() {
        // Init the dropped Item and add it in the world as a visible object at the position where mob was last
        _itemInstance = ItemEngine.getInstance().createItem("Combat", _itemId, 1, null, null);
        _itemInstance.dropMe(null, _location.getX(), _location.getY(), _location.getZ());
    }

    public synchronized void unSpawnMe() {
        if (_player != null) {
            dropIt();
        }
        if (_itemInstance != null) {
            _itemInstance.decayMe();
        }
    }

    public boolean activate(Player player, Item item) {
        if (player.isMounted()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
            return false;
        }

        // Player holding it data
        _player = player;
        _playerId = _player.getObjectId();
        _itemInstance = null;

        // Equip with the weapon
        _item = item;
        _player.getInventory().equipItem(_item);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EQUIPPED_YOUR_S1);
        sm.addItemName(_item);
        _player.sendPacket(sm);

        // Refresh inventory
        if (!Config.FORCE_INVENTORY_UPDATE) {
            final InventoryUpdate iu = new InventoryUpdate();
            iu.addItem(_item);
            _player.sendInventoryUpdate(iu);
        } else {
            _player.sendItemList();
        }
        // Refresh player stats
        _player.broadcastUserInfo();
        _player.setCombatFlagEquipped(true);
        return true;
    }

    public void dropIt() {
        // Reset player stats
        _player.setCombatFlagEquipped(false);
         var bodyPart = BodyPart.fromEquippedPaperdoll(_item);
        _player.getInventory().unEquipItemInBodySlot(bodyPart);
        _player.destroyItem("CombatFlag", _item, null, true);
        _item = null;
        _player.broadcastUserInfo();
        _player = null;
        _playerId = 0;
    }

    public int getPlayerObjectId() {
        return _playerId;
    }

    public Item getCombatFlagInstance() {
        return _itemInstance;
    }
}

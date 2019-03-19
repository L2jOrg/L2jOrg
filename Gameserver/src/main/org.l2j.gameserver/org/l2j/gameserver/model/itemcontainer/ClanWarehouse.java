package org.l2j.gameserver.model.itemcontainer;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanWHItemAdd;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanWHItemDestroy;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanWHItemTransfer;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;

public final class ClanWarehouse extends Warehouse {
    private final L2Clan _clan;

    public ClanWarehouse(L2Clan clan) {
        _clan = clan;
    }

    @Override
    public String getName() {
        return "ClanWarehouse";
    }

    @Override
    public int getOwnerId() {
        return _clan.getId();
    }

    @Override
    public L2PcInstance getOwner() {
        return _clan.getLeader().getPlayerInstance();
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.CLANWH;
    }

    @Override
    public boolean validateCapacity(long slots) {
        return (_items.size() + slots) <= Config.WAREHOUSE_SLOTS_CLAN;
    }

    @Override
    public L2ItemInstance addItem(String process, int itemId, long count, L2PcInstance actor, Object reference) {
        final L2ItemInstance item = super.addItem(process, itemId, count, actor, reference);

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanWHItemAdd(process, actor, item, this), item.getItem());
        return item;
    }

    @Override
    public L2ItemInstance addItem(String process, L2ItemInstance item, L2PcInstance actor, Object reference) {
        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanWHItemAdd(process, actor, item, this), item.getItem());
        return super.addItem(process, item, actor, reference);
    }

    @Override
    public L2ItemInstance destroyItem(String process, L2ItemInstance item, long count, L2PcInstance actor, Object reference) {
        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanWHItemDestroy(process, actor, item, count, this), item.getItem());
        return super.destroyItem(process, item, count, actor, reference);
    }

    @Override
    public L2ItemInstance transferItem(String process, int objectId, long count, ItemContainer target, L2PcInstance actor, Object reference) {
        final L2ItemInstance item = getItemByObjectId(objectId);

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanWHItemTransfer(process, actor, item, count, target), item.getItem());
        return super.transferItem(process, objectId, count, target, actor, reference);
    }
}

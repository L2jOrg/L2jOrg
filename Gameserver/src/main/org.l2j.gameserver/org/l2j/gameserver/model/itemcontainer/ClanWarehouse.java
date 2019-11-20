package org.l2j.gameserver.model.itemcontainer;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanWHItemAdd;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanWHItemDestroy;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanWHItemTransfer;
import org.l2j.gameserver.model.items.instance.Item;

public final class ClanWarehouse extends Warehouse {
    private final Clan _clan;

    public ClanWarehouse(Clan clan) {
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
    public Player getOwner() {
        return _clan.getLeader().getPlayerInstance();
    }

    @Override
    public ItemLocation getBaseLocation() {
        return ItemLocation.CLANWH;
    }

    @Override
    public boolean validateCapacity(long slots) {
        return (items.size() + slots) <= Config.WAREHOUSE_SLOTS_CLAN;
    }

    @Override
    public Item addItem(String process, int itemId, long count, Player actor, Object reference) {
        final Item item = super.addItem(process, itemId, count, actor, reference);

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanWHItemAdd(process, actor, item, this), item.getTemplate());
        return item;
    }

    @Override
    public Item addItem(String process, Item item, Player actor, Object reference) {
        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanWHItemAdd(process, actor, item, this), item.getTemplate());
        return super.addItem(process, item, actor, reference);
    }

    @Override
    public Item destroyItem(String process, Item item, long count, Player actor, Object reference) {
        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanWHItemDestroy(process, actor, item, count, this), item.getTemplate());
        return super.destroyItem(process, item, count, actor, reference);
    }

    @Override
    public Item transferItem(String process, int objectId, long count, ItemContainer target, Player actor, Object reference) {
        final Item item = getItemByObjectId(objectId);

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerClanWHItemTransfer(process, actor, item, count, target), item.getTemplate());
        return super.transferItem(process, objectId, count, target, actor, reference);
    }
}

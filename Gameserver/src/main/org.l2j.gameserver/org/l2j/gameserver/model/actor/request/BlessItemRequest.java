package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.actor.instance.Player;

public class BlessItemRequest extends AbstractRequest{

    private int _item;
    private Item _scroll;

    public BlessItemRequest(Player player, Item scroll) {
        super(player);
        this._scroll = scroll;
    }

    public void setItem(int item) {
        _item = item;
    }

    public Item getItem() {
        return getPlayer().getInventory().getItemByObjectId(_item);
    }

    @Override
    public boolean isItemRequest() {
        return true;
    }

    @Override
    public boolean canWorkWith(AbstractRequest request) {
        return !request.isItemRequest();
    }

    @Override
    public boolean isUsingItem(int objectId) {
        return (objectId > 0) && (objectId == _item);
    }

    public Item getScroll() {
        return _scroll;
    }
}

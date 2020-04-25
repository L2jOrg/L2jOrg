package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.instance.Item;

/**
 * @author UnAfraid
 */
public class OnPlayerClanWHItemDestroy implements IBaseEvent {
    private final String _process;
    private final Player _activeChar;
    private final Item _item;
    private final long _count;
    private final ItemContainer _container;

    public OnPlayerClanWHItemDestroy(String process, Player activeChar, Item item, long count, ItemContainer container) {
        _process = process;
        _activeChar = activeChar;
        _item = item;
        _count = count;
        _container = container;
    }

    public String getProcess() {
        return _process;
    }

    public Player getActiveChar() {
        return _activeChar;
    }

    public Item getItem() {
        return _item;
    }

    public long getCount() {
        return _count;
    }

    public ItemContainer getContainer() {
        return _container;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_CLAN_WH_ITEM_DESTROY;
    }
}

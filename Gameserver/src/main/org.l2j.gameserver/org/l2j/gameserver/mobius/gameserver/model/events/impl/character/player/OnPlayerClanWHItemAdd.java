package org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.ItemContainer;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;

/**
 * @author UnAfraid
 */
public class OnPlayerClanWHItemAdd implements IBaseEvent
{
    private final String _process;
    private final L2PcInstance _activeChar;
    private final L2ItemInstance _item;
    private final ItemContainer _container;

    public OnPlayerClanWHItemAdd(String process, L2PcInstance activeChar, L2ItemInstance item, ItemContainer container)
    {
        _process = process;
        _activeChar = activeChar;
        _item = item;
        _container = container;
    }

    public String getProcess()
    {
        return _process;
    }

    public L2PcInstance getActiveChar()
    {
        return _activeChar;
    }

    public L2ItemInstance getItem()
    {
        return _item;
    }

    public ItemContainer getContainer()
    {
        return _container;
    }

    @Override
    public EventType getType()
    {
        return EventType.ON_PLAYER_CLAN_WH_ITEM_ADD;
    }
}

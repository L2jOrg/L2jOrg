package org.l2j.gameserver.model.entity.events.actions;

import org.l2j.gameserver.data.dao.ItemsDAO;
import org.l2j.gameserver.model.entity.events.Event;
import org.l2j.gameserver.model.entity.events.EventAction;
import org.l2j.gameserver.model.entity.events.objects.ItemObject;

public class GlobalRemoveItemsAction implements EventAction
{
	private final String _name;

	public GlobalRemoveItemsAction(String name)
	{
		_name = name;
	}

	@Override
	public void call(Event event)
	{
		for(Object o : event.getObjects(_name))
		{
			if(o instanceof ItemObject)
				ItemsDAO.getInstance().globalRemoveItem(((ItemObject) o).getItemId(), "Remove items by event: " + event.getName());
		}
	}
}